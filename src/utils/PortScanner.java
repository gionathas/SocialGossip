package utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * Strumento per la ricerca di porte libere sulla macchina
 * @author Gionatha Sturba
 *
 */
public class PortScanner 
{
	public static final int MIN_PORT_NUMBER = 1024;
	public static final int MAX_PORT_NUMBER = 65535;
	
	/**
	 * 
	 * @return una porta libera se esiste,-1 altrimenti
	 */
	public synchronized static int freePort() {
        for(int i = MIN_PORT_NUMBER;  i <= MAX_PORT_NUMBER; i++) {
            if(available(i))
                return i;
        }

        return -1;
}
	
	/**
	 * 
	 * @param port
	 * @return true se la porta e' libera,false altrimenti
	 */
	public synchronized static boolean available(int port) {
	    if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
	        throw new IllegalArgumentException("Invalid start port: " + port);
	    }

	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try 
	    {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	        
	    } catch (IOException e) {
	    } 
	    finally {
	        if (ds != null) {
	            ds.close();
	        }

	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
	            }
	        }
	    }

	    return false;
	}

}
