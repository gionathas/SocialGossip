package communication.messages;

/**
 * Rappresenta un messaggio di richiesta di interazione con un altro utente
 * @author gio
 *
 */
public class InteractionRequest extends RequestMessage
{
	public enum Type{FIND_USER_REQUEST,FRIENDSHIP_REQUEST,MESSAGE_SEND_REQUEST}
	public final static String FIELD_NICKNAME_RECEIVER = "nickname-receiver";
	public final static String FIELD_INTERACTION_REQUEST_TYPE = "interaction-type";
	
	public InteractionRequest(InteractionRequest.Type type,String nicknameSender,String nicknameReceiver)
	{
		super(RequestMessage.Type.INTERACTION,nicknameSender);
		
		//inserisco nel messaggio nickname del receiver
		jsonMessage.put(FIELD_NICKNAME_RECEIVER,nicknameReceiver);
		
		switch (type) 
		{
			//caso richiesta ricerca utente
			case FIND_USER_REQUEST:
				jsonMessage.put(FIELD_INTERACTION_REQUEST_TYPE,type.name());
				break;
				
				
			//TODO inserire altri casi
			default:
				throw new IllegalArgumentException();
		}
	}
}
