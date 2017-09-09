package nz.ac.auckland.concert.common.jaxb;

import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB XML adapter to convert between LocalDate instances and Strings.
 * 
 * LocalDate objects are marshalled as Strings, and unmarshalled back 
 * into LocalDate instances. This adapter is necessary because JAXB hasn't
 * yet been updated to support Java's new java.time classes (introduced in 
 * Java 8).
 *
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

	@Override
	public LocalDate unmarshal(String dateAsString) throws Exception {
		if(dateAsString == null) {
			return null;
		}
		return LocalDate.parse(dateAsString);
	}

	@Override
	public String marshal(LocalDate date) throws Exception {
		if(date == null) {
			return null;
		}
		return date.toString();
	}
}