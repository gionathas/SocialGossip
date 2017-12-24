package communication.TCPMessages.notification;

/**
 * Messaggio di notifica dell'arrivo di un messaggio testuale da parte di un utente
 * @author Gionatha Sturba
 *
 */
public class NewIncomingMessage extends NotificationMessage
{
	public enum ReceiverType{USER,CHATROOM};
	public static final String FIELD_INCOMING_MESSAGE_SENDER_NICKNAME = "incoming-message-sender-nickname";
	public static final String FIELD_INCOMING_MESSAGE_TEXT = "incoming-message-text";
	public static final String FIELD_INCOMING_MESSAGE_RECEIVER_TYPE = "incoming-message-receiver-type";
	
	public NewIncomingMessage(ReceiverType type,String senderNickname,String text) 
	{
		jsonMessage.put(FIELD_INCOMING_MESSAGE_RECEIVER_TYPE,type.name());
		
		jsonMessage.put(FIELD_INCOMING_MESSAGE_SENDER_NICKNAME,senderNickname);
		
		jsonMessage.put(FIELD_INCOMING_MESSAGE_TEXT,text);
	}

}
