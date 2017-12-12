package client.controller;

import client.view.RegisterForm;

public class RegisterController 
{
	private RegisterForm registerView;
	
	public RegisterController()
	{
		registerView = new RegisterForm(this);
	}
	
	public void setVisibleRegisterForm(boolean visible) 
	{
		registerView.getFrame().setVisible(visible);
	}
	
	public void startLoginForm()
	{
		setVisibleRegisterForm(false);
		registerView.closeWindow();
		LoginController login = new LoginController();
		login.setVisibleLoginForm(true);
	}
}
