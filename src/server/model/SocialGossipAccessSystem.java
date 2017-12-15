package server.model;

import javax.management.InvalidAttributeValueException;

import server.model.exception.PasswordMismatchingException;
import server.model.exception.UserAlreadyRegistered;
import server.model.exception.UserNotFindException;
import server.model.exception.UserStatusException;
import utils.graph.exception.VertexAlreadyExist;

/**
 * Implementa sistema di gestione accessi degli utenti al sistema
 * @author gio
 *
 */
public class SocialGossipAccessSystem
{
	private SocialGossipNetwork rete;
	
	public SocialGossipAccessSystem(SocialGossipNetwork rete)
	{
		if(rete == null)
			throw new NullPointerException();
		
		this.rete = rete;
	}
	
	/**
	 * Logga un utente registrato alla rete di social Gossip
	 * @param nickname nickname utente
	 * @param password password utente
	 * @throws UserNotFindException se l'utente non risulta essere registrato
	 * @throws PasswordMismatchingException se le password non corrispondono
	 * @throws UserStatusException se l'utente risulta gia' essere online
	 * @throws InvalidAttributeValueException se i parametri di log in non sono validi
	 */
	public void logIn(String nickname,String password)throws UserNotFindException, PasswordMismatchingException, UserStatusException
	{
		if(nickname == null || password == null)
			throw new NullPointerException();
		
		User registeredUser = rete.cercaUtente(nickname);
		
		//se non ho trovato l'utente
		if(registeredUser == null)
			throw new UserNotFindException();
		
		//utente trovato,controllo le password
		boolean passwordMatch = registeredUser.getPassword().contentEquals(new StringBuffer(password));
		
		if(!passwordMatch)
			throw new PasswordMismatchingException();
		
		//infine controllo che l'utente non sia gia' online
		if(registeredUser.isOnline())
			throw new UserStatusException();
		
		//se tutti i controlli sono superati,metto online l'utente
		registeredUser.setOnline(true);
	}
	
	/**
	 * Registra un nuovo utente su Social Gossip
	 * @param nickname nickname dell'utente
	 * @param password password dell'utente
	 * @param language codice iso della lingua
	 * @throws UserAlreadyRegistered se l'utente risulta gia' essere registrato
	 */
	public void register(String nickname,String password,String language) throws UserAlreadyRegistered
	{
		try {
			//se questo non esiste,lo aggiungo e lo setto come online
			rete.aggiungiUtente(nickname, password, language);
		}
		//utente gia registrato
		catch(VertexAlreadyExist e) {
			throw new UserAlreadyRegistered();
		}
	}
	
	/**
	 * Effettua il logout di utente registrato online
	 * @param nickname 
	 * @throws UserNotFindException se l'utente non e' stato trovato
	 * @throws UserStatusException se l'utente non risulta essere online
	 */
	public void logOut(String nickname) throws UserNotFindException, UserStatusException
	{
		if(nickname == null)
			throw new NullPointerException();
		
		//cerco utente
		User registeredUser = rete.cercaUtente(nickname);
		
		//se non ho trovato l'utente
		if(registeredUser == null)
			throw new UserNotFindException();
		//utente non online
		else if(registeredUser.isOnline() == false)
			throw new UserStatusException();
		//altrimenti procedo al logout
		else {
			//metto l'utente offline
			registeredUser.setOnline(false);
		}
	}
}
