package server.thread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import utils.PortScanner;

/**
 * Thread che si occupa di inoltrare i messaggi di una chatroom
 * @author Gionatha Sturba
 *
 */
public class DispatcherChatRoomMessage extends Thread
{
	private int listeningPort; //porta su cui e' in ascolto questo thread
	private MulticastSocket ms; //multicast socket della chatroom
	private InetAddress msAddress;
	
	private DatagramSocket serverSock; //socket su cui ricevere i pacchetti udp,contenenti i messaggi da inoltrare sulla chatroom
	private static final int BUFFER_LEN = 1024; 
	private static final int timeout = 600; //timeout sul socket

	private byte[] buffer;
	
	/**
	 * 
	 * @param ms
	 * @param port
	 * @throws Excpetion errore inizializzazione dispatcherS
	 */
	public DispatcherChatRoomMessage(MulticastSocket ms,InetAddress msAddress) throws Exception 
	{
		super();
		
		if(ms == null || msAddress == null)
			throw new NullPointerException();
		
		this.ms = ms;
		this.msAddress = msAddress;
		
		//inizializzo socket per ricevere pacchetti
		this.listeningPort = PortScanner.freePort();
		
		//porta non trovata
		if(listeningPort == -1)
			throw new Exception();
		
		serverSock = new DatagramSocket(listeningPort);
		serverSock.setSoTimeout(timeout);
	}
	
	
	public int getListeningPort() {
		return listeningPort;
	}


	public void run() 
	{
		buffer = new byte[BUFFER_LEN];
		DatagramPacket receivedPacket = new DatagramPacket(buffer,buffer.length);

		while(!Thread.interrupted())
		{
			try {				
				serverSock.receive(receivedPacket);
				
				//ricevuto pacchetto,estraggo i dati e li invio agli iscritti alla chatroom
				byte[] dstBuf = new byte[receivedPacket.getLength()];
				System.arraycopy(receivedPacket.getData(),receivedPacket.getOffset(),dstBuf,0,dstBuf.length);
				sendMessageToSubscribers(dstBuf);				
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
		DatagramPacket dp = new DatagramPacket(msg,msg.length,msAddress,ms.getLocalPort());
		
		//invio il messaggio
		ms.send(dp);
	}

}
