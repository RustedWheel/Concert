package nz.ac.auckland.concert.service.domain;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;
import nz.ac.auckland.concert.service.domain.jpa.SeatNumberConverter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Domain class to represent seats at the concert venue. 
 * 
 * A Seat describes a seat in terms of:
 * _row    the row of the seat.
 * _number the number of the seat.
 *
 */

@Embeddable
public class Seat {

	
	@Column( nullable= false, name = "ROW" )
	private SeatRow _row;
	
	@Column( nullable= false, name = "NUMBER" )
	@Convert(converter = SeatNumberConverter.class)
	private SeatNumber _number;
	
	public Seat() {}
	
	public Seat(SeatRow row, SeatNumber number) {
		_row = row;
		_number = number;
	}
	
	public SeatRow getRow() {
		return _row;
	}
	
	public SeatNumber getNumber() {
		return _number;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Seat))
            return false;
        if (obj == this)
            return true;

        Seat rhs = (Seat) obj;
        return new EqualsBuilder().
            append(_row, rhs._row).
            append(_number, rhs._number).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_row).
	            append(_number).
	            hashCode();
	}
	
	@Override
	public String toString() {
		return _row + _number.toString();
	}
}

