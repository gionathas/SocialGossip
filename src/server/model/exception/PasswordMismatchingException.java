package server.model.exception;

/**
 * Eccezione che viene sollevata se le 2 password non corrispondo
 * @author Gionatha Sturba
 *
 */
public class PasswordMismatchingException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3285866702009475716L;

	public PasswordMismatchingException() {
		super();
	}

}
