package server.model.exception;

/**
 * Eccezione che viene sollevata se l'utente cercato non risulta essere iscritto
 * @author gio
 *
 */
public class UserNotFindException extends Exception 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4079716749500981954L;

	public UserNotFindException()
	{
		super();
	}
	
}
