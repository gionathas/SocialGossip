package utils.messages;


public class RequestAccessMessage extends RequestMessage
{
	public static final String FIELD_REQUEST_ACCESS_PASSWORD = "password";
	
	private char[] password;
	protected String requestAccessType;
	
	public RequestAccessMessage(String nickname,char[] password) 
	{
		//creo stato interno
		super(RequestMessage.Type.ACCESS,nickname);
		this.password = password;
		
		jsonMessage.put(this.FIELD_REQUEST_ACCESS_PASSWORD,password);
	}
	
	public char[] getPassword() {
		return this.password;
	}
}
