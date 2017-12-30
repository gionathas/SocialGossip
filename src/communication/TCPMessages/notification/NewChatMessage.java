package communication.TCPMessages.notification;

/**
 * Messaggio di notifica dell'arrivo di un messaggio testuale da parte di un utente
 * @author Gionatha Sturba
 *
 */
public class NewChatMessage extends NotificationMessage
{
	public static final String FIELD_MESSAGE_TEXT = "message-text";
	
	public NewChatMessage(String senderNickname,String text) 
	{
		super(EventType.NEW_MESSAGE,senderNickname);
		
		//inserisco messaggio testuale
		jsonMessage.put(FIELD_MESSAGE_TEXT,text);
	}

}
