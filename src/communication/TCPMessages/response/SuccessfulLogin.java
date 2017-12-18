package communication.TCPMessages.response;

import java.util.List;

import org.json.simple.JSONArray;

import server.model.User;

public class SuccessfulLogin extends ResponseSuccessMessage 
{
	public static final String FIELD_FRIEND_LIST = "friend-list";
	
	public SuccessfulLogin(List<User>amici)
	{
		super();
		
		JSONArray listaAmici = new JSONArray();
		//TODO inserire anche le chatroom attive
		
		//aggiungo lista amici al messaggio di risposta
		for (User user : amici) {
			listaAmici.add(User.toJsonObject(user));
		}
		
		jsonMessage.put(FIELD_FRIEND_LIST,listaAmici);
	}
}
