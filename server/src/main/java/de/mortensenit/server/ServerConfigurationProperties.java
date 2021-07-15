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
	 * name of the server side ssl keystore file to be loaded
	 */
	public static final String SERVER_KEYSTORE_FILE = "server.keystore.file";

	/**
	 * the keystore password protects the keystore which contains private keys and certificates provided by the server
	 */
	public static final String SERVER_KEYSTORE_PASSWORD = "server.keystore.password";
	
	/**
	 * Class should never be instantiated
	 */
	private ServerConfigurationProperties() {
		
	}

}
