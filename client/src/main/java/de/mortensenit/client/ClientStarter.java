package de.mortensenit.client;

import static de.mortensenit.client.ClientConfigKeys.CLIENT_MODE;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

		SSLSocket clientSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(
				ConfigurationContext.get(ClientConfigKeys.SERVER_HOST),
				Integer.valueOf(ConfigurationContext.get(ClientConfigKeys.SERVER_PORT)));
		clientSocket.setEnabledProtocols(new String[] { Constants.PROTOCOL_TLS_1_2 });
		
		//Arrays.asList(clientSocket.getEnabledCipherSuites()).forEach(e -> logger.debug(e));
		
		// clientSocket.setEnabledCipherSuites(new String[] {
		// Constants.CIPHER_TLS_AES_256_GCM_SHA384 });

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

	// disable hostname verification
//	static {
//		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
//
//			public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
//				return true;
//			}
//		});
//	}

}