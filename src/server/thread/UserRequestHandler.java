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
import communication.messages.ResponseFailedMessage;
import communication.messages.ResponseMessage;
import communication.messages.ResponseSuccessMessage;
import server.model.SocialGossipAccessSystem;
import server.model.SocialGossipNetwork;
import server.model.exception.PasswordMismatchingException;
import server.model.exception.UserAlreadyRegistered;
import server.model.exception.UserNotFindException;
import server.model.exception.UserStatusException;

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
				//chiudo connessione con il client
				client.close();
				
				//chiudo stream
				if(in != null)
					in.close();
				
				if(out != null)
					out.close();
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
				sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
				return;
			}
			
			//essendo un messaggio di richiesta,posso prendere il nickname dell'utente
			String nickname = MessageAnalyzer.getNickname(message);
			
			//prendo il tipo del messaggio di richiesta
			RequestMessage.Type requestType = MessageAnalyzer.getRequestMessageType(message);
			
			//nickname non trovato, oppure tipo richiesta non trovato,invio messaggio di errore
			if(nickname == null || requestType == null)
			{
				sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
				return;
			}
			
			
			//sistema per gestire gli accessi a social Gossip
			SocialGossipAccessSystem accessSystem = new SocialGossipAccessSystem(reteSG);
			
			//controllo i possibili casi di richiesta
			switch (requestType) 
			{	
				//richiesta di accesso al sistema
				case ACCESS:
					accessRequestHandler(accessSystem,message,nickname,out);			
					break;
				
				//richiesta logout dal sistema
				case LOGOUT:
					logoutRequestHandler(accessSystem,nickname,out);
					break;

				//richiesta non valida
				default:
					sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
					return;
			}
			
			//DEBUG Stampo rete
			reteSG.stampaRete();
			
			//operazione e' andata a buon fine mando un messaggio di OK
			sendResponseMessage(new ResponseSuccessMessage(),out);
			
			
		} 
		//errore lettura messaggio
		catch (ParseException  | NullPointerException e) 
		{
			try {
				//invio messaggio di errore richiesta non valida
				sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			e.printStackTrace();
			return;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} 
	}
	
	private void sendResponseMessage(ResponseMessage response,DataOutputStream out) throws IOException
	{
		out.writeUTF(response.getJsonMessage());
	}
	
	/**
	 * Gestione messaggio di richiesta
	 * @param message
	 * @param nickname
	 * @param out
	 * @throws IOException
	 */
	private void accessRequestHandler(SocialGossipAccessSystem accessSystem,JSONObject message,String nickname,DataOutputStream out) throws IOException
	{	
		//essendo una richiesta di accesso,prendo la password
		String password = MessageAnalyzer.getPassword(message);
		
		//leggo tipo richiesta di accesso
		RequestAccessMessage.Type requestAccessType = MessageAnalyzer.getRequestAccessMessageType(message);
		
		//caso password non trovata o tipo richiesta di accesso non trovato
		if(password == null || requestAccessType == null)
		{
			sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
			return;
		}
		
		//controllo i possibili casi di richiesta di accesso
		switch (requestAccessType) 
		{
			//caso richiesta di login
			case LOGIN:								
				
				loginRequestHandler(accessSystem,nickname,password,out);
				break;
			
			//caso richiesta di registrazione
			case REGISTER:
				
				//prendo il codice della lingua
				String language = MessageAnalyzer.getLanguage(message);
				
				//caso lingua non trovata
				if(language == null)
				{
					sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
					return;
				}
				
				//procedura registrazione
				registerRequestHandler(accessSystem,nickname,password,out,language);
				
				break;
				
			//caso messaggio non valido
			default:
				sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
				return;
				
		}
	}
	
	
	/**
	 * Gestione richiesta login
	 * @param accessSystem
	 * @param nickname
	 * @param password
	 * @param out
	 * @throws IOException
	 */
	private void loginRequestHandler(SocialGossipAccessSystem accessSystem,String nickname,String password,DataOutputStream out) throws IOException
	{
		try 
		{
			//login sul sistema
			accessSystem.logIn(nickname,password);
			
		} 
		//caso password errata
		catch (PasswordMismatchingException e) 
		{
			//invio messaggio di errore password errata
			sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.PASSWORD_MISMATCH),out);
			e.printStackTrace();
			return;
		} 
		//caso utente gia' online
		catch (UserStatusException e) 
		{
			//invio messaggio di errore stato utente non valido
			sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.USER_INVALID_STATUS),out);
			e.printStackTrace();
			return;
		} 
		catch (UserNotFindException e) 
		{
			//invio messaggio di errore, utente non trovato
			sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.USER_NOT_FOUND),out);
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Gestione richiesta di logout
	 * @param accessSystem
	 * @param nickname
	 * @param out
	 * @throws IOException
	 */
	private void logoutRequestHandler(SocialGossipAccessSystem accessSystem,String nickname,DataOutputStream out) throws IOException
	{
		try
		{
			accessSystem.logOut(nickname);
		}
		//utente non trovato
		catch(UserNotFindException e) 
		{
			//invio messaggio di errore, utente non trovato
			sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.USER_NOT_FOUND),out);
			e.printStackTrace();
			return;
		} 
		//utente offline
		catch (UserStatusException e) 
		{
			//invio messaggio di errore stato utente non valido
			sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.USER_INVALID_STATUS),out);
			e.printStackTrace();
			return;
		}
		
	}
	
	/**
	 * Gestione richiesta registrazione
	 * @param accessSystem
	 * @param nickname
	 * @param password
	 * @param out
	 * @param language
	 * @throws IOException
	 */
	private void registerRequestHandler(SocialGossipAccessSystem accessSystem,String nickname,String password,DataOutputStream out,String language) throws IOException
	{
		//avvio procedura di registrazione
		try 
		{
			accessSystem.register(nickname,password,language);
		} 
		//caso utente gia' registrato con quel nick
		catch (UserAlreadyRegistered e) 
		{	
			sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.USER_ALREADY_REGISTERED),out);
			e.printStackTrace();
			return;
}
	}
}
