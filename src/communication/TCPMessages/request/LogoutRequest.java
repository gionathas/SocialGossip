package communication.TCPMessages.request;


/**
 * Messaggio di richiesta di logout
 * @author Gionatha Sturba
 *
 */
public class LogoutRequest extends RequestMessage
{
	public LogoutRequest(String nickname) 
	{
		super(RequestMessage.Type.LOGOUT,nickname);
	}

}
