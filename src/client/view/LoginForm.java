package client.view;


import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JTextField;


import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;

import client.controller.LoginController;
import communication.messages.LoginRequest;
import communication.messages.RequestAccessMessage;

import javax.swing.JSeparator;
import java.awt.SystemColor;

public class LoginForm {

	private JFrame frmSocialgossip;
	private JPasswordField passwordField;
	private JTextField usernameField;
	private JButton btnLogin;
	private JButton RegisterButton;
	private JLabel attesa;


	/**
	 * Create the application.
	 */
	public LoginForm() 
	{
		initializeWindowContent();
	}
	
	/**
	 * Inizializza il contenuto della finestra
	 */
	private void initializeWindowContent() {
		frmSocialgossip = new JFrame();
		frmSocialgossip.setTitle("SocialGossip");
		frmSocialgossip.setResizable(false);
		frmSocialgossip.getContentPane().setBackground(new Color(51, 204, 255));
		frmSocialgossip.setBounds(100, 100, 800, 600);
		frmSocialgossip.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSocialgossip.getContentPane().setLayout(null);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setForeground(Color.BLACK);
		lblUsername.setFont(new Font("Dialog", Font.BOLD, 20));
		lblUsername.setBounds(250, 146, 128, 26);
		frmSocialgossip.getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setForeground(Color.BLACK);
		lblPassword.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPassword.setBounds(250, 240, 110, 26);
		frmSocialgossip.getContentPane().add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setToolTipText("Inserisci Password");
		passwordField.setFont(new Font("Dialog", Font.PLAIN, 18));
		passwordField.setBounds(250, 272, 310, 48);
		frmSocialgossip.getContentPane().add(passwordField);
		
		btnLogin = new JButton("Login");
		btnLogin.setToolTipText("Invia richiesta di Login");
		btnLogin.setFont(new Font("Dialog", Font.BOLD, 14));
		btnLogin.setBackground(UIManager.getColor("Button.disabledText"));
		btnLogin.setBounds(250, 349, 310, 48);
		frmSocialgossip.getContentPane().add(btnLogin);
		
		usernameField = new JTextField();
		usernameField.setToolTipText("Inserisci Username");
		usernameField.setFont(new Font("Dialog", Font.PLAIN, 18));
		usernameField.setBounds(250, 183, 310, 48);
		usernameField.setForeground(new Color(150, 150, 150));
		frmSocialgossip.getContentPane().add(usernameField);
		usernameField.setColumns(10);
		
		JLabel lblBenvenutoInSocialgossip = new JLabel("Benvenuto in SocialGossip");
		lblBenvenutoInSocialgossip.setForeground(Color.BLACK);
		lblBenvenutoInSocialgossip.setFont(new Font("DejaVu Serif", Font.BOLD, 28));
		lblBenvenutoInSocialgossip.setBounds(205, 38, 460, 55);
		frmSocialgossip.getContentPane().add(lblBenvenutoInSocialgossip);
		
		attesa = new JLabel("In Attesa di risposta dal server..");
		attesa.setForeground(Color.BLACK);
		attesa.setVisible(false);
		attesa.setBounds(12, 536, 236, 15);
		
		frmSocialgossip.getContentPane().add(attesa);
		
		RegisterButton = new JButton("Registrati");
		RegisterButton.setToolTipText("Registrati a SocialGossip");
		RegisterButton.setFont(new Font("Dialog", Font.BOLD, 14));
		RegisterButton.setBounds(250, 430, 310, 48);
		frmSocialgossip.getContentPane().add(RegisterButton);
		
		JLabel lblOppure = new JLabel("oppure");
		lblOppure.setForeground(Color.BLACK);
		lblOppure.setFont(new Font("DejaVu Serif Condensed", Font.BOLD, 12));
		lblOppure.setBounds(382, 405, 70, 15);
		frmSocialgossip.getContentPane().add(lblOppure);
		
		JSeparator separator = new JSeparator();
		separator.setBackground(new Color(0, 0, 0));
		separator.setBounds(0, 120, 800, 10);
		frmSocialgossip.getContentPane().add(separator);
	}
	
	public JButton getBtnLogin() {
		return btnLogin;
	}

	public JButton getRegisterButton() {
		return RegisterButton;
	}

	public JFrame getFrame() {
		return frmSocialgossip;
	}
	
	public void setVisible(boolean visible) {
		frmSocialgossip.setVisible(visible);
	}
	
	public void closeWindow() {
		frmSocialgossip.dispose();
	}

	public JPasswordField getPasswordField() {
		return passwordField;
	}

	public JTextField getUsernameField() {
		return usernameField;
	}

	public JLabel getAttesa() {
		return attesa;
	}
}
