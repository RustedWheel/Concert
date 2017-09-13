package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.types.PriceBand;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@XmlRootElement(name = "reservation")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReservationAndRequest {
	@Id
	@GeneratedValue
	private Long _RSVN_Id;

	@Column(nullable = false, name = "NUMBER_OF_SEAT")
	private int _numberOfSeats;

	@Column(nullable = false, name = "SEAT_TYPE")
	private PriceBand _seatType;

	@ManyToOne
	@JoinColumn(name = "CONCERT_ID", nullable = false)
	private Long _concertId;

	@Column
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _date;

	@OneToMany(mappedBy= "reservation", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private Set<Seat> _seats;

	public ReservationAndRequest() {}

	public ReservationAndRequest(int numberOfSeats, PriceBand seatType, Long concertId, LocalDateTime date, Long id, Set<Seat> seats) {
		_numberOfSeats = numberOfSeats;
		_seatType = seatType;
		_concertId = concertId;
		_date = date;

		_RSVN_Id = id;
		_seats = new HashSet<Seat>(seats);
	}

	public int getNumberOfSeats() {
		return _numberOfSeats;
	}

	public PriceBand getSeatType() {
		return _seatType;
	}

	public Long getConcertId() {
		return _concertId;
	}

	public LocalDateTime getDate() {
		return _date;
	}

	public Long getId() {
		return _RSVN_Id;
	}

	public Set<Seat> getSeats() {
		return Collections.unmodifiableSet(_seats);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ReservationAndRequest))
			return false;
		if (obj == this)
			return true;

		ReservationAndRequest rhs = (ReservationAndRequest) obj;
		return new EqualsBuilder().
				append(_numberOfSeats, rhs._numberOfSeats).
				append(_seatType, rhs._seatType).
				append(_concertId, rhs._concertId).
				append(_date, rhs._date).
				append(_seats, rhs._seats).
				isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(_numberOfSeats).
				append(_seatType).
				append(_concertId).
				append(_date).
				append(_seats).
				hashCode();
	}
}

