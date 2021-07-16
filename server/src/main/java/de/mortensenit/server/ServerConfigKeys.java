package de.mortensenit.server;

/**
 * A list of all possible server configuration keys
 * 
 * @author frederik.mortensen
 *
 */
public final class ServerConfigKeys {

	/**
	 * available server modes are PLAIN and SSL
	 */
	public static final String SERVER_MODE = "server.mode";

	/**
	 * name of the server side ssl keystore file to be loaded
	 */
	public static final String SERVER_KEYSTORE_FILE = "server.keystore.file";

	/**
	 * the keystore password protects the keystore which contains private keys and
	 * certificates provided by the server
	 */
	public static final String SERVER_KEYSTORE_PASSWORD = "server.keystore.password";

	/**
	 * #defines wether a client authentication is needed or not
	 */
	public static final String CLIENT_AUTHENTICATION_NEEDED = "client.authentication.needed";

	/**
	 * Class should never be instantiated
	 */
	private ServerConfigKeys() {

	}

}