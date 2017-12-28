package communication.TCPMessages.request.chatroom;

/**
 * Messaggio di richiesta creazione nuova chatroom
 * @author Gionatha Sturba
 *
 */
public class NewChatRoom extends ChatRoomRequest
{
	public NewChatRoom(String nicknameAdmin,String chatRoomName) 
	{
		super(ChatRoomRequest.ChatroomRequests.NEW_CHATROOM,nicknameAdmin,chatRoomName);
	}
}
