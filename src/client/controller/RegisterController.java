package client.controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import client.thread.RegisterRequestSender;
import client.thread.RequestSenderThread;
import client.view.RegisterForm;
import communication.TCPMessages.Message;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.request.RegisterRequest;
import communication.TCPMessages.response.ResponseFailedMessage;
import communication.TCPMessages.response.ResponseMessage;
import communication.TCPMessages.response.ResponseFailedMessage.Errors;
import server.model.User;

public class RegisterController extends Controller
{
	private RegisterForm registerView;
	private Controller controller = this;
	
	public RegisterController()
	{
		registerView = new RegisterForm();
		setWindow(registerView);
		
		initListeners();
		
	}
	
	public RegisterController(Point location)
	{
		registerView = new RegisterForm();
		setWindow(registerView);
		window.setLocation(location);
		
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
		
		//al click su invia registrazione
		registerView.getBtnInvia().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				//faccio partire il thread che si occupera' delle richiesta di registrazione
				new RegisterRequestSender(controller,registerView.getUsernameField().getText(),registerView.getPasswordField().getPassword(),
						registerView.getConfirmPasswordField().getPassword(),(String)registerView.getComboBox().getSelectedItem()).start();
			}
		});
	}
	
	/**
	 * Chiude schermata di registrazione e apre una di Login
	 */
	private void startLoginForm()
	{
		window.setVisible(false);
		window.dispose();
		
		//avvio schermata di login
		LoginController login = new LoginController(window.getLocation());
		login.setVisible(true);
	}
}
