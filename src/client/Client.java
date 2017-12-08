package client;

import client.view.Login;

public class Client 
{
	/**
	 * Avvio client
	 */
	public static void main(String[] args) 
	{
		try 
		{
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
