package server.model.exception;

/**
 * Eccezione che viene sollevata se un utente risulta gia' essere iscritto con i parametri inseriti
 * @author Gionatha Sturba
 *
 */
public class UserAlreadyRegistered extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -858523201826737923L;
	
	public UserAlreadyRegistered() {
		super();
	}

}
