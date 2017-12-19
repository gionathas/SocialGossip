package server.model;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

import communication.RMI.RMIClientNotifyEvent;
import communication.RMI.RMIServerInterface;
import server.model.exception.UserNotFindException;
import server.model.exception.UserStatusException;

/**
 * Gestisce le callback degli utenti registrati
 * @author gio
 *
 */
public class RMIChannelManager extends RemoteObject implements RMIServerInterface
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5015238682291518864L;
	private Network reteSG;

	public RMIChannelManager(Network reteSocialGossip) 
	{
		super();
		
		if(reteSocialGossip == null)
			throw new IllegalArgumentException();
		
		reteSG = reteSocialGossip;
	}

	@Override
	public synchronized void registerUserRMIChannel(String nickname,RMIClientNotifyEvent callback)throws RemoteException,RemoteException,UserNotFindException, UserStatusException
	{
		if(nickname == null || callback == null)
			throw new NullPointerException();
		
		User registeredUser = reteSG.cercaUtente(nickname);
		
		//utente non trovato
		if(registeredUser == null)
			throw new UserNotFindException();
		
		//se l'utente non e' online
		if(!registeredUser.isOnline())
			throw new UserStatusException();
		
		//controlli utente ok,procedo a registrare il suo canale per le notifiche RMI
		registeredUser.setRMIchannel(callback);
	}
	
	public synchronized void unregisterUserRMIChannel(String nickname)throws RemoteException, UserNotFindException
	{
		if(nickname == null)
			throw new NullPointerException();
		
		//cerco l'utente
		User registeredUser = reteSG.cercaUtente(nickname);
		
		//utente non trovato
		if(registeredUser == null)
			throw new UserNotFindException();
		
		//disassocio il precedente canale RMI dell'utente
		registeredUser.setRMIchannel(null);
		
	}


}
