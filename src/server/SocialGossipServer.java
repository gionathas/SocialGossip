package server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import communication.RMI.RMIServerInterface;
import server.model.Network;
import server.model.RMIChannelManager;
import server.thread.UserRequestHandler;


public class SocialGossipServer implements Runnable
{
	private Network reteSG; //rappresenta la struttura della rete degli utenti di social gossip
	private ServerSocket listenerSocket; //socket in cui e' in ascolto il server
	private ThreadPoolExecutor executor; //pool di thread per gestire i vari client che arrivano
	
	private static final String SERVER_RMI_SERVICE_NAME = "SocialGossipNotification";
	private static final int SERVER_RMI_PORT = 6000;
	
	public SocialGossipServer(int port) throws IOException 
	{
		//TODO controllo porta server
		
		reteSG = new Network();
		listenerSocket = new ServerSocket(port);
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	}
	
	//ciclo di vita del server di social gossip
	@Override
	public void run() 
	{	
		try 
		{
			initRMI();
			
			while(true)
			{
				Socket newClient = listenerSocket.accept();
				
				//sottometto la gestione del client arrivato ad un thread del pool
				executor.submit(new UserRequestHandler(newClient,reteSG));
				
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void initRMI() throws RemoteException
	{
		//oggetto che gestisce i canali RMI degli utenti
		RMIChannelManager RMIUserChannelManager = new RMIChannelManager(reteSG);
		
		//creo lo stub dell'oggetto allocato precedentemente
		RMIServerInterface stub = (RMIServerInterface) UnicastRemoteObject.exportObject(RMIUserChannelManager,3900);
		
		//creo il registro
		LocateRegistry.createRegistry(SERVER_RMI_PORT);
		
		//ottengo il registro
		Registry reg = LocateRegistry.getRegistry(SERVER_RMI_PORT);
		
		//istanzio l'oggetto 
		reg.rebind(SERVER_RMI_SERVICE_NAME,stub);
	}
		
	public static void main(String[] args) 
	{
		//TODO parse con configParse

		try 
		{
			//creo istanza del server di social gossip
			SocialGossipServer server = new SocialGossipServer(5000);
			
			//faccio partire il server
			server.run();
			
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
