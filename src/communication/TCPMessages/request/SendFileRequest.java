package communication.TCPMessages.request;

/**
 * Richiesta di invio di un file ad un utente
 * @author Gionatha Sturba
 *
 */
public class SendFileRequest extends InteractionRequest
{

	public static final String FIELD_SEND_FILE_REQUEST_FILENAME = "send-file-filename";
	
	public SendFileRequest(String nicknameSender, String nicknameReceiver,String filename) 
	{
		super(Type.FILE_SEND_REQUEST, nicknameSender, nicknameReceiver);
		
		//inserisco nome del file che si vuole inviare
		jsonMessage.put(FIELD_SEND_FILE_REQUEST_FILENAME,filename);
	}
}
