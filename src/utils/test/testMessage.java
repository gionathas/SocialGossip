package utils.test;

import org.json.simple.parser.ParseException;

import utils.messages.Message;
import utils.messages.RequestAccessMessage;
import utils.messages.RequestMessage;

/**
 * test funzionamento struttura messaggi
 * @author Gionatha Sturba
 *
 */
public class testMessage 
{
	public static void main(String[] args) throws ParseException 
	{
		char[] pass = {'c','i','a','o'};
		RequestAccessMessage msg = new RequestAccessMessage("gio",pass);
		System.out.println(msg.getJsonMessage());
		Message msg2 = new Message(Message.Type.REQUEST);
		//System.out.println(msg2.getJsonMessage());
		
		RequestMessage msg3 = new RequestMessage(RequestMessage.Type.ACCESS,"gio");
		String data = msg3.getJsonMessage();
		
		//System.out.println(data);
		
	}
}
