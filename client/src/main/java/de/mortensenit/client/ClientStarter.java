package de.mortensenit.client;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mortensenit.model.Constants;

/**
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
		// new ClientStarter().startPlainClient();
		new ClientStarter().startTLSClient();
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void startPlainClient() {
		logger.info("Trying to connect to server...");
		try (Socket socket = new Socket("localhost", 7000);
				OutputStream os = socket.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {

			logger.info("Connected.");
			while (true) {
				writer.append(".");
				writer.newLine();
				writer.flush();
				Thread.sleep(1000);
			}
		} catch (SocketException e) {
			logger.error("Connection to server lost! Stopping client.");
		} catch (Exception e2) {
			logger.error("A client exception occured!", e2);
		}
	}

	/**
	 * 
	 */
	private void startTLSClient() {
		try {
			logger.info("Trying to connect to server...");

			SSLSocket clientSocket = (SSLSocket) SSLSocketFactory.getDefault().createSocket("localhost", 7000);
			clientSocket.setEnabledProtocols(new String[] { Constants.PROTOCOL_TLS_1_2 });
			//Arrays.asList(clientSocket.getEnabledCipherSuites()).forEach(System.out::println);
			//clientSocket.setEnabledCipherSuites(new String[] { Constants.CIPHER_TLS_AES_256_GCM_SHA384 });

			OutputStream os = clientSocket.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

			logger.info("Connected.");

			while (true) {
				writer.append(".");
				writer.newLine();
				writer.flush();
				Thread.sleep(1000);
			}
		} catch (SocketException e) {
			logger.error("Connection to server lost! Stopping client.", e);
		} catch (Exception e2) {
			logger.error("A client exception occured!", e2);
		}
	}
}