package client.thread;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

/**
 * Threa che si occupa di ricevere un file
 * @author Gionatha Sturba
 *
 */
public class FileReceiver extends Thread
{
	private ServerSocketChannel server;
	private String filename;
	
	public static final String DIRECTORY = "/resources/downloads/";

	public FileReceiver(ServerSocketChannel server,String filename) 
	{
		if(server == null || filename == null)
			throw new NullPointerException();
		
		this.server = server;
		this.filename = filename;
	}
	
	public void run()
	{
		SocketChannel client = null;
		
		//path del file che ci sta per arrivare
		Path path = Paths.get(new File("").getAbsolutePath()+DIRECTORY+filename);
				
		try 
		{
			//creo il file da scaricare
			FileChannel file = FileChannel.open(path,EnumSet.of(StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE));
			
			//aspetto il client che ci inviera' i dati relativi al file
			client = server.accept();
			
			System.out.println("Client connesso");
			
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			//leggo i dati che manda il client
			while(client.read(buffer) > 0)
			{
				buffer.flip();
				
				//scrivo su file
				while(buffer.hasRemaining())
					file.write(buffer);
				
				buffer.clear();
			}
			
			//chiudo il file
			file.close();
			System.out.println("File received");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			//chiudo connessioni
			if(server != null)
			{
				try {
					//termino connessione con il clients
					if(client != null)
						client.close();
					
					//termino il server
					server.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
