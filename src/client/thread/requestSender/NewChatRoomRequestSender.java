package client.thread.requestSender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;

import javax.swing.JOptionPane;

import client.controller.ChatRoomController;
import client.controller.Controller;
import client.controller.HubController;
import client.thread.ListenerChatRoomMessage;
import communication.TCPMessages.request.chatroom.NewChatRoom;
import communication.TCPMessages.response.fail.ResponseFailedMessage.Errors;
import server.model.ChatRoom;
import server.model.User;

/**
 * Thread che si occupa della gestione della richiesta della creazione di uno nuova chatroom
 * @author Gionatha Sturba
 *
 */
public class NewChatRoomRequestSender extends RequestSenderThread
{
	private User user;
	private String chatroomName;
	private List<ListenerChatRoomMessage> listenersChatRoomMessages;
	
	public NewChatRoomRequestSender(HubController controller, Socket connection, DataInputStream in,
			DataOutputStream out,List<ListenerChatRoomMessage> listenersChatRoomMessages) 
	{
		super(controller, connection, in, out);
		
		if(listenersChatRoomMessages == null)
			throw new NullPointerException();
		
		this.listenersChatRoomMessages = listenersChatRoomMessages;
	}

	@Override
	protected void init() 
	{
		//richiedo nome della chatroom
		chatroomName = JOptionPane.showInputDialog(controller.getWindow(),"Inserisci nome della chatroom da creare");
		
		//controllo nome inserito
		if(chatroomName == null || chatroomName.isEmpty())
		{
			controller.showErrorMessage("Nome chatroom non valido","Errore");
			init = false;
			return;
		}
		
		//nome corretto si procede alla richiesta
		init = true;
	}

	@Override
	protected void createRequest() {
		request = new NewChatRoom(user.getNickname(),chatroomName);
	}

	@Override
	protected void IOErrorHandler() {
		controller.showErrorMessage("Errore richiesta creazione chatroom","ERRORE");
	}

	@Override
	protected void invalidResponseHandler() {
		controller.showErrorMessage("Errore richiesta creazione chatroom","ERRORE");
	}

	@Override
	protected void invalidResponseErrorTypeHandler() {
		controller.showErrorMessage("Errore richiesta creazione chatroom","ERRORE");		
	}

	@Override
	protected void failedResponseHandler(Errors error) 
	{
		switch(error)
		{
			case SENDER_USER_INVALID_STATUS:
				controller.showErrorMessage("Sei offline","Errore");
				break;
			
			case CANNOT_CREATE_CHATROOM:
				controller.showErrorMessage("Impossibile creare Chatroom","Errore");
				break;
			
			case INVALID_REQUEST:
				controller.showErrorMessage("Richiesta non valida","Errore");
				break; 
				
			default:
				controller.showErrorMessage("Messaggio di errore inaspettato","Errore");
				break;
		}
	}

	@Override
	protected void parseErrorHandler() {
		controller.showErrorMessage("Errore richiesta creazione chatroom","ERRORE");		
	}

	@Override
	protected void unexpectedMessageHandler() {
		controller.showErrorMessage("Errore richiesta creazione chatroom","ERRORE");
	}

	@Override
	protected void successResponseHandler() 
	{
		controller.showInfoMessage("ChatRoom "+chatroomName.toUpperCase()+" creata.","ChatRoom creata",false);
		
		//TODO aprire chatroom dalla list,che e' stata aggiornata
		HubController hubController = (HubController) controller;
		
		ChatRoomController chatroomControl = hubController.openChatRoomFromNewMessage(new ChatRoom(chatroomName));
		
		//TODO manipolare chatroom
	}

}
