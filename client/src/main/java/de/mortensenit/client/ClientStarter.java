package de.mortensenit.client;

import static de.mortensenit.client.ClientConfigKeys.CLIENT_ENABLED_CIPHER_SUITES;
import static de.mortensenit.client.ClientConfigKeys.CLIENT_EXTENDED_LOGGING;
import static de.mortensenit.client.ClientConfigKeys.CLIENT_MODE;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mortensenit.controller.TLSController;
import de.mortensenit.model.Constants;
import de.mortensenit.model.util.ConfigurationContext;

/**
 * Client side implementation of the EPP service. This client will connect to
 * the specified host and port.
 * 
 * @author frederik.mortensen
 *
 */
public class ClientStarter {

	private Logger logger = LogManager.getLogger();

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		if (ConfigurationContext.getBoolean(CLIENT_EXTENDED_LOGGING, false))
			System.setProperty("javax.net.debug", "all");

		// just get out of static
		new ClientStarter().start();
	}

	/**
	 * load application configuration properties, then start the client socket
	 * 
	 * @throws Exception
	 */
	private void start() throws Exception {
		logger.info("Starting up...");

		try {
			if (ConfigurationContext.get(CLIENT_MODE).equalsIgnoreCase(Constants.ENCRYPTION_MODE_TLS)) {
				startTLSClient();
			} else {
				startPlainClient();
			}
		} catch (SocketException e) {
			logger.error("Connection to server lost! Stopping client.", e);
		} catch (Exception e2) {
			logger.error("A client exception occured!", e2);
		}
	}

	/**
	 * 
	 * @throws SocketException
	 * @throws Exception
	 */
	private void startTLSClient() throws SocketException, Exception {
		logger.info("Trying to connect to the server via TLS...");

		SSLSocket clientSocket = createClientSocket();

		clientSocket.setEnabledProtocols(new String[] { Constants.PROTOCOL_TLS_1_2 });

		String[] cipherSuites = ConfigurationContext.getValues(CLIENT_ENABLED_CIPHER_SUITES, null);
		if (cipherSuites != null) {
			logger.info("Setting enabled cipher suites: ");
			for (String cipherSuite : cipherSuites) {
				logger.info(cipherSuite);
			}
			clientSocket.setEnabledCipherSuites(cipherSuites);
		}

		clientSocket.startHandshake();

		OutputStream os = clientSocket.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

		logger.info("Connected.");

		while (true) {
			writer.append(".");
			writer.newLine();
			writer.flush();
			Thread.sleep(1000);
		}
	}

	/**
	 * .<br />
	 * This method replaces the system parameters for<br />
	 * -Djavax.net.ssl.keyStore=...,<br />
	 * -Djavax.net.ssl.keyStorePassword=... and<br />
	 * -Djavax.net.ssl.trustStore=...<br />
	 * with runtime configuration and an application config.
	 * 
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private SSLSocket createClientSocket() throws UnknownHostException, IOException {

		String keyStoreFileName = null;
		String keyStorePassword = null;
		String trustStoreFileName = null;
		SSLSocket clientSocket = null;

		if (ConfigurationContext.getBoolean(ClientConfigKeys.CLIENT_AUTHENTICATION_NEEDED)) {
			keyStoreFileName = ConfigurationContext.get(ClientConfigKeys.CLIENT_KEYSTORE_FILE);
			keyStorePassword = ConfigurationContext.get(ClientConfigKeys.CLIENT_KEYSTORE_PASSWORD);
		}

		if (ConfigurationContext.getBoolean(ClientConfigKeys.SERVER_VALIDATION_NEEDED)) {
			trustStoreFileName = ConfigurationContext.get(ClientConfigKeys.CLIENT_TRUSTSTORE_FILE);

			SocketFactory socketFactory = TLSController.getTlsSocketFactory(keyStoreFileName, keyStorePassword,
					trustStoreFileName);
			clientSocket = (SSLSocket) socketFactory.createSocket(
					ConfigurationContext.get(ClientConfigKeys.SERVER_HOST),
					Integer.valueOf(ConfigurationContext.get(ClientConfigKeys.SERVER_PORT)));
		} else {
//			clientSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(
//					ConfigurationContext.get(ClientConfigKeys.SERVER_HOST),
//					Integer.valueOf(ConfigurationContext.get(ClientConfigKeys.SERVER_PORT)));

			SocketFactory socketFactory = TLSController.getTlsSocketFactory(keyStoreFileName, keyStorePassword,
					trustStoreFileName);
			clientSocket = (SSLSocket) socketFactory.createSocket(
					ConfigurationContext.get(ClientConfigKeys.SERVER_HOST),
					Integer.valueOf(ConfigurationContext.get(ClientConfigKeys.SERVER_PORT)));

		}
		return clientSocket;
	}

	/**
	 * 
	 * @throws SocketException
	 * @throws Exception
	 */
	private void startPlainClient() throws SocketException, Exception {
		logger.info("Trying to connect to the server via PLAIN connection...");
		Socket socket = new Socket(ConfigurationContext.get(ClientConfigKeys.SERVER_HOST),
				Integer.valueOf(ConfigurationContext.get(ClientConfigKeys.SERVER_PORT)));
		OutputStream os = socket.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

		logger.info("Connected.");
		while (true) {
			writer.append(".");
			writer.newLine();
			writer.flush();
			Thread.sleep(1000);
		}
	}

}