package communication.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

import server.model.exception.UserNotFindException;
import server.model.exception.UserStatusException;

/**
 * Interfaccia fornita dal server per far registrare al client le proprie Callback,attraverso il protocollo RMI
 * @author Gionatha Sturba
 */
public interface RMIServerInterface extends Remote
{
	/**
	 * Associa, ad un utente registrato, un canale RMI(callback), per ricevere notifiche di vario tipo.
	 * @param nickname nickname dell'utente 
	 * @param callback callback RMI da associare
	 * @throws RemoteException errore nella registrazione del canale RMI
	 * @throws UserNotFindException se l'utente non e' stato trovato
	 * @throws UserStatusException se l'utente risulta essere offline
	 */
	public void registerUserRMIChannel(String nickname,RMIClientNotifyEvent callback)throws RemoteException,UserNotFindException,UserStatusException;
	
	/**
	 * Disassocia, ad un utente registrato, il precedente canale RMI(callback) che aveva registrato.
	 * @param nickname nickname dell'utente
	 * @throws RemoteException errore nella dissasociazione del canale RMI
	 * @throws UserNotFindException se l'utente non e' stato trovato
	 */
	public void unregisterUserRMIChannel(String nickname)throws RemoteException, UserNotFindException;

}
