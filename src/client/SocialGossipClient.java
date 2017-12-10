package client;

import client.view.Login;

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
			Login login = new Login();
			login.showWindow();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
