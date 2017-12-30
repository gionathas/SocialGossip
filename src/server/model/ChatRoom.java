package server.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.MulticastSocket;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;

import server.model.exception.UserAlreadyRegistered;
import server.thread.DispatcherChatRoomMessage;
import utils.PortScanner;

/**
 * Rappresenta un gruppo di utenti in social Gossip
 * @author Gionatha Sturba
 *
 */
public class ChatRoom implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1787928909027681736L;
	private String name;
	private InetAddress msAddress;
	private InetAddress messageAddress;
	
	private int msPort; //porta per indirizzo multicast
	private int messagePort;  //porta per ricezione messaggi
	
	//thread dispatcher dei messaggi della chatroom
	private transient DispatcherChatRoomMessage dispatcherMessage;
	
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
	public ChatRoom(String name,InetAddress msAddress,InetAddress messageAddress) throws Exception
	{
		if(name == null || msAddress == null)
			throw new NullPointerException();
		
		if(name.isEmpty() || !msAddress.isMulticastAddress())
			throw new IllegalArgumentException();
		
		this.name = name;
		subscribers = new LinkedList<User>();

		
		//inizializzo multicast
		this.msPort = PortScanner.freePort();
		
		//porta non trovata
		if(msPort == -1)
			throw new Exception();

		MulticastSocket ms = new MulticastSocket(msPort);
		ms.setTimeToLive(LOCAL_ADDRESS);
		
		
		//inizializzo indirizzo thread listener messaggi
		this.messageAddress = messageAddress;
	
		//faccio partire il thread che si occupa di gestire i messaggi della chatroom
		dispatcherMessage = new DispatcherChatRoomMessage(ms,messageAddress);
		this.messagePort = dispatcherMessage.getListeningPort();
		dispatcherMessage.start();
	}
	
	public ChatRoom(String name,InetAddress msAddress,int port,InetAddress messageAddress,int messagePort)
	{
		if(name == null || msAddress == null || port <= 0 || messageAddress == null || messagePort <= 0)
			throw new IllegalArgumentException();
		
		this.name = name;
		this.msAddress = msAddress;
		this.messageAddress = messageAddress;
		this.messagePort = messagePort;
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
		this.msAddress = null;
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
	
	public synchronized String getIPAddress() {
		return msAddress.toString();
	}
	
	public synchronized Integer getPort() {
		return new Integer(msPort);
	}
	
	public synchronized InetAddress getMessageAddress() {
		return messageAddress;
	}
	
	
	public synchronized Integer getMessagePort() {
		return new Integer(messagePort);
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
