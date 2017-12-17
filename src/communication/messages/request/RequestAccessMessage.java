package communication.messages.request;

public class RequestAccessMessage extends RequestMessage
{
	public enum Type {LOGIN,REGISTER}; // tipo di richiesta accesso al sistema
	public static final String FIELD_REQUEST_ACCESS_TYPE = "request-access-type";
	public static final String FIELD_REQUEST_ACCESS_PASSWORD = "password";
	
	//stato interno
	protected RequestAccessMessage.Type requestAccessType;
	private String password;
	
	public RequestAccessMessage(String nickname,String password,RequestAccessMessage.Type requestAccessType) 
	{
		//creo stato interno
		super(RequestMessage.Type.ACCESS,nickname);
		this.requestAccessType = requestAccessType;
		this.password = password;
		
		jsonMessage.put(FIELD_REQUEST_ACCESS_TYPE,requestAccessType.name());
		jsonMessage.put(this.FIELD_REQUEST_ACCESS_PASSWORD,password);
	}
}
