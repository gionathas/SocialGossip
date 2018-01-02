package server.model;

import java.io.Serializable;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.json.simple.JSONObject;

import communication.RMI.RMIClientNotifyEvent;
import server.model.exception.UserAlreadyRegistered;

/**
 * Rappresenta un generico utente della rete di Social Gossip
 * @author Gionatha Sturba
 *
 */
public class User implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3195843110518616444L;
	
	private String nickname; 
	private String password;
	private boolean online;
	private String lingua; //ISO 639-2 Code
	private List<User> amici;
	private List<ChatRoom> chatrooms; //chatroom a cui e' iscritto l'utente
	private transient Socket notificationMessageChannel;

	
	//gestione notifiche eventi
	private RMIClientNotifyEvent RMIchannel = null; //canale per notifiche RMI
	
	public static final int LANG_LENGHT = 2; //lunghezza della stringa del codice della lingua
	public static final String NO_LANG = null;
	
	//per serializzazione
	public static final String FIELD_NAME = "name";
	public static final String FIELD_ONLINE = "online";
	
	/**
	 * Crea un nuovo utente,settando nick,password,stato online,e lingua
	 * @param nickname
	 * @param password
	 */
	public User(String nickname,String password,boolean online,String lingua)
	{
		if(nickname == null || password == null || lingua == null || lingua.length() != 2)
		{
			throw new IllegalArgumentException();
		}
		
		this.nickname = nickname;
		this.password = password;
		this.online = online;
		this.lingua = lingua;
		this.amici = new LinkedList<User>();
		this.chatrooms = new LinkedList<ChatRoom>(); 
		this.notificationMessageChannel = null;
	}
	
	/**
	 * Crea un nuovo utente settando solo nickname e status online
	 * @param nickname
	 * @param isOnline
	 */
	public User(String nickname,boolean isOnline)
	{
		if(nickname == null)
			throw new NullPointerException();
		
		this.nickname = nickname;
		this.online = isOnline;
		this.password = null;
		this.lingua = null;
		this.amici = null;
		this.chatrooms = null;
		this.notificationMessageChannel = null;
	}
	
	/**
	 * Crea un nuovo utente offline,settando solo il nickname
	 * @param nickname
	 */
	public User(String nickname)
	{
		if(nickname == null)
			throw new NullPointerException();
		
		this.nickname = nickname;
		this.password = null;
		this.online = false;
		this.lingua = null;
		this.amici = null;
		this.chatrooms = null;
		this.notificationMessageChannel = null;

	}
	
	/**
	 * Crea un nuovo utente offline,settando solo nickname e password
	 * @param nickname
	 * @param password
	 */
	public User(String nickname,String password)
	{
		if(nickname == null || password == null)
			throw new IllegalArgumentException();
		
		this.nickname = nickname;
		this.password = password;
		this.online = false;
		this.lingua = null;
		this.amici = null;
		this.chatrooms = null;
		this.notificationMessageChannel = null;

	}
	
	/**
	 * Crea un nuovo utente offline,settando solo nick,password e lingua
	 * @param nickname
	 * @param password
	 * @param lingua
	 */
	public User(String nickname,String password,String lingua)
	{
		if(nickname == null || password == null || lingua == null || lingua.length() != 2)
			throw new IllegalArgumentException();
		
		this.nickname = nickname;
		this.password = password;
		this.online = false;
		this.lingua = lingua;
		this.amici = null;
		this.chatrooms = null;
		this.notificationMessageChannel = null;
	}
	
	

	@Override
	public synchronized String toString() {
		String status = online ? "online" : "offline";
		
		return nickname+" ["+status.toUpperCase()+"]";
	}
	
	
	/**
	 * 
	 * @return canale di notifica utilizzato dall'utente per ricevere messaggi da altri utenti,null se non e' settato
	 */
	public synchronized Socket getNotificationMessageChannel() {
		return notificationMessageChannel;
	}
	
	/**
	 * Setta il canale di notifica per ricevere i messaggi da altri utenti
	 * @param notificationMessageChannel 
	 */
	public synchronized void setNotificationMessageChannel(Socket notificationMessageChannel) {
		this.notificationMessageChannel = notificationMessageChannel;
	}

	/**
	 * 
	 * @return canale RMI per notificare l'utente di un evento,null se non ha un canale RMI
	 */
	public synchronized RMIClientNotifyEvent getRMIchannel() {
		return RMIchannel;
	}
	
	/**
	 * Setta il canale RMI usato dall'utente per ricevere notifiche sullo stato degli altri utenti o sulle nuove amicizie
	 * @param RMIchannel canale RMI che si vuole associare all'utente.Se si inserisce null se disassocia il precedente canale RMI.
	 */
	public synchronized void setRMIchannel(RMIClientNotifyEvent RMIchannel) {
		this.RMIchannel = RMIchannel;
	}
	
	/**
	 * @return nickname di questo utente
	 */
	public synchronized String getNickname() {
		return nickname;
	}
	
	/**
	 * 
	 * @return password dell'utente
	 */
	public synchronized String getPassword() {
		return password;
	}
	
	/**
	 * @return Codice ISO 639-2 della lingua,oppure null se non e' settata
	 */
	public synchronized String getLingua() {
		return lingua;
	}
	
	/**
	 * 
	 * @return true se l'utente e' online,false se e' offline
	 */
	public synchronized boolean isOnline() {
		return online;
	}
	
	/**
	 * Setta lo stato online di questo utente
	 * @param isOnline
	 */
	public synchronized void setOnline(boolean isOnline) {
		online = isOnline;
	}
	
	public synchronized void setLinguaCode(String lingua) {
		if(lingua.length() != LANG_LENGHT)
			throw new IllegalArgumentException();
		
		this.lingua = lingua;
	}
	
	/**
	 * @return lista degli amici dell'utente
	 */
	public synchronized List<User> getAmici(){
		return amici;
	}
	
	/**
	 * Aggiunge l'utente user alla lista di amici di questo utente
	 * @param user
	 */
	public synchronized void aggiungiAmico(User user) {
		if(user == null)
			throw new NullPointerException();
		
		if(amici == null)
			return;
		
		//se non sono gia' amici
		if(!amici.contains(user)) {
			amici.add(user);
		}
	}
	
	/**
	 * Aggiunge una nuova chatroom a cui l'utente si e' iscritto
	 * @param chatroom
	 * @throws UserAlreadyRegistered
	 */
	public synchronized void aggiungiChatRoom(ChatRoom chatroom) throws UserAlreadyRegistered
	{
		if(chatroom == null)
			throw new NullPointerException();
		
		if(chatrooms.contains(chatroom))
			throw new UserAlreadyRegistered();
		else {
			chatrooms.add(chatroom);
		}
	}
	
	/**
	 * Rimuove una chatroom a cui era iscrittp
	 * @param chatroom chatroom da rimuovere
	 */
	public synchronized void rimuoviChatRoom(ChatRoom chatroom)
	{
		if(chatroom == null)
			throw new NullPointerException();
		
		chatrooms.remove(chatroom);
	}
	
	/**
	 * Crea un oggetto json con le informazioni base di un utente
	 * @param user
	 * @return
	 */
	public static JSONObject toJsonObject(User user) {
		if(user == null)
			throw new NullPointerException();
		
		if(user.nickname == null) {
			return null;
		}
		
		JSONObject jsonUser = new JSONObject();
		
		jsonUser.put(FIELD_NAME,user.nickname);
		jsonUser.put(FIELD_ONLINE,user.isOnline());
		
		return jsonUser;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj == null)
			return false;
		
		User other = (User) obj;
		
		//utenti uguali se hanno lo stesso nickname
		if(other.nickname == null)
			return false;
		else if(!other.nickname.equalsIgnoreCase(this.nickname))
			return false;
		
		return true;
	}
	
}
