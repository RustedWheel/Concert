package nz.ac.auckland.concert.common.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Class to represent seat numbers.
 * 
 * SeatNumber is a Number subtype that constrains values in the range 1..26. 
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SeatNumber extends Number {
	private static final int MIN = 1;
	private static final int MAX = 26;
	
	private int _value;
	
	public SeatNumber() {}
	
	public SeatNumber(int value) throws IllegalArgumentException {
		if(value < MIN || value > MAX) {
			throw new IllegalArgumentException();
		}
		_value = value;
	}

	@Override
	public int intValue() {
		return _value;
	}

	@Override
	public long longValue() {
		return _value;
	}

	@Override
	public float floatValue() {
		return _value;
	}

	@Override
	public double doubleValue() {
		return _value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SeatNumber))
            return false;
        if (obj == this)
            return true;

        SeatNumber rhs = (SeatNumber) obj;
        return _value == rhs._value;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_value).
	            hashCode();
	}
	
	@Override
	public String toString() {
		return Integer.toString(_value);
	}
	
}
