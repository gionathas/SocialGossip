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
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.management.ListenerNotFoundException;
import javax.swing.DefaultListModel;
import javax.swing.JList;

import client.thread.ListenerChatMessage;
import client.thread.ListenerChatRoomMessage;
import client.thread.requestSender.FindUserRequestSender;
import client.thread.requestSender.LogoutRequestSender;
import client.thread.requestSender.NewChatRoomRequestSender;
import client.view.ChatRoomWindow;
import client.view.ChatWindow;
import client.view.HubWindow;
import communication.RMI.RMIClientNotifyEvent;
import communication.RMI.RMIServerInterface;
import server.model.ChatRoom;
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
	private HubController controller = this;
	private Random rand;
	
	//gestione RMI,notifiche messaggi
	private RMIServerInterface serverRMI = null;
	private RMIClientNotifyEvent callback;
	
	private ListenerChatMessage listenerChatMessage; //thread che ascolta i messaggi provenienti da altri utenti
	private List<ListenerChatRoomMessage> listenersChatRoomMessages; //lista di thread che ascoltano messaggi provenienti da altre chatroom
	
	//gestione finestre chat e chatRoom
	private List<ChatController> chats;
	private List<ChatRoomController> chatrooms;
	
	private static final String SERVER_RMI_SERVICE_NAME = "SocialGossipNotification";
	private static final int SERVER_RMI_PORT = 6000;
	
	/**
	 * Inizializza un nuuovo Controller per comandare l'hub principale
	 * @param nickname nickname dell'utente che controlla l'hub
	 * @param amiciList lista degli amici dell'utente
	 * @param location posizione finestra
	 */
	public HubController(Socket connection,DataInputStream in,DataOutputStream out,String nickname,List<User> amiciList,List<ChatRoom> chatRooms,Point location) 
	{
		super(connection,in,out);
		
		hubView = new HubWindow();
		setWindow(hubView);
		window.setLocation(location);
		
		rand = new Random(System.currentTimeMillis());
		
		try 
		{
			initComponents(nickname,amiciList,chatRooms);
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
	private void initComponents(String nickname,List<User> amiciList,List<ChatRoom> chatRooms) throws Exception
	{	
		user = new User(nickname);
		hubView.setWelcomeText("Loggato come: "+nickname);
		chats = new LinkedList<ChatController>();
		chatrooms = new LinkedList<ChatRoomController>();

		//se mi e' stata passata una lista di amici
		if(amiciList != null) {
			//aggiorno lista amici
			for (User user : amiciList) {
				hubView.getModelUserFriendList().addElement(user);
			}
		}
		
		//aggiungo lista chatroom attive
		for(ChatRoom cr : chatRooms) {
			hubView.getModelChatRoomList().addElement(cr);;
		}
		
		
		//configuro RMI per ricevere notifiche sullo stato degli amici e sulle nuove amicizie
		initRMI(nickname);
		
		//configuro thread listener che ascolta i messaggi arrivati da altre chat,e lo faccio partire
		listenerChatMessage = new ListenerChatMessage(this,user,connection.getInetAddress(),connection.getPort());
		listenerChatMessage.start();
		
		//insieme di thread listener messaggi chatrooms
		listenersChatRoomMessages = new LinkedList<ListenerChatRoomMessage>();		
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
	            	closeAllChatRoom();
	            		            	
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
				closeAllChatRoom();
								
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
		
		//al click sul bottone CREA CHATROOM
		hubView.getBtnCreaChatroom().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new NewChatRoomRequestSender(controller,connection,in,out,listenersChatRoomMessages).start();
			}
		});
		
		//al click sul bottone unisciti a chatroom
		hubView.getBtnUniscitiAChatroom().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
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
	
	public void openChatRoomFromList()
	{
		JList<ChatRoom> list = hubView.getChatRoomList();
		
		synchronized (list) {
			ChatRoom selectedRoom = list.getSelectedValue();
			
			//se non e' stato selezionato nessun utente
			if(selectedRoom == null)
			{
				showInfoMessage("Seleziona una ChatRoom!","Nessuna ChatRoom selezionata",false);
			}
			else {
				ChatRoomController chatroom = null;
				
				//cerco se esiste gia' un istanza della chat,tra le liste
				for (ChatRoomController currentChatRoom : chatrooms) 
				{
					//chat trovata
					if(currentChatRoom.getChatRoomReceiver().equals(selectedRoom))
					{
						chatroom = currentChatRoom;
						break;
					}
				}
				
				//se non ho trovato la chatroom,ne creo una nuova e la aggiungo alla lista
				if(chatroom == null) 
				{
					try {
						chatroom = new ChatRoomController(connection, in, out,user,selectedRoom,generateRandomLocation());
					} catch (SocketException e) {
						controller.showErrorMessage("Impossibile aprire chatroom richiesta","ERRORE APERTURA CHATROOM");
						return;
					}
					
					chatrooms.add(chatroom);
				}
				//chat trovata,la mostro
				else {
					//se non e' gia' visibile
					if(!chatroom.isVisible())
						chatroom.setVisible(true);
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
	
	public ChatRoomController openChatRoomFromNewMessage(ChatRoom chatroom)
	{
		ChatRoomController chatroomControl = null;
		
		//cerco se esiste gia' un istanza della chat
		for (ChatRoomController currentChatRoom : chatrooms) 
		{
			//chat trovata
			if(currentChatRoom.getChatRoomReceiver().equals(chatroom))
			{
				chatroomControl = currentChatRoom;
				break;
			}
		}
		
		if(chatroomControl != null) {
			//se non e' gia' visibile
			if(!chatroomControl.isVisible())
				chatroomControl.setVisible(true);
		}
		
		return chatroomControl;
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
		synchronized (listenersChatRoomMessages) {
			//chiudo tutti i thread che ascoltano i messaggi dalle chatroom
			for (ListenerChatRoomMessage listener : listenersChatRoomMessages) {
				listener.interrupt();
			}
		}
		
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


		@Override
		public void newChatRoom(ChatRoom chatroom) throws RemoteException 
		{
			DefaultListModel<ChatRoom> list = hubView.getModelChatRoomList();
			
			synchronized (list) 
			{
				//aggiungo la chatroom alla lista delle chatroom attive
				list.addElement(chatroom);
			}
		}
	}
}
