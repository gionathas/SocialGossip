package client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import client.thread.RequestSenderThread;
import client.view.Hub;
import communication.MessageAnalyzer;
import communication.messages.FindUserRequest;
import communication.messages.LogoutRequest;
import communication.messages.Message;
import communication.messages.ResponseFailedMessage;
import communication.messages.ResponseFailedMessage.Errors;
import communication.messages.ResponseMessage;
import server.model.User;

public class HubController extends Controller
{
	private Hub hubView;
	private User user;
	private List<User> amiciList;
	
	public HubController(String nickname,List<User> amiciList) 
	{
		hubView = new Hub();
		setWindow(hubView);
		
		initComponents(nickname,amiciList);
		initListeners();
	}
	
	private void initComponents(String nickname,List<User> amiciList)
	{	
		user = new User(nickname);
		hubView.setWelcomeText("Loggato come "+nickname.toUpperCase());
		
		//se mi e' stata passata una lista di amici e di chatRoom
		if(amiciList != null) {
			//aggiorno lista amici
			for (User user : amiciList) {
				hubView.getModelUserFriendList().addElement(user);
			}
			
			//TODO aggiornare lista chatRoom
		}
	}
	
	@Override
	protected void initListeners() 
	{
		//richiesta logout alla chiusura della finestra
		hubView.addWindowListener(new WindowAdapter()
	        {
	            @Override
	            public void windowClosing(WindowEvent e)
	            {
	                new LogoutRequestSender().start();;
	            }
	        });
		
		//al click sul bottone ESCI
		hubView.getBtnLogout().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				//avvio procedura di logout
                new LogoutRequestSender().start();
			}
		});
		
		//al click sul bottone CERCA
		hubView.getBtnCerca().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//avvio thread che si avvia la richiesta di ricerca utente
				new FindUserRequestSender().start();
			}
		});
	}
	
	/**
	 * Thread che si occupa della gestione della richiesta di ricerca di un utente
	 * @author gio
	 *
	 */
	private class FindUserRequestSender extends RequestSenderThread
	{
		private String nicknameUserToFind;
		
		public FindUserRequestSender() 
		{
			nicknameUserToFind = hubView.getTextField().getText();
		}
		
		@Override
		protected void init() 
		{
			//se il nickname inserito non e' valido
			if(!FormInputChecker.checkNickname(nicknameUserToFind)) {
				showErrorMessage("Nome inserito non valido","Errore Form");
			}
			//altrimenti procedo alla richiesta
			else {
				init = true;
			}
			
		}

		@Override
		protected void createRequest() {
			request = new FindUserRequest(user.getNickname(),nicknameUserToFind);
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
			showErrorMessage("Errore nella richiesta di ricerca di un utente","Errore");			
		}

		@Override
		protected void invalidResponseHandler() {
			showErrorMessage("Errore nel messaggio di risposta del server","Errore");
		}

		@Override
		protected void invalidResponseErrorTypeHandler() {
			showErrorMessage("Errore nel messaggio di risposta di errore del server","Errore");
		}

		@Override
		protected void failedResponseHandler(Errors error) 
		{
			switch (error) 
			{
				case INVALID_REQUEST:
					showErrorMessage("Richiesta non valida","Errore");
					break;
				
				case SENDER_USER_INVALID_STATUS:
					showErrorMessage("Non sei online","Errore");
					break;
				
				case SENDER_USER_NOT_FOUND:
					showErrorMessage("Non risulti piu' essere registrato","Errore");
					break;
				
				case RECEIVER_USER_NOT_FOUND:
					showErrorMessage("Utente non trovato","Ops");
					break; 
				
				case SAME_USERS:
					showInfoMessage("Utente trovato");
					break;
				
				default:
					showErrorMessage("Errore nel messaggio di risposta di errore del server","Errore");
					break;
			}
		}

		@Override
		protected void parseErrorHandler() {
			showErrorMessage("Errore lettura risposta del server","Errore");			
		}

		@Override
		protected void unexpectedMessageHandler() {
			showErrorMessage("Errore nel messaggio di risposta del server","Errore");
		}
		
		@Override
		protected void successResponseHandler() 
		{
			//TODO richiedo se vuole diventare amico
			showInfoMessage("Utente trovato");
			
		}
		
	}
	
	/**
	 * Thread che si occupa di gestire la richiesta di logout
	 * @author gio
	 *
	 */
	private class LogoutRequestSender extends RequestSenderThread
	{

		@Override
		protected void init() 
		{
			//mostro finestra che chiede se si vuole uscire veramente
			int choice = JOptionPane.showConfirmDialog(hubView,"Sei sicuro di voler uscire?");
			
			//se la scelta e' diversa da Si,mi fermo
			if(choice != 0) {
				init = false;
			}
			//se la scelta e' stata si
			else {
				init = true;
			}
		}

		@Override
		protected void createRequest() 
		{
			request = new LogoutRequest(user.getNickname());
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
			showErrorMessage("Errore nella richiesta di logout","Errore");
		}

		@Override
		protected void invalidResponseHandler() {
			showErrorMessage("Errore nel messaggio di risposta del server","Errore");
		}

		@Override
		protected void invalidResponseErrorTypeHandler() {
			showErrorMessage("Errore nel messaggio di risposta di errore del server","Errore");
		}

		@Override
		protected void failedResponseHandler(ResponseFailedMessage.Errors error) 
		{
			
			//controllo tipi di errore che si possono riscontrare
			switch (error) 
			{
				//richiesta non valida
				case INVALID_REQUEST:
					showErrorMessage("Rcihiesta non valida","Errore");
					break;
					
				case SENDER_USER_INVALID_STATUS:
					showErrorMessage("Sei gia' offline","Warning");
					break;
							
				//errore non trovato
				default:
					showErrorMessage("Errore nel messaggio di risposta del server","Errore");
					break;
			}
			
		}

		@Override
		protected void parseErrorHandler() {
			showErrorMessage("Errore lettura risposta del server","Errore");			
		}

		@Override
		protected void unexpectedMessageHandler() {
			showErrorMessage("Errore nel messaggio di risposta del server","Errore");			
		}

		@Override
		protected void successResponseHandler() 
		{
			//chiudo hub
			hubView.setVisible(false);
			hubView.dispose();
		}
		
	}	
}
