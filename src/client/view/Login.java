package client.view;


import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JTextField;

import client.RequestSender;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.awt.event.ActionEvent;

public class Login {

	private JFrame frame;
	private JPasswordField passwordField;
	private JTextField textField;

	/**
	 * Create the application.
	 */
	public Login() {
		initializeWindowContent();
	}
	
	/**
	 * Inizializza il contenuto della finestra
	 */
	private void initializeWindowContent() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Dialog", Font.BOLD, 20));
		lblUsername.setBounds(160, 193, 128, 26);
		frame.getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPassword.setBounds(160, 286, 110, 26);
		frame.getContentPane().add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Dialog", Font.PLAIN, 18));
		passwordField.setBounds(284, 277, 310, 48);
		frame.getContentPane().add(passwordField);
		
		JButton btnLogin = new JButton("Login");
		
		//al click su login
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				LoginRequest();
			}
		});
		btnLogin.setBounds(312, 397, 117, 48);
		frame.getContentPane().add(btnLogin);
		
		textField = new JTextField();
		textField.setFont(new Font("Dialog", Font.PLAIN, 18));
		textField.setBounds(284, 183, 310, 48);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblBenvenutoInSocialgossip = new JLabel("Benvenuto in SocialGossip");
		lblBenvenutoInSocialgossip.setFont(new Font("DejaVu Serif", Font.BOLD, 26));
		lblBenvenutoInSocialgossip.setBounds(224, 51, 409, 55);
		frame.getContentPane().add(lblBenvenutoInSocialgossip);
	}
	
	private void LoginRequest()
	{
		Thread thread = new Thread(new Runnable() {
			public void run() {
				RequestSender sender = new RequestSender();
				Socket connection = null;
				
				try {
					//invio la richiesta di login al server
					connection = sender.sendLoginRequest();
					
					//attendo risposta server
					//TODO creare classe responseAnalyzer
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					
					JOptionPane.showMessageDialog(null,reader.readLine());
					
					
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null,"Errore nella richiesta di login");
					e.printStackTrace();
				}
				finally {
					if(connection != null)
						try {
							connection.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		});
		
		thread.start();
	}
	
	public void showWindow()
	{
		frame.setVisible(true);
	}
	
	private void closeWindow()
	{
		frame.setVisible(false);
		frame.dispose();
	}

}
