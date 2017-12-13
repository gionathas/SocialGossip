	package server.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.management.InvalidAttributeValueException;
import javax.naming.directory.InvalidAttributesException;

import server.model.exception.PasswordMismatchingException;
import server.model.exception.UserAlreadyRegistered;
import server.model.exception.UserNotFindException;
import server.model.exception.UserStatusException;
import utils.graph.Arco;
import utils.graph.Grafo;
import utils.graph.Nodo;
import utils.graph.exception.VertexAlreadyExist;

/**
 * Rappresenta la rete degli utenti di Social Gossip.
 * @author Gionatha Sturba
 *
 */
public class SocialGossipNetwork implements SocialGossipAccessService
{
	private Grafo<User> grafo; //grafo non orientato,rappresenta relazioni tra utenti
	
	public SocialGossipNetwork()
	{
		grafo = new Grafo<User>(false);
	}
	
	/**
	 * Inserisce un nuovo utente nel grafo della rete sociale,se questo non esiste gia'
	 * @throws UserAlreadyRegistered se un utente risulta gia essere iscritto con quei parametri
	 * @throws NullPointerException se l'utente risulta essere null
	 */
	public void register(String nickname,String password,String language) throws UserAlreadyRegistered
	{	
		try {
			//creo istanza utente da registrare
			User userToRegister = new User(nickname,password,true,language);
			//se questo non esiste,lo aggiungo e lo setto come online
			grafo.addVertice(userToRegister);
		}
		//utente gia registrato
		catch(VertexAlreadyExist e) {
			throw new UserAlreadyRegistered();
		}
	}
	
	/**
	 * @param a utente da controllare
	 * @return true se l'utente richiesto risulta essere iscritto al social network,false altrimenti
	 */
	public boolean iscrittoUtente(User a)
	{	
		return grafo.containsVertice(a);
	}
	
	/**
	 * Logga un utente registrato alla rete di social Gossip
	 * @param userToLog utente da loggare
	 * @throws UserNotFindException se l'utente non risulta essere registrato
	 * @throws PasswordMismatchingException se le password non corrispondono
	 * @throws UserStatusException se l'utente risulta gia' essere online
	 */
	public void logIn(String nickname,String password) throws UserNotFindException, PasswordMismatchingException, UserStatusException
	{
		
		if(nickname == null || password == null)
			throw new NullPointerException();
		
		//creo un istanza utente offline 
		User userToLog = new User(nickname, password);
		
		User registeredUser = grafo.getVertice(userToLog);
		
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
	 * Collega 2 utenti che sono diventati amici,tramite un arco
	 * @param a primo utente
	 * @param b secondo utente
	 * @return true se l'arco tra i 2 utenti e' stato creato,false se esisteva gia'
	 * @throws UserNotFindException se uno o entrambi gli utente non risultano essere iscritti
	 */
	public boolean nuovaAmicizia(User a,User b) throws UserNotFindException
	{
		if(a == null || b == null)
			throw new NullPointerException();
		
		//se uno o entrambi gli utenti non sono iscritti
		if(!iscrittoUtente(a) || !iscrittoUtente(b))
			throw new UserNotFindException();
		
		return grafo.addArco(a,b);
	}
	
	/**
	 * @return numero utenti attualmente iscritti
	 */
	public int numeroIscritti()
	{
		return grafo.getVerticiSize();
	}
	
	/**
	 * 
	 * @param a utente da controllare
	 * @return lista di utenti amici dell'utente cercato
	 * @throws UserNotFindException se l'utente risulta non essere iscritto
	 */
	public List<User> amiciDi(User a) throws UserNotFindException
	{
		if(a == null)
			throw new NullPointerException();
		
		if(!iscrittoUtente(a))
			throw new UserNotFindException();
		
		List<User> listaAmici = new LinkedList<User>();
		
		//prendiamo il vertice del grafo che rappresenta l'utente
		Nodo<User> verticeUtente = grafo.getAdj().get(a);
		
		//per tutte le amicizie dell'utente
		for (Arco<User> amicizia : verticeUtente.getArchi()) 
		{
			listaAmici.add(amicizia.getDst().getKey());
		}
		
		return Collections.unmodifiableList(listaAmici);
		
	}
	
	/**
	 * Stampa la rete degli utenti di Social Gossip
	 */
	public void stampaRete() {
		//TODO migliorare funzione di stampa rete
		grafo.stampaGrafo();
	}
}
