package utils.graph;

/**
 * Rappresenta un arco di un grafo G = (V,E).
 * Un arco unisce un nodo sorgente(src),ad un nodo destinazione(dest),e ha un relativo peso.
 * @author Gionatha Sturba
 * @see Nodo<E>
 * @param <E> parametro generico dei nodi dell'arco
 */
public class Arco<E>
{
   private Nodo<E> src; //nodo sorgente
   private Nodo<E> dst; //nodo destinazione
   private double weight; //peso dell'arco
  
   /*COSTRUTTORI*/
   
   /**
    * Crea un nuovo arco del tipo (src,dest,w).
    * @param src nodo sorgente
    * @param dst nodo destinazione
    * @param w peso dell'arco
    * @throws NullPointerException se uno o entrambi i nodi sono null
    * @throws IllegalArgumentException se il peso e' negativo
    */
   public Arco(Nodo<E> src,Nodo<E> dst,double w)
   {
       if(src == null || dst == null)
           throw new NullPointerException();
       
       if(w < 0)
           throw new IllegalArgumentException();
       
       this.src = src;
       this.dst = dst;
       this.weight = w;
   }
   
   /**
    * EFF: Crea ed inizializza un nuovo arco,con peso = 0.
    * 
    * @param src nodo sorgente
    * @param dst nodo destinazione
    * @throws NullPointerException se src o dst == null.
    */
   public Arco(Nodo<E> src,Nodo<E> dst)throws NullPointerException
   {
       if(src == null || dst == null)
           throw new NullPointerException();
       
       this.src = src;
       this.dst = dst;
       this.weight = 0;
   }
   
   /*GETTERS & SETTERS*/
   
   public Nodo<E> getSrc() {
       return src;
   }

   public Nodo<E> getDst() {
       return dst;
   }

   public double getWeight() {
       return weight;
   }
   
   /*OVERRIDE*/
   
   @Override
   public boolean equals(Object obj) 
   {
       if(obj == null || !(obj instanceof Arco))
           return false;
       
       final Arco<E> toMatch = (Arco<E>) obj;
       
       return toMatch.getSrc().equals(this.src) && toMatch.getDst().equals(this.dst);
           
   }
   
}
