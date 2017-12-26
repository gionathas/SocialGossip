package client.thread.requestSender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;

import javax.swing.JOptionPane;

import client.controller.Controller;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.request.SendFileRequest;
import communication.TCPMessages.response.ResponseFailedMessage.Errors;

public class FileSender extends RequestSenderThread
{
	private String senderNickname,receiverNickname;
	private File file = null;
	private final String DIR_FILES_PATH = "/resources/";
	
	public FileSender(Controller controller, Socket connection, DataInputStream in, DataOutputStream out,String senderNickname,String receiverNickname) {
		super(controller, connection, in, out);
		
		if(senderNickname == null || receiverNickname == null)
			throw new NullPointerException();
		
		this.senderNickname = senderNickname;
		this.receiverNickname = receiverNickname;
	}

	@Override
	protected void init() {
		String filename = JOptionPane.showInputDialog("Inserisci nome del file da inviare.. (dentro cartella resources)");
		
		//se il nome del file non e' valido 
		if(filename == null)
		{
			init = false;
			controller.showErrorMessage("File non valido","ERRORE");
			return;
		}
		
		file = new File("");
		
		//System.out.println(file.getAbsolutePath());
		file = new File(file.getAbsoluteFile()+DIR_FILES_PATH+filename);
		
		//se il file non esiste,oppure non e' un vero e proprio file
		if(!file.exists() || file.isDirectory())
		{
			init = false;
			controller.showErrorMessage("File non trovato oppure non valido","ERRORE");
			return;
		}
		
		//file valido
		init = true;
			
	}

	@Override
	protected void createRequest() {
		request = new SendFileRequest(senderNickname,receiverNickname,file.getName());
	}

	@Override
	protected void IOErrorHandler() {
		controller.showErrorMessage("Errore richiesta invio file","ERRORE");
	}

	@Override
	protected void invalidResponseHandler() {
		controller.showErrorMessage("Richiesta non valida","ERRORE");
	}

	@Override
	protected void invalidResponseErrorTypeHandler() {
		controller.showErrorMessage("Risposta di errore non valida","ERRORE");
	}

	@Override
	protected void failedResponseHandler(Errors error) {
		switch(error)
		{
			case CANNOT_RECEIVE_FILE:
				controller.showInfoMessage("L'utente non puo' ricevere il file richiesto","Errore invio file",false);
				break;
			
			case RECEIVER_USER_INVALID_STATUS:
				controller.showErrorMessage("Utente offline","Errore invio file");
				break;
				
			default:
				controller.showErrorMessage("Errore invio richiesta invio file","ERRORE");
				break;
		}
		
	}

	@Override
	protected void parseErrorHandler() {
		controller.showErrorMessage("Errore nel parsing della richiesta","ERRORE");
	}

	@Override
	protected void unexpectedMessageHandler() {
		controller.showErrorMessage("Risposta inaspettata","ERRORE");

	}

	@Override
	protected void successResponseHandler() {
		//prendo indirizzo e porta alla quale inviare il file
		String hostname = MessageAnalyzer.getFileReceiverHostname(response);
		long port = MessageAnalyzer.getFileReceiverPort(response);
		
		if(hostname == null)
		{
			controller.showErrorMessage("Errore nella ricerca dei parametri dell'indirizzo del client","ERRORE");
			return;
		}
		
		//TODO sendFIle
	}
}
