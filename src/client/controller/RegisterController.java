package client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import client.view.RegisterForm;

public class RegisterController extends Controller
{
	private RegisterForm registerView;
	
	public RegisterController()
	{
		registerView = new RegisterForm();
		setWindow(registerView);
		initListeners();
	}
	
	protected void initListeners() 
	{
		//al click su torna a login
		registerView.getBtnTornaALogin().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				startLoginForm();
			}
		});
	}
	
	/**
	 * Chiude schermata di registrazione e apre una di Login
	 */
	private void startLoginForm()
	{
		this.setVisible(false);
		this.close();
		
		//avvio schermata di login
		LoginController login = new LoginController();
		login.setVisible(true);
	}
}
