package communication.messages.request;

import communication.messages.request.RequestAccessMessage.Type;

public class LoginRequest extends RequestAccessMessage
{
	public LoginRequest(String nickname,String password)
	{		
		super(nickname,password,RequestAccessMessage.Type.LOGIN);	
		
	}
}
