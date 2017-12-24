package communication.TCPMessages.notification;

/**
 * Messaggio di notifica ad un utente dell'arrivo di un messaggio testuale
 * da parte di un amico
 * @author Gionatha Sturba
 *
 */
public class NewChatMessage extends NewIncomingMessage
{

	public NewChatMessage(String senderNickname, String text) 
	{
		super(NewIncomingMessage.ReceiverType.USER,senderNickname, text);
	}

}
