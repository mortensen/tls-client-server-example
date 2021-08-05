package de.mortensenit.client;

/**
 * A list of all possible client configuration keys
 * 
 * @author frederik.mortensen
 *
 */
public class ClientConfigKeys {

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
	 * With this parameter you can disable validation of server side certificates
	 * against the client keystore
	 */
	// TODO: im code einbauen / testen - deaktivieren
	public static final String SERVER_AUTHENTICATION_NEEDED = "server.authentication.needed";
	
	/**
	 * Contains the public keys of the trusted server certificates. This is needed
	 * when server authentication is activated.
	 */
	// TODO: im code einbauen / testen
	public static final String CLIENT_TRUSTSTORE_FILE = "client.truststore.file";

	/**
	 * Usually truststores should not be password protected as they only contain
	 * public available content. The truststore password protects the truststore
	 * which contains public keys of trusted clients.
	 */
	public static final String CLIENT_TRUSTSTORE_PASSWORD = "client.truststore.password";

}
