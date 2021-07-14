package de.mortensenit.server;

import de.mortensenit.model.server.Greeting;
import de.mortensenit.model.server.GreetingMarshaller;

/**
 * 
 * @author frederik.mortensen
 *
 */
public class GreetingController {

	/**
	 * 
	 * @return
	 */
	public String greetClient() {
		Greeting greeting = new Greeting();
		greeting.setName("Welcome to my EPP server!");
		return GreetingMarshaller.marshall(greeting);
	}

}
