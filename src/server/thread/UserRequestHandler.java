package server.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import communication.TCPMessages.Message;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.request.InteractionRequest;
import communication.TCPMessages.request.RequestAccessMessage;
import communication.TCPMessages.request.RequestMessage;
import communication.TCPMessages.response.ResponseFailedMessage;
import communication.TCPMessages.response.ResponseMessage;
import communication.TCPMessages.response.ResponseSuccessMessage;
import communication.TCPMessages.response.SuccessFriendship;
import communication.TCPMessages.response.SuccessfulLogin;
import server.model.*;
import server.model.exception.PasswordMismatchingException;
import server.model.exception.SameUserException;
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
	private Network reteSG; //rete social Gossip

	
	public UserRequestHandler(Socket client,Network reteSG)
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
			
			//essendo un messaggio di richiesta,posso prendere il nickname dell'utente mittente
			String nicknameSender = MessageAnalyzer.getNicknameSender(message);
			
			//prendo il tipo del messaggio di richiesta
			RequestMessage.Type requestType = MessageAnalyzer.getRequestMessageType(message);
			
			//nickname non trovato, oppure tipo richiesta non trovato,invio messaggio di errore
			if(nicknameSender == null || requestType == null)
			{
				sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
				return;
			}
			
			
			//sistema per gestire gli accessi a social Gossip
			AccessSystem accessSystem = new AccessSystem(reteSG);
			
			//controllo i possibili casi di richiesta
			switch (requestType) 
			{	
				//richiesta di accesso al sistema
				case ACCESS:
					accessRequestHandler(accessSystem,message,nicknameSender,out);			
					break;
				
				//richiesta logout dal sistema
				case LOGOUT:
					logoutRequestHandler(accessSystem,nicknameSender,out);
					break;
				
				case INTERACTION:
					interactionRequestHandler(message,nicknameSender,out);
					break;

				//richiesta non valida
				default:
					sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
					return;
			}
			
			//DEBUG Stampo rete
			reteSG.stampaRete();
			
			
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
	
	private void interactionRequestHandler(JSONObject message,String nicknameSender,DataOutputStream out)
	{	
		try 
		{
			//cerco utente mittente del messaggio
			User sender = reteSG.cercaUtente(nicknameSender);
			
			//se il mittente non e' registrato,invio messaggio di errore
			if(sender == null)
			{
				sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_NOT_FOUND),out);
				return;
			}
			
			//se il mittente non e' online,invio messaggio di errore
			if(!sender.isOnline()) {
				sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_INVALID_STATUS),out);
				return;
			}
			
			String nicknameReceiver = MessageAnalyzer.getNicknameReceiver(message);
			
			//se non ho trovato il nick del receiver nel messaggio
			if(nicknameReceiver == null) {
				sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
				return;
			}
			
			//cerco utente destinatario del messaggio
			User receiver = reteSG.cercaUtente(nicknameReceiver);
			
			//se il receiver non e' registrato
			if(receiver == null) {
				sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.RECEIVER_USER_NOT_FOUND),out);
				return;
			}
			
			//se gli utenti sono gli stessi
			if(sender.equals(receiver)) {
				sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SAME_USERS),out);
				return;
			}
			
			//controllo il tipo del messaggio di interazione
			InteractionRequest.Type interactionType = MessageAnalyzer.getInteractionType(message);
			
			switch (interactionType) 
			{
				//richiesta di ricerca utente destinatario
				case FIND_USER_REQUEST:
					//controllo gia' fatti rispondo con un messaggio di OK
					sendResponseMessage(new ResponseSuccessMessage(),out);
					break;
				
				//richiesta amicizia con utente destinatario
				case FRIENDSHIP_REQUEST:
					friendshipRequestHandler(sender,receiver,out);
					break;
				
				//TODO inserire altri casi
	
				default:
					break;
			}
			
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Gestione messaggio di richiesta
	 * @param message
	 * @param nickname
	 * @param out
	 * @throws IOException
	 */
	private void accessRequestHandler(AccessSystem accessSystem,JSONObject message,String nickname,DataOutputStream out) throws IOException
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
	 * Gestione richiesta amicizia tra 2 utente
	 * @param a
	 * @param b
	 * @param out
	 * @return
	 * @throws IOException 
	 */
	private void friendshipRequestHandler(User a, User b ,DataOutputStream out) throws IOException
	{
		try 
		{
			boolean friendship = reteSG.nuovaAmicizia(a, b);
			
			//se non erano amici
			if(friendship) 
			{
				//aggiorno amicizia tra i 2 utenti
				a.aggiungiAmico(b);
				b.aggiungiAmico(a);
				
				//invio messaggio di successo al mittente
				sendResponseMessage(new SuccessFriendship(b.isOnline()), out);
				return;
			}
			//se erano gia' amici
			else {
				sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.ALREADY_FRIEND), out);
				return;
			}
		} 
		//casi non possibile in quanto gli utenti sono stati gia' controllati
		catch (UserNotFindException e){
			e.printStackTrace();
		} catch (SameUserException e) {
			e.printStackTrace();
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
	private void loginRequestHandler(AccessSystem accessSystem,String nickname,String password,DataOutputStream out) throws IOException
	{
		List<User> amici = new LinkedList<User>();
		List<ChatRoom> chatRoom = new LinkedList<ChatRoom>();
		
		try 
		{
			//login sul sistema
			accessSystem.logIn(nickname,password,amici,chatRoom);
			
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
			sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_INVALID_STATUS),out);
			e.printStackTrace();
			return;
		} 
		catch (UserNotFindException e) 
		{
			//invio messaggio di errore, utente non trovato
			sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_NOT_FOUND),out);
			e.printStackTrace();
			return;
		}
		
		//TODO operazione e' andata a buon fine mando un messaggio di OK,con la lista degli amici e delle chatroom
		sendResponseMessage(new SuccessfulLogin(amici),out);
	}
	
	/**
	 * Gestione richiesta di logout
	 * @param accessSystem
	 * @param nickname
	 * @param out
	 * @throws IOException
	 */
	private void logoutRequestHandler(AccessSystem accessSystem,String nickname,DataOutputStream out) throws IOException
	{
		try
		{
			accessSystem.logOut(nickname);
		}
		//utente non trovato
		catch(UserNotFindException e) 
		{
			//invio messaggio di errore, utente non trovato
			sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_NOT_FOUND),out);
			e.printStackTrace();
			return;
		} 
		//utente offline
		catch (UserStatusException e) 
		{
			//invio messaggio di errore stato utente non valido
			sendResponseMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_INVALID_STATUS),out);
			e.printStackTrace();
			return;
		}
		
		//operazione e' andata a buon fine mando un messaggio di OK
		sendResponseMessage(new ResponseSuccessMessage(),out);
		
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
	private void registerRequestHandler(AccessSystem accessSystem,String nickname,String password,DataOutputStream out,String language) throws IOException
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
		
		//operazione e' andata a buon fine mando un messaggio di OK
		sendResponseMessage(new ResponseSuccessMessage(),out);
	}
}
