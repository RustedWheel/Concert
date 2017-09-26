package nz.ac.auckland.concert.common.dto;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO class to represent news items. A news item typically reports that a
 * concert with particular performers is coming to town, that ticket sales for
 * a concert are open, that a concert has additional dates etc.
 * 
 * A NewsItemDTO describes a new items in terms of:
 * _id        the unique identifier for the news item.
 * _timestamp the date and time that the news item was released.
 * _content   the news item context text.   
 *
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NewsItemDTO {

	private Long _id;
	private LocalDateTime _timestamp;
	private String _content;
	
	public NewsItemDTO() {}
	
	public NewsItemDTO(Long id, LocalDateTime timestamp, String content) {
		_id = id;
		_timestamp = timestamp;
		_content = content;
	}
	
	public Long getId() {
		return _id;
	}
	
	public LocalDateTime getTimetamp() {
		return _timestamp;
	}
	
	public String getContent() {
		return _content;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NewsItemDTO))
            return false;
        if (obj == this)
            return true;

        NewsItemDTO rhs = (NewsItemDTO) obj;
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
