package communication.TCPMessages.request;


/**
 * Messaggio di richiesta amicizia
 * @author Gionatha Sturba
 *
 */
public class FriendshipRequest extends InteractionRequest 
{

	public FriendshipRequest(String nicknameSender, String nicknameReceiver) 
	{
		super(InteractionRequest.Type.FRIENDSHIP_REQUEST,nicknameSender,nicknameReceiver);
	}

}
