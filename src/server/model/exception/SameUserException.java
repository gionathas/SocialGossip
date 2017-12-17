package server.model.exception;

/**
 * Eccezzione che viene lanciata se 2 utenti risultano essere gli stessi,in un particolare contesto
 * @author gio
 *
 */
public class SameUserException extends Exception{
	
	public SameUserException() {
		super();
	}

}
