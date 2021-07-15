package de.mortensenit.model.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author frederik.mortensen
 *
 */
public class StringUtilsTest {

	@Test
	public void testNotNullOrEmpty() {
		assertFalse(StringUtils.isNotNullOrEmpty((String)null));
		assertFalse(StringUtils.isNotNullOrEmpty(new String[0]));
		assertFalse(StringUtils.isNotNullOrEmpty(""));
		assertFalse(StringUtils.isNotNullOrEmpty(new String()));
		assertFalse(StringUtils.isNotNullOrEmpty(new String[] { "", "" }));
		assertFalse(StringUtils.isNotNullOrEmpty(new String[] { " ", "" }));
		assertFalse(StringUtils.isNotNullOrEmpty(new String[] { "", " " }));
		assertFalse(StringUtils.isNotNullOrEmpty(new String[] { " ", null }));
		assertFalse(StringUtils.isNotNullOrEmpty(new String[] { null, " " }));
		assertFalse(StringUtils.isNotNullOrEmpty(new String[] { " ", " " }));

		assertTrue(StringUtils.isNotNullOrEmpty("test"));
		assertTrue(StringUtils.isNotNullOrEmpty("test test"));
		assertTrue(StringUtils.isNotNullOrEmpty("  test"));
	}
	
	@Test
	public void testNullOrEmpty() {
		assertTrue(StringUtils.isNullOrEmpty((String)null));
		assertTrue(StringUtils.isNullOrEmpty(new String[0]));
		assertTrue(StringUtils.isNullOrEmpty(""));
		assertTrue(StringUtils.isNullOrEmpty(new String()));
		assertTrue(StringUtils.isNullOrEmpty(new String[] { "", "" }));
		assertTrue(StringUtils.isNullOrEmpty(new String[] { " ", "" }));
		assertTrue(StringUtils.isNullOrEmpty(new String[] { "", " " }));
		assertTrue(StringUtils.isNullOrEmpty(new String[] { " ", null }));
		assertTrue(StringUtils.isNullOrEmpty(new String[] { null, " " }));
		assertTrue(StringUtils.isNullOrEmpty(new String[] { " ", " " }));

		assertFalse(StringUtils.isNullOrEmpty("test"));
		assertFalse(StringUtils.isNullOrEmpty("test test"));
		assertFalse(StringUtils.isNullOrEmpty("  test"));
	}

}
