package communication.messages;

public class LoginRequest extends RequestAccessMessage
{
	public LoginRequest(String nickname,char[] password)
	{
		super(nickname,password,RequestAccessMessage.Type.LOGIN);
	}
}
