package client.thread.requestSender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.RandomAccess;

import javax.swing.JOptionPane;

import client.controller.Controller;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.request.SendFileRequest;
import communication.TCPMessages.response.ResponseFailedMessage.Errors;

public class FileSender extends RequestSenderThread
{
	private String senderNickname,receiverNickname;
	private File file = null;
	private final String DIR_FILES_PATH = "/resources/";
	
	public FileSender(Controller controller, Socket connection, DataInputStream in, DataOutputStream out,String senderNickname,String receiverNickname) {
		super(controller, connection, in, out);
		
		if(senderNickname == null || receiverNickname == null)
			throw new NullPointerException();
		
		this.senderNickname = senderNickname;
		this.receiverNickname = receiverNickname;
	}

	@Override
	protected void init() {
		String filename = JOptionPane.showInputDialog("Inserisci nome del file da inviare.. (dentro cartella resources)");
		
		//se il nome del file non e' valido 
		if(filename == null)
		{
			init = false;
			controller.showErrorMessage("File non valido","ERRORE");
			return;
		}
		
		file = new File("");
		
		//System.out.println(file.getAbsolutePath());
		file = new File(file.getAbsoluteFile()+DIR_FILES_PATH+filename);
		
		//se il file non esiste,oppure non e' un vero e proprio file
		if(!file.exists() || file.isDirectory())
		{
			init = false;
			controller.showErrorMessage("File non trovato oppure non valido","ERRORE");
			return;
		}
		
		//file valido
		init = true;
			
	}

	@Override
	protected void createRequest() {
		request = new SendFileRequest(senderNickname,receiverNickname,file.getName());
	}

	@Override
	protected void IOErrorHandler() {
		controller.showErrorMessage("Errore richiesta invio file","ERRORE");
	}

	@Override
	protected void invalidResponseHandler() {
		controller.showErrorMessage("Richiesta non valida","ERRORE");
	}

	@Override
	protected void invalidResponseErrorTypeHandler() {
		controller.showErrorMessage("Risposta di errore non valida","ERRORE");
	}

	@Override
	protected void failedResponseHandler(Errors error) {
		switch(error)
		{
			case CANNOT_RECEIVE_FILE:
				controller.showInfoMessage("L'utente non puo' ricevere il file richiesto","Errore invio file",false);
				break;
			
			case RECEIVER_USER_INVALID_STATUS:
				controller.showErrorMessage("Utente offline","Errore invio file");
				break;
				
			default:
				controller.showErrorMessage("Errore invio richiesta invio file","ERRORE");
				break;
		}
		
	}

	@Override
	protected void parseErrorHandler() {
		controller.showErrorMessage("Errore nel parsing della richiesta","ERRORE");
	}

	@Override
	protected void unexpectedMessageHandler() {
		controller.showErrorMessage("Risposta inaspettata","ERRORE");

	}

	@Override
	protected void successResponseHandler() {
		//prendo indirizzo e porta alla quale inviare il file
		String hostname = MessageAnalyzer.getFileReceiverHostname(response);
		long port = MessageAnalyzer.getFileReceiverPort(response);
		
		//client a cui dobbiamo connetterci
		SocketChannel peer = null;
		
		if(hostname == null)
		{
			controller.showErrorMessage("Errore nella ricerca dei parametri dell'indirizzo del client","ERRORE");
			return;
		}
		
		try {
			
			//mi connetto al client a cui devo inviare il file
			peer = SocketChannel.open();
			peer.connect(new InetSocketAddress(hostname,(int) port));
			
			//accedo al file da inviare
			RandomAccessFile aFile = new RandomAccessFile(file,"r");
			FileChannel inChannel = aFile.getChannel();
			
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			//leggo i byte del file e li invio
			while(inChannel.read(buffer) > 0) {
				buffer.flip();
				
				//invio dati al client
				while(buffer.hasRemaining())
				{
					peer.write(buffer);
				}
				
				buffer.clear();
			}
			
			aFile.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(peer != null)
					peer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
