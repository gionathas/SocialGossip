package communication.messages.response;

import communication.messages.Message;
import communication.messages.Message.Type;

/**
 * Rappresenta in generico messaggio di rispostas
 * @author gio
 *
 */
public class ResponseMessage extends Message
{
	public enum Type{FAIL,SUCCESS};
	public static final String FIELD_RESPONSE_TYPE = "response-type";
	protected ResponseMessage.Type responseType; //rappresenta il tipo di risposta

	public ResponseMessage(ResponseMessage.Type responseType) 
	{
		super(Message.Type.RESPONSE);
		
		this.responseType = responseType;
		
		//inserisco formato della risposta nel messaggio json
		jsonMessage.put(FIELD_RESPONSE_TYPE,responseType.name());
	}
}
