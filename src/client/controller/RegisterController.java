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
import java.util.zip.CheckedInputStream;

import client.view.RegisterForm;
import communication.messages.RegisterRequest;

public class RegisterController extends Controller
{
	private RegisterForm registerView;
	private AtomicBoolean canSendRegister;

	
	public RegisterController()
	{
		registerView = new RegisterForm();
		setWindow(registerView);
		
		canSendRegister = new AtomicBoolean(true);
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
				//se non ho gia' mandato una richiesta di login
				if(canSendRegister.get() == true)
				{
					canSendRegister.set(false);
					sendRegisterRequest();
				}
				
			}
		});
	}
	
	private void sendRegisterRequest()
	{
		Thread thread = new Thread(new Runnable() {
			
			public void run() 
			{
				String nickname = registerView.getUsernameField().getText();
				char password[] = registerView.getPasswordField().getPassword();
				char confirm_pass[] = registerView.getConfirmPasswordField().getPassword();
				String language = (String) registerView.getComboBox().getSelectedItem();
				
				//controllo dati inseriti
				if(FormInputChecker.checkRegisterInput(nickname,password,confirm_pass))
				{
					//se i dati inseriti vanno bene,allora apro una connessione con il server per inviare la richiesta
					Socket connection = null;
					DataOutputStream out = null;
					DataInputStream in = null;
					
					try
					{
						//apro connessione con server e creo stream per lettura scrittura
						connection = new Socket("localhost",5000);
						in = new DataInputStream(connection.getInputStream());
						out = new DataOutputStream(connection.getOutputStream());
						
						//creo messaggio di richiesta registrazione
						RegisterRequest request = new RegisterRequest(nickname,new String(password),language);
						
						//invio richiesta
						out.writeUTF(request.getJsonMessage());
						
						String response = in.readUTF();
						
						registerView.showMessage(response);
						
					}
					//se non riesco a connettermi al server
					catch(ConnectException e)
					{
						registerView.showMessage("Servizio attualmente non disponibile");
						e.printStackTrace();

					}
					catch (UnknownHostException e) 
					{
						registerView.showMessage("Server non trovato");
						e.printStackTrace();
					} 
					catch (IOException e) 
					{
						registerView.showMessage("Errore nella richiesta di registrazione");
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
						
						
						canSendRegister.set(true);
					}
				}
				//input form non corretta
				else {
					canSendRegister.set(true);
					registerView.showMessage(FormInputChecker.REGISTER_ERROR_INFO_STRING);;
				}
			}
		});
		
		thread.start();
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
