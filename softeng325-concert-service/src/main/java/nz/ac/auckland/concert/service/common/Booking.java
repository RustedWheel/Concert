package nz.ac.auckland.concert.service.common;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.types.PriceBand;

/**
 * Domain class to represent bookings (confirmed reservations). 
 * 
 * A BookingDTO describes a booking in terms of:
 * _dateTime       the concert's scheduled date and time for which the booking 
 *                 applies.
 * _seats          the seats that have been booked (represented as a Set of 
 *                 Seat objects).
 * _priceBand      the price band of the booked seats (all seats are within the 
 *                 same price band).
 *
 */

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Booking {
	
	@Id
	@GeneratedValue
	private Long _id;
	
	@ManyToOne
	private Concert concert;
	
	@Column( nullable= false )
	private LocalDateTime _dateTime;
	
	@ElementCollection
	private Set<Seat> _seats;
	
	@Column( nullable= false )
	private PriceBand _priceBand;

	public Booking() {
	}

	public Booking( LocalDateTime dateTime, Set<Seat> seats, PriceBand priceBand) {
		
		_dateTime = dateTime;

		_seats = new HashSet<Seat>();
		_seats.addAll(seats);

		_priceBand = priceBand;
	}

	public LocalDateTime getDateTime() {
		return _dateTime;
	}

	public Set<Seat> getSeats() {
		return Collections.unmodifiableSet(_seats);
	}
	
	public void addReservation(Seat seat) {
		_seats.add(seat);
	}

	public PriceBand getPriceBand() {
		return _priceBand;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Seat))
			return false;
		if (obj == this)
			return true;

		Booking rhs = (Booking) obj;
		return new EqualsBuilder()/*.append(_concertId, rhs._concertId)
				.append(_concertTitle, rhs._concertTitle)*/
				.append(_dateTime, rhs._dateTime)
				.append(_seats, rhs._seats)
				.append(_priceBand, rhs._priceBand).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)/*.append(_concertId)
				.append(_concertTitle)*/.append(_dateTime).append(_seats)
				.append(_priceBand).hashCode();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
/*		buffer.append("concert: ");
		buffer.append(_concertTitle);*/
		buffer.append(", date/time ");
		buffer.append(_seats.size());
		buffer.append(" ");
		buffer.append(_priceBand);
		buffer.append(" seats.");
		return buffer.toString();
	}
}
