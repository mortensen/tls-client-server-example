package de.mortensenit.model.util;

/**
 * 
 * @author frederik.mortensen
 *
 */
public class StringUtils {

	/**
	 * check strings for empty values
	 * @param strings the 
	 * @return true if none of the strings is null or empty, else false
	 */
	public static boolean isNotNullOrEmpty(String... strings) {
		if (strings == null || strings.length == 0)
			return false;

		for (String value : strings) {
			if (value == null)
				return false;
			if (value.trim().length() == 0)
				return false;
		}
		return true;
	}

	/**
	 * check strings for empty values
	 * @param strings the strings you want to check
	 * @return true if any of the strings is null or empty
	 */
	public static boolean isNullOrEmpty(String... strings) {
		return !isNotNullOrEmpty(strings);
	}

}