package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import client.controller.LoginController;

/**
 * Bootstrapper del client
 * @author Gionatha Sturba
 *
 */
public class SocialGossipClient 
{
	/**
	 * Avvio client
	 */
	public static void main(String[] args) 
	{
		try 
		{
			//TODO parse config file
			
			//apro connessione TCP con il server
			Socket connection = new Socket("localhost",5000);
			
			//apro stream di comunicazione
			DataInputStream in = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			
			//avvio client con schermata di Login
			LoginController login = new LoginController(connection,in,out);
			login.setVisible(true);
			
		}
		catch(ConnectException e) {
			System.out.println("Server offline");
		}
		catch (UnknownHostException e) {
			System.out.println("Server non trovato");
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
