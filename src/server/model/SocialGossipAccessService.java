package server.model;

import javax.management.InvalidAttributeValueException;

import server.model.exception.PasswordMismatchingException;
import server.model.exception.UserAlreadyRegistered;
import server.model.exception.UserNotFindException;
import server.model.exception.UserStatusException;

/**
 * Interfaccia per la gestione degli accessi al sistema, degli utenti di SocialGossip
 * @author Gionatha Sturba
 *
 */
public interface SocialGossipAccessService 
{
	/**
	 * Logga un utente registrato alla rete di social Gossip
	 * @param nickname nickname utente
	 * @param password password utente
	 * @throws UserNotFindException se l'utente non risulta essere registrato
	 * @throws PasswordMismatchingException se le password non corrispondono
	 * @throws UserStatusException se l'utente risulta gia' essere online
	 * @throws InvalidAttributeValueException se i parametri di log in non sono validi
	 */
	public void logIn(String nickname,String password)throws UserNotFindException, PasswordMismatchingException, UserStatusException;
	
	/**
	 * Registra un nuovo utente su Social Gossip
	 * @param nickname nickname dell'utente
	 * @param password password dell'utente
	 * @param language codice iso della lingua
	 * @throws UserAlreadyRegistered se l'utente risulta gia' essere registrato
	 */
	public void register(String nickname,String password,String language) throws UserAlreadyRegistered;
}
