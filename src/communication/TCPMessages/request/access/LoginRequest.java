package communication.TCPMessages.request.access;

/**
 * Messaggio di richiesta di login
 * @author Gionatha Sturba
 *
 */
public class LoginRequest extends RequestAccessMessage
{
	public LoginRequest(String nickname,String password)
	{		
		super(nickname,password,RequestAccessMessage.Type.LOGIN);	
		
	}
}
