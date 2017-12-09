package utils.messages;


public class RequestAccessMessage extends RequestMessage
{
	public static final String FIELD_REQUEST_ACCESS_PASSWORD = "password";
	
	private String password;
	protected String requestAccessType;
	
	public RequestAccessMessage(String nickname,String password) 
	{
		//creo stato interno
		super(RequestMessage.Type.ACCESS,nickname);
		this.password = password;
		
		jsonMessage.put(this.FIELD_REQUEST_ACCESS_PASSWORD,password);
	}
	
	public String getPassword() {
		return this.password;
	}
}
