package server.model;

/**
 * Rappresenta un generico utente della rete di Social Gossip
 * @author Gionatha Sturba
 *
 */
public class User 
{
	private String nickname;
	private char[] password;
	private boolean online; // se l'utente e' attualmente loggato,quindi online
	
	/**
	 * Crea un nuovo utente offline
	 * @param nickname
	 * @param password
	 */
	public User(String nickname,char[] password)
	{
		if(nickname == null || password == null)
		{
			throw new IllegalArgumentException();
		}
		this.nickname = nickname;
		this.password = password;
		this.online = false;
	}
	
	/**
	 * 
	 * @return nickname di questo utente
	 */
	public String getNickname() {
		return nickname;
	}
	
	public char[] getPassword() {
		return password;
	}
	
	public boolean isOnline() {
		return online;
	}
	
	public void setOnline(boolean status) {
		online = status;
	}
	
	/**
	 * Controlla se la password inserita corrisponde a quella dell'utente
	 * @param enteredPassword password da controllare
	 * @return true se la password inserita e quella dell'utente sono uguali,false altrimenti
	 */
	public boolean checkPassword(char[] enteredPassword)
	{
		if(enteredPassword.length != this.password.length || enteredPassword == null)
			return false;
		
		//controllo sequenza caratteri password
		for (int i = 0; i < enteredPassword.length; i++) {
			if(enteredPassword[i] != this.password[i])
				return false;
		}
		
		return true;
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
