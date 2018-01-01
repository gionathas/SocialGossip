package client.thread.requestSender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JOptionPane;
import client.controller.HubController;
import client.thread.ListenerChatRoomMessage;
import communication.TCPMessages.request.chatroom.JoinChatRoom;
import communication.TCPMessages.response.fail.ResponseFailedMessage.Errors;
import server.model.ChatRoom;

public class JoinChatRoomRequestSender extends RequestSenderThread
{
	private ChatRoom chatroom;
	private String nicknameUser;
	private List<ListenerChatRoomMessage> listenersChatRoomMessages;
	private HubController hubController;

	public JoinChatRoomRequestSender(HubController controller, Socket connection, DataInputStream in, DataOutputStream out,String nicknameUser,ChatRoom chatRoom,
			List<ListenerChatRoomMessage> listenersChatRoomMessages) 
	 {
		super(controller, connection, in, out);
		
		if(nicknameUser == null || chatRoom == null || listenersChatRoomMessages == null)
			throw new NullPointerException();
		
		this.chatroom = chatRoom;
		this.nicknameUser = nicknameUser;
		this.listenersChatRoomMessages = listenersChatRoomMessages;
		this.hubController = (HubController) controller;
	}

	@Override
	protected void init() 
	{
		init = true;
		
		//controllo se l'utente e' gia' iscritto alla chatroom
		synchronized (listenersChatRoomMessages) 
		{
			for (ListenerChatRoomMessage listenerChatRoomMessage : listenersChatRoomMessages) 
			{
				//se sono gia' iscritto alla chatroom,la apro e termino
				if(listenerChatRoomMessage.getChatRoomName().equals(chatroom.getName())) 
				{
					try 
					{
						hubController.openChatRoomFromList(chatroom);
					} 
					catch (SocketException | UnknownHostException e) 
					{
						hubController.showErrorMessage("Impossibile aprire chatroom","Errore Apertura ChatRoom");
					}
					finally {
						init = false;
					}
					
					break;
				}
			}
		}
		
		//se non sono registrato alla chatroom
		if(init == true)
		{
			int yes = 0;
			
			//chiedo se mi voglio realmente registrare
			int choice = JOptionPane.showConfirmDialog(null,"Vuoi davvero registrarti alla ChatRoom "+chatroom.getName().toUpperCase()+" ?");
			
			//se non mi voglio registrare
			if(choice != yes) {
				init = false;
			}
		}
	}

	@Override
	protected void createRequest() {
		request = new JoinChatRoom(nicknameUser,chatroom.getName());
	}

	@Override
	protected void IOErrorHandler() {
		controller.showErrorMessage("Errore nella richiesta di unione alla chatroom richiesta","Errore");
		
	}

	@Override
	protected void invalidResponseHandler() {
		controller.showErrorMessage("Errore nella richiesta di unione alla chatroom richiesta","Errore");
		
	}

	@Override
	protected void invalidResponseErrorTypeHandler() {
		controller.showErrorMessage("Errore nella richiesta di unione alla chatroom richiesta","Errore");

	}

	@Override
	protected void failedResponseHandler(Errors error) {
		switch(error)
		{
			case CHATROOM_NOT_FOUND:
				controller.showErrorMessage("ChatRoom richiesta non trovata","Errore");
				break;
			case USER_ALREADY_REGISTERED:
				controller.showErrorMessage("Risulti gia' iscritto alla chatroom richiesta","Errore");
				break;
				
			default:
				controller.showErrorMessage("Errore nella richiesta di unione alla chatroom richiesta","Errore");
				break;
		}
	}

	@Override
	protected void parseErrorHandler() {
		controller.showErrorMessage("Errore nella richiesta di unione alla chatroom richiesta","Errore");
	}

	@Override
	protected void unexpectedMessageHandler() {
		controller.showErrorMessage("Errore nella richiesta di unione alla chatroom richiesta","Errore");
	}

	@Override
	protected void successResponseHandler() 
	{			
		synchronized (listenersChatRoomMessages) 
		{
			try 
			{
				//aggiungo il listener alla chatroom per ricevere messaggi
				ListenerChatRoomMessage listener = new ListenerChatRoomMessage(hubController,chatroom);
				listenersChatRoomMessages.add(listener);
				listener.start();
				
				//apro la chatroom
				hubController.openChatRoomFromList(chatroom);
			} catch (IOException e) {
				controller.showErrorMessage("Errore apertura chatroom","Errore");
				return;
			}
		}
		
		controller.showInfoMessage("Ora sei registrato alla chatroom "+chatroom.getName().toUpperCase(),"Benvenuto",false);
	}

}
