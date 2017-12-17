package communication.messages.request;

import communication.messages.request.RequestAccessMessage.Type;

public class RegisterRequest extends RequestAccessMessage
{
	public final static String FIELD_REGISTER_REQUEST_LANGUAGE = "language"; 
	
	public RegisterRequest(String nickname,String password,String lang)
	{
		super(nickname,password,RequestAccessMessage.Type.REGISTER);
		
		jsonMessage.put(FIELD_REGISTER_REQUEST_LANGUAGE,lang);
	}
}
