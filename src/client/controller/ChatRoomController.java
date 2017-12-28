package client.controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JTextArea;

import client.thread.requestSender.SendTextToUser;
import client.view.ChatRoomWindow;
import server.model.ChatRoom;
import server.model.User;

public class ChatRoomController extends Controller
{
	private ChatRoomWindow chatView;
	private User sender;
	private ChatRoom chatRoomReceiver;

	public ChatRoomController(Socket connection, DataInputStream in, DataOutputStream out,User sender,ChatRoom chatRoomReceiver,Point location) 
	{
		super(connection, in, out);
		
		if(sender == null || chatRoomReceiver == null || location == null)
			throw new NullPointerException();
		
		chatView = new ChatRoomWindow("CHATROOM["+chatRoomReceiver.getName()+"]");
		setWindow(chatView);
		window.setLocation(location);
		
		this.sender = sender;
		this.chatRoomReceiver = chatRoomReceiver;
		
		initListeners();
	}
	
	protected void initListeners()
	{
		//alla chiusura della chat
		chatView.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				closeChat();
			}
		});
		
		//al click sul bottone INVIA
		chatView.getBtnInviaTextButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				//parte il thread che gestisce l'invio del messaggio
				//TODO
				new SendTextToUser(controller, connection, in, out,owner.getNickname(),receiver.getNickname(),chatView.getTextArea(),chatView.getConversationArea()).start();
			}
		});
	}
	
	private void closeChat() {
		window.setVisible(false);
	}
	
	public void openChat() {
		window.setVisible(true);
	}
	
	public boolean isVisible() {
		return window.isVisible();
	}
	
	public JTextArea getTextArea() {
		return chatView.getTextArea();
	}
	
	public JTextArea getJConversationArea() {
		return chatView.getConversationArea();
	}
}
