package communication.TCPMessages.request;


/**
 * Richiesta di ricerca di un utente
 * @author Gionatha Sturba
 *
 */
public class FindUserRequest extends InteractionRequest 
{	
	public FindUserRequest(String nicknameSender,String nicknameUserToFind) 
	{
		super(InteractionRequest.Type.FIND_USER_REQUEST,nicknameSender,nicknameUserToFind);
	}
}
