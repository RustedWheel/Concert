package nz.ac.auckland.concert.service.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;

@Embeddable
public class ReservationRequest {

	@Column( nullable= false, name = "NUMBER_OF_SEATS" )
	private int _numberOfSeats;
	
	@Enumerated(EnumType.STRING)
	@Column( nullable= false, name = "PRICEBAND" )
	private PriceBand _seatType;
	
	@ManyToOne
	private Concert _concert;
	
	@Column(nullable = false, name = "DATE" )
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _date;
	
	public ReservationRequest() {}
	
	public ReservationRequest(int numberOfSeats, PriceBand seatType, Concert concert, LocalDateTime date) {
		_numberOfSeats = numberOfSeats;
		_seatType = seatType;
		_concert = concert;
		_date = date;
	}
	
	public int getNumberOfSeats() {
		return _numberOfSeats;
	}
	
	public PriceBand getSeatType() {
		return _seatType;
	}
	
	public Concert getConcert() {
		return _concert;
	}
	
	public LocalDateTime getDate() {
		return _date;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ReservationRequest))
            return false;
        if (obj == this)
            return true;

        ReservationRequest rhs = (ReservationRequest) obj;
        return new EqualsBuilder().
            append(_numberOfSeats, rhs._numberOfSeats).
            append(_seatType, rhs._seatType).
            append(_concert, rhs._concert).
            append(_date, rhs._date).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_numberOfSeats).
	            append(_seatType).
	            append(_concert).
	            append(_date).
	            hashCode();
	}
	
}
