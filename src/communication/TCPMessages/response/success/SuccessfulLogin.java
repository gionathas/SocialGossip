package communication.TCPMessages.response.success;

import java.util.List;

import org.json.simple.JSONArray;

import server.model.ChatRoom;
import server.model.User;

public class SuccessfulLogin extends ResponseSuccessMessage 
{
	public static final String FIELD_FRIEND_LIST = "friend-list";
	public static final String FIELD_CHATROOM_LIST = "chatroom-list";
	
	public SuccessfulLogin(List<User>amici,List<ChatRoom> chatrooms)
	{
		super();
		
		JSONArray listaAmici = new JSONArray();
		JSONArray listaChatroom = new JSONArray();
		
		//aggiungo lista amici al messaggio di risposta
		for (User user : amici) {
			listaAmici.add(User.toJsonObject(user));
		}
		
		for (ChatRoom cr : chatrooms) {
			listaChatroom.add(ChatRoom.toJsonObject(cr));
		}
		
		//inserisco lista amici
		jsonMessage.put(FIELD_FRIEND_LIST,listaAmici);
		
		//inserisco lista chatroom
		jsonMessage.put(FIELD_CHATROOM_LIST, listaChatroom);
	}
}
