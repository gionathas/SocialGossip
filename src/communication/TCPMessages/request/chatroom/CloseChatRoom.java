package communication.TCPMessages.request.chatroom;

/**
 * Messaggio di richiesta di chiusura di una chatroom
 * @author Gionatha Sturba
 *
 */
public class CloseChatRoom extends ChatRoomRequest
{

	public CloseChatRoom(String nicknameUser, String chatRoomName) 
	{
		super(ChatRoomRequest.ChatroomRequests.CLOSE_CHATROOM, nicknameUser, chatRoomName);
	}

}
