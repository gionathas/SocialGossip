package client.view;


import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JTextField;

import utils.messages.RequestAccessMessage;

import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

public class Login {

	private JFrame frame;
	private JPasswordField passwordField;
	private JTextField usernameField;

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
		btnLogin.setBounds(312, 397, 117, 48);
		frame.getContentPane().add(btnLogin);
		
		usernameField = new JTextField();
		usernameField.setFont(new Font("Dialog", Font.PLAIN, 18));
		usernameField.setBounds(284, 183, 310, 48);
		frame.getContentPane().add(usernameField);
		usernameField.setColumns(10);
		
		JLabel lblBenvenutoInSocialgossip = new JLabel("Benvenuto in SocialGossip");
		lblBenvenutoInSocialgossip.setFont(new Font("DejaVu Serif", Font.BOLD, 26));
		lblBenvenutoInSocialgossip.setBounds(224, 51, 409, 55);
		frame.getContentPane().add(lblBenvenutoInSocialgossip);
		
		/* EVENT LISTENER */
		
		//al click sul bottone login
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				sendLoginRequest();
			}
		});
	}
	
	/**
	 * Effettua la richiesta di Login al server
	 */
	private void sendLoginRequest()
	{
		
		//TODO implementare operazione in corso,per non mandare piu richieste contemporaneamente
		
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
						//apro connessione con server e creo stream per lettura scrittura
						connection = new Socket("localhost",5000);
						DataInputStream in = new DataInputStream(connection.getInputStream());
						DataOutputStream out = new DataOutputStream(connection.getOutputStream());
												
						//creo il messaggio di richiesta di login
						RequestAccessMessage request = new RequestAccessMessage(usernameField.getText(),passwordField.getPassword());

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
