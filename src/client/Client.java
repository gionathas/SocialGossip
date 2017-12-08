package client;

import java.awt.EventQueue;

public class Client 
{
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		try 
		{
			//avvio con schermata di Login
			Login login = new Login();
			login.showWindow();
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
