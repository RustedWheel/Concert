/**
 * Declarations in this file apply to all classes in the 
 * nz.ac.auckland.concert.dto package.
 * 
 * The XmlJavaTypeAdapter applies to all fields of type javax.LocalDateTime in
 * classes in package nz.ac.auckland.concert.domain. Whenever fields of this
 * type need to be marshalled, they will be handled by the Adapter class
 * nz.ac.auckland.concert.jaxb.LocalDateTimeAdapter to convert the 
 * LocalDateTime object to an XML string. During unmarshalling, the converter
 * will convert the XML string value back to a LocalDateTime object. The 
 * converter is necessary because JAXB predates the Java 8 date/time classes
 * and doesn't know how to marshall/unmarshall them.
 */

 
 @XmlJavaTypeAdapters({
	    @XmlJavaTypeAdapter(type=LocalDate.class, 
	        value=LocalDateAdapter.class),
	    @XmlJavaTypeAdapter(type=LocalDateTime.class,
	        value=LocalDateTimeAdapter.class)
 })

package nz.ac.auckland.concert.common.dto;
 
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import nz.ac.auckland.concert.common.jaxb.LocalDateAdapter;
import nz.ac.auckland.concert.common.jaxb.LocalDateTimeAdapter;

