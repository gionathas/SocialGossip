package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server 
{
	public static void main(String[] args) 
	{
		try {
			ServerSocket servSocket = new ServerSocket(5000);
			
			while(true)
			{
				Socket clientSocket = servSocket.accept();
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				DataOutputStream toClient = new DataOutputStream(clientSocket.getOutputStream());
				
				String data;
				//leggo contenuto del client
				while(fromClient.ready() && (data = fromClient.readLine()) != null)
				{
					System.out.println("Received: "+data);
				}
				
				//rispondo al client
				toClient.writeUTF("Login Request received\n");
				
				clientSocket.close();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
