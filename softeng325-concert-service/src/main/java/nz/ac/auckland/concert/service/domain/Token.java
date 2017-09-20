package nz.ac.auckland.concert.service.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Embeddable
public class Token{
	
	@Column(nullable = false, name = "COOKIEVALUE")
	private String _cookieValue;
	
	protected Token() {}
	
	public Token(String cookieValue){
		_cookieValue = cookieValue;
	}
	
	public String getCookie() {
		return _cookieValue;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Token))
            return false;
        if (obj == this)
            return true;

        Token rhs = (Token) obj;
        return new EqualsBuilder().
            append(_cookieValue, rhs._cookieValue).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_cookieValue).
	            hashCode();
	}
	

}
