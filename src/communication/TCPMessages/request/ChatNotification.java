package communication.TCPMessages.request;

/**
 * Richiede al server di settare la seguente connessione TCP,come canale di
 * notifica,dove ricevere i messaggi ricevuti da altre chat
 * @author Gionatha Sturba
 *
 */
public class ChatNotification extends RequestMessage
{
	public ChatNotification(String nickname) 
	{
		super(RequestMessage.Type.CHAT_NOTIFICATION_CHAN, nickname);
	}
}
