	package server.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import server.model.exception.UserNotFindException;
import utils.graph.Arco;
import utils.graph.Grafo;
import utils.graph.Nodo;
import utils.graph.exception.VertexAlreadyExist;

/**
 * Gestisce il grafo degli utenti di social gossip
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
	 * @param a utente da controllare
	 * @return true se l'utente richiesto risulta essere iscritto al social network,false altrimenti
	 */
	public boolean iscrittoUtente(User a)
	{	
		return grafo.containsVertice(a);
	}
	
	/**
	 * 
	 * @param nickname
	 * @return utente cercato,altrimenti null se non e' registrato
	 */
	public User cercaUtente(String nickname)
	{
		if(nickname == null)
			throw new NullPointerException();
		
		return grafo.getVertice(new User(nickname));
	}
	
	public void aggiungiUtente(String nickname,String password,String language)throws VertexAlreadyExist
	{
		if(nickname == null || password == null || language == null)
			throw new NullPointerException();
		
		//aggiungo un utente con il suo nick password e lingua relativi ad esso,piu' lo metto online
		grafo.addVertice(new User(nickname,password,true,language));
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
