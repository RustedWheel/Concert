package nz.ac.auckland.concert.common.jaxb;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * JAXB XML adapter to convert between LocalDateTime instances and 
 * Strings. 
 * 
 * LocalDateTime objects are marshalled as Strings, and unmarshalled back 
 * into LocalDateTime instances. This adapter is necessary because JAXB hasn't
 * yet been updated to support Java's new java.time classes (introduced in Java 
 * 8).
 *
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

	@Override
	public LocalDateTime unmarshal(String dateTimeAsString) throws Exception {
		if(dateTimeAsString == null) {
			return null;
		}
		return LocalDateTime.parse(dateTimeAsString);
	}

	@Override
	public String marshal(LocalDateTime dateTime) throws Exception {
		if(dateTime == null) {
			return null;
		}
		return dateTime.toString();
	}
}