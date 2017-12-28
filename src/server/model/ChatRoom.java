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

/**
 * Rappresenta un gruppo di utenti in social Gossip
 * @author Gionatha Sturba
 *
 */
public class ChatRoom 
{
	private String name;
	private MulticastSocket ms;
	
	private List<User> subscribers;
	
	//per utilizzare solo indirizzi multicast locali
	private int LOCAL_ADDRESS = 1;
	
	//serializzazione
	public static final String FIELD_NAME = "name";
	public static final String FIELD_ADDRESS = "address";
	public static final String FIELD_PORT = "port";

	/**
	 * Crea una nuova chatroom vuota,con un nome e un indirizzo assegnato
	 * @param name
	 * @param address
	 * @throws IOException errore creazione socket della chatroom
	 */
	public ChatRoom(String name,InetAddress address,int port) throws IOException
	{
		if(name == null || address == null)
			throw new NullPointerException();
		
		if(name.isEmpty() || !address.isMulticastAddress() || port <= 0)
			throw new IllegalArgumentException();
		
		this.name = name;
		
		ms = new MulticastSocket(port);
		ms.setTimeToLive(LOCAL_ADDRESS);
		subscribers = new LinkedList<User>();
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
	 * Invia un messaggio a tutti gli iscritti della chatroom
	 * @throws IOException errore nell'invio del messaggio
	 */
	public void sendMessageToSubscribers(byte[] msg) throws IOException 
	{
		if(msg == null)
			throw new NullPointerException();
		
		//creo il pacchetto da inviare
		DatagramPacket dp = new DatagramPacket(msg,msg.length,ms.getInetAddress(),ms.getLocalPort());
		
		//invio il messaggio
		ms.send(dp);
	}
	
	/**
	 * Aggiunge un nuovo iscritto alla chatRoom. Il primo utente ad essere inserito e' l'admin
	 * @param user
	 * @throws UserAlreadyRegistered
	 */
	public void addNewSubscriber(User user) throws UserAlreadyRegistered
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
		
		jsonChatRoom.put(FIELD_NAME,cr.getName());
		jsonChatRoom.put(FIELD_ADDRESS,cr.getIPAddress());
		jsonChatRoom.put(FIELD_PORT, cr.getPort());
		
		return jsonChatRoom;
	}
	
	@Override
	public String toString()
	{
		return "["+name.toUpperCase()+"]"+" Iscritti: "+numSubscribers();
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
