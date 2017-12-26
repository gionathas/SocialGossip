package communication.TCPMessages.notification;

import communication.TCPMessages.Message;

/**
 * Messaggio di notifica Server -> Client
 * @author Gionatha Sturba
 *
 */
public class NotificationMessage extends Message
{
	public enum EventType{NEW_MESSAGE,NEW_FILE};

	public static final String FIELD_NOTIFICATION_SENDER_NICKNAME = "incoming-message-sender-nickname";
	public static final String FIELD_NOTIFICATION_TYPE = "notification-type";

	public NotificationMessage(EventType type,String senderNickname) 
	{
		super(Message.Type.NOTIFICATION);
		
		//inserisco tipo di evento della notifica
		jsonMessage.put(FIELD_NOTIFICATION_TYPE,type.name());
		
		//inserisco nome del mittente che ha generato l'evento di notifica
		jsonMessage.put(FIELD_NOTIFICATION_SENDER_NICKNAME,senderNickname);
	}
}
