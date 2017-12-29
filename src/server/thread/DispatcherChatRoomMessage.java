package server.thread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import server.model.ChatRoom;

/**
 * Thread che si occupa di inoltrare i messaggi di una chatroom
 * @author Gionatha Sturba
 *
 */
public class DispatcherChatRoomMessage extends Thread
{
	private int listeningPort; //porta su cui e' in ascolto questo thread
	private MulticastSocket ms; //multicast socket della chatroom
	
	private DatagramSocket serverSock; //socket su cui ricevere i pacchetti udp,contenenti i messaggi da inoltrare sulla chatroom
	private static final int BUFFER_LEN = 1024; 
	private static final int timeout = 600; //timeout sul socket

	private byte[] buffer = new byte[BUFFER_LEN];
	
	/**
	 * 
	 * @param ms
	 * @param port
	 * @throws Excpetion errore inizializzazione dispatcherS
	 */
	public DispatcherChatRoomMessage(MulticastSocket ms,int port) throws Exception 
	{
		super();
		
		if(ms == null)
			throw new NullPointerException();
		
		this.listeningPort = port;
		this.ms = ms;
		
		//inizializzo socket per ricevere pacchetti
		serverSock = new DatagramSocket(listeningPort);
		serverSock.setSoTimeout(timeout);

	}
	
	
	public void run() 
	{
		DatagramPacket receivedPacket = new DatagramPacket(buffer,buffer.length);

		while(!Thread.interrupted())
		{
			try {
				serverSock.receive(receivedPacket);
				
				//ricevuto pacchetto,estraggo i dati e li invio agli iscritti alla chatroom
				sendMessageToSubscribers(receivedPacket.getData());
				
			} 
			//timeout
			catch(SocketTimeoutException e) {}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//chiudo socket
		serverSock.close();
	}
	
	/**
	 * Invia un messaggio a tutti gli iscritti della chatroom
	 * @throws IOException errore nell'invio del messaggio
	 */
	private void sendMessageToSubscribers(byte[] msg) throws IOException 
	{
		if(msg == null)
			throw new NullPointerException();
				
		//creo il pacchetto da inviare
		DatagramPacket dp = new DatagramPacket(msg,msg.length,ms.getInetAddress(),ms.getPort());
		
		//invio il messaggio
		ms.send(dp);
	}

}
