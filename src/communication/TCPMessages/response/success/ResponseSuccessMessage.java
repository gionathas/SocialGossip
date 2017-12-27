package communication.TCPMessages.response.success;

import communication.TCPMessages.response.ResponseMessage;
import communication.TCPMessages.response.ResponseMessage.Type;

/**
 * Messaggio di risposta che segnala un operazione andata a buon fine
 * @author gio
 *
 */
public class ResponseSuccessMessage extends ResponseMessage 
{
	public ResponseSuccessMessage() 
	{
		super(ResponseMessage.Type.SUCCESS);
	}
}
