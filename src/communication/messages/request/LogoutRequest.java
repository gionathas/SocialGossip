package communication.messages.request;

import communication.messages.request.RequestMessage.Type;

/**
 * Messaggio di richiesta di logout
 * @author gio
 *
 */
public class LogoutRequest extends RequestMessage
{
	public LogoutRequest(String nickname) 
	{
		super(RequestMessage.Type.LOGOUT,nickname);
	}

}
