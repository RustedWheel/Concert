package nz.ac.auckland.concert.service.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "TOKEN")
@XmlRootElement(name = "token")
@XmlAccessorType(XmlAccessType.FIELD)
public class Token{
	
	@Id
	@Column(name = "TOKEN_KEY")
	private String _tokenKey;
	
	@OneToOne
	@JoinColumn(name="USER",unique=true )
	private User _user;
	
	protected Token() {}
	
	public Token(String Value, User user){
		_tokenKey = Value;
		_user = user;
	}
	
	public String getTokenValue() {
		return _tokenKey;
	}
	
	public User getUser() {
		return _user;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Token))
            return false;
        if (obj == this)
            return true;

        Token rhs = (Token) obj;
        return new EqualsBuilder().
            append(_tokenKey, rhs._tokenKey).
            append(_user, rhs._user).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_tokenKey).
	            append(_user).
	            hashCode();
	}
	

}
