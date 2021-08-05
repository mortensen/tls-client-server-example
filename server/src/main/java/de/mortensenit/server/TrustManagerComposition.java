package de.mortensenit.server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mortensenit.model.util.ConfigurationContext;

/**
 * This class loads the default trust manager and adds another local trust store
 * if it is configured.
 * 
 * @author frederik.mortensen
 *
 */
public class TrustManagerComposition implements X509TrustManager {

	private Logger logger = LogManager.getLogger();

	private List<X509TrustManager> trustManagers;

	/**
	 * constructor loading the default trust manager and a custom trust manager if configured
	 */
	public TrustManagerComposition() {
		trustManagers = new ArrayList<>();
		try {
			boolean clientAuthNeeded = ConfigurationContext.getBoolean(ServerConfigKeys.CLIENT_AUTHENTICATION_NEEDED);
			if (clientAuthNeeded)
				trustManagers.add(getCustomTrustmanager());
			trustManagers.add(getDefaultTrustmanager());
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @see X509TrustManager.checkClientTrusted(X509Certificate[] chain, String
	 *      authType)
	 */
	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		for (X509TrustManager trustManager : trustManagers) {
			try {
				trustManager.checkClientTrusted(chain, authType);
				return;
			} catch (CertificateException e) {
				// maybe the next trust manager will trust it, don't break the loop
			}
		}
		throw new CertificateException("None of the trust managers trust this certificate chain!");
	}

	/**
	 * @see X509TrustManager.checkServerTrusted(X509Certificate[] chain, String
	 *      authType)
	 */
	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		for (X509TrustManager trustManager : trustManagers) {
			try {
				trustManager.checkServerTrusted(chain, authType);
				return;
			} catch (CertificateException e) {
				// maybe the next trust manager will trust it, don't break the loop
			}
		}
		throw new CertificateException("None of the trust managers trust this certificate chain!");
	}

	/**
	 * @see X509TrustManager.getAcceptedIssuers()
	 */
	@Override
	public X509Certificate[] getAcceptedIssuers() {
		List<X509Certificate> certificates = new ArrayList<>();
		for (X509TrustManager trustManager : trustManagers) {
			for (X509Certificate certificate : trustManager.getAcceptedIssuers()) {
				certificates.add(certificate);
			}
		}
		return certificates.toArray(new X509Certificate[0]);
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private static X509TrustManager getCustomTrustmanager() throws Exception {
		String fileName = ConfigurationContext.get(ServerConfigKeys.SERVER_TRUSTSTORE_FILE);
		String password = ConfigurationContext.get(ServerConfigKeys.SERVER_TRUSTSTORE_PASSWORD);
		InputStream inputStream = new FileInputStream(fileName);
		return createTrustManager(inputStream, password.toCharArray());
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private static X509TrustManager getDefaultTrustmanager() throws Exception {
		return createTrustManager(null, null);
	}

	/**
	 * 
	 * @param trustStream
	 * @return
	 * @throws Exception
	 */
	private static X509TrustManager createTrustManager(InputStream trustStream, char[] password) throws Exception {
		// Now get trustStore
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

		// load the stream to your store
		trustStore.load(trustStream, password);

		// initialize a trust manager factory with the trusted store
		TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustFactory.init(trustStore);

		// get the trust managers from the factory
		TrustManager[] trustManagers = trustFactory.getTrustManagers();
		for (TrustManager trustManager : trustManagers) {
			if (trustManager instanceof X509TrustManager) {
				return (X509TrustManager) trustManager;
			}
		}
		return null;
	}

}