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
	public enum Type {ACCESS,LOGOUT,INTERACTION,CHAT_NOTIFICATION_CHAN} //tipi possibili di un messaggio di richiesta
	public static String FIELD_REQUEST_TYPE = "request-type";
	public static String FIELD_REQUEST_NICKNAME_SENDER= "nickname";
	
	//stato interno
	protected RequestMessage.Type requestType;
	
	public RequestMessage(RequestMessage.Type requestType,String nickname)
	{
		super(Message.Type.REQUEST); //creo un messaggio di tipo richiesta
		
		//inizializzo stato interno
		this.requestType = requestType;
		
		switch (requestType) 
		{
			//se e' una richiesta di accesso
			case ACCESS:
				jsonMessage.put(this.FIELD_REQUEST_TYPE,RequestMessage.Type.ACCESS.name());
				break;
				
			//se e' una richiesta di logout
			case LOGOUT:
				jsonMessage.put(this.FIELD_REQUEST_TYPE,RequestMessage.Type.LOGOUT.name());
				break;
			//se e' una richiesta di interazione con un altro utente
			case INTERACTION:
				jsonMessage.put(this.FIELD_REQUEST_TYPE,RequestMessage.Type.INTERACTION.name());
				break;
			
			//se e' una richiesta di settaggio canale di notifica messaggi chat
			case CHAT_NOTIFICATION_CHAN:
				jsonMessage.put(this.FIELD_REQUEST_TYPE,RequestMessage.Type.CHAT_NOTIFICATION_CHAN.name());
				break;

			default:
				throw new IllegalArgumentException();
		}		
		
		//inserisco il nome del mittente
		jsonMessage.put(this.FIELD_REQUEST_NICKNAME_SENDER,nickname);

	}
}
