package client.thread;

import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicInteger;

import client.controller.Controller;
import client.controller.HubController;
import client.controller.HubController.NotificationEvents;
import communication.RMI.RMIClientNotifyEvent;
import communication.RMI.RMIServerInterface;
import server.model.exception.UserNotFindException;
import server.model.exception.UserStatusException;

public class RMINotificationManager extends Thread
{
	private String userNickname;
	private Thread controllerThread;
	private Controller controller;
	private AtomicInteger status;
	private RMIClientNotifyEvent callback;

	
	public static final int CORRECT_INIT = 0;
	public static final int ERROR_INIT = -1;
	
	private static final String SERVER_RMI_SERVICE_NAME = "SocialGossipNotification";
	private static final int SERVER_RMI_PORT = 6000; 
	
	public RMINotificationManager(Controller controller,String nickname,Thread controllerThread,AtomicInteger status,RMIClientNotifyEvent callback) 
	{
		userNickname = nickname;
		this.controllerThread = controllerThread;
		this.controller = controller;
		this.status = status;
		this.callback = callback;
	}
	
	public void run()
	{
		RMIServerInterface serverRMI = null;
		RMIClientNotifyEvent stub = null;
		
		
		try {
			Registry registry = LocateRegistry.getRegistry(SERVER_RMI_PORT);
			
			serverRMI = (RMIServerInterface) registry.lookup(SERVER_RMI_SERVICE_NAME);
			
			//esporto la callback sul registro
			stub = (RMIClientNotifyEvent)UnicastRemoteObject.exportObject(callback,0);
			
			//registro la callback
			serverRMI.registerUserRMIChannel(userNickname,stub);
		} catch (RemoteException | NotBoundException | UserNotFindException | UserStatusException e) {
			controller.showErrorMessage("Errore nell'inizializzazione del protocollo RMI","ERRORE RMI");
			//segnalo inizializzazione andata male
			status.set(ERROR_INIT);
		}
		finally {
			//se non abbiamo riscontrato errori nell'inizializzazione,segnalo inizializzazione andata a buon fine
			if(status.get() != ERROR_INIT)
				status.set(CORRECT_INIT);
			
			//invio segnale al thread che controller della GUI
			controllerThread.interrupt();
			
		}
		
		//se l'inizializzazione e' andata bene,attendo il segnale di terminazione al logout
		while(true)
		{
			int timeout = 10000;
			
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) 
			{
				break;
			}
		}
		
		//deregistro la callback,quando ho finito
		try {
			serverRMI.unregisterUserRMIChannel(userNickname);
			UnicastRemoteObject.unexportObject(callback,true);
		} catch (RemoteException | UserNotFindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
