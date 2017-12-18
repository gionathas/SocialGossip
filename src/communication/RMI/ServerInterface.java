package communication.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaccia fornita dal server per far registrare al client le proprie Callback
 * @author gio
 *
 */
public interface ServerInterface extends Remote
{
	public void registerForCallback()throws RemoteException;
	
	public void unregisterForCallback()throws RemoteException;
}
