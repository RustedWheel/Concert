package nz.ac.auckland.concert.common.dto;

import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO class to represent seats at the concert venue. 
 * 
 * A SeatDTO describes a seat in terms of:
 * _row    the row of the seat.
 * _number the number of the seat.
 *
 */
public class SeatDTO {

	private SeatRow _row;
	private SeatNumber _number;
	
	public SeatDTO() {}
	
	public SeatDTO(SeatRow row, SeatNumber number) {
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
		if (!(obj instanceof SeatDTO))
            return false;
        if (obj == this)
            return true;

        SeatDTO rhs = (SeatDTO) obj;
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
