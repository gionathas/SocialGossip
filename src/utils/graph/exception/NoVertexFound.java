package utils.graph.exception;

/**
 * Eccezione che viene sollevata se uno o piu vertici non vengono trovati
 * all'interno dell'insieme dei vertici,per svolgere delle operazioni sul grafo G.
 * 
 * @author Gionatha Sturba
 */
public class NoVertexFound extends RuntimeException 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoVertexFound()
    {
        super();
    }
    
    public NoVertexFound(String s)
    {
        super(s);
    }
}
