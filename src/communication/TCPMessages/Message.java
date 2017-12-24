package communication.TCPMessages;

import org.json.simple.JSONObject;

/**
 * Rappresenta un generico messaggio scambiato tra client e server.
 * Un messaggio generico puo' essere o di richiesta, o di risposta.
 * @author Gionatha Sturba
 *
 */
public class Message
{
	public enum Type {REQUEST,RESPONSE,NOTIFICATION}; //enum che indica i tipi che puo' avere un  messaggio
	public static final String FIELD_MESSAGE_TYPE = "message-type"; //nome del field type all'interno di json
	
	//stato interno
	protected Message.Type messageType; //tipo del messaggio
	protected JSONObject jsonMessage = null; //messaggio in json
	
	public Message(Message.Type messageType) 
	{
		this.messageType = messageType;
		
		//creazione oggetto json
		jsonMessage = new JSONObject();
		
		//inserisco tipo del messaggio
		jsonMessage.put(Message.FIELD_MESSAGE_TYPE,messageType.name());

	}
	
	public String getJsonMessage() {
		return jsonMessage.toJSONString();
	}
}
