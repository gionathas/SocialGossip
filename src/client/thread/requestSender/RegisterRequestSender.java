package client.thread.requestSender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;

import client.controller.Controller;
import client.controller.FormInputChecker;
import client.controller.HubController;
import communication.TCPMessages.request.RegisterRequest;

import communication.TCPMessages.response.ResponseFailedMessage.Errors;
import server.model.User;

/**
 * Thread che gestisce l'invio una richiesta di registrazione
 * @author Gionatha Sturba
 *
 */
public class RegisterRequestSender extends RequestSenderThread
{
	private String nickname;
	private char[] password;
	private char[] confirm_pass;
	private String language;
	
	/**
	 * Setta i parametri per la configurazione della richiesta di registrazione
	 * @param controller controller della finestra che richiede l'invio della richiesta
	 * @param nickname nickname dell'utente
	 * @param pass password dell'utente
	 * @param confirmPass password di conferma dell'utente
	 * @param lang linguaggio dell'utente
	 */
	public RegisterRequestSender(Controller controller,Socket connection,DataInputStream in,DataOutputStream out,String nickname,char[] pass,char[]confirmPass,String lang) 
	{
		super(controller,connection,in,out);
		
		if(nickname == null || pass == null || confirmPass == null || lang == null)
			throw new NullPointerException();
		
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
		HubController hub = new HubController(connection,in,out,nickname,amiciList,controller.getWindow().getLocation());
		
		//chiudo form di login
		controller.closeWindow();
		
		hub.setVisible(true);
	}
}
