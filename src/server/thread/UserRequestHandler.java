package server.thread;

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
import server.model.SocialGossipNetwork;
import server.model.exception.PasswordMismatchingException;
import server.model.exception.UserNotFindException;
import server.model.exception.UserStatusException;
import server.model.User;

/**
 * Thread del server che si occupa di gestire una nuova richiesta da parte di un client
 * @author gio
 *
 */
public class UserRequestHandler implements Runnable
{
	private Socket client;
	private SocialGossipNetwork reteSG; //rete social Gossip

	
	public UserRequestHandler(Socket client,SocialGossipNetwork reteSG)
	{
		super();

		if(client == null || reteSG == null)
			throw new NullPointerException();
		
		this.client = client;
		this.reteSG = reteSG;
		
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
			out.writeUTF("request received");
			
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
				return;
			}
			
			//essendo un messaggio di richiesta,posso prendere il nickname dell'utente
			String nickname = MessageAnalyzer.getNickname(message);
			
			//nickname non trovato
			if(nickname == null)
			{
				//TODO messaggio di errore
				return;
			}
			
			//controlliamo di che tipo di messaggio di richiesta si tratta
			RequestMessage.Type requestType = MessageAnalyzer.getRequestMessageType(message);
			
			//controllo i possibili casi di richiesta
			switch (requestType) 
			{
				//richiesta di accesso al sistema
				case ACCESS:
					//essendo una richiesta di accesso,prendo la password
					char password[] = MessageAnalyzer.getPassword(message);
					
					//caso password non trovata
					if(password == null)
					{
						//TODO messaggio di errore
						return;
					}
					
					//leggo tipo richiesta di accesso
					RequestAccessMessage.Type requestAccessType = MessageAnalyzer.getRequestAccessMessageType(message);
					
					//controllo i possibili casi di richiesta di accesso
					switch (requestAccessType) 
					{
						case LOGIN:
							User userToLog = new User(nickname,password);
							
						//avvio procedura di login
						try 
						{
							reteSG.logInUtente(userToLog);
							
						} 
						catch (PasswordMismatchingException e) 
						{
							//TODO inviare messaggio di errore password non corrispondenti
							e.printStackTrace();
							return;
						} 
						catch (UserStatusException e) 
						{
							// TODO inviare messaggio di errore utente gia online
							e.printStackTrace();
							return;
						}
							
							break;
						
						case REGISTER:
							
							break;
							
						
						default:
							//TODO messaggio di errore
							return;
					}
					
					//TODO se l'operazione e' andata a buon fine mando un messaggio di OK
					break;
	
				default:
					//TODO invio messaggio di errore
					return;
			}
			
			
		} 
		catch (ParseException  | NullPointerException e) 
		{
			//TODO inviare una risposta di errore di messaggio non valido
			e.printStackTrace();
			return;
		}
		catch (UserNotFindException e) 
		{
			// TODO inviare messaggio di errore utente richiesto non trovato
			e.printStackTrace();
			return;
		} 
	}
}
