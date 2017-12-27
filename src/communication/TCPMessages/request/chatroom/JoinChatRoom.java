package communication.TCPMessages.request.chatroom;

/**
 * Messaggio di richiesta partecipazione ad una chatroom
 * @author Gionatha Sturba
 *
 */
public class JoinChatRoom extends ChatRoomRequest
{
	public JoinChatRoom(ChatroomRequests type, String nicknameUser, String chatRoomName) 
	{
		super(ChatRoomRequest.ChatroomRequests.JOIN_CHATROOM,nicknameUser,chatRoomName);
	}
}
