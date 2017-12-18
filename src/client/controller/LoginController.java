package client.controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import client.thread.LoginRequestSender;
import client.thread.RequestSenderThread;
import client.view.LoginForm;
import client.view.RegisterForm;
import communication.TCPMessages.Message;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.request.LoginRequest;
import communication.TCPMessages.response.ResponseFailedMessage;
import communication.TCPMessages.response.ResponseMessage;
import communication.TCPMessages.response.ResponseFailedMessage.Errors;
import server.model.User;

/**
 * Controller del form di login
 * @author gio
 *
 */
public class LoginController extends Controller
{
	private LoginForm loginView;
	private Controller controller = this;
	
	public LoginController()
	{
		super();
		//creo form di login
		this.loginView = new LoginForm();
		this.window = loginView.getFrame();

		//registro gli action listener
		initListeners();
	}
	
	public LoginController(Point location) {
		super();
		//creo form di login
		this.loginView = new LoginForm();
		this.window = loginView.getFrame();
		window.setLocation(location);

		//registro gli action listener
		initListeners();
	}
	
	protected void initListeners()
	{
		//al click sul bottone login
		loginView.getBtnLogin().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				//avvio thread che gestira la richiesta di Login
				new LoginRequestSender(controller,loginView.getUsernameField().getText(),loginView.getPasswordField().getPassword()).start();
			}
		});
		
		//al click sul bottone registrati
		loginView.getRegisterButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				startRegisterForm();
			}
		});
	}

	/**
	 * Fa partire il form di registrazione
	 */
	private void startRegisterForm() {
		RegisterController register = new RegisterController(window.getLocation());
		
		//chiudo form di login
		window.setVisible(false);
		window.dispose();
		
		//mostro form di registrazione
		register.setVisible(true);
	}
}
