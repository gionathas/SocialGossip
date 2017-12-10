package server.model.exception;

/**
 * Eccezione che viene sollevata se c'e' un problema con lo status di un utente(online,offline)
 * @author gio
 *
 */
public class UserStatusException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -170830959318387929L;

	public UserStatusException() {
		super();
	}

}
