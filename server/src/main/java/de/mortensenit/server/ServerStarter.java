package de.mortensenit.server;

import static de.mortensenit.server.ServerConfigKeys.CLIENT_AUTHENTICATION_NEEDED;
import static de.mortensenit.server.ServerConfigKeys.SERVER_KEYSTORE_FILE;
import static de.mortensenit.server.ServerConfigKeys.SERVER_KEYSTORE_PASSWORD;
import static de.mortensenit.server.ServerConfigKeys.SERVER_MODE;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mortensenit.model.Constants;
import de.mortensenit.model.exceptions.PortInUseException;
import de.mortensenit.model.util.ConfigurationContext;

/**
 * Server side server implementation of the EPP service. Opening the configured
 * port and waiting for incoming client socket connections via TCP. Clients need
 * to be authenticated via SSL/TLS certificate.
 * 
 * @author frederik.mortensen
 *
 */
public class ServerStarter {

	private Logger logger = LogManager.getLogger();

	/**
	 * Entry point for the server side service
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// just get out of static
		new ServerStarter().start();
	}

	/**
	 * load application configuration properties, then start the server socket
	 * 
	 * @throws Exception
	 */
	private void start() throws Exception {
		logger.info("Starting up...");

		// open server port
		try {
			if (ConfigurationContext.get(SERVER_MODE).equalsIgnoreCase(Constants.ENCRYPTION_MODE_TLS)) {
				startTLSServerSocket();
			} else {
				startPlainServerSocket();
			}
		} catch (PortInUseException e) {
			logger.error("Port seems in use!", e);
		} catch (IOException e) {
			logger.error("Starting the server failed! Reason: ", e);
		}
	}

	/**
	 * 
	 * @throws PortInUseException
	 * @throws IOException
	 */
	private void startTLSServerSocket() throws PortInUseException, IOException {

		ServerSocketFactory factory = getTlsServerSocketFactory(ConfigurationContext.get(SERVER_KEYSTORE_FILE),
				ConfigurationContext.get(SERVER_KEYSTORE_PASSWORD));

		try (SSLServerSocket sslServerSocket = (SSLServerSocket) factory.createServerSocket(7000)) {

			while (true) {
				logger.info("Waiting for TLS connections on port 7000...");

				sslServerSocket.setEnabledProtocols(new String[] { Constants.PROTOCOL_TLS_1_2 });

				// Arrays.asList(sslServerSocket.getEnabledCipherSuites()).forEach(e ->
				// logger.debug(e));

				// TODO: wie ?
				// sslServerSocket.setEnabledCipherSuites(new String[] {
				// Constants.CIPHER_TLS_AES_256_GCM_SHA384 });

				// TrustManagerComposition c = new TrustManagerComposition();

				sslServerSocket.setNeedClientAuth(ConfigurationContext.getBoolean(CLIENT_AUTHENTICATION_NEEDED));

				Socket sslClientSocket = sslServerSocket.accept();
				ClientConnectionThread clientConnectionThread = new ClientConnectionThread();
				clientConnectionThread.setClientSocket(sslClientSocket);
				// TODO: threads sammeln irgendwo
				Thread thread = new Thread(clientConnectionThread);
				thread.start();
			}

		} finally {
			logger.info("Server stopped.");
		}

	}

	/**
	 * 
	 * @throws PortInUseException
	 * @throws IOException
	 */
	private void startPlainServerSocket() throws PortInUseException, IOException {

		try (ServerSocket plainServerSocket = new ServerSocket(7000)) {

			while (true) {
				logger.info("Waiting for PLAIN connections on port 7000...");

				Socket plainClientSocket = plainServerSocket.accept();
				ClientConnectionThread clientConnectionThread = new ClientConnectionThread();
				clientConnectionThread.setClientSocket(plainClientSocket);
				Thread thread = new Thread(clientConnectionThread);
				thread.start();
			}

		} finally {
			logger.info("Server stopped.");
		}

	}

	/**
	 * generate a TLS server socket factory using the configured keystore
	 * 
	 * @param keyStoreFileName the serverside keystore with the public and private
	 *                         keys
	 * @param keyStorePassword the password for accessing the keystore
	 * @return the server socket factory or null if it failed to initialize
	 */
	private static ServerSocketFactory getTlsServerSocketFactory(String keyStoreFileName, String keyStorePassword) {
		SSLServerSocketFactory serverSocketFactory = null;
		try {
			// set up key manager to do server authentication
			SSLContext sslContext;
			KeyManagerFactory keyManagerFactory;
			KeyStore keyStore;
			char[] passphrase = keyStorePassword != null ? keyStorePassword.toCharArray() : null;

			sslContext = SSLContext.getInstance(Constants.ENCRYPTION_MODE_TLS);
			keyManagerFactory = KeyManagerFactory.getInstance(Constants.KEY_MANAGER_ALGORITHM_SUNX509);
			keyStore = KeyStore.getInstance(Constants.JAVA_KEYSTORE); // TODO: konfigurierbar PKCS12?

			keyStore.load(new FileInputStream(keyStoreFileName), passphrase);
			keyManagerFactory.init(keyStore, passphrase);
			sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

			serverSocketFactory = sslContext.getServerSocketFactory();
			return serverSocketFactory;
		} catch (Exception e) {
			LogManager.getLogger().error(e);
			return null;
		}
	}

}

//https://stackoverflow.com/questions/55854904/javax-net-ssl-sslhandshakeexception-no-available-authentication-scheme

//Your certificate is using the DSA algorithm, which has been deprecated a while ago in favor of RSA and is not supported at all in TLS1.3. Make sure to create RSA certificates instead.

//
//-Djavax.net.ssl.keyStore=serverkeystore.jks -Djavax.net.ssl.keyStorePassword=testtest -Djavax.net.debug=all
//
//-Djavax.net.ssl.trustStore=clienttruststore.jks -Djavax.net.ssl.trustStorePassword=testtest -Djavax.net.debug=all
//
