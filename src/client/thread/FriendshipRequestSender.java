package client.thread;

import javax.swing.DefaultListModel;

import client.controller.Controller;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.request.FriendshipRequest;
import communication.TCPMessages.response.ResponseFailedMessage.Errors;
import server.model.User;

/**
 * Thread che si occupa di gestire la richiesta di amicizia con un altro utente
 * @author gio
 *
 */
public class FriendshipRequestSender extends RequestSenderThread
{
	private String nicknameReceiverFriend;
	private String nicknameSender;
	private DefaultListModel<User> friendList;
	
	/**
	 * Inizializza i parametri della richiesta di amicizia
	 * @param controller controller della finestra 
	 * @param nicknameSender nickname utente che invia richiesta
	 * @param nicknameReceiverFriend nickname utente che riceve richiesta
	 * @param friendList modello della lista degli amici dell'utente richiedente
	 */
	public FriendshipRequestSender(Controller controller,String nicknameSender,String nicknameReceiverFriend,DefaultListModel<User> friendList) 
	{
		super(controller);
		
		if(controller == null || nicknameSender == null || nicknameReceiverFriend == null || friendList == null)
			throw new NullPointerException();
		
		this.nicknameReceiverFriend = nicknameReceiverFriend;
		this.nicknameSender = nicknameSender;
		this.friendList = friendList;
	}

	@Override
	protected void init() {
		init = true;
	}

	@Override
	protected void createRequest() 
	{
		request = new FriendshipRequest(nicknameSender,nicknameReceiverFriend);
	}

	protected void ConnectErrorHandler() {
		controller.showErrorMessage("Servizio attualmente non disponibile","Errore");
	}

	@Override
	protected void UnKwownHostErrorHandler() {
		controller.showErrorMessage("Server non trovato","Errore");
	}

	@Override
	protected void IOErrorHandler() {
		controller.showErrorMessage("Errore nella richiesta di amicizia","Errore");			
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
	protected void failedResponseHandler(Errors error) {
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
				controller.showInfoMessage("Non puoi diventare amico di te stesso!","Ops",true);
				break;
			
			case ALREADY_FRIEND:
				controller.showInfoMessage("Sei gia' amico di "+nicknameReceiverFriend,"Ops",true);
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
		//prendo stato dell'utente diventato amico
		boolean status = MessageAnalyzer.getStatusReceiverOfFriendShipRequest(response);
		controller.showInfoMessage("Ora sei amico di "+nicknameReceiverFriend,"Notifica Amicizia",false);
		
		friendList.addElement(new User(nicknameReceiverFriend,status));
	}
	
}


