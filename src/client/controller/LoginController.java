package client.controller;

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

import client.thread.RequestSenderThread;
import client.view.LoginForm;
import client.view.RegisterForm;
import communication.MessageAnalyzer;
import communication.messages.LoginRequest;
import communication.messages.Message;
import communication.messages.ResponseFailedMessage;
import communication.messages.ResponseMessage;
import server.model.User;

/**
 * Controller del form di login
 * @author gio
 *
 */
public class LoginController extends Controller
{
	private LoginForm loginView;
	
	public LoginController()
	{
		super();
		//creo form di login
		this.loginView = new LoginForm();
		this.window = loginView.getFrame();

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
				new LoginRequestSender().start();
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
	 * Thread che si occupa di inviare e gestire una richiesta di Login
	 * @author gio
	 *
	 */
	private class LoginRequestSender extends RequestSenderThread
	{
		private String nickname;
		private char[] password;
		
		public LoginRequestSender() 
		{
			nickname = loginView.getUsernameField().getText();
			password = loginView.getPasswordField().getPassword();
		}
		
		@Override
		protected void init() 
		{
			//dati nella form errati
			if(!FormInputChecker.checkLoginInput(nickname,password))
			{
				init = false;
				showErrorMessage(FormInputChecker.LOGIN_ERROR_INFO_STRING,"Form Errata");
				
			}
			else {
				//posso procedere
				init = true;
			}
		}

		@Override
		/**
		 * Creo messaggio di richiesta di Login
		 */
		protected void createRequest() 
		{
			System.out.println(nickname);
			request = new LoginRequest(nickname,new String(password));
		}

		@Override
		protected void ConnectErrorHandler() 
		{
			showErrorMessage("Servizio attualmente non disponibile","Errore");
		}

		@Override
		protected void UnKwownHostErrorHandler() {
			showErrorMessage("Server non trovato","Errore");			
		}

		@Override
		protected void IOErrorHandler() {
			showErrorMessage("Errore nella richiesta di login","Errore");
		}

		protected void analyzeResponse(String JsonResponse)
		{
			try
			{
				//parso json rappresentate risposta del server
				JSONObject response = MessageAnalyzer.parse(JsonResponse);
				
				//se non e' un messaggio di risposta
				if(MessageAnalyzer.getMessageType(response) != Message.Type.RESPONSE) 
				{
					showErrorMessage("Errore nel messaggio di risposta del server","Errore");
					return;
				}
				
				//prendo tipo esito della risposta
				ResponseMessage.Type outcome = MessageAnalyzer.getResponseType(response);
				
				//tipo risposta non trovato
				if(outcome == null)
				{
					showErrorMessage("Errore nel messaggio di risposta del server","Errore");
					return;
				}
				
				//controllo esito della risposta ricevuta
				switch(outcome) 
				{
					//registrazione avvenuta
					case SUCCESS:
						
						//TODO prendere lista delle chatroom
						List<User> amiciList = MessageAnalyzer.getListaAmici(response);
						
						//lista degli amici dell'utente loggato, non trovata
						if(amiciList == null) {
							showErrorMessage("Errore nel messaggio di risposta del server","Errore");
							return;
						}
						
						startHubView(nickname,amiciList);
						break;
					
					case FAIL:
						//analizzo l'errore riscontrato
						ResponseFailedMessage.Errors error = MessageAnalyzer.getResponseFailedErrorType(response);
						
						//errore non trovato
						if(error == null) {
							showErrorMessage("Errore nel messaggio di risposta del server","Errore");
							return;
						}
						
						//controllo tipi di errore che si possono riscontrare
						switch (error) 
						{
							//richiesta non valida
							case INVALID_REQUEST:
								showErrorMessage("Rcihiesta non valida","Errore");
								break;
								
							case PASSWORD_MISMATCH:
								showErrorMessage("Password errata","Warning");
								break;
							
							case USER_NOT_FOUND:
								showErrorMessage("Utente non trovato","Warning");
								break;
							
							case USER_INVALID_STATUS:
								showErrorMessage("Sei gia' online con un altro client","Warning");
								break;
										
							//errore non trovato
							default:
								showErrorMessage("Errore nel messaggio di risposta del server","Errore");
								break;
						}
						
						break;
						
					default:
						showErrorMessage("Errore nel messaggio di risposta del server","Errore");
						break;
				}
			}
			catch (ParseException e) 
			{
				showErrorMessage("Errore lettura risposta del server","Errore");
				e.printStackTrace();
			}
		}
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
	
	private void startHubView(String nickname,List<User>amiciList) {
		HubController hub = new HubController(nickname,amiciList);
		
		//chiudo form di login
		this.setVisible(false);
		this.close();
		
		hub.setVisible(true);
	}
}
