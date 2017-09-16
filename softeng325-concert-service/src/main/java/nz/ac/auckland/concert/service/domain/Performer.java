package nz.ac.auckland.concert.service.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Table(name = "PERFORMERS")
@XmlRootElement(name = "performer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Performer {

	@Id
	@GeneratedValue
	@Column( nullable = false, name = "PID" )
	private Long _pid;

	@Column( nullable = false, name = "NAME" )
	private String _name;
	
	@Column( nullable = false, name = "IMAGE_NAME" )
	private String _imageName;
	
	@Enumerated(EnumType.STRING)
	@Column( nullable= false, name = "GENRE"  )
	private Genre _genre;
	
	@ManyToMany(mappedBy = "_performers")
	private Set<Concert> _concerts;
	
	public Performer(Long id, String name, String imageName, Genre genre, Set<Concert> concerts) {
		_pid = id;
		_name = name;
		_imageName = imageName;
		_genre = genre;
		_concerts = concerts;
	}
	
	public Performer(Long id, String name, String imageName, Genre genre) {
		this(id, name, imageName, genre, null);
	}
	
	public Performer(String name, String imageName, Genre genre) {
		this(null, name, imageName, genre, null);
	}
	
	// Required for JPA and JAXB.
	protected Performer() {}
	
	public Long getId() {
		return _pid;
	}
	
	public void setId(Long id) {
		_pid = id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getImageName() {
		return _imageName;
	}

	public void setImageName(String imageName) {
		_imageName = imageName;
	}

	public Genre getGenre() {
		return _genre;
	}

	public void setGenre(Genre genre) {
		_genre = genre;
	}
	
	public Set<Concert> getConcerts() {
		return _concerts;
	}
	
	public void addConcert(Concert concert) {
		_concerts.add(concert);
	}
	
	public void setConcerts(Set<Concert> concerts) {
		_concerts = concerts;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Performer, id: ");
		buffer.append(_pid);
		buffer.append(", name: ");
		buffer.append(_name);
		buffer.append(", s3 image: ");
		buffer.append(_imageName);
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
            append(_imageName, rhs.getImageName()).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_name).hashCode();
	}
}
