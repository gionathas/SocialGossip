package communication.TCPMessages;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import communication.TCPMessages.notification.NewIncomingFile;
import communication.TCPMessages.notification.NewIncomingMessage;
import communication.TCPMessages.notification.NotificationMessage;
import communication.TCPMessages.request.RequestMessage;
import communication.TCPMessages.request.access.RegisterRequest;
import communication.TCPMessages.request.access.RequestAccessMessage;
import communication.TCPMessages.request.chatroom.ChatRoomRequest;
import communication.TCPMessages.request.interaction.InteractionRequest;
import communication.TCPMessages.request.interaction.SendFileRequest;
import communication.TCPMessages.request.interaction.SendMessageRequest;
import communication.TCPMessages.response.ResponseMessage;
import communication.TCPMessages.response.fail.ResponseFailedMessage;
import communication.TCPMessages.response.success.AcceptedFileReceive;
import communication.TCPMessages.response.success.SuccessFriendship;
import communication.TCPMessages.response.success.SuccessfulLogin;
import server.model.*;

/**
 * Deserializza i campi di un messaggio ricevuto in formato in Json.
 * @author Gionatha Sturba
 *
 */
public class MessageAnalyzer 
{
	/**
	 * Parsa un messaggio in formato Json
	 * @param JsonMessage stringa in formato Json da parsare
	 * @return JSONObject che rappresenta il messaggio parsato
	 * @throws ParseException Se il messaggio non e' in formato Json
	 */
	public static JSONObject parse(String JsonStringMessage) throws ParseException
	{
		if(JsonStringMessage == null)
			throw new NullPointerException();
		
		JSONParser parser = new JSONParser();
		
		return (JSONObject) parser.parse(JsonStringMessage);
	}
	
	/**
	 * @param JsonMessage oggetto Json rappresentante il messaggio
	 * @return tipo del messaggio,oppure null se non e' stato trovato
	 */
	public static Message.Type getMessageType(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		String type = (String) JsonMessage.get(Message.FIELD_MESSAGE_TYPE);
		
		if(type == null)
			return null;
		
		if(type.equals(Message.Type.REQUEST.name()))
			return Message.Type.REQUEST;
		else if(type.equals(Message.Type.RESPONSE.name()))
			return Message.Type.RESPONSE;
		else if(type.equals(Message.Type.NOTIFICATION.name()))
			return Message.Type.NOTIFICATION;
		else
			return null;
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return tipo del messaggio di notifica,null se non e' stato trovato
	 */
	public static NotificationMessage.EventType getNotificationMessageEventType(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		String type = (String) JsonMessage.get(NotificationMessage.FIELD_NOTIFICATION_TYPE);
		
		if(type == null) {
			return null;
		}
		
		//nuovo messaggio
		if(type.equals(NotificationMessage.EventType.NEW_FILE.name()))
			return NotificationMessage.EventType.NEW_FILE;
		//nuovo file
		else if(type.equals(NotificationMessage.EventType.NEW_MESSAGE.name()))
			return NotificationMessage.EventType.NEW_MESSAGE;
		else
			return null;
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return nome del mittente del messaggio di notifica del messaggio testuale,null se non e' stato trovato
	 */
	public static String getNotificationMessageSenderNickname(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (String) JsonMessage.get(NotificationMessage.FIELD_NOTIFICATION_SENDER_NICKNAME);
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return tipo del mittente del messaggio di notifica del messaggio testuale,null se non e' stato trovato
	 */
	public static NewIncomingMessage.ReceiverType getIncomingMessageReceiverType(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		String type = (String)JsonMessage.get(NewIncomingMessage.FIELD_INCOMING_MESSAGE_RECEIVER_TYPE);
		
		if(type == null)
			return null;
		
		if(type.equals(NewIncomingMessage.ReceiverType.USER.name()))
			return NewIncomingMessage.ReceiverType.USER;
		else if(type.equals(NewIncomingMessage.ReceiverType.CHATROOM.name()))
			return NewIncomingMessage.ReceiverType.CHATROOM;
		else
			return null;
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return testo del messaggio del messaggio di notifica del messaggio testuale,null se non e' stato trovato
	 */
	public static String getIncomingMessageText(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (String) JsonMessage.get(NewIncomingMessage.FIELD_INCOMING_MESSAGE_TEXT);
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return nome del file che sta inviando il mittente,null se non e' stato trovato
	 */
	public static String getIncomingFileFilename(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (String) JsonMessage.get(NewIncomingFile.FIELD_INCOMING_FILE_FILENAME);
	}
	
	/**
	 * 
	 * @param JsonMessage oggetto Json rappresentante il messaggio
	 * @return tipo della risposta,null se non e' stato trovato
	 */
	public static ResponseMessage.Type getResponseType(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		String type = (String) JsonMessage.get(ResponseMessage.FIELD_RESPONSE_TYPE);
		
		//campo non trovato
		if(type == null) {
			return null;
		}
		
		//risposta di errore
		if(type.equals(ResponseMessage.Type.FAIL.name()))
		{
			return ResponseMessage.Type.FAIL;
		}
		//risposta di successo
		else if(type.equals(ResponseMessage.Type.SUCCESS.name())) 
		{
			return ResponseMessage.Type.SUCCESS;
		}
		//caso messaggio non valido
		else {
			return null;
		}
	}
	
	/**
	 * 
	 * @param JsonMessage oggetto Json rappresentante il messaggio
	 * @return lista degli amici dell'utente loggato,null se non e' stata trovata
	 */
	public static List<User> getListaAmici(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		JSONArray listOfFriend;
		
		listOfFriend = (JSONArray) JsonMessage.get(SuccessfulLogin.FIELD_FRIEND_LIST);
		
		//campo lista amici non trovato
		if(listOfFriend == null)
			return null;
		
		//lista che contiene gli amici dell'utente loggato
		List<User> amiciList = new LinkedList<User>();
		
		//creo la lista degli amici dell'utente
		Iterator<JSONObject> iteratorUser = listOfFriend.iterator();
		
		//aggiungo utenti alla lista
		while(iteratorUser.hasNext()) 
		{
			amiciList.add(getUser(iteratorUser.next()));
		}
		
		return amiciList;
	}
	
	/**
	 * 
	 * @param Json Message
	 * @return lista delle chatroom attive,null se non sono state trovate
	 */
	public static List<ChatRoom> getListaChatRoom(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		JSONArray listOfChatRoom;
		
		listOfChatRoom = (JSONArray) JsonMessage.get(SuccessfulLogin.FIELD_CHATROOM_LIST);
		
		//campo lista amici non trovato
		if(listOfChatRoom == null)
			return null;
		
		//lista che contiene gli amici dell'utente loggato
		List<ChatRoom> chatroomList = new LinkedList<ChatRoom>();
		
		//creo la lista degli amici dell'utente
		Iterator<JSONObject> iteratorChatroom = listOfChatRoom.iterator();
		
		//aggiungo utenti alla lista
		while(iteratorChatroom.hasNext()) 
		{
			chatroomList.add(getChatRoom(iteratorChatroom.next()));
		}
		
		return chatroomList;
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return un istanza di una chatroom che e' presente nel messaggio
	 */
	private static ChatRoom getChatRoom(JSONObject JsonMessage)
	{
		String name = (String) JsonMessage.get(ChatRoom.FIELD_NAME);
		String address = (String) JsonMessage.get(ChatRoom.FIELD_ADDRESS);
		long port = (long) JsonMessage.get(ChatRoom.FIELD_PORT);
		
		try {
			return new ChatRoom(name,InetAddress.getByName(address),(int) port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *
	 * @param JsonMessage oggetto Json rappresentante il messaggio
	 * @return ritorna un istanza dell'utente che e' presente nel messaggio
	 */
	private static User getUser(JSONObject JsonMessage)
	{
		String name = (String) JsonMessage.get(User.FIELD_NAME);
		boolean online = (boolean) JsonMessage.get(User.FIELD_ONLINE);
		
		return new User(name,online);
	}
	
	/**
	 * 
	 * @param JsonMessage oggetto Json rappresentante il messaggio
	 * @return tipo dell'errore nel messaggio di errore,null se non e' stato trovato
	 */
	public static ResponseFailedMessage.Errors getResponseFailedErrorType(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		String type = (String) JsonMessage.get(ResponseFailedMessage.FIELD_FAIL_MESSAGE);
		
		//campo non trovato
		if(type == null) {
			return null;
		}
		
		//errore richiesta non valida
		if(type.equals(ResponseFailedMessage.Errors.INVALID_REQUEST.name()))
		{
			return ResponseFailedMessage.Errors.INVALID_REQUEST;
		}
		//errore password non corretta
		else if(type.equals(ResponseFailedMessage.Errors.PASSWORD_MISMATCH.name()))
		{
			return ResponseFailedMessage.Errors.PASSWORD_MISMATCH;
		}
		//errore utente gia registrato
		else if(type.equals(ResponseFailedMessage.Errors.USER_ALREADY_REGISTERED.name()))
		{
			return ResponseFailedMessage.Errors.USER_ALREADY_REGISTERED;
		}
		//errore stato utente mittente
		else if(type.equals(ResponseFailedMessage.Errors.SENDER_USER_INVALID_STATUS.name()))
		{
			return ResponseFailedMessage.Errors.SENDER_USER_INVALID_STATUS;
		}
		//errore stato utente destinatario
		else if(type.equals(ResponseFailedMessage.Errors.RECEIVER_USER_INVALID_STATUS.name()))
		{
			return ResponseFailedMessage.Errors.RECEIVER_USER_INVALID_STATUS;
		}
		//errore utente mittente non trovato
		else if(type.equals(ResponseFailedMessage.Errors.SENDER_USER_NOT_FOUND.name()))
		{
			return ResponseFailedMessage.Errors.SENDER_USER_NOT_FOUND;
		}
		//errore utente destinatario non trovato
		else if(type.equals(ResponseFailedMessage.Errors.RECEIVER_USER_NOT_FOUND.name()))
		{
			return ResponseFailedMessage.Errors.RECEIVER_USER_NOT_FOUND;
		}
		//errore utente gia' amici
		else if(type.equals(ResponseFailedMessage.Errors.ALREADY_FRIEND.name()))
		{
			return ResponseFailedMessage.Errors.ALREADY_FRIEND;
		}
		//errore utente gia' amici
		else if(type.equals(ResponseFailedMessage.Errors.SAME_USERS.name()))
		{
			return ResponseFailedMessage.Errors.SAME_USERS;
		}
		//errore utente gia' amici
		else if(type.equals(ResponseFailedMessage.Errors.CANNOT_RECEIVE_FILE.name()))
		{
			return ResponseFailedMessage.Errors.CANNOT_RECEIVE_FILE;
		}
		else {
			//TODO inserire altri casi
			return null;
		}
		
	}
	
	/**
	 * 
	 * @param JsonMessage messaggio di richiesta
	 * @return tipo della richiesta di un messaggio di richiesta,altrimenti null se non e' stato trovato
	 */
	public static RequestMessage.Type getRequestMessageType(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		String type = (String) JsonMessage.get(RequestMessage.FIELD_REQUEST_TYPE);
		
		//campo non trovato
		if(type == null)
			return null;
		
		//se e' un messaggio di richiesto accesso
		if(type.equals(RequestMessage.Type.ACCESS.name()))
		{
			return RequestMessage.Type.ACCESS;
		}
		//messaggio richiesta logout
		else if(type.equals(RequestMessage.Type.LOGOUT.name()))
		{
			return RequestMessage.Type.LOGOUT;
		}
		//messaggio richiesta ricerca utente
		else if(type.equals(RequestMessage.Type.INTERACTION.name())) 
		{
			return RequestMessage.Type.INTERACTION;
		}
		//messaggio richiesta settaggio canale notifica chat
		else if(type.equals(RequestMessage.Type.CHAT_NOTIFICATION_CHAN.name())) 
		{
			return RequestMessage.Type.CHAT_NOTIFICATION_CHAN;
		}
		//messaggio richiesta settaggio canale notifica chat
		else if(type.equals(RequestMessage.Type.CHATROOM_REQUEST.name())) 
		{
			return RequestMessage.Type.CHATROOM_REQUEST;
		}
		else {
			//TODO implementare altri casi
			return null;
		}	
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return nome della nuova chatroom da creare,null se non e' stata trovata
	 */
	public static final String getChatRoomName(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (String) JsonMessage.get(ChatRoomRequest.FIELD_CHATROOM_REQUEST_NAME);
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return tipo della richiesta che coinvolge una chatroom,null se non e' stata trovata
	 */
	public static ChatRoomRequest.ChatroomRequests getChatRoomRequestType(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		String type = (String) JsonMessage.get(ChatRoomRequest.FIELD_CHATROOM_REQUEST_TYPE);
		
		if(type == null)
			return null;
		
		//richiesta partecipazione a chatroom
		if(type.equals(ChatRoomRequest.ChatroomRequests.JOIN_CHATROOM.name()))
		{
			return ChatRoomRequest.ChatroomRequests.JOIN_CHATROOM;
		}
		//richiesta nuova chatroom
		else if(type.equals(ChatRoomRequest.ChatroomRequests.NEW_CHATROOM.name()))
		{
			return ChatRoomRequest.ChatroomRequests.NEW_CHATROOM;
		}
		else {
			return null;
		}
		
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return nome del file che si vuole inviare,null se non e' stato trovato
	 */
	public static final String getSendFileFilename(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (String) JsonMessage.get(SendFileRequest.FIELD_SEND_FILE_REQUEST_FILENAME);
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return hostname del client che deve ricevere il file,null se non e' stato trovato
	 */
	public static final String getFileReceiverHostname(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (String) JsonMessage.get(AcceptedFileReceive.FIELD_ACCEPTED_FILE_RECEIVE_HOSTNAME);
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return porta dell'hostname del client che deve ricevere il file,-1 se non e' stata trovata
	 */
	public static final long getFileReceiverPort(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (long) JsonMessage.get(AcceptedFileReceive.FIELD_ACCEPTED_FILE_RECEIVE_PORT);
	}
	
	/**
	 * 
	 * @param JsonMessage oggetto Json rappresentante il messaggio
	 * @return tipo della richiesta di interazione con un altro utente
	 */
	public static InteractionRequest.Type getInteractionType(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		String type = (String) JsonMessage.get(InteractionRequest.FIELD_INTERACTION_REQUEST_TYPE);
		
		//campo non trovato
		if(type == null)
			return null;
		
		//se e' un messaggio di richiesta ricerca utente
		if(type.equals(InteractionRequest.Type.FIND_USER_REQUEST.name()))
		{
			return InteractionRequest.Type.FIND_USER_REQUEST;
		}
		//messaggio richiesta invio messaggio
		else if(type.equals(InteractionRequest.Type.MESSAGE_SEND_REQUEST.name())) 
		{
			return InteractionRequest.Type.MESSAGE_SEND_REQUEST;
		}
		//messaggio di richiesta amicizia
		else if(type.equals(InteractionRequest.Type.FRIENDSHIP_REQUEST.name())) 
		{
			return InteractionRequest.Type.FRIENDSHIP_REQUEST;
		}
		else if(type.equals(InteractionRequest.Type.FILE_SEND_REQUEST.name())) 
		{
			return InteractionRequest.Type.FILE_SEND_REQUEST;
		}
		else {
			//TODO implementare altri casi
			return null;
		}
	}
	
	/**
	 * 
	 * @param JsonMessage messaggio di richiesta
	 * @return nickname dell'utente mittente del messaggio,null se non e' stato trovato
	 */
	public static String getNicknameSender(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (String) JsonMessage.get(RequestMessage.FIELD_REQUEST_NICKNAME_SENDER);
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return nickname dell'utente destinatario del messaggio,null se non e' stato trovato
	 */
	public static String getNicknameReceiver(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (String) JsonMessage.get(InteractionRequest.FIELD_NICKNAME_RECEIVER);
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return tipo del ricevente del messaggio testuale,null se non e' stato trovato
	 */
	public static SendMessageRequest.ReceiverType getReceiverType(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		String type = (String) JsonMessage.get(SendMessageRequest.FIELD_RECEIVER_TYPE);
		
		//campo non trovato
		if(type == null)
			return null;
		
		//se e' un messaggio di richiesta ricerca utente
		if(type.equals(SendMessageRequest.ReceiverType.USER.name()))
		{
			return SendMessageRequest.ReceiverType.USER;
		}
		else if(type.equals(SendMessageRequest.ReceiverType.CHATROOM.name()))
		{
			return SendMessageRequest.ReceiverType.CHATROOM;
		}
		else {
			return null;
		}
		
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return stringa che rappresenta messaggio testuale,null se non e' stato trovato
	 */
	public static String getText(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (String) JsonMessage.get(SendMessageRequest.FIELD_TEXT_MESSAGE);
	}
	
	/**
	 * 
	 * @param JsonMessage
	 * @return stato dell'utente appena aggiunto come amico,null se non e' stato trovato
	 */
	public static boolean getStatusReceiverOfFriendShipRequest(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (boolean) JsonMessage.get(SuccessFriendship.FIELD_STATUS_NEW_FRIEND);
	}
	
	/**
	 * 
	 * @param JsonMessage messaggio di richiesta di accesso
	 * @return password dell'utente,altrimenti null se non e' stata trovata
	 */
	public static String getPassword(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (String) JsonMessage.get(RequestAccessMessage.FIELD_REQUEST_ACCESS_PASSWORD);
	}
	
	/**
	 * 
	 * @param JsonMessage messaggio di richiesta di registrazione
	 * @return stringa rappresentante il linguaggio dell'utente,false se non e' stata trovata
	 */
	public static String getLanguage(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (String) JsonMessage.get(RegisterRequest.FIELD_REGISTER_REQUEST_LANGUAGE);
	}
	
	/**
	 * 
	 * @param JsonMessage messaggio di richiesta di accesso
	 * @return tipo del messaggio di richiesta di accesso
	 */
	public static RequestAccessMessage.Type getRequestAccessMessageType(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		String type = (String) JsonMessage.get(RequestAccessMessage.FIELD_REQUEST_ACCESS_TYPE);
		
		//caso login
		if(type.equals(RequestAccessMessage.Type.LOGIN.name()))
		{
			return RequestAccessMessage.Type.LOGIN;
		}
		//caso registrazione
		else if(type.equals(RequestAccessMessage.Type.REGISTER.name())) 
		{
			return RequestAccessMessage.Type.REGISTER;
		}
		//caso non valido
		else
		{
			return null;
		}
	}
	
	
	
}
