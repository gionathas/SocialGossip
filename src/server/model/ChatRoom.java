package server.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;

import server.model.exception.UserAlreadyRegistered;
import server.thread.DispatcherChatRoomMessage;

/**
 * Rappresenta un gruppo di utenti in social Gossip
 * @author Gionatha Sturba
 *
 */
public class ChatRoom 
{
	private String name;
	private MulticastSocket ms; //indirizzo di multicast
	private InetAddress messageAddress;
	private int messagePort;  //porta per ricezione messaggi
	
	private DispatcherChatRoomMessage dispatcherMessage;
	
	private List<User> subscribers;
	
	//per utilizzare solo indirizzi multicast locali
	private int LOCAL_ADDRESS = 1;
	
	//serializzazione
	public static final String FIELD_NAME = "name";
	public static final String FIELD_MS_ADDRESS = "ms-address";
	public static final String FIELD_MS_PORT = "ms-port";
	public static final String FIELD_MESSAGE_ADDRESS = "message-address";
	public static final String FIELD_MESSAGE_PORT = "message-port";

	/**
	 * Crea una nuova chatroom vuota,con un nome e un indirizzo assegnato
	 * @param name
	 * @param address
	 * @throws Exception 
	 */
	public ChatRoom(String name,InetAddress msAddress,int msPort,InetAddress messageAddress,int messagePort,boolean serverIstance) throws Exception
	{
		if(name == null || msAddress == null)
			throw new NullPointerException();
		
		if(name.isEmpty() || !msAddress.isMulticastAddress() || msPort <= 0 || messagePort <= 0)
			throw new IllegalArgumentException();
		
		this.name = name;
		
		//inizializzo multicast
		ms = new MulticastSocket(msPort);
		ms.setTimeToLive(LOCAL_ADDRESS);
		
		//inizializzo indirizzo thread listener messaggi
		this.messageAddress = messageAddress;
		this.messagePort = messagePort;
		
		//se e' un istanza del server
		if(serverIstance)
		{
			subscribers = new LinkedList<User>();

			//faccio partire il thread che si occupa di gestire i messaggi della chatroom
			dispatcherMessage = new DispatcherChatRoomMessage(ms,messagePort);
			dispatcherMessage.start();
		}
		
	}
	
	/**
	 * Crea una chatroom ,a partire dal solo nome
	 * @param name
	 */
	public ChatRoom(String name)
	{
		if(name == null)
			throw new NullPointerException();
		
		if(name.isEmpty())
			throw new IllegalArgumentException();
		
		this.name = name;
		this.ms = null;
		this.subscribers = null;
	}
	
	public synchronized void close() {
		dispatcherMessage.interrupt();
		
		while(dispatcherMessage.isAlive()) {
			//aspetto che il thread termini
		}
		
		
	}
	
	public synchronized String getName() {
		return this.name;
	}
	
	public synchronized List<User> getSubscribers(){
		return Collections.unmodifiableList(subscribers);
	}
	
	public synchronized int numSubscribers() {
		return subscribers.size();
	}
	
	
	
	/**
	 * Aggiunge un nuovo iscritto alla chatRoom. Il primo utente ad essere inserito e' l'admin
	 * @param user
	 * @throws UserAlreadyRegistered
	 */
	public synchronized void addNewSubscriber(User user) throws UserAlreadyRegistered
	{
		if(user == null)
			throw new NullPointerException();
		
		//se l'utente e' gia' iscritto
		if(subscribers.contains(user))
			throw new UserAlreadyRegistered();
		
		subscribers.add(user);
	}
	
	public static JSONObject toJsonObject(ChatRoom cr)
	{
		if(cr == null)
			throw new NullPointerException();
		
		JSONObject jsonChatRoom = new JSONObject();
		
		//nome chatroom
		jsonChatRoom.put(FIELD_NAME,cr.getName());
		//indirizzo multicast
		jsonChatRoom.put(FIELD_MS_ADDRESS,cr.getIPAddress());
		//porta indirizzo multicast
		jsonChatRoom.put(FIELD_MS_PORT, cr.getPort());
		//indrizzo per ricevere messaggi
		jsonChatRoom.put(FIELD_MESSAGE_ADDRESS,cr.getMessageAddress());
		//porta per ricevere messaggi
		jsonChatRoom.put(FIELD_MESSAGE_PORT,cr.getMessagePort());
		
		return jsonChatRoom;
	}
	
	@Override
	public String toString()
	{
		return "["+name.toUpperCase()+"]"+" Iscritti: "+numSubscribers();
	}
	
	public synchronized MulticastSocket getMulticastSocket() {
		return ms;
	}
	
	public synchronized InetAddress getMessageAddress() {
		return messageAddress;
	}
	
	public synchronized Integer getMessagePort() {
		return new Integer(messagePort);
	}
	
	public synchronized String getIPAddress() {
		return ms.getInetAddress().getHostAddress();
	}
	
	public synchronized Integer getPort() {
		return new Integer(ms.getPort());
	}
	
	public synchronized void newUser(User newUser) throws UserAlreadyRegistered
	{
		if(newUser == null)
			throw new NullPointerException();
		
		//se l'utente e' gia' registrato
		if(subscribers.contains(newUser))
			throw new UserAlreadyRegistered();
		
		//inserisco l'utente
		subscribers.add(newUser);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj == null)
			return false;
		
		ChatRoom other = (ChatRoom) obj;
		
		//2 chatroom sono uguali se hanno lo stesso nome
		
		if (name == null) {
			if (other.name != null)
				return false;
			
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		
		return true;
	}
}
