package de.mortensenit.model.util;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class that manages application configuration via file system or other
 * resources. The current service uses app.properties to setup runtime behavior.
 * The configuration will be loaded as soon as the application uses the context
 * the first time.
 * 
 * @author frederik.mortensen
 *
 */
public class ConfigurationContext {

	/**
	 * The configuration file must be named app.properties. The keys should be
	 * defined as constants in ServerConfigurationProperties or
	 * ClientConfigurationProperties class
	 */
	private static ResourceBundle applicationProperties;

	/**
	 * the loaded content of the resource bundle
	 */
	private static Map<String, String> internalContext = null;

	private static boolean initialized = false;

	private static Logger logger = LogManager.getLogger();

	/**
	 * private constructor, no instance needed, methods are static
	 */
	private ConfigurationContext() {
	}

	/**
	 * load the app.properties configuration file into memory
	 * 
	 * @return
	 */
	public static final void loadOrRefreshContext() throws MissingResourceException {
		logger.info("Loading application properties.");
		reset();

		applicationProperties = ResourceBundle.getBundle("app");

		logger.info("Server configuration loaded:");
		for (String key : applicationProperties.keySet()) {
			String value = applicationProperties.getString(key);
			internalContext.put(key, value);
			logger.info(key + ": " + value);
		}

		initialized = true;
	}

	/**
	 * unload the current loaded configuration
	 */
	public static final void reset() {
		internalContext = new HashMap<>();
		applicationProperties = null;
		initialized = false;
	}

	/**
	 * Load config value for a given key from the application configuration file.
	 * 
	 * @param key the property that needs to be configured
	 * @return the value of the needed key from the loaded context
	 * @throws IllegalArgumentException if the value for the given key was not set
	 *                                  in the configuration
	 * @throws MissingResourceException if no application properties configuration
	 *                                  file was found
	 */
	public static String get(String key) throws IllegalArgumentException, MissingResourceException {
		if (!isInitialized())
			loadOrRefreshContext();

		return get(key, true);
	}

	/**
	 * Load config value for a given key from the application configuration file.
	 * 
	 * @param key    the property that needs to be configured
	 * @param needed flag to tell how to react if the key was not found. Either
	 *               throw an Exception or return null.
	 * @return null if the value was not set and not needed.
	 * @throws IllegalArgumentException if the value for the given key was not set
	 *                                  in the configuration but was needed
	 * @throws MissingResourceException if no application properties configuration
	 *                                  file was found
	 */
	public static String get(String key, boolean needed) throws IllegalArgumentException, MissingResourceException {
		if (!isInitialized())
			loadOrRefreshContext();

		String value = internalContext.get(key);

		if (StringUtils.isNullOrEmpty(value)) {
			if (needed) {
				throw new IllegalArgumentException(key);
			} else
				return null;
		} else {
			return value;
		}
	}

	/**
	 * Load config value for a given key from the application configuration file.
	 * 
	 * @param key          the property that needs to be configured
	 * @param defaultValue returns this value if the given value was not configured
	 * @return the value of the needed key from the loaded context or else the
	 *         default value
	 * @throws MissingResourceException if no application properties configuration
	 *                                  file was found
	 */
	public static String get(String key, String defaultValue) throws MissingResourceException {
		if (!isInitialized())
			loadOrRefreshContext();

		String value = get(key, false);
		if (StringUtils.isNullOrEmpty(value))
			return defaultValue;
		return value;
	}

	/**
	 * Load config value for a given key from the application configuration file and
	 * parse it to boolean.
	 * 
	 * @param key the property that needs to be configured
	 * @return the value of the needed key from the loaded context
	 * @throws IllegalArgumentException if the value for the given key was not set
	 *                                  in the configuration
	 * @throws MissingResourceException if no application properties configuration
	 *                                  file was found
	 */
	public static Boolean getBoolean(String key) throws IllegalArgumentException, MissingResourceException {
		String value = get(key, true);
		return Boolean.valueOf(value);
	}

	/**
	 * Load config value for a given key from the application configuration file and
	 * parse it to boolean.
	 * 
	 * @param key          the property that needs to be configured
	 * @param defaultValue returns this value if the given value was not configured
	 * @return the value of the needed key from the loaded context or else the
	 *         default value
	 * @throws IllegalArgumentException if the value for the given key was not set
	 *                                  in the configuration
	 * @throws MissingResourceException if no application properties configuration
	 *                                  file was found
	 */
	public static Boolean getBoolean(String key, boolean defaultValue)
			throws IllegalArgumentException, MissingResourceException {
		String value = get(key, false);
		if (StringUtils.isNotNullOrEmpty(value)) {
			return Boolean.valueOf(value);
		} else {
			return defaultValue;
		}
	}

	/**
	 * Load config value for a given key from the application configuration file.
	 * Then split it comma separated and trim it.
	 * 
	 * @param key the property that needs to be configured
	 * @return a comma separated list witht the values of the needed key from the
	 *         loaded context
	 * @throws IllegalArgumentException if the value for the given key was not set
	 *                                  in the configuration
	 * @throws MissingResourceException if no application properties configuration
	 *                                  file was found
	 */
	public static String[] getValues(String key) {
		String value = get(key, true);
		String[] values = value.split(",");
		for (Integer i = 0; i < values.length; i++) {
			values[i] = values[i].trim();
		}
		return values;
	}

	/**
	 * Load config value for a given key from the application configuration file.
	 * Then split it comma separated and trim it.
	 * 
	 * @param key           the property that needs to be configured
	 * @param defaultValues the default values if none were configured
	 * @return a comma separated list witht the values of the needed key from the
	 *         loaded context
	 * @throws IllegalArgumentException if the value for the given key was not set
	 *                                  in the configuration
	 * @throws MissingResourceException if no application properties configuration
	 *                                  file was found
	 */
	public static String[] getValues(String key, String[] defaultValues) {
		String value = get(key, false);

		if (value == null)
			return defaultValues;

		String[] values = value.split(",");
		for (Integer i = 0; i < values.length; i++) {
			values[i] = values[i].trim();
		}
		return values;
	}

	/**
	 * shows if the configuration context was already loaded
	 */
	public static boolean isInitialized() {
		return initialized;
	}

}
