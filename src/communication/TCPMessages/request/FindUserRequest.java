package communication.TCPMessages.request;

import communication.TCPMessages.request.InteractionRequest.Type;

/**
 * Richiesta di ricerca di un utente
 * @author gio
 *
 */
public class FindUserRequest extends InteractionRequest 
{	
	public FindUserRequest(String nicknameSender,String nicknameUserToFind) 
	{
		super(InteractionRequest.Type.FIND_USER_REQUEST,nicknameSender,nicknameUserToFind);
	}
}
