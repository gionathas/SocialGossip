package client.controller;

import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import client.view.ChatWindow;
import server.model.User;

public class ChatController extends Controller
{
	private ChatWindow chatView;
	private User owner; //utente possessore della chat
	private User receiver; //utente con cui chatta l'owner

	public ChatController(Socket connection,DataInputStream in,DataOutputStream out,User owner,User receiver,Point location)
	{
		super(connection,in,out);
		
		if(owner == null || receiver == null || location == null)
			throw new NullPointerException();
		
		chatView = new ChatWindow("Chat con "+receiver.getNickname());
		setWindow(chatView);
		window.setLocation(location);
		
		this.owner = owner;
		this.receiver = receiver;
	}
	
	@Override
	protected void initListeners() 
	{
		//alla chiusura della chat
		chatView.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				closeChat();
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
	
	public User getOwner() {
		return owner;
	}

	public User getReceiver() {
		return receiver;
	}

}
