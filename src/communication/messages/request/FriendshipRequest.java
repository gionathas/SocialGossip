package communication.messages.request;

import communication.messages.request.InteractionRequest.Type;

/**
 * Messaggio di richiesta amicizia
 * @author gio
 *
 */
public class FriendshipRequest extends InteractionRequest 
{

	public FriendshipRequest(String nicknameSender, String nicknameReceiver) 
	{
		super(InteractionRequest.Type.FRIENDSHIP_REQUEST,nicknameSender,nicknameReceiver);
	}

}
