package client.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Classe astratta per il controllo di una finestra
 * @author Gionatha Sturba
 *
 */
public abstract class Controller 
{
	protected JFrame window; //finestra relativo al controllo
	protected Socket connection; //connessione TCP con il server
	protected DataInputStream in; //stream input, per ricevere dal server
	protected DataOutputStream out; //stream output,per inviare messaggi al server
	
	public Controller(Socket connection,DataInputStream in,DataOutputStream out)
	{	
		if(connection == null || in == null || out == null)
			throw new NullPointerException();
		
		if(connection.isClosed())
			throw new IllegalArgumentException();
		
		this.window = new JFrame();
		this.connection = connection;
		this.in = in;
		this.out = out;
	}
	
	/** 
	 * Setta visibilita' della finestra del controllo
	 * @param visible
	 */
	public void setVisible(boolean visible)
	{
		window.setVisible(visible);
	}
	
	/**
	 * 
	 * @return la finestra del controllo
	 */
	public JFrame getWindow() {
		return window;
	}
	
	/**
	 * Setta la finestra del controllo
	 * @param frame
	 */
	protected void setWindow(JFrame frame) {
		
		if(window == null)
			throw new NullPointerException();
		
		this.window = frame;
	}
	
	/**
	 * Mostra un messaggio di notifica,specificando se operare in modalita bloccante o meno
	 * @param message messaggio da mostrare
	 * @param title titolo del messaggio
	 * @param block per scegliere se operare in modalita' bloccante
	 */
	public void showInfoMessage(String message,String title,boolean block)
	{
		if(message == null || title == null)
			throw new NullPointerException();
		
		JLabel msg = new JLabel(message,JLabel.CENTER);
		JOptionPane pane = new JOptionPane(msg,JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE);
		JDialog dialog = pane.createDialog(window,title);
		
		dialog.setModal(block);
		dialog.setVisible(true);
	}
	
	/**
	 * Mostra un semplice messaggio di errore
	 * @param message
	 * @param title
	 */
	public void showErrorMessage(String message,String title)
	{
		if(message == null)
			throw new NullPointerException();
		
		JOptionPane.showMessageDialog(window,message,title,JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Metodo per inizializzare eventuali listener dei componenti della finestra
	 */
	protected void initListeners()
	{
		//richiesta logout alla chiusura della finestra
		window.addWindowListener(new WindowAdapter()
	        {
	            @Override
	            public void windowClosing(WindowEvent e)
	            {
	            	closeConnection();
	            	closeWindow();
	            }
	        });
	}
	
	/**
	 * Chiude la finestra controllata
	 */
	public void closeWindow()
	{
		window.setVisible(false);
    	window.dispose();
	}
	
	/**
	 * Chiude la connessione con il server
	 */
	public void closeConnection()
	{
		//chiudiamo la connessione e gli stream
		try {
			if(connection != null)
				connection.close();
			
			if(in != null)
				in.close();
			
			if(out != null)
				out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
