package communication.TCPMessages.request.interaction;

/**
 * Messaggio di richiesta invio messaggio testuale
 * @author Gionatha Sturba
 *
 */
public class SendMessageRequest extends InteractionRequest
{
	public enum ReceiverType{USER,CHATROOM}//tipi del ricevente
	public static final String FIELD_RECEIVER_TYPE = "receiver-type";
	public static final String FIELD_TEXT_MESSAGE = "text";
	
	public SendMessageRequest(String nicknameSender,SendMessageRequest.ReceiverType receiverType ,String nicknameReceiver,String text) 
	{
		super(InteractionRequest.Type.MESSAGE_SEND_REQUEST, nicknameSender, nicknameReceiver);
		
		//inserisco tipo del ricevente
		jsonMessage.put(FIELD_RECEIVER_TYPE,receiverType.name());
		jsonMessage.put(FIELD_TEXT_MESSAGE,text);
	}
}
