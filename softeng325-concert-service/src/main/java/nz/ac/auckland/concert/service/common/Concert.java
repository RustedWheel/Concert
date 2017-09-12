package nz.ac.auckland.concert.service.common;

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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.jaxb.LocalDateTimeAdapter;
import nz.ac.auckland.concert.common.types.PriceBand;


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
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Concert implements Comparable<Concert> {

	@Id
	@GeneratedValue
	private Long _id;
	
	@Column( nullable= false )
	private String _title;
	
	@ElementCollection
	@Convert(converter = LocalDateTimeAdapter.class)
	private Set<LocalDateTime> _dates;
	
	@ElementCollection
	@CollectionTable(name = "TARIFF" )
	@MapKeyColumn( name = "PRICEBAND" )
	@Column( name = "PRICEBANDS" )
	private Map<PriceBand, BigDecimal> _tariff;
	
	@ManyToMany(mappedBy= "_concerts", cascade = { CascadeType.PERSIST,CascadeType.REMOVE} )
	private Set<Performer> _performers;
	
	@OneToMany(mappedBy="concert", cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
	private Set<Booking> _bookings;
	
	public Concert(Long id, String title, Set<LocalDateTime> dates, Map<PriceBand, BigDecimal> ticketPrices, Set<Performer> performers, Set<Booking> bookings) {
		_id = id;
		_title = title;
		_dates = dates;
		_tariff = new HashMap<PriceBand, BigDecimal>(ticketPrices);
		_performers = performers;
		_bookings = bookings;
	}
	
	public Concert(String title, Set<LocalDateTime> date, Map<PriceBand, BigDecimal> ticketPrices, Set<Performer> performers, Set<Booking> bookings) {
		this(null, title, date, ticketPrices, performers, bookings);
	}

	// Required for JPA and JAXB.
	protected Concert() {}
	
	public Long getId() {
		return _id;
	}
	
	public void setId(Long id) {
		_id = id;
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
	
	public Map<PriceBand, BigDecimal> getTariff() {
		return _tariff;
	}
	
	public void setTariff(Map<PriceBand, BigDecimal> Tariff) {
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
		buffer.append(_id);
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
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_title).hashCode();
	}

	@Override
	public int compareTo(Concert concert) {
		return _title.compareTo(concert.getTitle());
	}
}
