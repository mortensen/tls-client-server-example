package de.mortensenit.model.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author frederik.mortensen
 *
 */
public class GreetingsMarshallerTest {

	@Test
	public void testMarshall() {
		Greeting greeting = new Greeting();
		greeting.setId(1);
		greeting.setName("test");
		String greetingMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><greeting id=\"1\"><name>test</name></greeting>";
		String result = GreetingMarshaller.marshall(greeting);
		assertEquals(greetingMessage, result);
	}

	@Test
	public void testUnmarshall() {
		String greetingMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><greeting id=\"1\"><name>test</name></greeting>";
		Greeting result = GreetingMarshaller.unmarshall(greetingMessage);
		assertEquals(1, result.getId());
		assertEquals("test", result.getName());

	}

}
