package client.controller;

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
	
	public Controller()
	{	
		this.window = new JFrame();
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
	 * Chiude la finestra del controllo
	 */
	public void close()
	{
		window.dispose();
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
	protected abstract void initListeners();
}
