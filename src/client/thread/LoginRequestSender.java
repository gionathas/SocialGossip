package client.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;

import client.controller.Controller;
import client.controller.FormInputChecker;
import client.controller.HubController;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.request.LoginRequest;
import communication.TCPMessages.response.ResponseFailedMessage.Errors;
import server.model.User;

/**
 * Thread che gestisce la richiesta di Login
 * @author Gionatha Sturba
 *
 */
public class LoginRequestSender extends RequestSenderThread
{
	private String nickname;
	private char[] password;
	
	/**
	 * Inizializza i parametri per inviare la richiesta
	 * @param controller controller della finestra che richiede l'invio
	 * @param nickname nickname scelto dall'utente
	 * @param password password scelta dall'utente
	 */
	public LoginRequestSender(Controller controller,Socket connection,DataInputStream in,DataOutputStream out,String nickname,char[] password) 
	{
		super(controller,connection,in,out);
		
		if(controller == null || nickname == null || password == null)
			throw new NullPointerException();
		
		this.nickname = nickname;
		this.password = password;
	}
	
	@Override
	protected void init() 
	{
		//se i dati nella form sono errati
		if(!FormInputChecker.checkLoginInput(nickname,password))
		{
			init = false;
			controller.showErrorMessage(FormInputChecker.LOGIN_ERROR_INFO_STRING,"Form Errata");
			
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
		request = new LoginRequest(nickname,new String(password));
	}

	@Override
	protected void IOErrorHandler() {
		controller.showErrorMessage("Errore nella richiesta di login","Errore");
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
	protected void failedResponseHandler(Errors error) 
	{
		//controllo tipi di errore che si possono riscontrare
		switch (error) 
		{
			//richiesta non valida
			case INVALID_REQUEST:
				controller.showErrorMessage("Rcihiesta non valida","Errore");
				break;
				
			case PASSWORD_MISMATCH:
				controller.showErrorMessage("Password errata","Warning");
				break;
			
			case SENDER_USER_NOT_FOUND:
				controller.showErrorMessage("Utente non trovato","Warning");
				break;
			
			case SENDER_USER_INVALID_STATUS:
				controller.showErrorMessage("Sei gia' online con un altro client","Warning");
				break;
						
			//errore non trovato
			default:
				controller.showErrorMessage("Errore nel messaggio di risposta del server","Errore");
				break;
		}
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
	protected void successResponseHandler() 
	{
		List<User> amiciList = MessageAnalyzer.getListaAmici(response);
		
		//lista degli amici dell'utente loggato, non trovata
		if(amiciList == null) {
			controller.showErrorMessage("Errore nel messaggio di risposta del server","Errore");
			return;
		}
		
		//TODO prendere lista delle chatroom
		
		//faccio partire l'hub 
		startHubView(nickname,amiciList);
	}
	
	private void startHubView(String nickname,List<User>amiciList) {
		HubController hub = new HubController(connection,in,out,nickname,amiciList,controller.getWindow().getLocation());
		
		//chiudo form di login
		controller.closeWindow();
		
		hub.setVisible(true);
	}

}
