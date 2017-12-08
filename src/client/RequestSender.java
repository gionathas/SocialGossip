package client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Si occupa di creare delle richieste da parte del client e inviarle al server,
 * tramite il protocollo TCP
 * @author Gionatha Sturba
 *
 */
public class RequestSender
{
	private Socket socket;
	BufferedWriter writer;
	
	public RequestSender()
	{}
	
	public Socket sendLoginRequest() throws UnknownHostException, IOException
	{
		openConnectionWithServer();
				
		writer.write("login request");
		writer.newLine();
		writer.flush();
		
		return socket;
		
	}
	
	private void openConnectionWithServer() throws UnknownHostException, IOException
	{
		socket = new Socket("localhost",5000);
		writer= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}
}
