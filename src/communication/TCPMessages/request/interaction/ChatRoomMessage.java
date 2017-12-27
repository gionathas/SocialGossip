package communication.TCPMessages.request.interaction;

/**
 * Messaggio di richiesta invio messaggio testuale ad una chatroom
 * @author Gionatha Sturba
 *
 */
public class ChatRoomMessage extends SendMessageRequest
{
	public ChatRoomMessage(String nicknameSender, String nicknameReceiver, String text) 
	{
		super(nicknameSender,SendMessageRequest.ReceiverType.CHATROOM, nicknameReceiver, text);
	}
	
}
