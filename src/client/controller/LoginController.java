package client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;

import client.view.LoginForm;
import client.view.RegisterForm;
import communication.messages.LoginRequest;

/**
 * Controller del form di login
 * @author gio
 *
 */
public class LoginController extends Controller
{
	private LoginForm loginView;
	private AtomicBoolean canSendLogin;
	
	public LoginController()
	{
		super();
		//creo form di login
		this.loginView = new LoginForm();
		this.window = loginView.getFrame();
		
		//variabile di supporto
		canSendLogin = new AtomicBoolean(true);

		//registro gli action listener
		initListeners();
	}
	
	protected void initListeners()
	{
		//al click sul bottone login
		loginView.getBtnLogin().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				//se non ho gia' mandato una richiesta di login
				if(canSendLogin.get() == true)
				{
					canSendLogin.set(false);
					sendLoginRequest();
				}
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
	 * Effettua la richiesta di Login al server
	 */
	private void sendLoginRequest()
	{		
		/* Un thread si occupera' di gestire la comunicazione con il server */
		Thread thread = new Thread(new Runnable() {
			public void run() 
			{
				String nickname = loginView.getUsernameField().getText();
				char[] password = loginView.getPasswordField().getPassword();
				
				//prima di inviare la richiesta controllo i dati inseriti
				if(FormInputChecker.checkLoginInput(nickname,password))
				{
					Socket connection = null;
					DataInputStream in = null;
					DataOutputStream out = null;
					
					try 
					{
						loginView.getAttesa().setVisible(true);
						
						//apro connessione con server e creo stream per lettura scrittura
						connection = new Socket("localhost",5000);
						in = new DataInputStream(connection.getInputStream());
						out = new DataOutputStream(connection.getOutputStream());
												
						//creo il messaggio di richiesta di login
						LoginRequest request = new LoginRequest(nickname,new String(password));

						//invio la richiesta di login al server
						out.writeUTF(request.getJsonMessage());
						
						//attendo risposta server
						String response = in.readUTF();
						
						//mostro risposta server
						loginView.showMessage(response);
												
					}
					//se non riesco a connettermi al server
					catch(ConnectException e)
					{
						loginView.showMessage("Servizio attualmente non disponibile");
						e.printStackTrace();

					}
					//problema connessione al server
					catch(UnknownHostException e)
					{
						loginView.showMessage("Server non trovato");
						e.printStackTrace();

					}
					catch (IOException e) 
					{
						loginView.showMessage("Errore nella richiesta di login");
						e.printStackTrace();
					}
					
					finally 
					{
						try
						{
							//chiud connessione se aperta
							if(connection != null){
								connection.close();
							}
							
							//chiudo stream input se aperto
							if(in != null) {
								in.close();
							}
							
							//chiudo stream output se aperto
							if(out != null) {
								out.close();
							}
						}
						catch(IOException e)
						{
							//TODO
							e.printStackTrace();
						}
						
						
						canSendLogin.set(true);
						loginView.getAttesa().setVisible(false);
					}

				}
				//input form non corretta
				else 
				{
					canSendLogin.set(true);
					loginView.showMessage(FormInputChecker.LOGIN_ERROR_INFO_STRING);
				}
			}
				
		});
		
		//avvio thread comunicazione
		thread.start();
	}
	
	/**
	 * Fa partire il form di registrazione
	 */
	private void startRegisterForm() {
		RegisterController register = new RegisterController();
		
		//chiudo form di login
		this.setVisible(false);
		this.close();
		
		//mostro form di registrazione
		register.setVisible(true);
	}
}
