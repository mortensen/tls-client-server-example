package de.mortensenit.controller;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.logging.log4j.LogManager;

import de.mortensenit.model.Constants;

/**
 * TLS Configuration Controller<br />
 * <br />
 * 
 * This controller replaces the system parameters for<br />
 * -Djavax.net.ssl.keyStore=...,<br />
 * -Djavax.net.ssl.keyStorePassword=... and<br />
 * -Djavax.net.ssl.trustStore=...<br />
 * with runtime configuration and an application config.
 * 
 * @author frederik.mortensen
 *
 */
// TODO: PKCS12 konfigurierbar?
public abstract class TLSController {

	/**
	 * Generate a TLS socket factory using the configured keystore and truststore.
	 * This is used by clients.
	 * 
	 * @param keyStoreFileName   the serverside keystore with the public and private
	 *                           keys
	 * @param keyStorePassword   the password for accessing the keystore
	 * @param trustStoreFileName the name of the trustStore to use for server
	 *                           validation
	 * @return the socket factory or null if it failed to initialize
	 */
	public static SocketFactory getTlsSocketFactory(String keyStoreFileName, String keyStorePassword,
			String trustStoreFileName) {

		SSLSocketFactory socketFactory = null;
		SSLContext sslContext = null;

		try {

			sslContext = SSLContext.getInstance(Constants.ENCRYPTION_MODE_TLS);

			// set up key manager to authenticate against the server
			char[] keyStorePassphrase = keyStorePassword != null ? keyStorePassword.toCharArray() : null;

			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(Constants.KEY_MANAGER_ALGORITHM_SUNX509);
			KeyStore keyStore = KeyStore.getInstance(Constants.JAVA_KEYSTORE);

			FileInputStream fis = null;
			if (keyStoreFileName != null) {
				fis = new FileInputStream(keyStoreFileName);
			}

			keyStore.load(fis, keyStorePassphrase);
			keyManagerFactory.init(keyStore, keyStorePassphrase);

			// set up trust manager to do server validation
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(Constants.KEY_MANAGER_ALGORITHM_SUNX509);
			KeyStore trustStore = KeyStore.getInstance(Constants.JAVA_KEYSTORE);

			fis = null;
			if (trustStoreFileName != null) {
				fis = new FileInputStream(trustStoreFileName);
			}

			// trust stores should not have passwords
			trustStore.load(fis, null);
			trustManagerFactory.init(trustStore);

			// initialize context and return socket factory
			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
			socketFactory = sslContext.getSocketFactory();
			return socketFactory;

		} catch (Exception e) {
			LogManager.getLogger().error(e);
			return null;
		}
	}

	/**
	 * Generate a TLS server socket factory using the configured keystore. This is
	 * used by servers.
	 * 
	 * @param keyStoreFileName   the serverside keystore with the public and private
	 *                           keys
	 * @param keyStorePassword   the password for accessing the keystore
	 * @param trustStoreFileName the serverside truststore for the public keys of
	 *                           the clients to trust
	 * @return the server socket factory or null if it failed to initialize
	 */
	public static ServerSocketFactory getTlsServerSocketFactory(String keyStoreFileName, String keyStorePassword,
			String trustStoreFileName) {

		SSLServerSocketFactory serverSocketFactory = null;
		SSLContext sslContext = null;

		try {

			sslContext = SSLContext.getInstance(Constants.ENCRYPTION_MODE_TLS);

			// set up key manager to do server authentication against the client
			char[] passphrase = keyStorePassword != null ? keyStorePassword.toCharArray() : null;

			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(Constants.KEY_MANAGER_ALGORITHM_SUNX509);
			KeyStore keyStore = KeyStore.getInstance(Constants.JAVA_KEYSTORE);

			FileInputStream fis = null;
			if (keyStoreFileName != null) {
				fis = new FileInputStream(keyStoreFileName);
			}

			keyStore.load(fis, passphrase);
			keyManagerFactory.init(keyStore, passphrase);

			// set up trust manager to do client validation
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(Constants.KEY_MANAGER_ALGORITHM_SUNX509);
			KeyStore trustStore = KeyStore.getInstance(Constants.JAVA_KEYSTORE);

			fis = null;
			if (trustStoreFileName != null) {
				fis = new FileInputStream(trustStoreFileName);
			}

			trustStore.load(fis, null);
			trustManagerFactory.init(trustStore);

			// initialize context and return socket factory
			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
			serverSocketFactory = sslContext.getServerSocketFactory();
			return serverSocketFactory;
		} catch (Exception e) {
			LogManager.getLogger().error(e);
			return null;
		}
	}

}