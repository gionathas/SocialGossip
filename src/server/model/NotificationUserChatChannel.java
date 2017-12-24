package server.model;

import java.net.Socket;

/**
 * Rappresenta il canale di notifica con cui si informa un utente dell'arrivo di messaggi da altri utenti
 * @author Gionatha Sturba
 *
 */
public class NotificationUserChatChannel 
{
	private User user;
	private Socket notifyChannel;

	public NotificationUserChatChannel(User user,Socket notifyChannel) 
	{
		if(user == null)
			throw new NullPointerException();
		
		this.user = user;
		this.notifyChannel = notifyChannel;
	}

	public User getUser() {
		return user;
	}

	public synchronized Socket getNotifyChannel() {
		return notifyChannel;
	}

	public synchronized void setNotifyChannel(Socket notifyChannel) {
		this.notifyChannel = notifyChannel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		NotificationUserChatChannel other = (NotificationUserChatChannel) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	
	

}
