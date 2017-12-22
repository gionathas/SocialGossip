package client.thread;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

import client.controller.Controller;
import communication.RMI.RMIClientNotifyEvent;
import communication.RMI.RMIServerInterface;
import communication.TCPMessages.request.LogoutRequest;
import communication.TCPMessages.response.ResponseFailedMessage;
import server.model.exception.UserNotFindException;

/**
 * Thread che gestisce la richiesta di Logout
 * @author Gionatha Sturba
 *
 */
public class LogoutRequestSender extends RequestSenderThread
{
	private String nickname;
	private RMIServerInterface serverRMI;
	private RMIClientNotifyEvent callback;
	
	private static final int YES = 0;

	/**
	 * Inizializza i parametri per l'invio della richiesta
	 * @param controller controller della finestra che richiede l'invio
	 * @param nickname nickname dell'utente
	 * @param serverRMI Interfaccia RMI per informare il server del Logout
	 * @param callback callback da deregistrare al momento del Logout
	 */
	public LogoutRequestSender(Controller controller,Socket connection,DataInputStream in,DataOutputStream out,String nickname,RMIServerInterface serverRMI,RMIClientNotifyEvent callback) 
	{	
		super(controller,connection,in,out);
		
		if(nickname == null || serverRMI == null || callback == null)
			throw new NullPointerException();
		
		this.nickname = nickname;
		this.serverRMI = serverRMI;
		this.callback = callback;
	}

	@Override
	protected void init() 
	{
		//mostro finestra che chiede se si vuole uscire veramente
		int choice = JOptionPane.showConfirmDialog(controller.getWindow(),"Sei sicuro di voler uscire?");
		
		//se la scelta e' diversa da Si,mi fermo
		if(choice != YES) {
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
		request = new LogoutRequest(nickname);
	}

	@Override
	protected void IOErrorHandler() {
		controller.showErrorMessage("Errore nella richiesta di logout","Errore");
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
	protected void failedResponseHandler(ResponseFailedMessage.Errors error) 
	{
		
		//controllo tipi di errore che si possono riscontrare
		switch (error) 
		{
			//richiesta non valida
			case INVALID_REQUEST:
				controller.showErrorMessage("Richiesta non valida","Errore");
				break;
				
			case SENDER_USER_INVALID_STATUS:
				controller.showErrorMessage("Sei gia' offline","Warning");
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
		//deregistro la callback dal registry
		try {
			serverRMI.unregisterUserRMIChannel(nickname);
			UnicastRemoteObject.unexportObject(callback,true);
		} catch (RemoteException | UserNotFindException e) {
			controller.showErrorMessage("Errore nel protcollo RMI,in fase di Logout","ERRORE LOGOUT");
			e.printStackTrace();
		}
		finally {
			//chiudo connessione e hub
			controller.closeConnection();
			controller.closeWindow();
		}
	}
	
}
