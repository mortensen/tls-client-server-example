package de.mortensenit.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mortensenit.model.Constants;
import de.mortensenit.model.exceptions.PortInUseException;

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
	 * The server configuration file must be named app.properties. The keys are
	 * defined as constants in ServerConfigurationProperties class
	 */
	private ResourceBundle applicationProperties;

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

		// load server configuration
		logger.info("Loading application properties.");
		try {
			applicationProperties = ResourceBundle.getBundle("app");
		} catch (Exception e) {
			logger.error("No configuration file found! Shutting down...", e);
			return;
		}
		logger.info("Server configuration found:");
		for (String key : applicationProperties.keySet()) {
			logger.info(key + ": " + applicationProperties.getString(key));
		}

		// open server port
		try {
			if (applicationProperties.containsKey(ServerConfigurationProperties.SERVER_MODE) && applicationProperties
					.getString(ServerConfigurationProperties.SERVER_MODE).equalsIgnoreCase("TLS")) {
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

		try (SSLServerSocket sslServerSocket = (SSLServerSocket) SSLServerSocketFactory.getDefault()
				.createServerSocket(7000)) {

			while (true) {
				logger.info("Waiting for TLS connections on port 7000...");

				sslServerSocket.setEnabledProtocols(new String[] { Constants.PROTOCOL_TLS_1_2 });
				//Arrays.asList(sslServerSocket.getEnabledCipherSuites()).forEach(System.out::println);
				// sslServerSocket.setEnabledCipherSuites(new String[] {
				// Constants.CIPHER_TLS_AES_256_GCM_SHA384 });

				sslServerSocket.setNeedClientAuth(false);

				Socket sslClientSocket = sslServerSocket.accept();
				ClientConnectionThread clientConnectionThread = new ClientConnectionThread();
				clientConnectionThread.setClientSocket(sslClientSocket);
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