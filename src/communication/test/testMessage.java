package communication.test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import communication.TCPMessages.Message;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.request.RequestMessage;
import communication.TCPMessages.request.access.LoginRequest;

/**
 * test funzionamento struttura messaggi
 * @author Gionatha Sturba
 *
 */
public class testMessage 
{
	public static void main(String[] args) throws ParseException 
	{
		LoginRequest msg = new LoginRequest("ale","cioa");
		
		//RequestMessage test = new RequestMessage(RequestMessage.Type.ACCESS,"gio");
		
		JSONObject test2 = new JSONObject();
		
		
		System.out.println(test2.toJSONString());
		
		JSONParser parser = new JSONParser();
		
		parser.parse(test2.toJSONString());
		
		//MessageAnalyzer.parse(test.getJsonMessage());
		
	}
}
