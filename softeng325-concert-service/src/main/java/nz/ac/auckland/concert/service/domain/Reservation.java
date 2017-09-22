package nz.ac.auckland.concert.service.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;

@Entity
@Table(name = "RESERVATION")
@XmlRootElement(name = "reservation")
@XmlAccessorType(XmlAccessType.FIELD)
public class Reservation {
	
	@Id
	@GeneratedValue
	@Column( name = "RID" )
	private Long _id;
	
	@Enumerated(EnumType.STRING)
	@Column( nullable= false, name = "PRICEBAND" )
	private PriceBand _seatType;
	
	@ManyToOne(fetch = FetchType.LAZY )
	@JoinColumn(name="CONCERT_CID",nullable = false )
	private Concert _concert;
	
	@Column(nullable = false, name = "DATE" )
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _date;
	
	@ElementCollection(fetch = FetchType.LAZY )
	@CollectionTable(name = "RESERVATION_SEATS",joinColumns= @JoinColumn( name = "RID" ) )
	@Column( name = "SEAT" )
	private Set<Seat> _seats;
	
	@Column(nullable = false, name = "Confirmed" )
	private boolean _confirmed;
	
	public Reservation() {}
	
	public Reservation(PriceBand seatType, Concert concert, LocalDateTime date , Set<Seat> seats) {
		_seatType = seatType;
		_concert = concert;
		_date = date;
		_seats = new HashSet<Seat>(seats);
		_confirmed = false;
	}
	
	public Long getId() {
		return _id;
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
	
	public Set<Seat> getSeats() {
		return Collections.unmodifiableSet(_seats);
	}
	
	public boolean getStatus() {
		return _confirmed;
	}
	
	public void setStatus(boolean status) {
		_confirmed = status;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Reservation))
            return false;
        if (obj == this)
            return true;

        Reservation rhs = (Reservation) obj;
        return new EqualsBuilder().
        	append(_seatType, rhs._seatType).
        	append(_concert, rhs._concert).
        	append(_date, rhs._date).
            append(_seats, rhs._seats).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	        	append(_seatType).
	        	append(_concert).
	        	append(_date).
	            append(_seats).
	            hashCode();
	}
	

}
