package communication.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

import server.model.User;

public interface RMIClientNotifyEvent extends Remote
{
	/**
	 * Notifica per aggiornare lo stato di un amico
	 * @param friend
	 * @throws RemoteException
	 */
	public void updateFriendStatus(User friend)throws RemoteException;
	
	/**
	 * Notifica nuova amicizia
	 * @param newFriend
	 * @throws RemoteException
	 */
	public void newFriend(User newFriend)throws RemoteException;
}
