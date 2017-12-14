package communication;

import java.awt.TrayIcon.MessageType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import communication.messages.*;

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
		else {
			//TODO implementare altri casi
			return null;
		}	
	}
	
	/**
	 * 
	 * @param JsonMessage messaggio di richiesta
	 * @return nickname dell'utente all'interno del messaggio di richiesta,null se non e' stato trovato
	 */
	public static String getNickname(JSONObject JsonMessage)
	{
		if(JsonMessage == null)
			throw new NullPointerException();
		
		return (String) JsonMessage.get(RequestMessage.FIELD_REQUEST_NICKNAME);
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
