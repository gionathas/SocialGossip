package utils;

/**
 * Parametri configurazione del sistema
 * @author Gionatha Sturba
 *
 */
public class Config 
{
	//TCP
	public static final String SERVER_HOST_NAME = "localhost";
	public static final int	SERVER_TCP_PORT= 5000;
	
	//RMI
	public static final String SERVER_RMI_SERVICE_NAME = "SocialGossipNotification";
	public static final int SERVER_RMI_PORT = 6000;

	//MULTICAST
	public static final String FIRST_MULTICAST_ADDR = "224.0.0.1";
	public static final String LAST_MULTICAST_ADDR = "224.0.0.255";
	
	public static final String DOWNLOAD_DIRECTORY = "/resources/downloads/";
}
