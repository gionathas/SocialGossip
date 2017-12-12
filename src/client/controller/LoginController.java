package client.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import client.view.LoginForm;
import client.view.RegisterForm;
import communication.messages.LoginRequest;

/**
 * Controller del form di login
 * @author gio
 *
 */
public class LoginController 
{
	private LoginForm loginView; 
	
	public LoginController()
	{
		this.loginView = new LoginForm(this);
	}
	
	public void setVisibleLoginForm(boolean visible) 
	{
		loginView.setVisible(visible);
	}
	
	/**
	 * Effettua la richiesta di Login al server
	 */
	public void sendLoginRequest()
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
					
					try 
					{
						loginView.getAttesa().setVisible(true);
						
						//apro connessione con server e creo stream per lettura scrittura
						connection = new Socket("localhost",5000);
						DataInputStream in = new DataInputStream(connection.getInputStream());
						DataOutputStream out = new DataOutputStream(connection.getOutputStream());
												
						//creo il messaggio di richiesta di login
						LoginRequest request = new LoginRequest(nickname,new String(password));

						//invio la richiesta di login al server
						out.writeUTF(request.getJsonMessage());
						
						//attendo risposta server
						String response = in.readUTF();
						
						//mostro risposta server
						loginView.showMessage(response);
						
						in.close();
						out.close();
												
					}
					//se non riesco a connettermi al server
					catch(ConnectException e)
					{
						loginView.showMessage("Servizio attualemente non disponibile");
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
						if(connection != null)
						{
							try {
								connection.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						loginView.getCanSendLogin().set(true);
						loginView.getAttesa().setVisible(false);
					}

				}
				//input form non corretta
				else 
				{
					loginView.getCanSendLogin().set(true);
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
	public void startRegisterForm() {
		RegisterController register = new RegisterController();
		
		//chiudo form di login
		loginView.setVisible(false);
		loginView.closeWindow();
		
		//mostro form di registrazione
		register.setVisibleRegisterForm(true);
	}
}
