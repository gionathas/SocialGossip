package client.controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import javax.swing.DefaultListModel;

import client.thread.FindUserRequestSender;
import client.thread.LogoutRequestSender;
import client.view.Hub;
import communication.RMI.RMIClientNotifyEvent;
import communication.RMI.RMIServerInterface;

import server.model.User;
import server.model.exception.UserNotFindException;
import server.model.exception.UserStatusException;

public class HubController extends Controller
{
	private Hub hubView;
	private User user;
	private Controller controller = this;
	
	private RMIServerInterface serverRMI = null;
	private RMIClientNotifyEvent callback;
	
	private static final String SERVER_RMI_SERVICE_NAME = "SocialGossipNotification";
	private static final int SERVER_RMI_PORT = 6000;
	
	
	public HubController(String nickname,List<User> amiciList,Point location) 
	{
		hubView = new Hub();
		setWindow(hubView);
		window.setLocation(location);
		
		try 
		{
			initComponents(nickname,amiciList);
			initListeners();
		} 
		catch (Exception e) 
		{
			showErrorMessage("Errore nell'inizializzazione della comunicazione RMI","ERRORE CONNESSIONE");
			
			//chiudo sessione
			window.setVisible(false);
			window.dispose();
			
			e.printStackTrace();
		} 
	}
	
	private void initComponents(String nickname,List<User> amiciList) throws RemoteException, NotBoundException, UserNotFindException, UserStatusException
	{	
		user = new User(nickname);
		hubView.setWelcomeText("Loggato come: "+nickname);

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
		
		//TODO faccio partire thread listener RMI
	}
	
	private void initRMI(String nickname) throws RemoteException, NotBoundException, UserNotFindException, UserStatusException
	{
		Registry registry = LocateRegistry.getRegistry(SERVER_RMI_PORT);
		
		serverRMI = (RMIServerInterface) registry.lookup(SERVER_RMI_SERVICE_NAME);
		
		//creo la classe che implementa le callback
		callback = new NotificationEvents();
		
		//esporto la callback sul registro
		RMIClientNotifyEvent stub = (RMIClientNotifyEvent)UnicastRemoteObject.exportObject(callback,0);
		
		//registro la callback
		serverRMI.registerUserRMIChannel(nickname,stub);
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
	                new LogoutRequestSender(controller,user.getNickname(),serverRMI,callback).start();
	            }
	        });
		
		//al click sul bottone ESCI
		hubView.getBtnLogout().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				//avvio procedura di logout
                new LogoutRequestSender(controller,user.getNickname(),serverRMI,callback).start();
			}
		});
		
		//al click sul bottone CERCA
		hubView.getBtnCerca().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//avvio thread che gestisce la richiesta di ricerca utente
				new FindUserRequestSender(controller,user.getNickname(),hubView.getTextField().getText(),hubView.getModelUserFriendList()).start();
			}
		});
	}
	
	/**
	 * Classe che implementa le callback del client
	 * @author gio
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

		@Override
		public void newFriend(User newFriend) throws RemoteException 
		{
			showInfoMessage(newFriend.getNickname()+" ti ha aggiunto ai suoi amici","Notifica Amicizia",false);
			
			//aggiungo l'amico alla lista
			hubView.getModelUserFriendList().addElement(newFriend);
		}
	}
}
