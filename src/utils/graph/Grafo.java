package utils.graph;

import utils.graph.exception.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rappresenta la struttura dati di un grafo,composto da vertici ed archi.
 * @author Gionatha Sturba
 *
 * @param <E> parametro generico dei valori dei vertici
 */
public final class Grafo<E>
{
    private Map<E,Nodo<E>> adj;
    private boolean isOriented;
    
    /**
     * per rappresentare un grafo 'unweighted',viene utilizzato questo peso di default.
     */
    public static final double DEFAULT_WEIGHT = 1; 
    
    /*COSTRUTTORI*/
    
    /**
     * Crea e inzializza un nuovo grafo,senza vertici.
     * @param orient setta orientamento del grafo
     */
    public Grafo(boolean orient)
    {
        this.adj = new HashMap<>();
        this.isOriented = orient;
    }
    
    /**
     * Crea e inizializza un nuovo grafo,con una lista di vertici.
     * @param orient valore dell'orientamento del grafo.
     * @param vertici lista di vertici da inserire.
     * @throws NullPointerException se l'insieme dei vertici e' null
     */
    public Grafo(boolean orient,List<E> vertici) throws NullPointerException
    {
        if(vertici == null)
        {
            throw new NullPointerException();
        }
        
        adj = new HashMap<>();
        isOriented = orient;
        
        for(E i : vertici)
        {
            addVertice(i);
        }
    }
    
    /* METODI */
    
    /**
     * Stampa sullo stdout una rappresentazione attuale del grafo.
     */
    public void stampaGrafo()
    {
        for(Nodo<E> i : adj.values())
        {
            System.out.print(i.getKey()+" ->[");
            for(Arco<E> j : i.getArchi())
            {
                System.out.print(j.getDst().getKey()+" ");
            }
            System.out.println("]"+'\n');
        }
    }
    
//    /**
//     * 
//     */
//    public void resetGraph()
//    {
//        for(Nodo<E> i : adj.values())
//        {
//            i.setParent(null);
//            i.setVisited(false);
//            i.setMinDistance(Double.POSITIVE_INFINITY);
//        }
//    }
    
    /**
     * Aggiunge un vertice al grafo
     * @param vertice vertice da aggiungere
     * @throws VertexAlreadyExist se un vertice uguale esiste gia'
     */
    public void addVertice(E vertice)throws VertexAlreadyExist
    {
        //se il vertice e' gia presente
        if(adj.containsKey(vertice))
            throw new VertexAlreadyExist();
        
        //altrimenti inserisco
        Nodo<E> toAdd = new Nodo<E>(vertice);
        adj.put(vertice, toAdd);
    }
    
    /**
     * Aggiunge un arco tra 2 vertici
     * @param src vertice sorgente
     * @param dest vertice destinazione
     * @param w peso dell'arco
     * @return true se l'arco e' stato aggiunto,false altrimenti
     */
    public boolean addArco(E src,E dest,double w)
    {
        checkVertici(src, dest);
        
        //aggiungiamo l'arco
        if(adj.get(src).addArco(adj.get(dest), w))
        {
            //caso arco non orientato,aggiungo l'arco in senso opposto,contandolo sempre come un singolo arco.
            if(!isOriented)
            {
                adj.get(dest).addArco(adj.get(src), w);
            }
            
            return true;
        }
        
        //arco gia' presente nel grafo
        return false;
    }
    
    /**
     * Aggiunge un arco tra 2 vertici
     * @param src vertice sorgente
     * @param dest vertice destinazione
     * @return true se l'arco e' stato aggiunto,false altrimenti
     */
    public boolean addArco(E src,E dest)
    {
        return addArco(src,dest,DEFAULT_WEIGHT);
    }
    
    /**
     * Rimuove un vertice dal grafo
     * @param vertice vertice da rimuovere
     */
    public void removeVertice(E vertice)
    {
        checkVertice(vertice);
        
        //se lo troviamo,rimuoviamo sia il vertice,sia i relativi archi che puntano a lui
        adj.remove(vertice);
        
        Nodo<E> toRemove = new Nodo<E>(vertice);
        
        //Controllo se ogni vertice ha l'arco da eliminare..
        for(Nodo<E> i : adj.values())
        {
            i.removeArco(toRemove);
        }
    }
    
    /**
     * Rimuove un arco tra 2 vertici del grafo
     * @param src vertice sorgente
     * @param dest vertice destinazione
     * @return true se l'arco e' stato rimosso,false altrimenti
     */
    public boolean removeArco(E src,E dest)
    {
        checkVertici(src, dest);
        
        //rimuovo l'arco,se c'e'
        if(hasArco(src,dest))
        {
            adj.get(src).removeArco(adj.get(dest));
            
            //caso non orientato,rimuovo anche l'arco opposto
            if(!isOriented)
            {
                adj.get(dest).removeArco(adj.get(src));
            }
            
            return true;
        }
        
        //arco non presente
        return false;
        
    }
    
    /**
     * Controlla se esiste l'arco (src,dest),nel grafo
     * @param src vertice sorgente
     * @param dest vertice destinazione
     * @return true se l'arco esiste,false altrimenti
     */
    public boolean hasArco(E src,E dest)
    {
        checkVertici(src,dest);
        
        //arco da ricercare
        Arco<E> toFind = new Arco<E>(new Nodo<E>(src),new Nodo<E>(dest));
                
        return adj.get(src).hasArco(toFind) != -1;
    }
    
    /**
     * Controlla se esiste il vertice cercato,nel grafo.
     * @param vertice vertice da ricercare
     * @return true se il vertice esiste,false altrimenti
     * @throws NullPointerException se il vertice e' null
     */
    public boolean containsVertice(E vertice)throws NullPointerException
    {
        if(vertice == null)
        {
            throw new NullPointerException();
        }
        
        return adj.containsKey(vertice);
    }
    
    /*METODI PRIVATI*/
    
    private void checkVertici(E src,E dest) throws NullPointerException,NoVertexFound
    {
        if(src == null || dest == null)
            throw new NullPointerException();
        if(!containsVertice(src)||!containsVertice(dest))
            throw new NoVertexFound();
    }
    
    private void checkVertice(E vertice)throws NullPointerException,NoVertexFound
    {
        if(vertice == null)
            throw new NullPointerException();
        
        //se non contiene il vertice il vertice
        if(!containsVertice(vertice))
            throw new NoVertexFound();
    }
    
    /*GETTERS & SETTERS*/
    
    /**
     * Ritorna la lista dei vertici del grafo
     * @return lista dei vertici
     */
    public List<E> getVertici()
    {
        List<E> toReturn = new ArrayList<>();
        
        for(Nodo<E> i : adj.values())
        {
            toReturn.add(i.getKey());
        }
        
        return Collections.unmodifiableList(toReturn);
    }
    
    public Map<E, Nodo<E>> getAdj() {
        return Collections.unmodifiableMap(adj);
    }
    
    /**
     * @return se il grafo e' orientato o meno
     */
    public boolean IsOriented() {
        return isOriented;
    }
    
    /**
     * @return numero dei vertici del grafo
     */
    public int getVerticiSize()
    {
        return adj.values().size();
    }
    
    /**
     * 
     * @return numero degli archi tra vertici del grago
     */
    public int getArchiSize()
    {
        int count = 0;
        
        for(Nodo<E> i :adj.values())
        {
            count += i.num_of_Archi();
        }
        
        return count;
            
    }
}
