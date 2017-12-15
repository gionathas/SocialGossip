package client.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import client.view.Hub;
import communication.MessageAnalyzer;
import communication.messages.LogoutRequest;
import communication.messages.Message;
import communication.messages.RegisterRequest;
import communication.messages.ResponseFailedMessage;
import communication.messages.ResponseMessage;
import server.model.User;

public class HubController extends Controller
{
	private Hub hubView;
	private User user;
	
	public HubController(String nickname) 
	{
		hubView = new Hub();
		setWindow(hubView);
		
		user = new User(nickname);
		hubView.setWelcomeText("Loggato come "+nickname.toUpperCase());
		initListeners();
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
	                logOut();
	            }
	        });
	}
	
	private void logOut() 
	{
		//mostro finestra che chiede se si vuole uscire veramente
		int choice = JOptionPane.showConfirmDialog(hubView,"Sei sicuro di voler uscire?");
		
		//se la scelta e' SI
		if(choice == 0) 
		{
			//thread che si occupa di inviare la richiesta di logout al server
			Thread thread = new Thread(new Runnable() {
				
				public void run() 
				{
					
					//apro una connessione con il server per inviare la richiesta
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
						LogoutRequest request = new LogoutRequest(user.getNickname());
						
						//invio richiesta
						out.writeUTF(request.getJsonMessage());
						
						//leggo risposta del server
						String response = in.readUTF();
						
						//analizzo risposta del server
						analyzeResponseLogout(response);
						
					}
					//se non riesco a connettermi al server
					catch(ConnectException e)
					{
						showErrorMessage("Servizio attualmente non disponibile","Errore");
						e.printStackTrace();

					}
					catch (UnknownHostException e) 
					{
						showErrorMessage("Server non trovato","Errore");
						e.printStackTrace();
					} 
					catch (IOException e) 
					{
						showErrorMessage("Errore nella richiesta di logout","Errore");
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
						
						
					}
				}
			});
			
			thread.start();
		}
		
	}
	
	private void analyzeResponseLogout(String JsonResponse)
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
					showInfoMessage("Alla prossima");
					//chiudo hub
					hubView.setVisible(false);
					hubView.dispose();
					
					//TODO aggiungere chiusura dei thread attivi
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
