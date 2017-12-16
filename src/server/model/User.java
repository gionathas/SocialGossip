package server.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Rappresenta un generico utente della rete di Social Gossip
 * @author Gionatha Sturba
 *
 */
public class User 
{
	private String nickname;
	private String password;
	private boolean online;
	private String lingua; //ISO 639-2 Code
	private List<User> amici;
	
	public static final int LANG_LENGHT = 2;
	public static final String NO_LANG = null;
	
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
	}
	
	@Override
	public String toString() {
		String status = online ? "online" : "offline";
		
		return nickname+"["+status.toUpperCase()+"]";
	}
	
	/**
	 * @return nickname di questo utente
	 */
	public String getNickname() {
		return nickname;
	}
	
	/**
	 * 
	 * @return password dell'utente
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @return Codice ISO 639-2 della lingua,oppure null se non e' settata
	 */
	public String getLingua() {
		return lingua;
	}
	
	public boolean isOnline() {
		return online;
	}
	
	public void setOnline(boolean isOnline) {
		online = isOnline;
	}
	
	public void setLinguaCode(String lingua) {
		if(lingua.length() != LANG_LENGHT)
			throw new IllegalArgumentException();
		
		this.lingua = lingua;
	}
	
	/**
	 * @return lista degli amici dell'utente
	 */
	public List<User> getAmici(){
		return Collections.unmodifiableList(amici);
	}
	
	/**
	 * 
	 * @param user
	 * @return se questo utente e' amico di user
	 */
	public boolean amicoDi(User user) {
		
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
	public void aggiungiAmico(User user) {
		if(user == null)
			throw new NullPointerException();
		
		if(amici == null)
			return;
		
		//se non sono gia' amici
		if(!amici.contains(user)) {
			amici.add(user);
		}
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
