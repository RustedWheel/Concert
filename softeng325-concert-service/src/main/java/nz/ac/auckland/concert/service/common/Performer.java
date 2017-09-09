package nz.ac.auckland.concert.service.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.concert.common.types.Genre;

/**
 * Class to represent a Performer (an artist or band that plays at Concerts). A
 * Performer object has an ID (a database primary key value), a name, the name 
 * of an image file, and a genre.
 *
 */

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Performer {

	@Id
	@GeneratedValue
	private Long _id;

	@Column( nullable= false )
	private String _name;
	
	@Column( nullable= false )
	private String _s3ImageUri;
	
	@Column( nullable= false )
	private Genre _genre;
	
	public Performer(Long id, String name, String s3ImageUri, Genre genre) {
		_id = id;
		_name = name;
		_s3ImageUri = s3ImageUri;
		_genre = genre;
	}
	
	public Performer(String name, String s3ImageUri, Genre genre) {
		this(null, name, s3ImageUri, genre);
	}
	
	// Required for JPA and JAXB.
	protected Performer() {}
	
	public Long getId() {
		return _id;
	}
	
	public void setId(Long id) {
		_id = id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getS3ImageUri() {
		return _s3ImageUri;
	}

	public void setS3ImageUri(String s3ImageUri) {
		_s3ImageUri = s3ImageUri;
	}

	public Genre getGenre() {
		return _genre;
	}

	public void setGenre(Genre genre) {
		_genre = genre;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Performer, id: ");
		buffer.append(_id);
		buffer.append(", name: ");
		buffer.append(_name);
		buffer.append(", s3 image: ");
		buffer.append(_s3ImageUri);
		buffer.append(", genre: ");
		buffer.append(_genre.toString());
		
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Performer))
            return false;
        if (obj == this)
            return true;

        Performer rhs = (Performer) obj;
        return new EqualsBuilder().
            append(_name, rhs.getName()).
            append(_genre, rhs.getGenre()).
            append(_s3ImageUri, rhs.getS3ImageUri()).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_name).hashCode();
	}
}
