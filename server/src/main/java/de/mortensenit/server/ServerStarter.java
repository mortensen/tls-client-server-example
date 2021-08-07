package de.mortensenit.server;

import static de.mortensenit.server.ServerConfigKeys.CLIENT_AUTHENTICATION_NEEDED;
import static de.mortensenit.server.ServerConfigKeys.SERVER_ENABLED_CIPHER_SUITES;
import static de.mortensenit.server.ServerConfigKeys.SERVER_EXTENDED_LOGGING;
import static de.mortensenit.server.ServerConfigKeys.SERVER_KEYSTORE_FILE;
import static de.mortensenit.server.ServerConfigKeys.SERVER_KEYSTORE_PASSWORD;
import static de.mortensenit.server.ServerConfigKeys.SERVER_MODE;
import static de.mortensenit.server.ServerConfigKeys.SERVER_TRUSTSTORE_FILE;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mortensenit.controller.TLSController;
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

		if (ConfigurationContext.getBoolean(SERVER_EXTENDED_LOGGING, false))
			System.setProperty("javax.net.debug", "all");

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

		String keyStoreFile = ConfigurationContext.get(SERVER_KEYSTORE_FILE);
		String keyStorePwd = ConfigurationContext.get(SERVER_KEYSTORE_PASSWORD);
		String trustStoreFile = ConfigurationContext.get(SERVER_TRUSTSTORE_FILE);
		boolean clientAuthNeeded = ConfigurationContext.getBoolean(CLIENT_AUTHENTICATION_NEEDED);

		ServerSocketFactory factory = TLSController.getTlsServerSocketFactory(keyStoreFile, keyStorePwd,
				trustStoreFile);

		try (SSLServerSocket sslServerSocket = (SSLServerSocket) factory.createServerSocket(7000)) {

			while (true) {
				logger.info("Waiting for TLS connections on port 7000...");

				sslServerSocket.setEnabledProtocols(new String[] { Constants.PROTOCOL_TLS_1_2 });

				String[] cipherSuites = ConfigurationContext.getValues(SERVER_ENABLED_CIPHER_SUITES, null);
				if (cipherSuites != null) {
					logger.info("Setting enabled cipher suites: ");
					for (String cipherSuite : cipherSuites) {
						logger.info(cipherSuite);
					}
					sslServerSocket.setEnabledCipherSuites(cipherSuites);
				}

				// Servers normally authenticate themselves, and clients are not required to do
				// sslServerSocket.setNeedClientAuth(clientAuthNeeded);
				sslServerSocket.setWantClientAuth(clientAuthNeeded);

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

}