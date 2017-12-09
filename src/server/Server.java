package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
				DataInputStream in = new DataInputStream(clientSocket.getInputStream());
				DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
				
				String request = in.readUTF();
				System.out.println("received: "+request);
				
				//rispondo al client
				out.writeUTF("login request received\ntest");
				
				in.close();
				out.close();
				clientSocket.close();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
