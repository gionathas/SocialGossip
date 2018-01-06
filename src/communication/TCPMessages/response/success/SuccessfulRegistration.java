package communication.TCPMessages.response.success;

import java.util.List;

import org.json.simple.JSONArray;

import server.model.ChatRoom;

/**
 * Rappresenta un messaggio di risposta ad una richiesta di registrazione,andata a buon fine
 * @author Gionatha Sturba
 *
 */
public class SuccessfulRegistration extends ResponseSuccessMessage
{
	public static final String FIELD_CHATROOM_LIST = "chatroom-list";
	
	public SuccessfulRegistration(List<ChatRoom> chatrooms)
	{
		super();
		
		JSONArray listaChatroom = new JSONArray();
		
		for (ChatRoom cr : chatrooms) {
			listaChatroom.add(ChatRoom.toJsonObject(cr));
		}
		
		//inserisco lista chatroom
		jsonMessage.put(FIELD_CHATROOM_LIST, listaChatroom);
	}
}
