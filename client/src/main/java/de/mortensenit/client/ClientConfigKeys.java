package de.mortensenit.client;

/**
 * A list of all possible client configuration keys
 * 
 * @author frederik.mortensen
 *
 */
public final class ClientConfigKeys {

	/**
	 * available client modes are PLAIN and TLS
	 */
	public static final String CLIENT_MODE = "client.mode";

	/**
	 * The server host to be connected to
	 */
	public static final String SERVER_HOST = "server.host";

	/**
	 * The server port to be connected to
	 */
	public static final String SERVER_PORT = "server.port";

	/**
	 * If the client wants to authenticate itself against the server and send the
	 * server its client certificate, this option must be activated and a keystore
	 * must be set
	 */
	public static final String CLIENT_AUTHENTICATION_NEEDED = "client.authentication.needed";

	/**
	 * The keystore the client uses for authentication against the server. If client
	 * authentication is activated, this file is needed to provide the client
	 * certificate.
	 */
	public static final String CLIENT_KEYSTORE_FILE = "client.keystore.file";

	/**
	 * In case the client authentication is activated and the clients keystore is
	 * password protected, then this provides the password for this keystore
	 */
	public static final String CLIENT_KEYSTORE_PASSWORD = "client.keystore.password";

	/**
	 * With this parameter you can disable validation of server side certificates
	 * against the client keystore
	 */
	public static final String SERVER_VALIDATION_NEEDED = "server.validation.needed";

	/**
	 * Contains the public keys of the trusted server certificates. This is needed
	 * when server validation is activated.
	 */
	public static final String CLIENT_TRUSTSTORE_FILE = "client.truststore.file";

	/**
	 * comma separated list of the enabled cipher suites for tls
	 */
	public static final String CLIENT_ENABLED_CIPHER_SUITES = "client.enabled.cipher.suites";
	
	/**
	 * enables javax.net.debug = all. This parameter is optional.
	 */
	public static final String CLIENT_EXTENDED_LOGGING = "client.extended.logging";

	/**
	 * class should never be instantiated
	 */
	private ClientConfigKeys() {

	}

}