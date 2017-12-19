package client.controller;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Classe astratta del controllo di una finestra
 * @author gio
 *
 */
public abstract class Controller 
{
	protected JFrame window;
	
	public Controller()
	{	
		this.window = new JFrame();
	}
	
	public void setVisible(boolean visible)
	{
		window.setVisible(visible);
	}
	
	public void close()
	{
		window.dispose();
	}
	
	public JFrame getWindow() {
		return window;
	}
	
	protected void setWindow(JFrame frame) {
		this.window = frame;
	}
	
	/**
	 * Mostra un messaggio di notifica,specificando se operare in modalita bloccante o meno
	 * @param message
	 * @param title
	 * @param block
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
	
	protected abstract void initListeners();
}
