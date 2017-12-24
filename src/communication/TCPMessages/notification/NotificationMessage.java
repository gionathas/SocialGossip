package communication.TCPMessages.notification;

import communication.TCPMessages.Message;

/**
 * Messaggio di notifica Server -> Client
 * @author Gionatha Sturba
 *
 */
public class NotificationMessage extends Message
{
	public NotificationMessage() 
	{
		super(Message.Type.NOTIFICATION);
	}
}
