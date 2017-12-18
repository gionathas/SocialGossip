package communication.TCPMessages.request;

import communication.TCPMessages.request.RequestAccessMessage.Type;

public class LoginRequest extends RequestAccessMessage
{
	public LoginRequest(String nickname,String password)
	{		
		super(nickname,password,RequestAccessMessage.Type.LOGIN);	
		
	}
}
