package server.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.json.simple.JSONObject;

import communication.RMI.RMIClientNotifyEvent;

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
	private RMIClientNotifyEvent RMIchannel; //canale per notifiche RMI
	private List<User> amici;
	
	public static final int LANG_LENGHT = 2;
	public static final String NO_LANG = null;
	
	public static final String FIELD_NAME = "name";
	public static final String FIELD_ONLINE = "online";
	
	/**
	 * Crea un nuovo utente
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
		this.RMIchannel = null;
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
		this.RMIchannel = null;

	}
	
	/**
	 * Crea un nuovo utente offline,senza settare password e lingua
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
		this.RMIchannel = null;

	}
	
	/**
	 * Crea un nuovo utente offline,senza settare la lingua 
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
		this.RMIchannel = null;

	}
	
	/**
	 * Crea un nuovo utente offline,settando anche la lingua
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
		this.RMIchannel = null;
	}
	
	

	@Override
	public String toString() {
		String status = online ? "online" : "offline";
		
		return nickname+" ["+status.toUpperCase()+"]";
	}
	
	/**
	 * 
	 * @return canale RMI per notificare l'utente di un evento,null se non ha un canale RMI
	 */
	public synchronized RMIClientNotifyEvent getRMIchannel() {
		return RMIchannel;
	}
	
	/**
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
	
	public synchronized boolean isOnline() {
		return online;
	}
	
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
	 * 
	 * @param user
	 * @return se questo utente e' amico di user
	 */
	public synchronized boolean amicoDi(User user) {
		
		if(user == null)
			throw new NullPointerException();
		
		if(amici == null)
			return false;
		
		return amici.contains(user);
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
		
		jsonUser.put("name",user.nickname);
		jsonUser.put("online",user.isOnline());
		
		return jsonUser;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		User other = (User) obj;
		
		//2 utenti sono "uguali" se hanno lo stesso nickname
		if (nickname == null) {
			if (other.nickname != null)
				return false;
		} else if (!nickname.equals(other.nickname))
			return false;
	
		
		return true;
	}
	
}
