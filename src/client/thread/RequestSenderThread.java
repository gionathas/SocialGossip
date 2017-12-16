package client.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import communication.messages.RequestMessage;

/**
 * Classe astratta che rappresenta un thread che invia una richiesta al server tramite connessione TCP
 * @author gio
 *
 */
public abstract class RequestSenderThread extends Thread 
{	
	private Socket connection;
	private DataInputStream in;
	private DataOutputStream out;
	protected final String serverName = "localhost";
	protected final int port = 5000;
	protected RequestMessage request;
	protected String response;
	protected boolean init;
	
	
	public RequestSenderThread()
	{
		super();
		connection = new Socket();
		in = null;
		out = null;
		request = null;
		response = null;
		init = false;
	}
	
	public void run()
	{
		init();
		
		if(init)
		{
			try 
			{
				//apro connessione
				connection = new Socket(serverName, port);
				//apro stream di comunicazione
				in = new DataInputStream(connection.getInputStream());
				out = new DataOutputStream(connection.getOutputStream());
				
				createRequest();
				
				//se la richiesta e' stata creata,invio il messaggio
				if(request != null)
				{
					out.writeUTF(request.getJsonMessage());
					
					//non appena arriva la risposta la leggo
					response = in.readUTF();
					
					//analizzo risposta e mando una risposta la server
					analyzeResponse(response);
				}
			} 
			catch(ConnectException e) {
				ConnectErrorHandler();
				e.printStackTrace();
			}
			catch (UnknownHostException e) 
			{
				UnKwownHostErrorHandler();
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				IOErrorHandler();
				e.printStackTrace();
			}
			finally 
			{
				try
				{
					//chiud connessione se aperta
					if(connection != null){
						connection.close();
					}
					
					//chiudo stream input se aperto
					if(in != null) {
						in.close();
					}
					
					//chiudo stream output se aperto
					if(out != null) {
						out.close();
					}
				}
				catch(IOException e)
				{
					IOErrorHandler();
					e.printStackTrace();
				}
			}
		}
	}
	
	protected abstract void init();
	protected abstract void createRequest();
	protected abstract void ConnectErrorHandler();
	protected abstract void UnKwownHostErrorHandler();
	protected abstract void IOErrorHandler();
	protected abstract void analyzeResponse(String response);
}
