package server.model;

/**
 * Rappresenta un utente del Social Network
 * @author Gionatha Sturba
 *
 */
public class Utente 
{
	private String nickname;
	private char[] password;
	private boolean isOnline;
	
	/**
	 * Crea un nuovo utente offline
	 * @param nickname
	 * @param password
	 */
	public Utente(String nickname,char[] password)
	{
		if(nickname == null || password == null)
		{
			throw new IllegalArgumentException();
		}
		
		this.nickname = nickname;
		this.password = password;
		isOnline = false;
	}
	
	/**
	 * 
	 * @return nickname di questo utente
	 */
	public String getNickname() {
		return nickname;
	}
	
	/**
	 * 
	 * @return se questo utente e' attualmente online
	 */
	public boolean isOnline() {
		return isOnline;
	}
	
	/**
	 * 
	 * @param isOnline per settare lo status di online di questo utente
	 */
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
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
		result = prime * result + (isOnline ? 1231 : 1237);
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
		
		Utente other = (Utente) obj;
		
		//2 utenti sono "uguali" se hanno lo stesso nickname
		if (nickname == null) {
			if (other.nickname != null)
				return false;
		} else if (!nickname.equals(other.nickname))
			return false;
	
		
		return true;
	}
	
}
