package nz.ac.auckland.concert.service.domain;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.service.domain.jpa.LocalDateTimeConverter;


/**
 * Domain class to represent news items. A news item typically reports that a
 * concert with particular performers is coming to town, that ticket sales for
 * a concert are open, that a concert has additional dates etc.
 * 
 * A NewsItem describes a new items in terms of:
 * _id        the unique identifier for the news item.
 * _timestamp the date and time that the news item was released.
 * _content   the news item context text.   
 *
 */

@Entity
@Table(name = "NEWSITEM")
@XmlRootElement(name = "newsItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class NewsItem {
	
	@Id
	@Column( nullable = false, name = "ID" )
	private Long _niid;
	
	@Column(nullable = false, name = "TIMESTAMP")
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime _timestamp;
	
	@Column(nullable = false, name = "CONTENT")
	private String _content;
	
	public NewsItem() {}
	
	public NewsItem(Long id, LocalDateTime timestamp, String content) {
		_niid = id;
		_timestamp = timestamp;
		_content = content;
	}
	
	public Long getId() {
		return _niid;
	}
	
	public LocalDateTime getTimetamp() {
		return _timestamp;
	}
	
	public String getContent() {
		return _content;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NewsItem))
            return false;
        if (obj == this)
            return true;

        NewsItem rhs = (NewsItem) obj;
        return new EqualsBuilder().
            append(_timestamp, rhs._timestamp).
            append(_content, rhs._content).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_timestamp).
	            append(_content).
	            hashCode();
	}
	

}
