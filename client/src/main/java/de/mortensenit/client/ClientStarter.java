package de.mortensenit.client;

import static de.mortensenit.client.ClientConfigKeys.CLIENT_MODE;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

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

		SSLSocket clientSocket = createClientSocket();

		clientSocket.setEnabledProtocols(new String[] { Constants.PROTOCOL_TLS_1_2 });

		// Arrays.asList(clientSocket.getEnabledCipherSuites()).forEach(e ->
		// logger.debug(e));

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

			SocketFactory socketFactory = getTlsSocketFactory(keyStoreFileName, keyStorePassword, trustStoreFileName);
			clientSocket = (SSLSocket) socketFactory.createSocket(
					ConfigurationContext.get(ClientConfigKeys.SERVER_HOST),
					Integer.valueOf(ConfigurationContext.get(ClientConfigKeys.SERVER_PORT)));
		} else {
			// TODO: wie deaktiviert man die host prüfung?
			clientSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(
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

	/**
	 * generate a TLS server socket factory using the configured keystore
	 * 
	 * @param keyStoreFileName the serverside keystore with the public and private
	 *                         keys
	 * @param keyStorePassword the password for accessing the keystore
	 * @return the server socket factory or null if it failed to initialize
	 */
	private static SocketFactory getTlsSocketFactory(String keyStoreFileName, String keyStorePassword,
			String trustStoreFileName) {
		SSLSocketFactory socketFactory = null;
		try {
			// set up key manager to do server authentication
			SSLContext sslContext;
			KeyManagerFactory keyManagerFactory;
			TrustManagerFactory trustManagerFactory;
			KeyStore keyStore;
			KeyStore trustStore;
			char[] keyStorePassphrase = keyStorePassword != null ? keyStorePassword.toCharArray() : null;

			sslContext = SSLContext.getInstance(Constants.ENCRYPTION_MODE_TLS);
			keyManagerFactory = KeyManagerFactory.getInstance(Constants.KEY_MANAGER_ALGORITHM_SUNX509);
			trustManagerFactory = TrustManagerFactory.getInstance(Constants.KEY_MANAGER_ALGORITHM_SUNX509);
			keyStore = KeyStore.getInstance(Constants.JAVA_KEYSTORE);
			trustStore = KeyStore.getInstance(Constants.JAVA_KEYSTORE);

			FileInputStream fis = null;
			if (keyStoreFileName != null) {
				fis = new FileInputStream(keyStoreFileName);
			}

			keyStore.load(fis, keyStorePassphrase);
			trustStore.load(new FileInputStream(trustStoreFileName), null);
			keyManagerFactory.init(keyStore, keyStorePassphrase);
			trustManagerFactory.init(trustStore);
			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

			socketFactory = sslContext.getSocketFactory();
			return socketFactory;
		} catch (Exception e) {
			LogManager.getLogger().error(e);
			return null;
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