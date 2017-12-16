package client.view;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import server.model.ChatRoom;
import server.model.User;

import java.awt.Color;
import java.awt.Font;

public class Hub extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6642814114735519702L;
	private JPanel contentPane;
	private JLabel WelcomeText;
	private JTextField textField;
	private JButton btnLogout;
	private JButton btnAvviaChat;
	private JButton btnCerca;
	private JButton btnUniscitiAChatroom;
	private JButton btnCreaChatroom;
	private JList<User> userFriendList;
	private DefaultListModel<User> modelUserFriendList = new DefaultListModel<User>();
	private JList<ChatRoom> chatRoomList;
	private DefaultListModel<ChatRoom> modelChatRoomList = new DefaultListModel<ChatRoom>();

	/**
	 * Create the frame.
	 */
	public Hub() 
	{
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("Social Gossip");
		setBounds(100, 100, 800,600);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		WelcomeText = new JLabel("");
		WelcomeText.setBounds(12, 535, 290, 28);
		contentPane.add(WelcomeText);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(550, 70, 200, 400);
		contentPane.add(scrollPane);
		
		userFriendList = new JList<User>();
		userFriendList.setModel(modelUserFriendList);
		scrollPane.setViewportView(userFriendList);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 101, 440, 367);
		contentPane.add(scrollPane_1);
		
		chatRoomList = new JList<ChatRoom>();
		chatRoomList.setModel(modelChatRoomList);
		scrollPane_1.setViewportView(chatRoomList);
		
		btnCreaChatroom = new JButton("Crea ChatRoom");
		btnCreaChatroom.setBounds(12, 478, 151, 35);
		contentPane.add(btnCreaChatroom);
		
		btnUniscitiAChatroom = new JButton("Unisciti a ChatRoom");
		btnUniscitiAChatroom.setBounds(175, 478, 200, 35);
		contentPane.add(btnUniscitiAChatroom);
		
		JLabel lblCercaUtente = new JLabel("Cerca Utente:");
		lblCercaUtente.setFont(new Font("Dialog", Font.BOLD, 14));
		lblCercaUtente.setBounds(15, 27, 110, 15);
		contentPane.add(lblCercaUtente);
		
		textField = new JTextField();
		textField.setBounds(130, 20, 207, 30);
		contentPane.add(textField);
		textField.setColumns(10);
		
		btnCerca = new JButton("Cerca");
		btnCerca.setBounds(345, 22, 74, 25);
		contentPane.add(btnCerca);
		
		JLabel lblChatroomAttive = new JLabel("ChatRoom Attive:");
		lblChatroomAttive.setFont(new Font("Dialog", Font.BOLD, 14));
		lblChatroomAttive.setBounds(15, 85, 140, 15);
		contentPane.add(lblChatroomAttive);
		
		btnLogout = new JButton("Esci");
		btnLogout.setBounds(671, 538, 117, 25);
		contentPane.add(btnLogout);
		
		JLabel lblAmiciOnline = new JLabel("I Tuoi Amici:");
		lblAmiciOnline.setFont(new Font("Dialog", Font.BOLD, 14));
		lblAmiciOnline.setBounds(550, 55, 110, 15);
		contentPane.add(lblAmiciOnline);
		
		btnAvviaChat = new JButton("Avvia Chat");
		btnAvviaChat.setBounds(550, 478, 200, 35);
		contentPane.add(btnAvviaChat);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(505, 0, 2, 600);
		contentPane.add(separator);
	}

	public DefaultListModel<User> getModelUserFriendList() {
		return modelUserFriendList;
	}

	public DefaultListModel<ChatRoom> getModelChatRoomList() {
		return modelChatRoomList;
	}

	public JPanel getContentPane() {
		return contentPane;
	}

	public JTextField getTextField() {
		return textField;
	}

	public JButton getBtnLogout() {
		return btnLogout;
	}

	public JButton getBtnAvviaChat() {
		return btnAvviaChat;
	}

	public JButton getBtnCerca() {
		return btnCerca;
	}

	public JButton getBtnUniscitiAChatroom() {
		return btnUniscitiAChatroom;
	}

	public JButton getBtnCreaChatroom() {
		return btnCreaChatroom;
	}

	public JList<User> getUserFriendList() {
		return userFriendList;
	}

	public JList<ChatRoom> getChatRoomList() {
		return chatRoomList;
	}

	public JLabel getWelcomeText() {
		return WelcomeText;
	}

	public void setWelcomeText(String welcomeText) {
		WelcomeText.setText(welcomeText);
	}
}
