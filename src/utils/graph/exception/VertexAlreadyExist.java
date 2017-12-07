package utils.graph.exception;

/**
 * Eccezione,che viene lanciata se il vertice e' gia' presente nel grafo.
 * 
 * @author Gionatha Sturba
 */
public class VertexAlreadyExist extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VertexAlreadyExist()
    {
        super();
    }
    
    public VertexAlreadyExist(String s)
    {
        super(s);
    }
}