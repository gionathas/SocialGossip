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
		
		System.out.println("Fine analisi lista amici");
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
		protected void analyzeResponse(String response) {
			// TODO Auto-generated method stub
			
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
					//logout avvenuto
					case SUCCESS:
						//chiudo hub
						hubView.setVisible(false);
						hubView.dispose();
						
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
								
							case USER_INVALID_STATUS:
								showErrorMessage("Sei gia' offline","Warning");
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
}
