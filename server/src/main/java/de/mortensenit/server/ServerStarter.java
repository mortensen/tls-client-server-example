package de.mortensenit.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.ResourceBundle;

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
			if (ConfigurationContext.getNeeded(ServerConfigurationProperties.SERVER_MODE).equalsIgnoreCase("TLS")) {
				
				if (ConfigurationContext.getNeeded(ServerConfigurationProperties.SERVER_MODE)equalsIgnoreCase("TLS")) {

				
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

		ServerSocketFactory factory = getTlsServerSocketFactory("", "");

		try (SSLServerSocket sslServerSocket = (SSLServerSocket) factory.createServerSocket(7000)) {

			while (true) {
				logger.info("Waiting for TLS connections on port 7000...");

				sslServerSocket.setEnabledProtocols(new String[] { Constants.PROTOCOL_TLS_1_2 });
				// Arrays.asList(sslServerSocket.getEnabledCipherSuites()).forEach(System.out::println);
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

	/**
	 * 
	 * @return
	 */
	private static ServerSocketFactory getTlsServerSocketFactory(String password, String keyStoreFileName) {
		SSLServerSocketFactory serverSocketFactory = null;
		try {
			// set up key manager to do server authentication
			SSLContext sslContext;
			KeyManagerFactory keyManagerFactory;
			KeyStore keyStore;
			char[] passphrase = password.toCharArray();

			sslContext = SSLContext.getInstance("TLS");
			keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyStore = KeyStore.getInstance("JKS");

			keyStore.load(new FileInputStream(keyStoreFileName), passphrase);
			keyManagerFactory.init(keyStore, passphrase);
			sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

			serverSocketFactory = sslContext.getServerSocketFactory();
			return serverSocketFactory;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}