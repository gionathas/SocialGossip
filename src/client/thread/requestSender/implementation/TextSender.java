package client.thread.requestSender.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JTextArea;

import client.controller.Controller;
import client.thread.requestSender.RequestSenderThread;
import communication.TCPMessages.response.fail.ResponseFailedMessage.Errors;

/**
 * Modello del thread che si occuppa di inviare un messaggio testuale
 * @author Gionatha Sturba
 *
 */
public class TextSender extends RequestSenderThread
{
	protected String senderNick; //nick mittente
	protected String receiverNick; //nick destinatario
	protected String text = null; //testo messaggio
	private JTextArea textArea; 
	private JTextArea conversationArea;

	public TextSender(Controller controller, Socket connection, DataInputStream in, DataOutputStream out,String senderNick,String receiverNick,
			JTextArea textArea,JTextArea conversationArea) 
	{
		super(controller, connection, in, out);
		
		if( senderNick == null || receiverNick == null || textArea == null || conversationArea == null) {
			throw new NullPointerException();
		}
		
		this.senderNick = senderNick;
		this.receiverNick = receiverNick;
		this.textArea = textArea;
		this.conversationArea = conversationArea;
		
	}

	@Override
	protected void init() 
	{
		text = textArea.getText();
		
		if(text == null || text.isEmpty()) {
			init = false;
		}
		//messaggio valido
		else {
			//pulisco la textArea
			textArea.setText("");
			init = true;
		}
	}

	@Override
	protected void createRequest() {}

	@Override
	protected void IOErrorHandler() {
		controller.showErrorMessage("Errore nell'invio del messaggio","ERRORE");
	}

	@Override
	protected void invalidResponseHandler() {
		controller.showErrorMessage("Errore: risposta del server non valida","ERRORE");
	}

	@Override
	protected void invalidResponseErrorTypeHandler() {
		controller.showErrorMessage("Errore: risposta del server non valida","ERRORE");
	}

	@Override
	protected void failedResponseHandler(Errors error) {}

	@Override
	protected void parseErrorHandler() {
		controller.showErrorMessage("Errore lettura risposta del server","ERRORE");
	}

	@Override
	protected void unexpectedMessageHandler() {
		controller.showErrorMessage("Errore: risposta del server inaspettata","ERRORE");		
	}

	@Override
	protected void successResponseHandler() 
	{
		//se il messaggio e' stato inviato con successo,lo aggiungo alla conversation area
		synchronized (conversationArea) 
		{
			conversationArea.append("["+senderNick+"]"+": "+text+"\n");
		}
	}
}
