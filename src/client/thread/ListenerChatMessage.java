package client.thread;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import javax.swing.JTextArea;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import client.controller.ChatController;
import client.controller.Controller;
import client.controller.HubController;
import communication.TCPMessages.Message;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.notification.NewIncomingMessage.ReceiverType;
import communication.TCPMessages.request.ChatNotification;
import communication.TCPMessages.response.ResponseFailedMessage;
import communication.TCPMessages.response.ResponseMessage;
import server.model.User;

/**
 * Thread che ascolta i messaggi che arrivano da altri utenti
 * @author Gionatha Sturba
 *
 */
public class ListenerChatMessage extends Thread
{
	private User user;
	private Socket connection;
	private InetAddress serverAddress;
	private DataInputStream in;
	private DataOutputStream out;
	private int port;
	private HubController controller;
	private List<ChatController> chats; //lista delle chat dell'utente

	
	/**
	 * Configura i parametri del thread,per essere avviato
	 * @param user utente che vuole ricevere notifiche 
	 * @param address indirzzo server
	 * @param port porta del server
	 * @throws Exception se viene riscontrato un errore nell'inizializzazione del thread
	 */
	public ListenerChatMessage(HubController controller,User user,List<ChatController> chats,InetAddress address,int port) throws Exception
	{
		super();
		
		if(user == null || address == null || controller == null || chats == null)
			throw new NullPointerException();
		
		this.controller = controller;
		this.user = user;
		this.serverAddress = address;
		this.port = port;
		this.chats = chats;
		
		init();
	}
	
	private void init() throws Exception
	{
		connection = new Socket(serverAddress,port);
		
		//apro stream
		in = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
		out = new DataOutputStream(connection.getOutputStream());
		
		//invio messsaggio per identificare questa connessione,come quella per ricevere notifiche chat
		Message request = new ChatNotification(user.getNickname());
		
		out.writeUTF(request.getJsonMessage());
		
		String response = in.readUTF();
		
		analyzeResponse(response);
			
		
	}
	
	private void analyzeResponse(String JsonResponse) throws Exception
	{
		//parso messaggio Json rappresentate risposta del server
		System.out.println(JsonResponse); //TODO DEBUG
		JSONObject response = MessageAnalyzer.parse(JsonResponse);
		
		//se non e' un messaggio di risposta
		if(MessageAnalyzer.getMessageType(response) != Message.Type.RESPONSE) 
		{
			controller.showErrorMessage("Messaggio risposta del server non valido","Errore listener Chat Message");
			throw new Exception();
		}
		
		//analizzo il tipo del messaggio di risposta
		ResponseMessage.Type outcome = MessageAnalyzer.getResponseType(response);
		
		//tipo risposta non trovato
		if(outcome == null)
		{
			controller.showErrorMessage("Messaggio risposta del server non valido","Errore listener Chat Message");
			throw new Exception();
		}
		
		//controllo esito della risposta ricevuta
		switch(outcome) 
		{
			//logout avvenuto
			case SUCCESS:
				//non si fa nulla
				break;
			
			case FAIL:
				//analizzo l'errore riscontrato
				ResponseFailedMessage.Errors error = MessageAnalyzer.getResponseFailedErrorType(response);
				
				//errore non trovato
				if(error == null) 
				{
					controller.showErrorMessage("Messaggio risposta del server non valido","Errore listener Chat Message");
					throw new Exception();
				}
				
				//in caso di errore
				controller.showErrorMessage("Errore nell'inizializzazione listener chat messaggi","Errore listener Chat Message");
				throw new Exception();
								
			default:
				controller.showErrorMessage("Messaggio risposta del server non valido","Errore listener Chat Message");
				throw new Exception();
		}
	} 

	/**
	 * Ciclo in cui ascolto eventuali messaggi di notifiche relative a messaggi da chat
	 */
	public void run()
	{
		while(true)
		{
			try 
			{
				String notification = in.readUTF();
				
				//ricevuta nuova notifica
				analyzeNotifcation(notification);
				
			} catch (IOException e) {
				System.out.println("Shutting down listener chat message");
				break;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void analyzeNotifcation(String JsonNotification) throws ParseException
	{
		System.out.println(JsonNotification); //TODO DEBUG
		JSONObject notificationMessage = MessageAnalyzer.parse(JsonNotification);
		
		//se e' un messaggio di notifica
		if(MessageAnalyzer.getMessageType(notificationMessage) == Message.Type.NOTIFICATION) 
		{
			//se e' un messaggio di una chat
			if(MessageAnalyzer.getIncomingMessageReceiverType(notificationMessage) == ReceiverType.USER)
			{
				//prendo nickname del mittente
				String sender = MessageAnalyzer.getIncomingMessageSenderNickname(notificationMessage);
				
				//se il mittente e' valido
				if(sender != null)
				{
					//se il messaggio e' valido
					String text = MessageAnalyzer.getIncomingMessageText(notificationMessage);
					
					if(text != null)
					{
						//cerco la chat con l'utente che ha inviato il messaggio
						ChatController chat = controller.openChatFromNewMessage(new User(sender));
						
						synchronized (chat) 
						{
							//aggiungo messaggio arrivato alla conversation area della chat
							JTextArea conversationArea = chat.getJConversationArea();
							
							synchronized (conversationArea) 
							{
								conversationArea.append(sender+": "+text);
							}
						}
						
						//controller.showInfoMessage(sender+" ti ha inviato un messaggio","NUOVO MESSAGGIO",false);
					}
				}
			}
		}
	}
	
	public void shutdown()
	{
		try {
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
