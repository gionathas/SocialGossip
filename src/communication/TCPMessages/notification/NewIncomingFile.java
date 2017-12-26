package communication.TCPMessages.notification;

/**
 * Messaggio di notifica della richiesta di invio di un file da parte di un altro utente
 * @author Gionatha Sturba
 *
 */
public class NewIncomingFile extends NotificationMessage
{
	public static final String FIELD_INCOMING_FILE_FILENAME = "incoming-file-name";
	
	public NewIncomingFile(String senderNickname,String filename) 
	{
		super(EventType.NEW_FILE,senderNickname);
		
		//inserisco nome del file che si sta per ricevere
		jsonMessage.put(FIELD_INCOMING_FILE_FILENAME,filename);
	}

}
