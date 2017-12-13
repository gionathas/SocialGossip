package client.controller;

import javax.swing.JFrame;

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
	
	protected abstract void initListeners();
}
