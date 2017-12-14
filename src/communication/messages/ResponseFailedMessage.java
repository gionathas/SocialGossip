package communication.messages;

/**
 * Messaggio di risposta che segnala un operazione fallita
 * @author gio
 *
 */
public class ResponseFailedMessage extends ResponseMessage
{
	public enum Errors{INVALID_REQUEST,USER_NOT_FOUND,USER_ALREADY_REGISTERED,PASSWORD_MISMATCH,USER_INVALID_STATUS} //tipi di errori riscontrabili
	public static final String FIELD_FAIL_MESSAGE = "Error"; //nome del campo che riporta l'errore
	
	public ResponseFailedMessage(ResponseFailedMessage.Errors errorType)
	{
		super(ResponseMessage.Type.FAIL);
		
		jsonMessage.put(FIELD_FAIL_MESSAGE,errorType.name());
	}
}
