package client.controller;

import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import client.view.ChatWindow;
import server.model.User;

public class ChatController extends Controller
{
	private ChatWindow chatView;
	private User owner; //utente possessore della chat
	private User receiver; //utente con cui chatta l'owner

	public ChatController(User owner,User receiver,Point location)
	{
		if(owner == null || receiver == null || location == null)
			throw new NullPointerException();
		
		chatView = new ChatWindow();
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
	
	public User getOwner() {
		return owner;
	}

	public User getReceiver() {
		return receiver;
	}

}
