package client.view;

import java.awt.Color;
import java.awt.SystemColor;

import javax.swing.JButton;

public class ChatRoomWindow extends ChatWindow
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -436743372016244797L;
	
	protected JButton btnChiudiChatRoom;
	
	public static void main(String[] args) {
		new ChatRoomWindow("CHATROOM").setVisible(true);
	}

	public ChatRoomWindow(String title) {
		super(title);
		
		//modifico le misure di alcuni componenti
		setBounds(100,100,WIDTH+100,HEIGHT);
		getContentPane().setBackground(SystemColor.blue);

		
		scrollPane.setBounds(10, 12, 450, 250);
		scrollPane_1.setBounds(10, 300, 350, 50);
		btnInviaTextButton.setBounds(370, 300, 90, 50);
		
		//rimuovo il bottone invia file
		this.remove(btnInviaFile);
		
		//aggiungo bottone chiusura chatroom
		btnChiudiChatRoom = new JButton("Chiudi ChatRoom");
		btnChiudiChatRoom.setBounds(10, 276, 170, 20);
		btnChiudiChatRoom.setBackground(Color.RED);
		getContentPane().add(btnChiudiChatRoom);
	}
	
	public void setButtonColor(Color color) {
		btnChiudiChatRoom.setBackground(color);
	}
	
	public JButton getBtnChiudiChatRoom() {
		return btnChiudiChatRoom;
	}
	
	@Override
	public JButton getBtnInviaFile() {
		return null;
	}

}
