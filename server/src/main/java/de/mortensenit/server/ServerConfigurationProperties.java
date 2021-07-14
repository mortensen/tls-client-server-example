package de.mortensenit.server;

/**
 * 
 * @author frederik.mortensen
 *
 */
public final class ServerConfigurationProperties {
	
	/**
	 * available server modes are PLAIN and SSL
	 */
	public static final String SERVER_MODE = "server.mode";
	
	/**
	 * Class should never be instantiated
	 */
	private ServerConfigurationProperties() {
		
	}

}
