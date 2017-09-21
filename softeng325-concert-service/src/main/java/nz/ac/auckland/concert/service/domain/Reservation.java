package nz.ac.auckland.concert.service.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
	
	@ElementCollection
	@CollectionTable(name = "RESERVATION_SEATS",joinColumns= @JoinColumn( name = "RID" ) )
	@Column( name = "SEAT" )
	private Set<Seat> _seats;

	
	@Column(nullable = false, name = "CID")
	private Long _concertId;
	
	@Column(nullable = false, name = "DATE" )
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _date;	
	
	@Enumerated(EnumType.STRING)
	@Column( nullable= false, name = "PRICEBAND" )
	private PriceBand _seatType;
	
	public Reservation() {}
	
	public Reservation(Long id, Long concertId,  Set<Seat> seats) {
		_id = id;
		_concertId = concertId;
		
		_seats = new HashSet<Seat>(seats);
	}
	
	public Long getId() {
		return _id;
	}
	
	
	
	public Set<Seat> getSeats() {
		return Collections.unmodifiableSet(_seats);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ReservationDTO))
            return false;
        if (obj == this)
            return true;

        ReservationDTO rhs = (ReservationDTO) obj;
        return new EqualsBuilder().
            /*append(_request, rhs._request).
            append(_seats, rhs._seats).*/
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            /*append(_request).
	            append(_seats).*/
	            hashCode();
	}
	

}
