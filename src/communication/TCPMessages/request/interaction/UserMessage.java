package communication.TCPMessages.request.interaction;

/**
 * Messaggio di richiesta invio messaggio testuale ad un altro utente
 * @author Gionatha Sturba
 *
 */
public class UserMessage extends SendMessageRequest
{

	public UserMessage(String nicknameSender, String nicknameReceiver, String text) 
	{
		super(nicknameSender,SendMessageRequest.ReceiverType.USER, nicknameReceiver, text);
	}

}
