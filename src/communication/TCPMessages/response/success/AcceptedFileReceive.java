package communication.TCPMessages.response.success;

/**
 * Messaggio di risposta di accettazione di ricezione di un file
 * @author Gionatha Sturba
 *
 */
public class AcceptedFileReceive extends ResponseSuccessMessage
{
	
	public static final String FIELD_ACCEPTED_FILE_RECEIVE_HOSTNAME = "hostname";
	public static final String FIELD_ACCEPTED_FILE_RECEIVE_PORT = "port";
	
	public AcceptedFileReceive(String hostname,int port) 
	{
		super();
		
		//inserisco nome dell'host a cui inviare il file
		jsonMessage.put(FIELD_ACCEPTED_FILE_RECEIVE_HOSTNAME,hostname);
		
		//inserisco porta in cui e' in ascolto l'host
		jsonMessage.put(FIELD_ACCEPTED_FILE_RECEIVE_PORT,new Integer(port));
	}

}
