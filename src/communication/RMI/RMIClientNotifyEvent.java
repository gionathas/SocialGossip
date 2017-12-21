package communication.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

import server.model.User;

/**
 * Interfaccia della callback usata dal client per ricevere notifiche dal server,tramite il protocollo RMI
 * @author Gionatha Sturba
 *
 */
public interface RMIClientNotifyEvent extends Remote
{
	/**
	 * Notifica per aggiornare lo stato di un amico
	 * @param friend utente che ha cambiato stato
	 * @throws RemoteException se c'e' un errore nel protocollo RMI
	 */
	public void updateFriendStatus(User friend)throws RemoteException;
	
	/**
	 * Notifica nuova amicizia
	 * @param newFriend l'utente che rappresenta il nuovo amico
	 * @throws RemoteException se c'e' un errore nel protocollo RMI
	 */
	public void newFriend(User newFriend)throws RemoteException;
}
