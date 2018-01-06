package client.thread.requestSender.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JTextArea;

import client.controller.Controller;
import communication.TCPMessages.request.interaction.SendMessageRequest;
import communication.TCPMessages.response.fail.ResponseFailedMessage.Errors;

/**
 * Thread che si occupa della richiesta di invio di un messaggio ad un utente amico
 * @author Gionatha Sturba
 *
 */
public class SendTextToUser extends TextSender
{
	public SendTextToUser(Controller controller, Socket connection, DataInputStream in, DataOutputStream out,
			String senderNick, String receiverNick, JTextArea textArea, JTextArea conversationArea) 
	{
		super(controller, connection, in, out, senderNick, receiverNick, textArea, conversationArea);
	}
	
	protected void createRequest() {
		request = new SendMessageRequest(senderNick,receiverNick,text);
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
