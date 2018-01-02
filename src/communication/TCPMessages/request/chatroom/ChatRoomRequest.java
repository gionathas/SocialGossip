package communication.TCPMessages.request.chatroom;

import communication.TCPMessages.request.RequestMessage;
import communication.TCPMessages.request.RequestMessage.Type;

/**
 * Messaggio di richiesta che coinvolge una chatroom
 * @author Gionatha Sturba
 *
 */
public class ChatRoomRequest extends RequestMessage
{
	public static final String FIELD_CHATROOM_REQUEST_NAME = "chatroom-name";
	public enum ChatroomRequests{NEW_CHATROOM,JOIN_CHATROOM,CLOSE_CHATROOM};
	public static final String FIELD_CHATROOM_REQUEST_TYPE = "chatroom-request-type";
	
	public ChatRoomRequest(ChatroomRequests type,String nicknameUser,String chatRoomName) 
	{
		super(RequestMessage.Type.CHATROOM_REQUEST,nicknameUser);
		
		//inserisco tipo del messaggio di interazione con la chatroom
		jsonMessage.put(FIELD_CHATROOM_REQUEST_TYPE,type.name());
		
		//inserisco nome della chatrooms
		jsonMessage.put(FIELD_CHATROOM_REQUEST_NAME,chatRoomName);
	}

}
