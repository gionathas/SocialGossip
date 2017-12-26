package communication.TCPMessages.response;

import communication.TCPMessages.response.ResponseMessage.Type;

/**
 * Messaggio di risposta che segnala un operazione fallita
 * @author gio
 *
 */
public class ResponseFailedMessage extends ResponseMessage
{
	/**
	 * Tipi dei messaggi di errore
	 * @author gio
	 *
	 */
	public enum Errors
	{
		INVALID_REQUEST,SENDER_USER_NOT_FOUND,RECEIVER_USER_NOT_FOUND,SENDER_USER_INVALID_STATUS,
		RECEIVER_USER_INVALID_STATUS,USER_ALREADY_REGISTERED,PASSWORD_MISMATCH,ALREADY_FRIEND,
		SAME_USERS,CANNOT_RECEIVE_FILE
	} 	
	
	public static final String FIELD_FAIL_MESSAGE = "Error"; //nome del campo che riporta l'errore
	
	public ResponseFailedMessage(ResponseFailedMessage.Errors errorType)
	{
		super(ResponseMessage.Type.FAIL);
		
		jsonMessage.put(FIELD_FAIL_MESSAGE,errorType.name());
	}
}
