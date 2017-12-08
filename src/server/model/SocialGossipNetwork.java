package server.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
	private Grafo<Utente> grafo; //grafo non orientato,rappresenta relazioni tra utenti
	
	public SocialGossipNetwork()
	{
		grafo = new Grafo<Utente>(false);
	}
	
	/**
	 * Inserisce un nuovo utente nel grafo della rete sociale,se questo non esiste gia'
	 * @param u utente da inserire
	 * @return true se l'utente e' stato iscritto correttamente,false se esiste gia'.
	 * @throws VertexAlreadyExist se l'utente esiste gia'
	 * @throws NullPointerException se l'utente risulta essere null
	 */
	public boolean nuovoUtente(Utente u)
	{
		if(u == null)
			throw new NullPointerException();
		
		try {
			grafo.addVertice(u);
		}catch(VertexAlreadyExist e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param a utente da controllare
	 * @return se l'utente richiesto risulta essere iscritto al social network
	 */
	public boolean iscrittoUtente(Utente a)
	{	
		return grafo.containsVertice(a);
	}
	
	/**
	 * Collega 2 utenti che sono diventati amici,tramite un arco
	 * @param a primo utente
	 * @param b secondo utente
	 * @return true se l'arco tra i 2 utenti e' stato creato,false se esisteva gia'
	 * @throws UtenteNonTrovatoException se uno o entrambi gli utente non risultano essere iscritti
	 */
	public boolean nuovaAmicizia(Utente a,Utente b) throws UtenteNonTrovatoException
	{
		if(a == null || b == null)
			throw new NullPointerException();
		
		//se uno o entrambi gli utenti non sono iscritti
		if(!iscrittoUtente(a) || !iscrittoUtente(b))
			throw new UtenteNonTrovatoException();
		
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
	 * @throws UtenteNonTrovatoException se l'utente risulta non essere iscritto
	 */
	public List<Utente> amiciDi(Utente a) throws UtenteNonTrovatoException
	{
		if(a == null)
			throw new NullPointerException();
		
		if(!iscrittoUtente(a))
			throw new UtenteNonTrovatoException();
		
		List<Utente> listaAmici = new LinkedList<Utente>();
		
		//prendiamo il vertice del grafo che rappresenta l'utente
		Nodo<Utente> verticeUtente = grafo.getAdj().get(a);
		
		//per tutte le amicizie dell'utente
		for (Arco<Utente> amicizia : verticeUtente.getArchi()) 
		{
			listaAmici.add(amicizia.getDst().getKey());
		}
		
		return Collections.unmodifiableList(listaAmici);
		
	}
}
