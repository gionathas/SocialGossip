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

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import client.view.RegisterForm;
import communication.MessageAnalyzer;
import communication.messages.Message;
import communication.messages.RegisterRequest;
import communication.messages.ResponseFailedMessage;
import communication.messages.ResponseMessage;

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
				//prendo dati inseriti nella form
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
						
						//leggo risposta del server
						String response = in.readUTF();
						
						//analizzo risposta del server
						analyzeResponse(response);
						
					}
					//se non riesco a connettermi al server
					catch(ConnectException e)
					{
						registerView.showErrorMessage("Servizio attualmente non disponibile","Errore");
						e.printStackTrace();

					}
					catch (UnknownHostException e) 
					{
						registerView.showErrorMessage("Server non trovato","Errore");
						e.printStackTrace();
					} 
					catch (IOException e) 
					{
						registerView.showErrorMessage("Errore nella richiesta di registrazione","Errore");
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
					registerView.showErrorMessage(FormInputChecker.REGISTER_ERROR_INFO_STRING,"Errore Form");
				}
			}
		});
		
		thread.start();
	}
	
	private void analyzeResponse(String JsonResponse)
	{
		try 
		{
			//parso json rappresentate risposta del server
			JSONObject response = MessageAnalyzer.parse(JsonResponse);
			
			//se non e' un messaggio di risposta
			if(MessageAnalyzer.getMessageType(response) != Message.Type.RESPONSE) 
			{
				registerView.showErrorMessage("Errore nel messaggio di risposta del server","Errore");
				return;
			}
			
			ResponseMessage.Type outcome = MessageAnalyzer.getResponseType(response);
			
			//tipo risposta non trovato
			if(outcome == null)
			{
				registerView.showErrorMessage("Errore nel messaggio di risposta del server","Errore");
				return;
			}
			
			//controllo esito della risposta ricevuta
			switch(outcome) 
			{
				//registrazione avvenuta
				case SUCCESS:
					registerView.showInfoMessage("Registrazione Avvenuta");
					
					//TODO far partire l'hub
					break;
				
				case FAIL:
					//analizzo l'errore riscontrato
					ResponseFailedMessage.Errors error = MessageAnalyzer.getResponseFailedErrorType(response);
					
					//errore non trovato
					if(error == null) {
						registerView.showErrorMessage("Errore nel messaggio di risposta del server","Errore");
						return;
					}
					
					//controllo tipi di errore che si possono riscontrare
					switch (error) 
					{
						//richiesta non valida
						case INVALID_REQUEST:
							registerView.showErrorMessage("Rcihiesta non valida","Errore");
							break;
							
						case USER_ALREADY_REGISTERED:
							registerView.showErrorMessage("Utente gia' registrato","Warning");
							break;
									
						//errore non trovato
						default:
							registerView.showErrorMessage("Errore nel messaggio di risposta del server","Errore");
							break;
					}
					
					break;
					
				default:
					registerView.showErrorMessage("Errore nel messaggio di risposta del server","Errore");
					break;
			}
		} 
		catch (ParseException e) 
		{
			registerView.showErrorMessage("Errore lettura risposta del server","Errore");
			e.printStackTrace();
		}
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
