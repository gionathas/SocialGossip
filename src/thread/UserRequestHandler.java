package thread;

import java.awt.TrayIcon.MessageType;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import communication.MessageAnalyzer;
import communication.messages.Message;
import communication.messages.RequestAccessMessage;
import communication.messages.RequestMessage;
import server.model.Utente;
import utils.graph.Grafo;

/**
 * Thread del server che si occupa di gestire una nuova richiesta da parte di un client
 * @author gio
 *
 */
public class UserRequestHandler implements Runnable
{
	private Socket client;
	private Grafo<Utente> reteSG; //rete social Gossip

	
	public UserRequestHandler(Socket client,Grafo<Utente> rete)
	{
		super();

		if(client == null || rete == null)
			throw new NullPointerException();
		
		this.client = client;
		this.reteSG = rete;
		
	}
	
	public void run()
	{
		DataInputStream in = null;
		DataOutputStream out = null;
		
		
		try 
		{
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());
			
			//leggo messaggio inviatomi dal client
			String request = in.readUTF();
			System.out.println("received: "+request);
			
			//analizzo richiesta del client
			analyzeRequestMessage(request,out);
			
			//rispondo al client
			out.writeUTF("login request received\ntest");
			
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally 
		{
			try 
			{
				in.close();
				out.close();
				client.close();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void analyzeRequestMessage(String StringMessage,DataOutputStream out)
	{	
		try 
		{
			//parso il messaggio arrivato 
			JSONObject message = MessageAnalyzer.parse(StringMessage);
			
			//controllo che sia un messaggio di richiesta altrimenti invio messaggio di errore
			if(MessageAnalyzer.getMessageType(message) != Message.Type.REQUEST)
			{
				//TODO inviare messaggio di errore messaggio invalido
			}
			
			//essendo un messaggio di richiesta,posso prendere il nickname dell'utente
			String nickname = MessageAnalyzer.getNickname(message);
			
			//nickname non trovato
			if(nickname == null)
			{
				//TODO messaggio di errore
			}
			
			//controlliamo di che tipo di messaggio di richiesta si tratta
			RequestMessage.Type requestType = MessageAnalyzer.getRequestMessageType(message);
			
			//controllo i possibili casi di richiesta
			switch (requestType) 
			{
				//richiesta di accesso al sistema
				case ACCESS:
					//controllo tipo della richiesta di accesso
					RequestAccessMessage.Type requestAccessType = MessageAnalyzer.getRequestAccessMessageType(message);
					
					//TODO 
					break;
	
				default:
					//invio messaggio di errore
					break;
			}
			
			
		} 
		catch (ParseException  | NullPointerException e1) 
		{
			//TODO inviare una risposta di errore di messaggio non valido
			e1.printStackTrace();
		}
	}
}
