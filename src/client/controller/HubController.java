package client.controller;

import java.awt.Point;
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

import javax.swing.DefaultListModel;

import client.thread.FindUserRequestSender;
import client.thread.LogoutRequestSender;
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
	private Controller controller = this;
	
	//gestione RMI
	private RMIServerInterface serverRMI = null;
	private RMIClientNotifyEvent callback;
	
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
		
		try 
		{
			initComponents(nickname,amiciList);
			initListeners();
		} 
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
		
		//TODO sconfiguro thread listener che ascolta i messaggi arrivati da altre chat 
		//initListenerNotificationChatMessage();
		
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
	                new LogoutRequestSender(controller,connection,in,out,user.getNickname(),serverRMI,callback).start();
	            }
	        });
		
		//al click sul bottone ESCI
		hubView.getBtnLogout().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
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
