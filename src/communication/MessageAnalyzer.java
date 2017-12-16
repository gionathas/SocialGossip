package communication;

import java.awt.TrayIcon.MessageType;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import communication.messages.*;
import server.model.*;

/**
 * Analizza un messaggio in formato in Json
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
	 * @param JsonMessage
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
	 * @param JsonMessage
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
	 * @param JsonMessage
	 * @param amiciList
	 * @param chatRoomList
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
		Iterator<User> iteratorUser = listOfFriend.iterator();
		
		//aggiungo utenti alla lista
		while(iteratorUser.hasNext()) {
				amiciList.add(iteratorUser.next());
		}
		
		return amiciList;
	}
	
	/**
	 * 
	 * @param JsonMessage
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
		else if(type.equals(ResponseFailedMessage.Errors.PASSWORD_MISMATCH.name()))
		{
			return ResponseFailedMessage.Errors.PASSWORD_MISMATCH;
		}
		else if(type.equals(ResponseFailedMessage.Errors.USER_ALREADY_REGISTERED.name()))
		{
			return ResponseFailedMessage.Errors.USER_ALREADY_REGISTERED;
		}
		else if(type.equals(ResponseFailedMessage.Errors.USER_INVALID_STATUS.name()))
		{
			return ResponseFailedMessage.Errors.USER_INVALID_STATUS;
		}
		else if(type.equals(ResponseFailedMessage.Errors.USER_NOT_FOUND.name()))
		{
			return ResponseFailedMessage.Errors.USER_NOT_FOUND;
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
	 * @param JsonMessage
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
		
		//se e' un messaggio di richiesto accesso
		if(type.equals(InteractionRequest.Type.FIND_USER_REQUEST.name()))
		{
			return InteractionRequest.Type.FIND_USER_REQUEST;
		}
		//messaggio richiesta logout
		else if(type.equals(InteractionRequest.Type.FRIENDSHIP_REQUEST.name()))
		{
			return InteractionRequest.Type.FRIENDSHIP_REQUEST;
		}
		//messaggio richiesta ricerca utente
		else if(type.equals(InteractionRequest.Type.MESSAGE_SEND_REQUEST.name())) 
		{
			return InteractionRequest.Type.MESSAGE_SEND_REQUEST;
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
