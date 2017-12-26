package client.thread;

import java.net.ServerSocket;

/**
 * Threa che si occupa di ricevere un file
 * @author Gionatha Sturba
 *
 */
public class FileReceiver extends Thread
{
	private ServerSocket server;

	public FileReceiver(ServerSocket server) 
	{
		if(server == null)
			throw new NullPointerException();
		
		this.server = server;
	}
	
	public void run()
	{
		
	}

}
