package client.controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import client.thread.LoginRequestSender;
import client.view.LoginForm;


/**
 * Controller del form di login
 * @author Gionatha Sturba
 *
 */
public class LoginController extends Controller
{
	private LoginForm loginView;
	private Controller controller = this;
	
	public LoginController(Socket connection,DataInputStream in,DataOutputStream out)
	{
		super( connection, in, out);
		//creo form di login
		this.loginView = new LoginForm();
		this.window = loginView.getFrame();

		//registro gli action listener
		initListeners();
	}
	
	public LoginController(Socket connection,DataInputStream in,DataOutputStream out,Point location) {
		super(connection,in,out);
		//creo form di login
		this.loginView = new LoginForm();
		this.window = loginView.getFrame();
		window.setLocation(location);

		//registro gli action listener
		initListeners();
	}
	
	@Override
	protected void initListeners()
	{
		super.initListeners();
		
		//al click sul bottone login
		loginView.getBtnLogin().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				//avvio thread che gestira la richiesta di Login
				new LoginRequestSender(controller, connection, in, out,loginView.getUsernameField().getText(),
						loginView.getPasswordField().getPassword()).start();
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
		RegisterController register = new RegisterController(connection,in,out,window.getLocation());
		
		//chiudo form di login
		controller.closeWindow();
		
		//mostro form di registrazione
		register.setVisible(true);
	}
}
