package client.thread;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import client.controller.Controller;
import client.controller.FormInputChecker;
import client.controller.HubController;
import client.controller.LoginController;
import communication.TCPMessages.Message;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.request.RegisterRequest;
import communication.TCPMessages.response.ResponseFailedMessage;
import communication.TCPMessages.response.ResponseMessage;
import communication.TCPMessages.response.ResponseFailedMessage.Errors;
import server.model.User;

/**
 * Thread che si occupa di gestire la richiesta di registrazione lato client
 * @author gio
 *
 */
public class RegisterRequestSender extends RequestSenderThread
{
	private String nickname;
	private char[] password;
	private char[] confirm_pass;
	private String language;
	
	public RegisterRequestSender(Controller controller,String nickname,char[] pass,char[]confirmPass,String lang) 
	{
		super(controller);
		this.nickname = nickname;
		this.password = pass;
		this. confirm_pass = confirmPass;
		this.language = lang;
	}

	@Override
	protected void init() {
		//se i dati inseriti nella form non sono validi
		if(!FormInputChecker.checkRegisterInput(nickname,password,confirm_pass))
		{
			controller.showErrorMessage(FormInputChecker.REGISTER_ERROR_INFO_STRING,"Errore Form");
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
		controller.showErrorMessage("Servizio attualmente non disponibile","Errore");			
	}

	@Override
	protected void UnKwownHostErrorHandler() {
		controller.showErrorMessage("Server non trovato","Errore");
	}

	@Override
	protected void IOErrorHandler() {
		controller.showErrorMessage("Errore nella richiesta di registrazione","Errore");
	}
	
	@Override
	protected void invalidResponseHandler() {
		controller.showErrorMessage("Errore nel messaggio di risposta del server","Errore");
	}

	@Override
	protected void invalidResponseErrorTypeHandler() {
		controller.showErrorMessage("Errore nel messaggio di risposta di errore del server","Errore");
	}
	
	@Override
	protected void parseErrorHandler() {
		controller.showErrorMessage("Errore lettura risposta del server","Errore");			
	}

	@Override
	protected void unexpectedMessageHandler() {
		controller.showErrorMessage("Errore nel messaggio di risposta del server","Errore");			
	}

	@Override
	protected void failedResponseHandler(Errors error) 
	{
		//controllo tipi di errore che si possono riscontrare
		switch (error) 
		{
			//richiesta non valida
			case INVALID_REQUEST:
				controller.showErrorMessage("Rcihiesta non valida","Errore");
				break;
				
			case USER_ALREADY_REGISTERED:
				controller.showErrorMessage("Utente gia' registrato","Warning");
				break;
						
			//errore non trovato
			default:
				controller.showErrorMessage("Errore nel messaggio di risposta del server","Errore");
				break;
		}
	}

	@Override
	protected void successResponseHandler() {
		controller.showInfoMessage("Registrazione Avvenuta","Benvenuto",true);
		startHubView(nickname,null);			
	}
	
	private void startHubView(String nickname,List<User> amiciList) 
	{
		HubController hub = new HubController(nickname,amiciList,controller.getWindow().getLocation());
		
		//chiudo form di login
		controller.setVisible(false);
		controller.close();
		
		hub.setVisible(true);
	}
}
