package de.mortensenit.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author frederik.mortensen
 *
 */
public class ConfigurationContextTest {

	@Test
	public void testIsInitialized() {
		ConfigurationContext.reset();
		assertFalse(ConfigurationContext.isInitialized());
		ConfigurationContext.loadOrRefreshContext();
		assertTrue(ConfigurationContext.isInitialized());
	}

	public void testReset() {
		ConfigurationContext.loadOrRefreshContext();
		assertTrue(ConfigurationContext.isInitialized());
		ConfigurationContext.reset();
		assertFalse(ConfigurationContext.isInitialized());
	}

	@Test
	public void testLoadOrRefreshContext() {
		ConfigurationContext.loadOrRefreshContext();
		assertEquals("junit1", ConfigurationContext.get("junit.value1"));
		assertEquals("junit2", ConfigurationContext.get("junit.value2"));
	}

	@Test
	public void testGet() {
		ConfigurationContext.reset();
		assertEquals("junit1", ConfigurationContext.get("junit.value1"));
		assertThrows(IllegalArgumentException.class, () -> ConfigurationContext.get("invalid"));
		assertThrows(IllegalArgumentException.class, () -> ConfigurationContext.get(null));
	}

	@Test
	public void testGetWithNeededParameter() {
		ConfigurationContext.reset();
		assertThrows(IllegalArgumentException.class, () -> ConfigurationContext.get("invalid1"));
		assertThrows(IllegalArgumentException.class, () -> ConfigurationContext.get("invalid2", true));
		assertEquals("junit1", ConfigurationContext.get("junit.value1"));
	}

	@Test
	public void testGetWithDefaultParameter() {
		ConfigurationContext.reset();
		assertEquals("junit1", ConfigurationContext.get("junit.value1", "defaultValue"));
		assertEquals("defaultValue", ConfigurationContext.get("invalid", "defaultValue"));
		assertEquals("", ConfigurationContext.get("invalid", ""));
		assertNull(ConfigurationContext.get("invalid", null));
		assertNull(ConfigurationContext.get("invalid", false));
	}

	@Test
	public void testGetBoolean() {
		ConfigurationContext.reset();
		assertTrue(ConfigurationContext.getBoolean("junit.booleanTrue"));
		assertFalse(ConfigurationContext.getBoolean("junit.booleanFalse"));
	}

	@Test
	public void testGetBooleanWithDefaultParameter() {
		assertTrue(ConfigurationContext.getBoolean("invalid", true));
		assertFalse(ConfigurationContext.getBoolean("invalid", false));
	}

	@Test
	public void testGetValues() {
		ConfigurationContext.reset();
		String[] result = ConfigurationContext.getValues("junit.strings.commaSeparated");
		assertEquals(3, result.length);
		assertEquals("val1", result[0]);
		assertEquals("val3", result[2]);

		result = ConfigurationContext.getValues("junit.booleanTrue");
		assertEquals(1, result.length);
		assertEquals("true", result[0]);
	}

	@Test
	public void testGetValuesWithDefaultValue() {
		ConfigurationContext.reset();
		String[] defaultValues = null;
		String[] result = ConfigurationContext.getValues("invalid", defaultValues);
		assertNull(defaultValues);

		defaultValues = new String[] { "def1", "def2" };
		result = ConfigurationContext.getValues("invalid", defaultValues);
		assertEquals(2, result.length);
		assertEquals("def2", result[1]);
	}

}