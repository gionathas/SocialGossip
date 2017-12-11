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

import communication.messages.LoginRequest;
import communication.messages.RequestAccessMessage;

import javax.swing.JSeparator;

public class Login {

	private JFrame frame;
	private JPasswordField passwordField;
	private JTextField usernameField;
	private AtomicBoolean canSendLogin = new AtomicBoolean(true);
	private JLabel attesa;
	private JLabel hint1;
	private JLabel hint2;
	private JLabel hint0;

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
		frame.getContentPane().setBackground(new Color(176, 224, 230));
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Dialog", Font.BOLD, 20));
		lblUsername.setBounds(250, 146, 128, 26);
		frame.getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPassword.setBounds(250, 240, 110, 26);
		frame.getContentPane().add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Dialog", Font.PLAIN, 18));
		passwordField.setBounds(250, 272, 310, 48);
		frame.getContentPane().add(passwordField);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setFont(new Font("Dialog", Font.BOLD, 14));
		btnLogin.setBackground(UIManager.getColor("Button.disabledText"));
		btnLogin.setBounds(250, 349, 310, 48);
		frame.getContentPane().add(btnLogin);
		
		usernameField = new JTextField();
		usernameField.setFont(new Font("Dialog", Font.PLAIN, 18));
		usernameField.setBounds(250, 183, 310, 48);
		usernameField.setForeground(new Color(150, 150, 150));
		frame.getContentPane().add(usernameField);
		usernameField.setColumns(10);
		
		JLabel lblBenvenutoInSocialgossip = new JLabel("Benvenuto in SocialGossip");
		lblBenvenutoInSocialgossip.setFont(new Font("DejaVu Serif", Font.BOLD, 28));
		lblBenvenutoInSocialgossip.setBounds(205, 38, 460, 55);
		frame.getContentPane().add(lblBenvenutoInSocialgossip);
		
		attesa = new JLabel("In Attesa di risposta dal server..");
		attesa.setVisible(false);
		attesa.setBounds(12, 536, 236, 15);
		
		frame.getContentPane().add(attesa);
		
		hint1 = new JLabel("Caratteri Minimi: 3");
		hint1.setForeground(new Color(255, 51, 0));
		hint1.setBounds(577, 210, 179, 15);
		hint1.setVisible(false);
		frame.getContentPane().add(hint1);
		
		hint2 = new JLabel("Caratteri Minimi: 5");
		hint2.setForeground(new Color(255, 51, 0));
		hint2.setBounds(577, 300, 179, 15);
		hint2.setVisible(false);
		frame.getContentPane().add(hint2);
		
		hint0 = new JLabel("Spazi non ammessi");
		hint0.setForeground(new Color(255, 51, 0));
		hint0.setBounds(577, 186, 179, 15);
		hint0.setVisible(false);
		frame.getContentPane().add(hint0);
		
		JButton RegisterButton = new JButton("Registrati");
		RegisterButton.setFont(new Font("Dialog", Font.BOLD, 14));
		RegisterButton.setBounds(250, 430, 310, 48);
		frame.getContentPane().add(RegisterButton);
		
		JLabel lblOppure = new JLabel("oppure");
		lblOppure.setFont(new Font("DejaVu Serif Condensed", Font.BOLD, 12));
		lblOppure.setBounds(382, 405, 70, 15);
		frame.getContentPane().add(lblOppure);
		
		JSeparator separator = new JSeparator();
		separator.setBackground(new Color(0, 0, 0));
		separator.setBounds(0, 120, 800, 10);
		frame.getContentPane().add(separator);
		
		/* EVENT LISTENER */
		
		//al click sul bottone login
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				//se non ho gia' mandato una richiesta di login
				if(canSendLogin.get() == true)
				{
					canSendLogin.set(false);
					sendLoginRequest();
				}
			}
		});
	}
	
	/**
	 * Effettua la richiesta di Login al server
	 */
	private void sendLoginRequest()
	{
				
		//TODO inserire numero caratteri minimo nell'interfaccia
		
		/* Un thread si occupera' di gestire la comunicazione con il server */
		Thread thread = new Thread(new Runnable() {
			public void run() 
			{
				//prima di inviare la richiesta controllo i dati inseriti
				if(checkInput(usernameField.getText(),passwordField.getPassword()))
				{
					Socket connection = null;
					
					try 
					{
						attesa.setVisible(true);
						
						//apro connessione con server e creo stream per lettura scrittura
						connection = new Socket("localhost",5000);
						DataInputStream in = new DataInputStream(connection.getInputStream());
						DataOutputStream out = new DataOutputStream(connection.getOutputStream());
												
						//creo il messaggio di richiesta di login
						LoginRequest request = new LoginRequest(usernameField.getText(),new String(passwordField.getPassword()));

						//invio la richiesta di login al server
						out.writeUTF(request.getJsonMessage());
						
						//attendo risposta server
						String response = in.readUTF();
						
						//mostro risposta server
						JOptionPane.showMessageDialog(null,response);
						
						in.close();
						out.close();
												
					}
					catch(ConnectException e)
					{
						JOptionPane.showMessageDialog(null,"Servizio offline");
						e.printStackTrace();

					}
					catch(UnknownHostException e)
					{
						JOptionPane.showMessageDialog(null,"Server non trovato");
						e.printStackTrace();

					}
					catch (IOException e) 
					{
						JOptionPane.showMessageDialog(null,"Errore nella richiesta di login");
						e.printStackTrace();
					}
					
					finally 
					{
						if(connection != null)
						{
							try {
								connection.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						canSendLogin.set(true);
						attesa.setVisible(false);
					}

				}
				//input form non corretta
				else {
					canSendLogin.set(true);
					hint0.setVisible(true);
					hint1.setVisible(true);
					hint2.setVisible(true);
				}
			}
				
		});
		
		//avvio thread comunicazione
		thread.start();
	}
	
	private boolean checkInput(String nick,char[] password)
	{
		final int MIN_PASS_CHAR = 5;
		final int MIN_USER_CHAR = 3;
		
		if(nick == null || password == null || nick.isEmpty() || nick.length() < MIN_USER_CHAR || password.length < MIN_PASS_CHAR ||
				nick.contains(" "))
		{			
			JOptionPane.showMessageDialog(null,"Username o password non validi");
			return false;
		}
		else {
			return true;
		}
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
