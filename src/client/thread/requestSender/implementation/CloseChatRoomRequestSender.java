package client.thread.requestSender.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import client.controller.ChatRoomController;
import client.thread.requestSender.RequestSenderThread;
import communication.TCPMessages.request.chatroom.CloseChatRoom;
import communication.TCPMessages.response.fail.ResponseFailedMessage.Errors;

/**
 * Thread che invia la richiesta di chiusura di una chatroom
 * @author Gionatha Sturba
 *
 */
public class CloseChatRoomRequestSender extends RequestSenderThread
{
	private ChatRoomController chatroomControl; //controller della chatroom da chiudere
	private String chatroomName; //nome chatroom da chiudere
	private String nicknameUser; //nome utente che richiede chiusura

	public CloseChatRoomRequestSender(ChatRoomController controller, Socket connection, DataInputStream in,
			DataOutputStream out,String chatroomName,String nicknameUser) {
		super(controller, connection, in, out);
		
		if(chatroomName == null || nicknameUser == null)
			throw new NullPointerException();
		
		this.chatroomControl = controller;
		this.chatroomName = chatroomName;
		this.nicknameUser = nicknameUser;

	}

	@Override
	protected void init() {
		int yes = 0;
		int choice = JOptionPane.showConfirmDialog(chatroomControl.getWindow(),"Sei sicuro di voler chiudere definitivamente la chatroom?");
		
		//in base alla scelta
		if(choice == yes) {
			init = true;
		}else {
			init = false;
		}
		
	}

	@Override
	protected void createRequest() {
		request = new CloseChatRoom(nicknameUser,chatroomName);
	}

	@Override
	protected void IOErrorHandler() {
		controller.showErrorMessage("Errore richiesta chiusura chatroom","Errore richiesta");
		
	}

	@Override
	protected void invalidResponseHandler() {
		controller.showErrorMessage("Errore richiesta chiusura chatroom","Errore richiesta");
		
	}

	@Override
	protected void invalidResponseErrorTypeHandler() {
		controller.showErrorMessage("Errore richiesta chiusura chatroom","Errore richiesta");
		
	}

	@Override
	protected void failedResponseHandler(Errors error) 
	{
		switch(error)
		{
			case OPERATION_NOT_PERMITTED:
				controller.showInfoMessage("Non sei autorizzato a chiudere la chatroom!","PERMESSO NEGATO",false);
				break;
			
			default:
				controller.showErrorMessage("Errore richiesta chiusura chatroom","Errore richiesta");
				break;
		}
	}

	@Override
	protected void parseErrorHandler() {
		controller.showErrorMessage("Errore richiesta chiusura chatroom","Errore richiesta");
		
	}

	@Override
	protected void unexpectedMessageHandler() {
		controller.showErrorMessage("Errore richiesta chiusura chatroom","Errore richiesta");
		
	}

	@Override
	protected void successResponseHandler() 
	{
		//non si fa nulla
	}

}
