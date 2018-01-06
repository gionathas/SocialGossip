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

import client.thread.requestSender.implementation.FileSender;
import client.thread.requestSender.implementation.SendTextToUser;
import client.view.ChatWindow;
import server.model.User;

/**
 * Controller della GUI di una chat con un altro utente
 * @author Gionatha Sturba
 *
 */
public class ChatController extends Controller
{
	private ChatWindow chatView;
	private User owner; //utente possessore della chat
	private User receiver; //utente con cui chatta l'owner
	private Controller controller = this;

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
		
		initListeners();
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
		
		//al click sul bottone INVIA
		chatView.getBtnInviaTextButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				//parte il thread che gestisce l'invio del messaggio
				new SendTextToUser(controller, connection, in, out,owner.getNickname(),receiver.getNickname(),chatView.getTextArea(),chatView.getConversationArea()).start();
			}
		});
		
		//al click sul bottono INVIA FILE
		chatView.getBtnInviaFile().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				new FileSender(controller, connection, in, out,owner.getNickname(),receiver.getNickname()).start();
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
	
	public JTextArea getTextArea() {
		return chatView.getTextArea();
	}
	
	public JTextArea getJConversationArea() {
		return chatView.getConversationArea();
	}

}
