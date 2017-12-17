package communication.messages.response;

/**
 * Risposta di successo alla richiesta di amicizia
 * @author gio
 *
 */
public class SuccessFriendship extends ResponseSuccessMessage
{
	public static final String FIELD_STATUS_NEW_FRIEND = "status-new-friend";
	
	public SuccessFriendship(boolean status)
	{
		super();
		
		//inserisco stato dell'utente aggiunto
		jsonMessage.put(FIELD_STATUS_NEW_FRIEND,status);
	}
}
