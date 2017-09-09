package nz.ac.auckland.concert.service.domain.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import nz.ac.auckland.concert.common.types.SeatNumber;

/**
 * AttributeConverter class to convert SeatNumber objects to Integers, which
 * can be readily mapped to a relational schema using JPA.
 *
 */
@Converter
public class SeatNumberConverter implements
		AttributeConverter<SeatNumber, Integer> {

	@Override
	public Integer convertToDatabaseColumn(SeatNumber seatNumber) {
		return (seatNumber == null ? null : seatNumber.intValue());
	}

	@Override
	public SeatNumber convertToEntityAttribute(Integer number) {
		return (number == null ? null : new SeatNumber(number));
	}
}
