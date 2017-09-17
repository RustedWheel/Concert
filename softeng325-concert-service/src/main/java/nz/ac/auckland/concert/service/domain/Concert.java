package nz.ac.auckland.concert.service.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.jaxb.LocalDateTimeAdapter;
import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;


/**
 * Domain class to represent a Concert. A Concert is characterised by an unique ID, 
 * title, dates and time, ticket costs, and featuring Performers.
 * 
 * Concert implements Comparable with a natural ordering based on its title.
 * Hence, in a List, Concert instances can be sorted into alphabetical order
 * based on their title value.
 *
 */

@Entity
@Table(name = "CONCERTS")
@XmlRootElement(name = "concert")
@XmlAccessorType(XmlAccessType.FIELD)
public class Concert implements Comparable<Concert> {

	@Id
	@GeneratedValue
	@Column(nullable = false, name = "CID")
	private Long _cid;
	
	@Column(nullable = false, name = "TITLE")
	private String _title;
	
	@ElementCollection
	@CollectionTable(name = "CONCERT_DATES",joinColumns= @JoinColumn( name = "CID" ) )
	@Column( name = "DATE" )
	@Convert(converter = LocalDateTimeConverter.class)
	private Set<LocalDateTime> _dates;
	
	@ElementCollection
	@CollectionTable(name = "CONCERT_TARIFS" )
	@MapKeyColumn( name = "PRICEBANDS" )
	@MapKeyClass(PriceBand.class)
	@MapKeyEnumerated(EnumType.STRING)
	@Column( name = "TICKET_PRICES" )
	private Map<PriceBand, BigDecimal> _tariff;
	 
	@ManyToMany
	@JoinTable(name="CONCERT_PERFORMER", joinColumns= @JoinColumn(name = "CID"),inverseJoinColumns= @JoinColumn(name = "PID"))
	@Column(name = "PERFORMER_ID")
	private Set<Performer> _performers;
	
	@OneToMany(mappedBy="concert", cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
	private Set<Booking> _bookings;
	
	public Concert(Long id, String title, Set<LocalDateTime> dates, Map<PriceBand, BigDecimal> ticketPrices, Set<Performer> performers, Set<Booking> bookings) {
		_cid = id;
		_title = title;
		_dates = dates;
		_tariff = new HashMap<PriceBand, BigDecimal>(ticketPrices);
		_performers = performers;
		_bookings = bookings;
	}
	
	public Concert(Long id, String title, Set<LocalDateTime> date, Map<PriceBand, BigDecimal> ticketPrices, Set<Performer> performers) {
		this(id, title, date, ticketPrices, performers, null);
	}
	
	public Concert(String title, Set<LocalDateTime> date, Map<PriceBand, BigDecimal> ticketPrices, Set<Performer> performers, Set<Booking> bookings) {
		this(null, title, date, ticketPrices, performers, bookings);
	}

	// Required for JPA and JAXB.
	protected Concert() {}
	
	public Long getId() {
		return _cid;
	}
	
	public void setId(Long id) {
		_cid = id;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public Set<LocalDateTime> getDates() {
		return _dates;
	}
	
	public void setDates(Set<LocalDateTime> dates) {
		_dates = dates;
	}

	public Set<Performer> getPerformers() {
		return _performers;
	}
	
	public Map<PriceBand, BigDecimal> getTicketPrice() {
		return _tariff;
	}
	
	public void setTicketPrice(Map<PriceBand, BigDecimal> Tariff) {
		_tariff = Tariff;
	}
	
	public Set<Booking> getBookings() {
		return _bookings;
	}
	
	public void setBookings(Set<Booking> bookings) {
		_bookings = bookings;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Concert, id: ");
		buffer.append(_cid);
		buffer.append(", title: ");
		buffer.append(_title);
		buffer.append(", date: ");
		
		for(LocalDateTime date: _dates){
			buffer.append(date.toString());
		}
		
		buffer.append(", featuring: ");
		
		for(Performer peroformer: _performers){
			buffer.append(peroformer.getName());
		}
		
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Concert))
            return false;
        if (obj.equals(this))
            return true;

        Concert rhs = (Concert) obj;
        return new EqualsBuilder().
            append(_title, rhs.getTitle()).
            append(_dates, rhs.getDates()).
            append(_tariff, rhs.getTicketPrice()).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_title).
	            append(_dates).
	            append(_tariff).hashCode();
	}

	@Override
	public int compareTo(Concert concert) {
		return _title.compareTo(concert.getTitle());
	}
}
