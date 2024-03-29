package client.thread;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;

import javax.swing.JTextArea;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import client.controller.ChatController;
import client.controller.HubController;
import communication.TCPMessages.Message;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.notification.NotificationMessage;
import communication.TCPMessages.request.ChatNotification;
import communication.TCPMessages.response.ResponseMessage;
import communication.TCPMessages.response.fail.ResponseFailedMessage;
import communication.TCPMessages.response.success.AcceptedFileReceive;
import server.model.User;
import utils.PortScanner;

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
	
	/**
	 * Configura i parametri del thread,per essere avviato
	 * @param user utente che vuole ricevere notifiche 
	 * @param address indirzzo server
	 * @param port porta del server
	 * @throws Exception se viene riscontrato un errore nell'inizializzazione del thread
	 */
	public ListenerChatMessage(HubController controller,User user,InetAddress address,int port) throws Exception
	{
		super();
		
		if(user == null || address == null || controller == null )
			throw new NullPointerException();
		
		this.controller = controller;
		this.user = user;
		this.serverAddress = address;
		this.port = port;
		
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
	
	/**
	 * Analizza risposta alla richiesta del settaggio del canale di notifica
	 * @param JsonResponse risposta del server
	 * @throws Exception se viene riscontrato un errore nell'analisi della risposta
	 */
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
	
	/**
	 * Analizza la notifica appena arrivata
	 * @param JsonNotification messaggio di notifica
	 * @throws ParseException 
	 * @throws IOException
	 */
	private void analyzeNotifcation(String JsonNotification) throws ParseException, IOException
	{
		System.out.println(JsonNotification); //TODO DEBUG
		JSONObject notificationMessage = MessageAnalyzer.parse(JsonNotification);
		
		//se e' un messaggio di notifica
		if(MessageAnalyzer.getMessageType(notificationMessage) == Message.Type.NOTIFICATION) 
		{
				//prendo nickname del mittente
				String sender = MessageAnalyzer.getNotificationMessageSenderNickname(notificationMessage);
				
				//se il mittente e' valido
				if(sender != null)
				{
					//prendo il tipo del messaggio di notifica
					NotificationMessage.EventType notificationType = MessageAnalyzer.getNotificationMessageEventType(notificationMessage);
					
					//se il messaggio di notifica e' valido
					if(notificationType != null) 
					{
						switch (notificationType) 
						{
							//ricevuto messaggio testuale
							case NEW_MESSAGE:
								
								//se il messaggio e' valido
								String text = MessageAnalyzer.getChatMessageText(notificationMessage);
								
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
											conversationArea.append("["+sender+"]"+": "+text+"\n");
										}
									}
									
									//controller.showInfoMessage(sender+" ti ha inviato un messaggio","NUOVO MESSAGGIO",false);
								}
								
								break;
							
							//ricevuto file
							case NEW_FILE:
								
								//preparo un socket per ricevere il file,e poi avvio il thread che si occupera' della ricezione del file
								
								ServerSocketChannel server= ServerSocketChannel.open();
								int port = PortScanner.freePort();
								
								ResponseMessage response;
								
								//se il server per ricevere il file e' stato creato correttamente
								if(port != -1)
								{
									//creo il socket server
									server.socket().bind(new InetSocketAddress(port));

									//invio messaggio di successo con ip e porta su cui si e' in ascolto
									response = new AcceptedFileReceive("localhost",port);
									
									//invio risposta
									try {
										out.writeUTF(response.getJsonMessage());
									} catch (IOException e) {
										e.printStackTrace();
										return;
									}
									
									//nome del file
									String filename = MessageAnalyzer.getIncomingFileFilename(notificationMessage);
									
									//se il nome del file e' valido
									if(filename != null)
									{
										//faccio partire il thread che si occupera' di ricevere il file
										new FileReceiver(server,filename).start();
										
										//cerco la chat con l'utente che ha inviato il messaggio
										ChatController chat = controller.openChatFromNewMessage(new User(sender));
										
										synchronized (chat) 
										{
											//aggiungo messaggio arrivato alla conversation area della chat
											JTextArea conversationArea = chat.getJConversationArea();
											
											synchronized (conversationArea) 
											{
												conversationArea.append("RICEVUTO FILE: "+filename+".\n "+"Il file verra' ora scaricato e "+
											"salvato sulla \ncartella resources/download"+"\n");
											}
										}
									}
									
								}
								//errore creazione server ricezione file
								else {
									response = new ResponseFailedMessage(ResponseFailedMessage.Errors.CANNOT_RECEIVE_FILE);
									
									//invio risposta
									try {
										out.writeUTF(response.getJsonMessage());
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								break;
								
							default:
								break;
						}
					}
					
				}
		}
	}
	
	/**
	 * Termina il listener dei messaggi delle chat
	 */
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
