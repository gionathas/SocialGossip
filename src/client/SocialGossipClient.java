package client;

import client.controller.LoginController;
import client.controller.*;

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
			
			//avvio client con schermata di Login
			LoginController login = new LoginController();
			login.setVisible(true);
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
