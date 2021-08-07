package de.mortensenit.server;

/**
 * A list of all possible server configuration keys
 * 
 * @author frederik.mortensen
 *
 */
public final class ServerConfigKeys {

	/**
	 * available server modes are PLAIN and TLS
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
	 * the truststore file contains public keys from clients that are trusted by the
	 * server, if client authentication is activated
	 */
	public static final String SERVER_TRUSTSTORE_FILE = "server.truststore.file";

	/**
	 * #defines wether a client authentication is needed or not
	 */
	public static final String CLIENT_AUTHENTICATION_NEEDED = "client.authentication.needed";
	
	/**
	 * comma separated list of the enabled cipher suites for tls
	 */
	public static final String SERVER_ENABLED_CIPHER_SUITES = "server.enabled.cipher.suites";
	
	/**
	 * enables javax.net.debug = all. This parameter is optional.
	 */
	public static final String SERVER_EXTENDED_LOGGING = "server.extended.logging";

	/**
	 * Class should never be instantiated
	 */
	private ServerConfigKeys() {

	}

}
