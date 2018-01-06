package client.thread.requestSender.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import javax.swing.JOptionPane;

import client.controller.ChatRoomController;
import client.controller.HubController;
import client.thread.ListenerChatRoomMessage;
import client.thread.requestSender.RequestSenderThread;
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
	private User user; //utente che richiede creazione chatroom
	private String chatroomName; //nome chatroom da creare
	private List<ListenerChatRoomMessage> listenersChatRoomMessages; //lista dei listener delle chatroom
	
	public NewChatRoomRequestSender(HubController controller, Socket connection, DataInputStream in,User user,
			DataOutputStream out,List<ListenerChatRoomMessage> listenersChatRoomMessages) 
	{
		super(controller, connection, in, out);
		
		if(listenersChatRoomMessages == null || user == null)
			throw new NullPointerException();
		
		this.listenersChatRoomMessages = listenersChatRoomMessages;
		this.user = user;
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
			
			case CHATROOM_ALREADY_REGISTERED:
				controller.showErrorMessage("E' gia presente una ChatRoom con lo stesso nome","Ops");
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
		
		//apro la nuova chatroom creata
		HubController hubController = (HubController) controller;
		
		ChatRoomController chatroomControl = null;
		
			try {
				//prendo il controller della chatroom
				chatroomControl = hubController.openChatRoomFromList(new ChatRoom(chatroomName));
				
				//faccio partire il listener dei messaggi della chatroom
				synchronized (listenersChatRoomMessages) {
					ListenerChatRoomMessage listener = new ListenerChatRoomMessage(hubController,chatroomControl.getChatRoomReceiver());
					listenersChatRoomMessages.add(listener);
					listener.start();
				}
			} catch (IOException e) {
				controller.showErrorMessage("Errore apertura chatroom","ERRORE");
				return;
			}		
		
		if(chatroomControl != null)
			chatroomControl.setVisible(true);
	}

}
