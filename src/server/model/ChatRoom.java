package server.model;

import java.net.InetAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import server.model.exception.UserAlreadyRegistered;

/**
 * Rappresenta un gruppo di utenti in social Gossip
 * @author Gionatha Sturba
 *
 */
public class ChatRoom 
{
	private String name;
	private InetAddress address;
	private List<User> subscribers;
	
	public ChatRoom(String name,InetAddress address)
	{
		if(name == null || address == null)
			throw new NullPointerException();
		
		if(name.isEmpty() || !address.isMulticastAddress())
			throw new IllegalArgumentException();
		
		this.name = name;
		this.address = address;
		subscribers = new LinkedList<User>();
	}
	
	public synchronized String getName() {
		return this.name;
	}
	
	public synchronized InetAddress getAddress() {
		return this.address;
	}
	
	public synchronized List<User> getSubscribers(){
		return Collections.unmodifiableList(subscribers);
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
}
