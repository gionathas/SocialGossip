package server.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import server.model.exception.PasswordMismatchingException;
import server.model.exception.UserAlreadyRegistered;
import server.model.exception.UserNotFindException;
import server.model.exception.UserStatusException;
import utils.graph.Arco;
import utils.graph.Grafo;
import utils.graph.Nodo;
import utils.graph.exception.VertexAlreadyExist;

/**
 * Rappresenta l'implementazione del grafo utenti della rete di Social Gossip.
 * Implementa le operazioni sul grafo del social network(nuovi utenti,nuova amicizia ecc..)
 * @author Gionatha Sturba
 *
 */
public class SocialGossipNetwork 
{
	private Grafo<User> grafo; //grafo non orientato,rappresenta relazioni tra utenti
	
	public SocialGossipNetwork()
	{
		grafo = new Grafo<User>(false);
	}
	
	/**
	 * Inserisce un nuovo utente nel grafo della rete sociale,se questo non esiste gia'
	 * @param u utente da inserire
	 * @throws UserAlreadyRegistered se un utente risulta gia essere iscritto con quei parametri
	 * @throws NullPointerException se l'utente risulta essere null
	 */
	public void nuovoUtente(User u) throws UserAlreadyRegistered
	{
		if(u == null)
			throw new NullPointerException();
		
		try {
			grafo.addVertice(u);
		}catch(VertexAlreadyExist e) {
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
	public void logInUtente(User userToLog) throws UserNotFindException, PasswordMismatchingException, UserStatusException
	{
		if(userToLog == null)
			throw new NullPointerException();
		
		User registeredUser = grafo.getVertice(userToLog);
		
		//se non ho trovato l'utente
		if(registeredUser == null)
			throw new UserNotFindException();
		
		//utente trovato,controllo le password
		boolean passwordMatch = registeredUser.checkPassword(userToLog.getPassword());
		
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
}
