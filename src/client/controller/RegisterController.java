package client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import client.thread.RequestSenderThread;
import client.view.RegisterForm;
import communication.MessageAnalyzer;
import communication.messages.Message;
import communication.messages.RegisterRequest;
import communication.messages.ResponseFailedMessage;
import communication.messages.ResponseMessage;
import server.model.User;

public class RegisterController extends Controller
{
	private RegisterForm registerView;
	
	public RegisterController()
	{
		registerView = new RegisterForm();
		setWindow(registerView);
		
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
				new RegisterRequestSender().start();
			}
		});
	}
	
	/**
	 * Thread che si occupa di gestira l'intera richiesta di registrazione
	 * @author gio
	 *
	 */
	private class RegisterRequestSender extends RequestSenderThread
	{
		private String nickname;
		private char[] password;
		private char[] confirm_pass;
		private String language;
		
		public RegisterRequestSender() 
		{
			this.nickname = registerView.getUsernameField().getText();
			this.password = registerView.getPasswordField().getPassword();
			this. confirm_pass = registerView.getConfirmPasswordField().getPassword();
			this.language = (String) registerView.getComboBox().getSelectedItem();
		}

		@Override
		protected void init() {
			//se i dati inseriti nella form non sono validi
			if(!FormInputChecker.checkRegisterInput(nickname,password,confirm_pass))
			{
				showErrorMessage(FormInputChecker.REGISTER_ERROR_INFO_STRING,"Errore Form");
			}
			//altrimenti possiamo procedere alla richiesta
			else {
				init = true;
			}
		}

		@Override
		protected void createRequest() 
		{
			request = new RegisterRequest(nickname,new String(password),language);
		}

		@Override
		protected void ConnectErrorHandler() {
			showErrorMessage("Servizio attualmente non disponibile","Errore");			
		}

		@Override
		protected void UnKwownHostErrorHandler() {
			showErrorMessage("Server non trovato","Errore");
		}

		@Override
		protected void IOErrorHandler() {
			showErrorMessage("Errore nella richiesta di registrazione","Errore");
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
						showInfoMessage("Registrazione Avvenuta");
						startHubView(nickname,null);
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
								
							case USER_ALREADY_REGISTERED:
								showErrorMessage("Utente gia' registrato","Warning");
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
	
	private void startHubView(String nickname,List<User> amiciList) 
	{
		HubController hub = new HubController(nickname,amiciList);
		
		//chiudo form di login
		this.setVisible(false);
		this.close();
		
		hub.setVisible(true);
	}
}
