package client.view;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.UIManager;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class Hub extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6642814114735519702L;
	private JPanel contentPane;
	private JLabel WelcomeText;
	private JTextField textField;

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
		
		JList list = new JList();
		list.setBorder(UIManager.getBorder("List.noFocusBorder"));
		scrollPane.setViewportView(list);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 101, 440, 367);
		contentPane.add(scrollPane_1);
		
		JList list_1 = new JList();
		scrollPane_1.setViewportView(list_1);
		
		JButton btnCreaChatroom = new JButton("Crea ChatRoom");
		btnCreaChatroom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnCreaChatroom.setBounds(12, 478, 151, 35);
		contentPane.add(btnCreaChatroom);
		
		JButton btnUniscitiAChatroom = new JButton("Unisciti a ChatRoom");
		btnUniscitiAChatroom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnUniscitiAChatroom.setBounds(175, 478, 200, 35);
		contentPane.add(btnUniscitiAChatroom);
		
		JLabel lblCercaUtente = new JLabel("Cerca Utente:");
		lblCercaUtente.setBounds(23, 27, 110, 15);
		contentPane.add(lblCercaUtente);
		
		textField = new JTextField();
		textField.setBounds(141, 20, 207, 30);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnCerca = new JButton("Cerca");
		btnCerca.setBounds(358, 20, 74, 25);
		contentPane.add(btnCerca);
		
		JLabel lblChatroomAttive = new JLabel("ChatRoom Attive");
		lblChatroomAttive.setBounds(15, 85, 140, 15);
		contentPane.add(lblChatroomAttive);
		
		JButton btnLogout = new JButton("Esci");
		btnLogout.setBounds(671, 535, 117, 25);
		contentPane.add(btnLogout);
		
		JLabel lblAmiciOnline = new JLabel("Amici Online");
		lblAmiciOnline.setBounds(550, 55, 110, 15);
		contentPane.add(lblAmiciOnline);
		
		JButton btnAvviaChat = new JButton("Avvia Chat");
		btnAvviaChat.setBounds(560, 482, 117, 25);
		contentPane.add(btnAvviaChat);
		
		JButton btnAggiorna = new JButton("Aggiorna");
		btnAggiorna.setBounds(649, 38, 98, 25);
		contentPane.add(btnAggiorna);
	}

	public JLabel getWelcomeText() {
		return WelcomeText;
	}

	public void setWelcomeText(String welcomeText) {
		WelcomeText.setText(welcomeText);
	}
}
