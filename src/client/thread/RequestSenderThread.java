package client.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import communication.MessageAnalyzer;
import communication.messages.Message;
import communication.messages.request.RequestMessage;
import communication.messages.response.ResponseFailedMessage;
import communication.messages.response.ResponseMessage;
import communication.messages.response.ResponseSuccessMessage;

/**
 * Modello astratto che rappresenta un Thread che invia una richiesta al server tramite connessione TCP
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
	protected String JsonResponse;
	protected JSONObject response;
	protected boolean init;
	
	
	public RequestSenderThread()
	{
		super();
		connection = new Socket();
		in = null;
		out = null;
		request = null;
		JsonResponse = null;
		response = null;
		init = false;
	}
	
	/**
	 * Ciclo di vita del thread che invia la richiesta
	 */
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
					JsonResponse = in.readUTF();
					
					//analizzo risposta e mando una risposta la server
					analyzeResponse(JsonResponse);
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
	
	/**
	 * Analizza la risposta alla richiesta inviata al server.
	 * @param JsonResponse
	 */
	protected void analyzeResponse(String JsonResponse)
	{
		try 
		{
			//parso json rappresentate risposta del server
			System.out.println(JsonResponse);
			response = MessageAnalyzer.parse(JsonResponse);
			
			//se non e' un messaggio di risposta
			if(MessageAnalyzer.getMessageType(response) != Message.Type.RESPONSE) 
			{
				unexpectedMessageHandler();
				return;
			}
			
			ResponseMessage.Type outcome = MessageAnalyzer.getResponseType(response);
			
			//tipo risposta non trovato
			if(outcome == null)
			{
				invalidResponseHandler();
				return;
			}
			
			//controllo esito della risposta ricevuta
			switch(outcome) 
			{
				//logout avvenuto
				case SUCCESS:
					successResponseHandler();
					break;
				
				case FAIL:
					//analizzo l'errore riscontrato
					ResponseFailedMessage.Errors error = MessageAnalyzer.getResponseFailedErrorType(response);
					
					//errore non trovato
					if(error == null) 
					{
						invalidResponseErrorTypeHandler();
						return;
					}
					
					failedResponseHandler(error);
					
					break;
					
				default:
					invalidResponseHandler();
					break;
			}
		} 
		catch (ParseException e) 
		{
			parseErrorHandler();
			e.printStackTrace();
		}
	}
	
	/**
	 * Fase di inizializzazione prima di inviare la richiesta al server
	 */
	protected abstract void init();
	/**
	 * Creazione messaggio di richiesta da inviare al server
	 */
	protected abstract void createRequest();
	/**
	 * Gestione errore di connessione al server
	 */
	protected abstract void ConnectErrorHandler();
	/**
	 * Gestione errore server non trovato
	 */
	protected abstract void UnKwownHostErrorHandler();
	/**
	 * Gestione errore di IO
	 */
	protected abstract void IOErrorHandler();
	/**
	 * Gestione risposta del server non valida
	 */
	protected abstract void invalidResponseHandler();
	/**
	 * Gestione errore nel tipo dell'errore di risposta
	 */
	protected abstract void invalidResponseErrorTypeHandler();
	/**
	 * Gestione alla risposta di un messaggio di errore da parte del server
	 * @param error errore riscontrato dal server alla richiestat
	 */
	protected abstract void failedResponseHandler(ResponseFailedMessage.Errors error);
	/**
	 * Gestione nel parsing del messaggio di risposta dal server
	 */
	protected abstract void parseErrorHandler();
	/**
	 * Gestione messaggio di risposta inaspettato
	 */
	protected abstract void unexpectedMessageHandler();
	/**
	 * Gestione caso di risposta di successo alla richiesta
	 */
	protected abstract void successResponseHandler();
}
