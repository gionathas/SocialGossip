package server.thread;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.Pipe.SinkChannel;
import java.util.LinkedList;
import java.util.List;

import javax.xml.ws.soap.AddressingFeature.Responses;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import communication.TCPMessages.Message;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.notification.NewChatMessage;
import communication.TCPMessages.notification.NewIncomingFile;
import communication.TCPMessages.request.RequestMessage;
import communication.TCPMessages.request.access.RequestAccessMessage;
import communication.TCPMessages.request.chatroom.ChatRoomRequest;
import communication.TCPMessages.request.interaction.InteractionRequest;
import communication.TCPMessages.request.interaction.SendMessageRequest;
import communication.TCPMessages.response.ResponseMessage;
import communication.TCPMessages.response.fail.ResponseFailedMessage;
import communication.TCPMessages.response.success.ResponseSuccessMessage;
import communication.TCPMessages.response.success.SuccessFriendship;
import communication.TCPMessages.response.success.SuccessfulLogin;
import communication.TCPMessages.response.success.SuccessfulRegistration;
import server.model.*;
import server.model.exception.PasswordMismatchingException;
import server.model.exception.SameUserException;
import server.model.exception.UserAlreadyRegistered;
import server.model.exception.UserNotFindException;
import server.model.exception.UserStatusException;

/**
 * Thread del server che si occupa di gestire una nuova richiesta da parte di un client
 * @author Gionatha Sturba
 */
public class UserRequestHandler implements Runnable
{
	private Socket client; //connessioni TCP con il client
	
	private Network reteSG; //rete degli utenti di Social Gossip
	private List<ChatRoom> chatrooms;
	private List<NotificationUserChatChannel> notificationUsersChatMessage; //canali per notificare gli utenti dell'arrivo dei messaggi 
	
	public static final String FIRST_MULTICAST_ADDR = "224.0.0.1";
	public static final String LAST_MULTICAST_ADDR = "224.0.0.255";
	public static final int FIRST_MULTICAST_PORT = 8500;
	public static final int LAST_MULTICAST_PORT = 8755;
	
	private boolean isNotificationThread = false;


	public UserRequestHandler(Socket client,Network reteSG,List<ChatRoom> chatrooms,List<NotificationUserChatChannel> notificationUsersChatMessage)
	{
		super();

		if(client == null || reteSG == null || chatrooms == null)
			throw new NullPointerException();
		
		this.client = client;
		this.reteSG = reteSG;
		this.chatrooms = chatrooms;
		this.notificationUsersChatMessage = notificationUsersChatMessage;
		
	}
	
	public void run()
	{
		DataInputStream in = null;
		DataOutputStream out = null;
		
		
		try 
		{
			in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
			out = new DataOutputStream(client.getOutputStream());
			
			//fin quando il client mi invia richieste
			while(true)
			{
				try 
				{
					//leggo messaggio inviatomi dal client
					String request = in.readUTF();
					System.out.println("received: "+request);
					
					//analizzo richiesta del client
					analyzeRequestMessage(request,out);
					
					//se un thread che ha avviato un canale di notifica puo' terminare
					if(isNotificationThread)
						break;
				}
				//client ha chiuso la connessione
				catch(EOFException e) {
					System.out.println("Chiusura client");
					break;
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} 
		catch (IOException e1) {
			e1.printStackTrace();
		}
		//chiudo connessione e stream
		finally 
		{
			if(!isNotificationThread)
			{
				try {
					if(client != null)
						client.close();
					if(in != null)
						in.close();
					if(out != null)
						out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * Analizza la richiesta del client
	 * @param StringMessage messaggio del client
	 * @param out stream per rispondere al client
	 */
	private void analyzeRequestMessage(String StringMessage,DataOutputStream out)
	{	
		try 
		{
			//parso il messaggio arrivato 
			JSONObject message = MessageAnalyzer.parse(StringMessage);
			
			//controllo che sia un messaggio di richiesta altrimenti invio messaggio di errore
			if(MessageAnalyzer.getMessageType(message) != Message.Type.REQUEST)
			{
				sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
				return;
			}
			
			//essendo un messaggio di richiesta,posso prendere il nickname dell'utente mittente
			String nicknameSender = MessageAnalyzer.getNicknameSender(message);
			
			//prendo il tipo del messaggio di richiesta
			RequestMessage.Type requestType = MessageAnalyzer.getRequestMessageType(message);
			
			//nickname non trovato, oppure tipo richiesta non trovato,invio messaggio di errore
			if(nicknameSender == null || requestType == null)
			{
				sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
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
				
				//richiesta interazione tra utenti o chatroom
				case INTERACTION:
					interactionRequestHandler(message,nicknameSender,out);
					break;
				
				//richiesta registrazione canale notifiche messaggi chat
				case CHAT_NOTIFICATION_CHAN:
					chatNotificationChannelRequestHandler(nicknameSender,out);
					break;
				
				//richiesta azione che coinvlge una chatroom
				case CHATROOM_REQUEST:
					ChatRoomRequestHandler(message,nicknameSender,out);
					break;
						
				//richiesta non valida
				default:
					sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
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
				sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
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
	
	/**
	 * Invia una risposta al client
	 * @param response risposta da inviare
	 * @param out stream sulla quale inviare la risposta
	 * @throws IOException se ci sono errori nell'invio della risposta
	 */
	private void sendMessage(Message response,DataOutputStream out) throws IOException
	{
		out.writeUTF(response.getJsonMessage());
	}
	
	private void ChatRoomRequestHandler(JSONObject message,String nicknameSender,DataOutputStream out) throws IOException
	{		
		//cerco utente mittente del messaggio
		User sender = reteSG.cercaUtente(nicknameSender);
		
		//se il mittente non e' registrato,invio messaggio di errore
		if(sender == null)
		{
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_NOT_FOUND),out);
			return;
		}
		
		//se il mittente non e' online,invio messaggio di errore
		if(!sender.isOnline()) {
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_INVALID_STATUS),out);
			return;
		}
		
		String chatroomName = MessageAnalyzer.getChatRoomName(message);
		
		//se non e' stata trovata il nome della chatrooms
		if(chatroomName == null)
		{
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
			return;
		}
		
		//controllo il tipo della richiesta che coinvolge la chatroom
		ChatRoomRequest.ChatroomRequests type = MessageAnalyzer.getChatRoomRequestType(message);
		
		//tipo richiesta non trovato
		if(type == null) {
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
			return;
		}
		
		switch (type) {
		case NEW_CHATROOM:
			newChatRoomRequestHandler(sender,chatroomName,out);
			break;
			
		case JOIN_CHATROOM:
			//joinChatRoomRequestHandler(sender,chatroomName,out);
			break;
		
		default:
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
			break;
		}
		
		
	}
	
	private void newChatRoomRequestHandler(User sender,String chatroomName,DataOutputStream out) throws IOException
	{
		//controllo se la chatroom esiste gia'
		synchronized (chatrooms) {
			
			//chatroom gia' esistente
			if(chatrooms.contains(new ChatRoom(chatroomName)))
			{
				sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.CHATROOM_ALREADY_REGISTERED), out);
				return;
			}
			//posso creare la nuvoa chatroom
			else {
				//creo indirizzo della chatroom
				int port = FIRST_MULTICAST_PORT + chatrooms.size();
				String address = getNewChatRoomAddress();
				
				//indirizzi non piu disponibili
				if(address == null)
				{
					sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.CANNOT_CREATE_CHATROOM), out);
					return;
				}
				
				//creo l'indirizzo della chatroom assegnatogli
				InetAddress addr = InetAddress.getByName(address);
				
				ChatRoom newChatRoom = new ChatRoom(chatroomName,addr,port);
				
				//aggiungo l'utente che ha creato il gruppo. Essendo il primo e' l'admin
				try {
					newChatRoom.addNewSubscriber(sender);
				} 
				catch (UserAlreadyRegistered e) {
					sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.USER_ALREADY_REGISTERED), out);
					return;
				}
				
				//aggiungo chatroom alla lista
				chatrooms.add(newChatRoom);
			}
			
			//mando un messaggio di ok al sender
			sendMessage(new ResponseSuccessMessage(), out);
				
		}
	}
	
	private String getNewChatRoomAddress()
	{		
		String[] byteIP = FIRST_MULTICAST_ADDR.split(".");
		
		Integer offset = Integer.parseInt(byteIP[3]);
		
		offset = offset + chatrooms.size();
		
		//se non ci sono piu indirizzi dispoibili
		if(offset.equals(256))
			return null;
		
		return new String(byteIP[0]+byteIP[1]+byteIP[2]+offset.toString());
	}
	
	private void chatNotificationChannelRequestHandler(String nickname,DataOutputStream out) throws IOException
	{
		isNotificationThread = true;
		
		//cerco utente mittente del messaggio
		User user = reteSG.cercaUtente(nickname);
		
		//se il mittente non e' registrato,invio messaggio di errore
		if(user == null)
		{
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_NOT_FOUND),out);
			return;
		}
		
		//se il mittente non e' online,invio messaggio di errore
		if(!user.isOnline()) {
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_INVALID_STATUS),out);
			return;
		}
		
		//utente valido registro il canale per ricevere notifiche dei messaggi dagli altri utenti
		synchronized (notificationUsersChatMessage) {
			
			NotificationUserChatChannel channel = new NotificationUserChatChannel(user,client);
			
			//aggiungo canale alla lista dei canali
			notificationUsersChatMessage.add(channel);
			
			System.out.println(notificationUsersChatMessage.toString());

		}
		
		//mando messaggio di OK al mittente
		sendMessage(new ResponseSuccessMessage(),out);
	}
	
	/**
	 * Gestisce una richiesta di interazione da parte dell'utente
	 * @param message messaggio del client
	 * @param nicknameSender nickname dell'utente sender
	 * @param out stream sulla quale rispondere al clients
	 */
	private void interactionRequestHandler(JSONObject message,String nicknameSender,DataOutputStream out)
	{	
		try 
		{
			//cerco utente mittente del messaggio
			User sender = reteSG.cercaUtente(nicknameSender);
			
			//se il mittente non e' registrato,invio messaggio di errore
			if(sender == null)
			{
				sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_NOT_FOUND),out);
				return;
			}
			
			//se il mittente non e' online,invio messaggio di errore
			if(!sender.isOnline()) {
				sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_INVALID_STATUS),out);
				return;
			}
			
			String nicknameReceiver = MessageAnalyzer.getNicknameReceiver(message);
			
			//se non ho trovato il nick del receiver nel messaggio
			if(nicknameReceiver == null) {
				sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
				return;
			}
			
			//cerco utente destinatario del messaggio
			User receiver = reteSG.cercaUtente(nicknameReceiver);
			
			//se il receiver non e' registrato
			if(receiver == null) {
				sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.RECEIVER_USER_NOT_FOUND),out);
				return;
			}
			
			//se gli utenti sono gli stessi
			if(sender.equals(receiver)) {
				sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SAME_USERS),out);
				return;
			}
			
			//controllo il tipo del messaggio di interazione
			InteractionRequest.Type interactionType = MessageAnalyzer.getInteractionType(message);
			
			switch (interactionType) 
			{
				//richiesta di ricerca utente destinatario
				case FIND_USER_REQUEST:
					//controllo gia' fatti rispondo con un messaggio di OK
					sendMessage(new ResponseSuccessMessage(),out);
					break;
				
				//richiesta amicizia con utente destinatario
				case FRIENDSHIP_REQUEST:
					friendshipRequestHandler(sender,receiver,out);
					break;
				
				case MESSAGE_SEND_REQUEST:
					messageSendHandler(sender,receiver,out,message);
					break;
				
				case FILE_SEND_REQUEST:
					fileSendRequest(sender,receiver,out,message);
					break;
					
				//richiesta non valida
				default:
					sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
					break;
			}
			
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Gestisce una richiesta di accesso al sistema
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
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
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
					sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
					return;
				}
				
				//procedura registrazione
				registerRequestHandler(accessSystem,nickname,password,out,language);
				
				break;
				
			//caso messaggio non valido
			default:
				sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
				return;
				
		}
	}
	
	
	/**
	 * Gestione richiesta amicizia tra 2 utente
	 * @param a utente richiedente
	 * @param b utente richiesto
	 * @param out stream output
	 * @throws IOException errore invio risposta
	 */
	private void friendshipRequestHandler(User a, User b ,DataOutputStream out) throws IOException
	{
		RelationshipManager relationship = new RelationshipManager(reteSG);
		
		try 
		{
			boolean friendship = relationship.nuovaAmicizia(a, b);
			
			//se non erano amici,ora lo sono
			if(friendship) 
			{	
				//invio messaggio di successo al mittente
				sendMessage(new SuccessFriendship(b.isOnline()), out);
				return;
			}
			//se erano gia' amici
			else {
				sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.ALREADY_FRIEND), out);
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
	 * Gestisce le richiesta di invio di un messaggio testuale
	 * @param a
	 * @param b
	 * @param out
	 * @param message
	 * @throws IOException
	 */
	private void messageSendHandler(User a,User b,DataOutputStream out,JSONObject message) throws IOException
	{	
		//analizzo tipo del ricevente
		SendMessageRequest.ReceiverType type = MessageAnalyzer.getReceiverType(message);
		
		//prendo messaggio da inviare
		String text = MessageAnalyzer.getText(message);
		
		//se non e' stato trovato il tipo del ricevente oppure il messaggio
		if(type == null || text == null) {
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
			return;
		}
		
		try {
			switch(type) 
			{
				//il destinatario e' un utente
				case USER:
					sendUserMessageHandler(a,b,text,out);
					break;
				
				//il destinatario e' una chatroom
				case CHATROOM:
					//TODO
					break;
				
				//errore richiesta
				default:
					sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
					break;
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void fileSendRequest(User sender,User receiver,DataOutputStream out,JSONObject message) throws IOException
	{
		String filename = MessageAnalyzer.getSendFileFilename(message);
		
		//nome file non trovato
		if(filename == null) {
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.INVALID_REQUEST),out);
			return;
		}
		
		Socket notifyChannelReceiver = null;
		
		//se il receiver non e' online,oppure se non ha un canale settato per ricevere i messaggi, invio un messaggio di errore al sender
		if(!receiver.isOnline()) {
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.RECEIVER_USER_INVALID_STATUS),out);
			return;
		}
		
		//prendo canale di notifica dei messaggi del receiver
		synchronized (notificationUsersChatMessage) {
			notifyChannelReceiver = notificationUsersChatMessage.get(notificationUsersChatMessage.indexOf(new NotificationUserChatChannel(receiver,null))).getNotifyChannel();
		}
		
		//invio un messaggio di notifica al receiver,dell'arrivo del file
		synchronized (notifyChannelReceiver) 
		{
			NewIncomingFile msg = new NewIncomingFile(sender.getNickname(),filename);
			
			//invio il messaggio di notifica al receiver
			sendMessage(msg,new DataOutputStream(notifyChannelReceiver.getOutputStream()));
						
			//inoltro la risposta del destinatario al mittente
			out.writeUTF(new DataInputStream(new BufferedInputStream(notifyChannelReceiver.getInputStream())).readUTF());
		}
		
	}
	
	/**
	 * Gestisce la richiesta di invio di un messaggio testuale ad un utente
	 * @param sender
	 * @param receiver
	 * @param text
	 * @param out
	 * @throws IOException
	 */
	private void sendUserMessageHandler(User sender,User receiver,String text,DataOutputStream out) throws IOException
	{
		Socket notifyChannelReceiver = null;
		
		//se il receiver non e' online,oppure se non ha un canale settato per ricevere i messaggi, invio un messaggio di errore al sender
		if(!receiver.isOnline()) {
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.RECEIVER_USER_INVALID_STATUS),out);
			return;
		}
		
		//prendo canale di notifica dei messaggi del receiver
		synchronized (notificationUsersChatMessage) {
			notifyChannelReceiver = notificationUsersChatMessage.get(notificationUsersChatMessage.indexOf(new NotificationUserChatChannel(receiver,null))).getNotifyChannel();
		}
		
		synchronized (notifyChannelReceiver) 
		{
			String translatedText = text;
			
			//traduzione del messaggio per il destinatario
			try {
				 translatedText = Translator.translate(text,sender.getLingua(),receiver.getLingua());
			} 
			//errore nella traduzione,si invia il messaggio originale non tradotto
			catch (Exception e) {}
			
			//creo il messaggio di notifica da inviare al receiver
			NewChatMessage msg = new NewChatMessage(sender.getNickname(), translatedText);
			
			//invio il messaggio di notifica al receiver
			sendMessage(msg,new DataOutputStream(notifyChannelReceiver.getOutputStream()));
			
			//messaggio inviato,mando messaggio di ok al sender
			sendMessage(new ResponseSuccessMessage(),out);
		}
	}
	
	/**
	 * Gestione richiesta login
	 * @param accessSystem sistema di gestione accessi al sistema
	 * @param nickname nickname utente che si vuole loggare
	 * @param password password utente
	 * @param out stream output
	 * @throws IOException errore invio riposta
	 */
	private void loginRequestHandler(AccessSystem accessSystem,String nickname,String password,DataOutputStream out) throws IOException
	{
		List<User> amici = null;		
		
		try 
		{
			//login sul sistema
			amici = accessSystem.logIn(nickname,password);
			
		} 
		//caso password errata
		catch (PasswordMismatchingException e) 
		{
			//invio messaggio di errore password errata
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.PASSWORD_MISMATCH),out);
			e.printStackTrace();
			return;
		} 
		//caso utente gia' online
		catch (UserStatusException e) 
		{
			//invio messaggio di errore stato utente non valido
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_INVALID_STATUS),out);
			e.printStackTrace();
			return;
		} 
		catch (UserNotFindException e) 
		{
			//invio messaggio di errore, utente non trovato
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_NOT_FOUND),out);
			e.printStackTrace();
			return;
		}
		
		//operazione e' andata a buon fine mando un messaggio di OK,con la lista degli amici e delle chatroom
		synchronized (chatrooms) {
			sendMessage(new SuccessfulLogin(amici,chatrooms),out);
		}
	}
	
	/**
	 * Gestione richiesta di logout
	 * @param accessSystem sistema di gestione accessi al sistema
	 * @param nickname nickname utente che si vuole sloggare
	 * @param out stream output
	 * @throws IOException errore invio risposta
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
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_NOT_FOUND),out);
			e.printStackTrace();
			return;
		} 
		//utente offline
		catch (UserStatusException e) 
		{
			//invio messaggio di errore stato utente non valido
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.SENDER_USER_INVALID_STATUS),out);
			e.printStackTrace();
			return;
		}
		
		//operazione e' andata a buon fine mando un messaggio di OK,e rimuovo i canali di notifica relativi all'utente
		synchronized (notificationUsersChatMessage) {
			NotificationUserChatChannel UserNotifyChannel = notificationUsersChatMessage.get(notificationUsersChatMessage.indexOf(new NotificationUserChatChannel(new User(nickname),null)));
			Socket connection = UserNotifyChannel.getNotifyChannel();
			connection.close();
			notificationUsersChatMessage.remove(UserNotifyChannel);
		}
		
		//invio messaggio di ok
		sendMessage(new ResponseSuccessMessage(),out);
		
	}
	
	/**
	 * Gestione richiesta registrazione
	 * @param accessSystem sistema di gestione accessi al sistema
	 * @param nickname nickname utente che si vuole registrare
	 * @param password passwords scelta
	 * @param language lingua scelta
	 * @param out stream output
	 * @throws IOException errore invio risposta
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
			sendMessage(new ResponseFailedMessage(ResponseFailedMessage.Errors.USER_ALREADY_REGISTERED),out);
			e.printStackTrace();
			return;
		}
		
		synchronized (chatrooms) {
			//operazione e' andata a buon fine mando un messaggio di OK
			sendMessage(new SuccessfulRegistration(chatrooms),out);
		}
	
	}
}
