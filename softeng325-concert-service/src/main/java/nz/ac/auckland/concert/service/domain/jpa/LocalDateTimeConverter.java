package nz.ac.auckland.concert.service.domain.jpa;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * AttributeConverter implementation that allows Java's time.LocalDateTime
 * objects to be persisted, via JPA, in a database. 
 * 
 * The converter is necessary because JPA 2.1 predates Java 8, which is when 
 * the time.LocalDateTime classes was introduced. Without this converter, 
 * time.LocalDateTime values would be stored as BLOBs - and hence database
 * queries would not be able to be expressed in terms of date/time.
 *
 */
@Converter(autoApply=true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {
	
    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime locDateTime) {
    	return (locDateTime == null ? null : Timestamp.valueOf(locDateTime));
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
    	return (sqlTimestamp == null ? null : sqlTimestamp.toLocalDateTime());
    }
}