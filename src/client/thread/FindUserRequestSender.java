package client.thread;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import client.controller.Controller;
import client.controller.FormInputChecker;
import communication.TCPMessages.request.FindUserRequest;
import communication.TCPMessages.response.ResponseFailedMessage.Errors;
import server.model.User;

/**
 * Thread che si occupa della gestione della richiesta di ricerca utente
 * @author gio
 *
 */
public class FindUserRequestSender extends RequestSenderThread
{	
	private String nicknameUserToFind;
	private String nicknameUser;
	private DefaultListModel<User> friendList;
	private static final int YES = 0;
	
	public FindUserRequestSender(Controller controller,String nicknameUser,String nicknameUserToFind,DefaultListModel<User> friendList)
	{
		super(controller);
		this.nicknameUserToFind = nicknameUserToFind;
		this.nicknameUser = nicknameUser;
		this.friendList = friendList;
	}
	
	@Override
	protected void init() 
	{
		//se il nickname inserito non e' valido
		if(!FormInputChecker.checkNickname(nicknameUserToFind)) {
			controller.showErrorMessage("Nome inserito non valido","Errore Form");
		}
		//altrimenti procedo alla richiesta
		else {
			init = true;
		}
		
	}

	@Override
	protected void createRequest() {
		request = new FindUserRequest(nicknameUser,nicknameUserToFind);
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
		controller.showErrorMessage("Errore nella richiesta di ricerca di un utente","Errore");			
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
		switch (error) 
		{
			case INVALID_REQUEST:
				controller.showErrorMessage("Richiesta non valida","Errore");
				break;
			
			case SENDER_USER_INVALID_STATUS:
				controller.showErrorMessage("Non sei online","Errore");
				break;
			
			case SENDER_USER_NOT_FOUND:
				controller.showErrorMessage("Non risulti piu' essere registrato","Errore");
				break;
			
			case RECEIVER_USER_NOT_FOUND:
				controller.showErrorMessage("Utente non trovato","Ops");
				break; 
			
			case SAME_USERS:
				controller.showInfoMessage("Utente trovato","",true);
				break;
			
			default:
				controller.showErrorMessage("Errore nel messaggio di risposta di errore del server","Errore");
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
		//TODO richiedo se vuole diventare amico
		//showInfoMessage("Utente trovato");
		
		//richiedo se vuole diventare amico
		int choice = JOptionPane.showConfirmDialog(controller.getWindow(),"Utente "+nicknameUserToFind+" Trovato! Vuoi diventare suo amico?");
		
		//se si e' scelto di diventare suo amico
		if(choice == YES)
		{
			//faccio partire il thread che gestira' la richiesta di amicizia
			new FriendshipRequestSender(controller,nicknameUser,nicknameUserToFind,friendList).start();
		}

		
	}

}
