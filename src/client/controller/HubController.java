package client.controller;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import client.thread.ListenerChatMessage;
import client.thread.requestSender.FindUserRequestSender;
import client.thread.requestSender.LogoutRequestSender;
import client.view.ChatWindow;
import client.view.HubWindow;
import communication.RMI.RMIClientNotifyEvent;
import communication.RMI.RMIServerInterface;

import server.model.User;
import server.model.exception.UserNotFindException;
import server.model.exception.UserStatusException;

/**
 * Controller dell'hub principale gestito dall'utente .
 * @author Gionatha Sturba
 *
 */
public class HubController extends Controller
{
	private HubWindow hubView;
	private User user; //utente che controlla l'hub
	
	//utilita'
	private Controller controller = this;
	private Random rand;
	
	//gestione RMI,notifiche messaggi
	private RMIServerInterface serverRMI = null;
	private RMIClientNotifyEvent callback;
	private ListenerChatMessage listenerChatMessage;
	
	//gestione finestre chat e chatRoom
	private List<ChatController> chats;
	
	private static final String SERVER_RMI_SERVICE_NAME = "SocialGossipNotification";
	private static final int SERVER_RMI_PORT = 6000;
	
	/**
	 * Inizializza un nuuovo Controller per comandare l'hub principale
	 * @param nickname nickname dell'utente che controlla l'hub
	 * @param amiciList lista degli amici dell'utente
	 * @param location posizione finestra
	 */
	public HubController(Socket connection,DataInputStream in,DataOutputStream out,String nickname,List<User> amiciList,Point location) 
	{
		super(connection,in,out);
		
		hubView = new HubWindow();
		setWindow(hubView);
		window.setLocation(location);
		
		rand = new Random(System.currentTimeMillis());
		
		try 
		{
			initComponents(nickname,amiciList);
			initListeners();
		} 
		//errore in fase di inizializzazione
		catch (Exception e) 
		{			
			//chiudo sessione
			closeConnection();
			closeWindow();
					
			e.printStackTrace();
		} 
	}
	
	/**
	 * Inizializza i vari componenti principali dell'hub (connessioni,testi,notifiche,ecc..)
	 * @param nickname nome dell'utente che controlla l'hub
	 * @param amiciList lista degli amici dell'utente
	 * @throws Exception se c'e' un errore nell'inizializzazione dei componenti principali
	 */
	private void initComponents(String nickname,List<User> amiciList) throws Exception
	{	
		user = new User(nickname);
		hubView.setWelcomeText("Loggato come: "+nickname);
		chats = new LinkedList<ChatController>();

		//se mi e' stata passata una lista di amici e di chatRoom
		if(amiciList != null) {
			//aggiorno lista amici
			for (User user : amiciList) {
				hubView.getModelUserFriendList().addElement(user);
			}
			
			//TODO aggiornare lista chatRoom
		}
		
		//configuro RMI per ricevere notifiche sullo stato degli amici e sulle nuove amicizie
		initRMI(nickname);
		
		//configuro thread listener che ascolta i messaggi arrivati da altre chat,e lo faccio partire
		listenerChatMessage = new ListenerChatMessage(this,user,connection.getInetAddress(),connection.getPort());
		listenerChatMessage.start();
		
		//TODO configurare thread per ricevere mesaggi chatroom
		
	}
	
	/**
	 * Inizializza il protocollo RMI per ricevere le notifiche dal server
	 * @param nickname nickname dell'utente
	 * @throws Exception se viene riscontrare un errore nell'inizializzazione del protocollo RMI
	 */
	private void initRMI(String nickname) throws Exception
	{
		try {
			Registry registry = LocateRegistry.getRegistry(SERVER_RMI_PORT);
			
			//cerco registro
			serverRMI = (RMIServerInterface) registry.lookup(SERVER_RMI_SERVICE_NAME);
			
			//creo la classe che implementa le callback
			callback = new NotificationEvents();
			
			//esporto la callback sul registro
			RMIClientNotifyEvent stub = (RMIClientNotifyEvent)UnicastRemoteObject.exportObject(callback,0);
			
			//registro la callback
			serverRMI.registerUserRMIChannel(nickname,stub);
		} 
		catch (RemoteException | NotBoundException | UserNotFindException | UserStatusException e) 
		{
			showErrorMessage("Errore nell'inizializzazione della comunicazione RMI","ERRORE CONNESSIONE");
			throw new Exception();
		}
	}
	
	@Override
	protected void initListeners() 
	{
		//richiesta logout alla chiusura della finestra
		hubView.addWindowListener(new WindowAdapter()
	        {
	            @Override
	            public void windowClosing(WindowEvent e)
	            {
	            	//termino thread attivi
	            	listenerChatMessage.shutdown();
	            	
	            	
	            	closeAllChats();
	            	
	            	//TODO chiudere tutte le finestre di chatroom
	            	
	                new LogoutRequestSender(controller,connection,in,out,user.getNickname(),serverRMI,callback).start();
	            }
	        });
		
		//al click sul bottone ESCI
		hubView.getBtnLogout().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				
				//termino thread attivi
            	listenerChatMessage.shutdown();
            	
				closeAllChats();
				
				//TODO chiudere chatroom
				
				//avvio procedura di logout
                new LogoutRequestSender(controller,connection,in,out,user.getNickname(),serverRMI,callback).start();
			}
		});
		
		//al click sul bottone CERCA
		hubView.getBtnCerca().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//avvio thread che gestisce la richiesta di ricerca utente
				new FindUserRequestSender(controller,connection,in,out,user.getNickname(),hubView.getTextField().getText(),hubView.getModelUserFriendList()).start();
			}
		});
		
		//al click sul bottone AVVIA CHAT
		hubView.getBtnAvviaChat().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//apro la chat richiesta
				openChatFromList();
			}
		});
	}
	
	private void openChatFromList()
	{
		JList<User> list = hubView.getUserFriendList();
		
		synchronized (list) 
		{
			User selectedUser = list.getSelectedValue();
			
			//se non e' stato selezionato nessun utente
			if(selectedUser == null)
			{
				showInfoMessage("Seleziona un utente con cui avviare una chat","Nessun utente selezionatao",false);
			}
			else {
				ChatController chat = null;
				
				//cerco se esiste gia' un istanza della chat,tra le liste
				for (ChatController currentChat : chats) 
				{
					//chat trovata
					if(currentChat.getReceiver().equals(selectedUser))
					{
						chat = currentChat;
						break;
					}
				}
				
				//se non ho trovato la chat,ne creo una nuova e la aggiungo alla lista
				if(chat == null) 
				{
					chat = new ChatController(connection, in, out,user,selectedUser,generateRandomLocation());
					chats.add(chat);
					chat.setVisible(true);
				}
				//chat trovata,la mostro
				else {
					//se non e' gia' visibile
					if(!chat.isVisible())
						chat.setVisible(true);
				}				
			}
		}
	}
	
	public ChatController openChatFromNewMessage(User sender)
	{
		ChatController chat = null;
		
		//cerco se esiste gia' un istanza della chat
		for (ChatController currentChat : chats) 
		{
			//chat trovata
			if(currentChat.getReceiver().equals(sender))
			{
				chat = currentChat;
				break;
			}
		}
		
		//se non ho trovato la chat,ne creo una nuova e la aggiungo alla lista
		if(chat == null) 
		{
			chat = new ChatController(connection, in, out,user,sender,generateRandomLocation());
			chats.add(chat);
			chat.setVisible(true);
		}
		//chat trovata,la mostro
		else {
			//se non e' gia' visibile
			if(!chat.isVisible())
				chat.setVisible(true);
		}
		
		return chat;
	}
	
	
	private void closeAllChats()
	{
		//chiudo tutte le finestre di chat
    	for (ChatController chat : chats) {
			chat.closeWindow();
		}
	}
	
	private void closeAllChatRoom()
	{
		//TODO
	}
	
	private Point generateRandomLocation() {
		//genero un posizione a caso dove generare la chat
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		 
		int x = rand.nextInt(screenSize.width - ChatWindow.WIDTH);
		int y = rand.nextInt(screenSize.height - ChatWindow.HEIGHT);
				
		return new Point(x,y);
	}
	
	/**
	 * Classe che implementa le callback del client.
	 * Viene usata nel protocollo RMI per ricevere notifiche dal server.
	 * @author Gionatha Sturba
	 *
	 */
	public class NotificationEvents extends RemoteObject implements RMIClientNotifyEvent
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8202454271436406419L;

		public NotificationEvents() {
			super();
		}
		
		
		@Override
		public void updateFriendStatus(User friend) throws RemoteException 
		{
			//cerco l'amico da aggiornare nella lista
			DefaultListModel<User> list = hubView.getModelUserFriendList();
			
			synchronized (list) {
				for (int i = 0; i < list.size(); i++) {
					User currentUser = list.getElementAt(i);
					
					//se ho trovato l'amico a cui aggiornare lo stato
					if(currentUser.equals(friend)) {
						list.removeElementAt(i);
						list.insertElementAt(friend,i);
						break;
					}
				}
			}
		}

		@Override
		public void newFriend(User newFriend) throws RemoteException 
		{
			showInfoMessage(newFriend.getNickname()+" ti ha aggiunto ai suoi amici","Notifica Amicizia",false);
			
			DefaultListModel<User> list = hubView.getModelUserFriendList();
			
			synchronized (list) {
				//aggiungo l'amico alla lista
				list.addElement(newFriend);
			}
		}
	}
}
