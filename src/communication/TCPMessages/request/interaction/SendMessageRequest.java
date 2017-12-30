package communication.TCPMessages.request.interaction;

/**
 * Messaggio di richiesta invio messaggio testuale
 * @author Gionatha Sturba
 *
 */
public class SendMessageRequest extends InteractionRequest
{
	public static final String FIELD_TEXT_MESSAGE = "text";
	
	public SendMessageRequest(String nicknameSender,String nicknameReceiver,String text) 
	{
		super(InteractionRequest.Type.MESSAGE_SEND_REQUEST, nicknameSender, nicknameReceiver);
		
		//inserisco testo del messaggio
		jsonMessage.put(FIELD_TEXT_MESSAGE,text);
	}
}
