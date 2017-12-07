package utils.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta un nodo generico di un grafo.
 * @author Gionatha Sturba
 *
 * @param <E> Tipo generico della chiave del nodo.
 */
public class Nodo<E>
{
    private E key; //chiave del nodo
    private List<Arco<E>> archi;
    private boolean visited; //stato di visita del nodo
    private Nodo<E> parent; //nodo padre
    private double minDistance; //distanza minima @see Dijistrka Alghritm
    
    /*COSTRUTTORI*/
    
    /**
     * Inizializza un nuovo nodo,con una chiave generica E
     * @param key chiave del nodo
     * @throws NullPointerException se la chiave del nodo e' null
     */
    public Nodo(E key)
    {
        if(key == null)
        {
            throw new NullPointerException();
        }
        
        this.key = key;
        archi = new ArrayList<>();
        visited = false;
        parent = null;
        minDistance = Double.POSITIVE_INFINITY;
    }
    
    /*GETTERS & SETTERS*/
    
    public void setMinDistance(double val)
    {
        if(val < 0)
        {
            throw new IllegalArgumentException();
        }
        
        minDistance = val;
    }
    
    public boolean IsVisited() {
        return visited;
    }

    public void setVisited(boolean bool) {
        this.visited = bool;
    }
    
    public E getKey() {
        return key;
    }

    public List<Arco<E>> getArchi() {
        return Collections.unmodifiableList(archi);
    }
    
    public Nodo<E> getParent() {
        return parent;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public void setParent(Nodo<E> parent)
    {
        this.parent = parent;
    }
    
    /*OVERRIDE*/

    @Override
    public boolean equals(Object obj) 
    {
        if(obj == null)
            return false;
        if(!(obj instanceof Nodo))
            return false;
        
        Nodo<E> toMatch = ((Nodo<E>) obj);
        
        return toMatch.getKey().equals(this.key);
    }
    
    
    /*METODI*/
    
    public int num_of_Archi()
    {
        return archi.size();
    }
    
    /**
     * Aggiunge un arco uscente da questo nodo
     * @param dest nodo destinazione
     * @param w peso dell'arco
     * @return true se l'arco e' stato aggiunto con successo,false se e' gia' presente
     * @throws IllegalArgumentException se il nodo destinazione non e' valido oppure il peso e' negativo
     */
    public boolean addArco(Nodo<E> dest,double w)
    {
    	if(dest == null || w < 0)
    		throw new IllegalArgumentException();
    	
        //creo l'arco da aggiungere
        Arco<E> toAdd = new Arco<E>(this,dest,w);
        
        //arco gia presente..
        if(hasArco(toAdd) != -1)
            return false;
        
        //agiungo arco..
        archi.add(toAdd);
        
        return true;
    }
    
    /**
     * Cerca un arco uscente da questo nodo.
     * @param toFind arco da cercare
     * @return posizione dell'arco nella lista degli archi di questo nodo.
     * @throws NullPointerException se l'arco da cercare e' null
     */
    public int hasArco(Arco<E> toFind)
    {
        if(toFind == null)
            throw new NullPointerException();
        
        return archi.indexOf(toFind);
    }
    
    /**
     * Rimuove l'arco (this,dest), se questo esiste.
     * @param dest nodo destrinazione dell'arco
     * @return true se l'arco e' stato rimosso,false altrimenti
     */
    public boolean removeArco(Nodo<E> dest)
    {
        //creo l'arco da rimuovere.
        Arco<E> toRemove = new Arco<E>(this,dest);
        
        //cerco la posizione dell'arco da rimuovere.
        int pos = hasArco(toRemove);
        
        //arco presente
        if(pos != -1)
        {
            archi.remove(pos);
            return true;
        }
        //arco non presente
        else
        {
            return false;
        }  
    }
} 
