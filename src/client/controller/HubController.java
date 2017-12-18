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
import java.util.List;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import client.thread.FindUserRequestSender;
import client.thread.LogoutRequestSender;
import client.thread.RequestSenderThread;
import client.view.Hub;
import communication.RMI.ClientNotifyEvent;
import communication.RMI.ServerInterface;
import communication.TCPMessages.Message;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.request.FindUserRequest;
import communication.TCPMessages.request.FriendshipRequest;
import communication.TCPMessages.request.LogoutRequest;
import communication.TCPMessages.response.ResponseFailedMessage;
import communication.TCPMessages.response.ResponseMessage;
import communication.TCPMessages.response.ResponseFailedMessage.Errors;
import server.model.User;

public class HubController extends Controller implements ClientNotifyEvent
{
	private Hub hubView;
	private User user;
	private ServerInterface serverRMI = null;
	private final int YES = 0;
	private Thread RMIHandler;
	private Controller controller = this;
	
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
		catch (RemoteException | NotBoundException e) 
		{
			showErrorMessage("Errore nell'inizializzazione della comunicazione RMI","ERRORE CONNESSIONE");
			e.printStackTrace();
		} 
	}
	
	private void initComponents(String nickname,List<User> amiciList) throws RemoteException, NotBoundException
	{	
		user = new User(nickname);
		hubView.setWelcomeText("Loggato come: "+nickname);
		
		//configuro RMI per ricevere notifiche sullo stato degli amici e sulle nuove amicizie
		initRMI();
		
		//se mi e' stata passata una lista di amici e di chatRoom
		if(amiciList != null) {
			//aggiorno lista amici
			for (User user : amiciList) {
				hubView.getModelUserFriendList().addElement(user);
			}
			
			//TODO aggiornare lista chatRoom
		}
	}
	
	private void initRMI() throws RemoteException, NotBoundException
	{
		Registry registry = LocateRegistry.getRegistry(SERVER_RMI_PORT);
		
		serverRMI = (ServerInterface) registry.lookup(SERVER_RMI_SERVICE_NAME);
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
	                new LogoutRequestSender(controller,user.getNickname()).start();
	            }
	        });
		
		//al click sul bottone ESCI
		hubView.getBtnLogout().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				//avvio procedura di logout
                new LogoutRequestSender(controller,user.getNickname()).start();
			}
		});
		
		//al click sul bottone CERCA
		hubView.getBtnCerca().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//avvio thread che gestisce la richiesta di ricerca utente
				new FindUserRequestSender(controller,user.getNickname(),hubView.getTextField().getText()).start();
			}
		});
	}
	
	/**
	 * Thread che si occupa di gestire la richiesta di logout
	 * @author gio
	 *
	 */
	
	@Override
	public void updateFriendStatus(User friend) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newFriend(User newFriend) throws RemoteException {
		// TODO Auto-generated method stub
		
	}	
}
