package client.thread.requestSender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JTextArea;

import client.controller.Controller;
import communication.TCPMessages.request.UserMessage;
import communication.TCPMessages.response.ResponseFailedMessage.Errors;

public class SendTextToUser extends TextSender
{

	public SendTextToUser(Controller controller, Socket connection, DataInputStream in, DataOutputStream out,
			String senderNick, String receiverNick, JTextArea textArea, JTextArea conversationArea) 
	{
		super(controller, connection, in, out, senderNick, receiverNick, textArea, conversationArea);
	}
	
	protected void createRequest() {
		request = new UserMessage(senderNick,receiverNick,text);
	}
	
	protected void failedResponseHandler(Errors error) 
	{
		switch(error)
		{	
			//utente offline
			case RECEIVER_USER_INVALID_STATUS:
				controller.showErrorMessage("Utente offline","Messaggio non inviato");
				break;
			
			//utente non trovato
			case RECEIVER_USER_NOT_FOUND:
				controller.showErrorMessage("Utente non trovato","Messaggio non inviato");
				break;
				
			default:
				controller.showErrorMessage("Errore nell'invio del messaggio","Messaggio non inviato");
				break;
		}
	}



}
