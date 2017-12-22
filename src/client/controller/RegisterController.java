package client.controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import client.thread.RegisterRequestSender;
import client.view.RegisterForm;


/**
 * Controller del form di registrazione
 * @author Gionatha Sturba
 *
 */
public class RegisterController extends Controller
{
	private RegisterForm registerView;
	private Controller controller = this;
	
	public RegisterController(Socket connection,DataInputStream in,DataOutputStream out,Point location)
	{
		super(connection,in,out);

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
				new RegisterRequestSender(controller,connection, in, out, registerView.getUsernameField().getText(),registerView.getPasswordField().getPassword(),
						registerView.getConfirmPasswordField().getPassword(),(String)registerView.getComboBox().getSelectedItem()).start();
			}
		});
	}
	
	/**
	 * Chiude schermata di registrazione e apre una di Login
	 */
	private void startLoginForm()
	{
		closeWindow();
		
		//avvio schermata di login
		LoginController login = new LoginController(connection,in,out,window.getLocation());
		login.setVisible(true);
	}
}
