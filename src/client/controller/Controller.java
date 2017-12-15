package client.controller;

import javax.swing.JFrame;
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
	
	protected void setWindow(JFrame frame) {
		this.window = frame;
	}
	
	public void showInfoMessage(String message)
	{
		if(message == null)
			throw new NullPointerException();
		
		JOptionPane.showMessageDialog(window,message);
	}
	
	public void showErrorMessage(String message,String title)
	{
		if(message == null)
			throw new NullPointerException();
		
		JOptionPane.showMessageDialog(window,message,title,JOptionPane.ERROR_MESSAGE);
	}
	
	protected abstract void initListeners();
}
