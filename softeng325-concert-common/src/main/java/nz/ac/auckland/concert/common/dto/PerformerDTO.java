package nz.ac.auckland.concert.common.dto;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nz.ac.auckland.concert.common.types.Genre;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * DTO class to represent performers. 
 * 
 * A PerformerDTO describes a performer in terms of:
 * _id         the unique identifier for a performer.
 * _name       the performer's name.
 * _imageName  the name of an image file for the performer.
 * _genre      the performer's genre.
 * _concertIds identification of each concert in which the performer is 
 *             playing. 
 *             
 */
public class PerformerDTO {

	private Long _id;
	private String _name;
	private String _imageName;
	private Genre _genre;
	
	private Set<Long> _concertIds;
	
	public PerformerDTO() {}
	
	public PerformerDTO(Long id, String name, String imageName, Genre genre, Set<Long> concertIds) {
		_id = id;
		_name = name;
		_imageName = imageName;
		_genre = genre;
		_concertIds = new HashSet<Long>(concertIds);
	}
	
	public Long getId() {
		return _id;
	}
	
	public String getName() {
		return _name;
	}
	
	public String getImageName() {
		return _imageName;
	}
	
	public Set<Long> getConcertIds() {
		return Collections.unmodifiableSet(_concertIds);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PerformerDTO))
            return false;
        if (obj == this)
            return true;

        PerformerDTO rhs = (PerformerDTO) obj;
        return new EqualsBuilder().
            append(_name, rhs._name).
            append(_imageName, rhs._imageName).
            append(_genre, rhs._genre).
            append(_concertIds, rhs._concertIds).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_name).
	            append(_imageName).
	            append(_genre).
	            append(_concertIds).
	            hashCode();
	}
}
