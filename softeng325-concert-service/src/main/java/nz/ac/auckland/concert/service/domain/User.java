package nz.ac.auckland.concert.service.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Domain class to represent users. 
 * 
 * A UserDTO describes a user in terms of:
 * _username  the user's unique username.
 * _password  the user's password.
 * _firstname the user's first name.
 * _lastname  the user's family name.
 *
 */

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

	/*@Id
	@GeneratedValue
	private Long _id;*/
	
	@Id
	/*@Column( nullable= false )*/
	private String _username;
	
	@Column( nullable= false )
	private String _password;
	
	@Column( nullable= false )
	private String _firstname;
	
	@Column( nullable= false )
	private String _lastname;
	
	@Embedded
	private CreditCard _creditcard;
	
	protected User() {}
	
	public User(String username, String password, String lastname, String firstname, CreditCard creditcard) {
		_username = username;
		_password = password;
		_lastname = lastname;
		_firstname = firstname;
		_creditcard = creditcard;
	}
	
	public User(String username, String password, String lastname, String firstname) {
		this(username, password, lastname, firstname, null);
	}
	
	public User(String username, String password) {
		this(username, password, null, null, null);
	}
	
	public String getUsername() {
		return _username;
	}
	
	public String getPassword() {
		return _password;
	}
	
	public String getFirstname() {
		return _firstname;
	}
	
	public String getLastname() {
		return _lastname;
	}
	
	public CreditCard getCreditcard() {
		return _creditcard;
	}
	
	public void setCreditcard(CreditCard creditcard) {
		_creditcard = creditcard;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof User))
            return false;
        if (obj == this)
            return true;

        User rhs = (User) obj;
        return new EqualsBuilder().
            append(_username, rhs._username).
            append(_password, rhs._password).
            append(_firstname, rhs._firstname).
            append(_lastname, rhs._lastname).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_username).
	            append(_password).
	            append(_firstname).
	            append(_password).
	            hashCode();
	}
}

