package communication.TCPMessages.request;

import communication.TCPMessages.Message;

/**
 * Rappresenta un generico messaggio di richiesta tra client e server.
 * Un messaggio di richiesta puo' essere: richiesta di accesso,richiesta inizio chat,
 * @author Gionatha Sturba
 *
 */
public class RequestMessage extends Message 
{
	public enum Type {ACCESS,LOGOUT,INTERACTION,CHAT_NOTIFICATION_CHAN,CHATROOM_REQUEST} //tipi possibili di un messaggio di richiesta
	public static String FIELD_REQUEST_TYPE = "request-type";
	public static String FIELD_REQUEST_NICKNAME_SENDER= "nickname";
	
	//stato interno
	protected RequestMessage.Type requestType;
	
	public RequestMessage(RequestMessage.Type requestType,String nickname)
	{
		super(Message.Type.REQUEST); //creo un messaggio di tipo richiesta
		
		//inizializzo stato interno
		this.requestType = requestType;
		
		//inserisco tipo della richiesta
		jsonMessage.put(this.FIELD_REQUEST_TYPE,requestType.name());	
		
		//inserisco il nome del mittente
		jsonMessage.put(this.FIELD_REQUEST_NICKNAME_SENDER,nickname);

	}
}
