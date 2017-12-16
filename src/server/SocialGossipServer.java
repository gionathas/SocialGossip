package server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import server.model.Network;
import server.thread.UserRequestHandler;


public class SocialGossipServer implements Runnable
{
	private Network reteSG; //rappresenta la struttura della rete degli utenti di social gossip
	private ServerSocket listenerSocket; //socket in cui e' in ascolto il server
	private ThreadPoolExecutor executor; //pool di thread per gestire i vari client che arrivano
	
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
