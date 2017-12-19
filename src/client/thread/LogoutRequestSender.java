package client.thread;


import java.rmi.NoSuchObjectException;
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
 * Thread che si occupa di gestire la richiesta di logout lato client
 * @author gio
 *
 */
public class LogoutRequestSender extends RequestSenderThread
{
	public static final int YES = 0;
	private String nickname;
	private RMIServerInterface serverRMI;
	private RMIClientNotifyEvent callback;
	
	public LogoutRequestSender(Controller controller,String nickname,RMIServerInterface serverRMI,RMIClientNotifyEvent callback) 
	{
		super(controller);
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
	protected void ConnectErrorHandler() {
		controller.showErrorMessage("Servizio attualmente non disponibile","Errore");
	}

	@Override
	protected void UnKwownHostErrorHandler() {
		controller.showErrorMessage("Server non trovato","Errore");
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
				controller.showErrorMessage("Rcihiesta non valida","Errore");
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
		try 
		{
			serverRMI.unregisterUserRMIChannel(nickname);
			UnicastRemoteObject.unexportObject(callback,true);
		} 
		catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (UserNotFindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//chiudo hub
		controller.getWindow().setVisible(false);
		controller.getWindow().dispose();
	}
	
}
