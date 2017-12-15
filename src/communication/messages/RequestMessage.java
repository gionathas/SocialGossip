package communication.messages;

/**
 * Rappresenta un generico messaggio di richiesta tra client e server.
 * Un messaggio di richiesta puo' essere: richiesta di accesso,richiesta inizio chat,
 * @author Gionatha Sturba
 *
 */
public class RequestMessage extends Message 
{
	
	public enum Type {ACCESS,LOGOUT} //tipi possibili di un messaggio di richiesta
	public static String FIELD_REQUEST_TYPE = "request-type";
	public static String FIELD_REQUEST_NICKNAME = "nickname";
	
	//stato interno
	protected RequestMessage.Type requestType;
	protected String nickname;
	
	public RequestMessage(RequestMessage.Type requestType,String nickname)
	{
		super(Message.Type.REQUEST); //creo un messaggio di tipo richiesta
		
		//inizializzo stato interno
		this.nickname = nickname;
		this.requestType = requestType;
		
		//aggiorno formato json del messaggio
		
		//se e' una richiesta di accesso
		if(requestType.equals(RequestMessage.Type.ACCESS))
		{
			jsonMessage.put(this.FIELD_REQUEST_TYPE,RequestMessage.Type.ACCESS.name());

		}
		//se e' una richiesta di logout
		else if(requestType.equals(RequestMessage.Type.LOGOUT))
		{
			jsonMessage.put(this.FIELD_REQUEST_TYPE,RequestMessage.Type.LOGOUT.name());

		}
		
		//inserisco il nome
		jsonMessage.put(this.FIELD_REQUEST_NICKNAME,nickname);

	}
	
	public RequestMessage.Type getRequestType() {
		return this.requestType;
	}
	
	public String getNickname() {
		return this.nickname;
	}
}
