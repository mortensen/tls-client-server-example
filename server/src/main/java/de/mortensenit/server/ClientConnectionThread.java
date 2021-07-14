package de.mortensenit.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author frederik.mortensen
 *
 */
public class ClientConnectionThread implements Runnable {

	private Logger logger = LogManager.getLogger();

	private Socket clientSocket = null;
	
	/**
	 * 
	 */
	@Override
	public void run() {

		try (OutputStream os = clientSocket.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
				InputStream is = clientSocket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

			logger.info("Connection to client established.");
			
			//first send greeting message
			sendGreeting(writer);

			while (true) {
				try {
					String line = reader.readLine();
					if (line == null) {
						logger.info("Connection to client was closed.");
						break;
					}
					logger.info(this.toString() + " received: " + line);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error("Sleep interrupted.", e);
				}
			}
		} catch (SocketException e) {
			logger.error("The client connection was lost!");
		} catch (Exception e2) {
			logger.error("A general server exception occured!", e2);
		} finally {
			logger.info("Connection closed through client side on port.");
		}

	}
	
	/**
	 * 
	 * @param writer
	 * @throws IOException
	 */
	private void sendGreeting(BufferedWriter writer) throws IOException {
		GreetingController greetingController = new GreetingController();
		String greetingMessage = greetingController.greetClient();
		writer.write(greetingMessage);
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

}