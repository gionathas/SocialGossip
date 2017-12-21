package server.model;

import java.rmi.RemoteException;

import communication.RMI.RMIClientNotifyEvent;
import server.model.exception.SameUserException;
import server.model.exception.UserNotFindException;

/**
 * Gestisce le relazioni tra gli utenti di SocialGossip
 * @author Gionatha Sturba
 */
public class RelationshipManager 
{
	private Network reteSG; //rete degli utenti di Social Gossip

	public RelationshipManager(Network reteSocialGossip) 
	{
		if(reteSocialGossip == null)
			throw new NullPointerException();
		
		reteSG = reteSocialGossip;
	}
	
	/**
	 * Crea una nuova amicizia tra 2 utenti
	 * @param a utente che richiede amicizia
	 * @param b utente che riceve amicizia
	 * @return true se i 2 utenti sono ora amici,false se erano gia' amici
	 * @throws UserNotFindException se uno degli utenti non e' stato trovato
	 * @throws SameUserException se sono lo stesso utente
	 * @throws RemoteException se viene riscontrato un errore nella notifica dell'amicizia
	 */
	public boolean nuovaAmicizia(User a,User b) throws UserNotFindException, SameUserException, RemoteException
	{
		boolean relationshipCreated = reteSG.nuovaAmicizia(a, b);
		
		//se l'amicizia e' stata creata
		if(relationshipCreated)
		{
			//aggiorno amicizia tra i 2 utenti
			a.aggiungiAmico(b);
			b.aggiungiAmico(a);
			
			//notifico b che ora e' amico di a
			RMIClientNotifyEvent RMIChannelFiend = b.getRMIchannel();
			
			//se b non e' offline,invio la notifica
			if(RMIChannelFiend != null)
			{
				//invio notifica all'amico
				RMIChannelFiend.newFriend(a);
			}
			
			return true;
		}
		else {
			return false;
		}
	}

}
