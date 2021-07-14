package de.mortensenit.model.server;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author frederik.mortensen
 *
 */
public class GreetingMarshaller {

	private static Logger logger = LogManager.getLogger();

	/**
	 * 
	 * @param greeting
	 * @return
	 */
	public static String marshall(Greeting greeting) {

		StringWriter resultWriter = new StringWriter();

		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(Greeting.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

			jaxbMarshaller.marshal(greeting, resultWriter);

			return resultWriter.toString();

		} catch (JAXBException e) {
			logger.error("Could not marshall object!", e);
			return null;
		}

	}

	/**
	 * 
	 * @param greetingMessage
	 * @return
	 */
	public static Greeting unmarshall(String greetingMessage) {

		StringReader stringReader = new StringReader(greetingMessage);

		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(Greeting.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			Greeting greeting = (Greeting) jaxbUnmarshaller.unmarshal(stringReader);

			return greeting;

		} catch (JAXBException e) {
			logger.error("Could not unmarshall string!", e);
			return null;
		}
	}

}