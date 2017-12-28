package communication.TCPMessages.response.success;

import java.util.List;

import org.json.simple.JSONArray;

import server.model.ChatRoom;
import server.model.User;

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
