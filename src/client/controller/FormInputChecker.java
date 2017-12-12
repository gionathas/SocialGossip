package client.controller;

import java.util.Arrays;

/**
 * Classe per la gestione dei dati inseriti dall'utente nei form
 * @author Gionatha Sturba
 *
 */
public class FormInputChecker 
{
	
	public static final int MIN_PASS_CHAR_LEN = 5;
	public static final int MIN_USER_CHAR_LEN = 3;
	
	public static final String LOGIN_ERROR_INFO_STRING = "Username o password non validi,controlla i seguenti campi:\n"
			+ "1)Username: Spazi non consentiti. Caratteri minimi 3.\n"
			+ "2)Password: Caratteri minimi 5";
	
	public static final String REGISTER_ERROR_INFO_STRING = LOGIN_ERROR_INFO_STRING +"\n3)Le passwords inserite potrebbero non coincidere";
	
	/**
	 * Controlla i dati inseriti nella form di login
	 * @param nick nickname inserito
	 * @param password password inserita
	 * @return true se la form e' valida,false altrimenti
	 */
	public static boolean checkLoginInput(String nick,char[] password)
	{
		if(nick == null || password == null || nick.isEmpty() || nick.length() < MIN_USER_CHAR_LEN || password.length < MIN_PASS_CHAR_LEN ||
				nick.contains(" "))
		{			
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * Controllo i dati inseriti nella form di registazione
	 * @param nick
	 * @param password
	 * @param confirmPassword
	 * @return
	 */
	public static boolean checkRegisterInput(String nick,char[] password,char[] confirmPassword)
	{
		if(!checkLoginInput(nick,password))
		{
			return false;
		}
		//controllo che le password siano uguali
		else 
		{
			if(confirmPassword == null)
				return false;
			
			return Arrays.equals(password,confirmPassword);
		}
	}
	
}
