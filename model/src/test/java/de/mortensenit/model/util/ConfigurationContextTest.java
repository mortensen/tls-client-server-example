package de.mortensenit.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
	}

	@Test
	public void testGetWithNeededParameter() {
		ConfigurationContext.reset();
		assertThrows(IllegalArgumentException.class, () -> ConfigurationContext.get("invalid1"));
		assertThrows(IllegalArgumentException.class, () -> ConfigurationContext.get("invalid2", true));
	}

	@Test
	public void testGetWithDefaultParameter() {
		ConfigurationContext.reset();
		assertEquals("junit1", ConfigurationContext.get("junit.value1", "defaultValue"));
		assertEquals("defaultValue", ConfigurationContext.get("invalid", "defaultValue"));
	}

}