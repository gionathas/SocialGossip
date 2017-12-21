package communication.TCPMessages;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import communication.TCPMessages.request.InteractionRequest;
import communication.TCPMessages.request.RegisterRequest;
import communication.TCPMessages.request.RequestAccessMessage;
import communication.TCPMessages.request.RequestMessage;
import communication.TCPMessages.response.ResponseFailedMessage;
import communication.TCPMessages.response.ResponseMessage;
import communication.TCPMessages.response.SuccessFriendship;
import communication.TCPMessages.response.SuccessfulLogin;
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
		else
			return Message.Type.RESPONSE;
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
		
		//TODO controllo lista chatroom
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
		else {
			//TODO implementare altri casi
			return null;
		}	
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
