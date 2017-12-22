package client.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.JButton;
import java.awt.SystemColor;
import java.awt.Font;

public class ChatWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7018723357317188387L;
	private JTextArea conversationArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatWindow frame = new ChatWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChatWindow() {
		getContentPane().setBackground(SystemColor.desktop);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 370, 400);
		setResizable(false);
		setTitle("CHAT");
		getContentPane().setLayout(null);
		
		Border border = BorderFactory.createLineBorder(Color.BLACK);

		conversationArea = new JTextArea();
		conversationArea.setBackground(new Color(240, 248, 255));
		conversationArea.setEditable(false);
		conversationArea.setBounds(10, 12, 350, 250);
		conversationArea.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		getContentPane().add(conversationArea);
		
		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Dialog", Font.PLAIN, 17));
		textArea.setBackground(new Color(240, 248, 255));
		textArea.setBounds(10, 300, 250, 50);
		textArea.setBorder(border);
		getContentPane().add(textArea);
		
		JButton btnNewButton = new JButton("Invia");
		btnNewButton.setBounds(270, 300, 90, 50);
		getContentPane().add(btnNewButton);
		
		JButton btnInviaFile = new JButton("Invia File");
		btnInviaFile.setBounds(10, 276, 100, 20);
		getContentPane().add(btnInviaFile);
		
	}
}
