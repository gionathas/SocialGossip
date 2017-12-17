package communication.messages.request;

import communication.messages.Message;
import communication.messages.Message.Type;

/**
 * Rappresenta un generico messaggio di richiesta tra client e server.
 * Un messaggio di richiesta puo' essere: richiesta di accesso,richiesta inizio chat,
 * @author Gionatha Sturba
 *
 */
public class RequestMessage extends Message 
{
	
	public enum Type {ACCESS,LOGOUT,INTERACTION} //tipi possibili di un messaggio di richiesta
	public static String FIELD_REQUEST_TYPE = "request-type";
	public static String FIELD_REQUEST_NICKNAME_SENDER= "nickname";
	
	//stato interno
	protected RequestMessage.Type requestType;
	protected String nicknameSender;
	
	public RequestMessage(RequestMessage.Type requestType,String nickname)
	{
		super(Message.Type.REQUEST); //creo un messaggio di tipo richiesta
		
		//inizializzo stato interno
		this.nicknameSender = nickname;
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

			default:
				throw new IllegalArgumentException();
		}		
		
		//inserisco il nome del mittente
		jsonMessage.put(this.FIELD_REQUEST_NICKNAME_SENDER,nickname);

	}
}
