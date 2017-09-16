package nz.ac.auckland.concert.service.domain;

import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
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
@Table(name = "USER")
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

	/*@Id
	@GeneratedValue
	private Long _id;*/
	
	@Id
	@Column( nullable= false, name = "USERNAME" )
	private String _username;
	
	@Column(nullable = false, name = "PASSWORD")
	private String _password;
	
	@Column(nullable = false, name = "FIRST_NAME")
	private String _firstname;
	
	@Column(nullable = false, name = "LAST_NAME")
	private String _lastname;
	
	@ElementCollection
	@CollectionTable( name = "USER_CREDITCARDS", joinColumns= @JoinColumn( name = "USERNAME" ) )
/*	@AttributeOverrides( {
		@AttributeOverride( name = "_type" ,
		column = @Column( name = "TYPE" , nullable = false) ),
		@AttributeOverride( name = "_name" ,
		column = @Column( name = "NAME" , nullable = false) ),
		@AttributeOverride( name = "_number" ,
		column = @Column( name = "NUMBER" , nullable = false) ),
		@AttributeOverride( name = "_expiryDate" ,
		column = @Column( name = "EXPIRY_DATE" , nullable = false) )
		} )*/
	private Set<CreditCard> _creditcard;
	
	protected User() {}
	
	public User(String username, String password, String lastname, String firstname, Set<CreditCard> creditcard) {
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
	
	public Set<CreditCard> getCreditcard() {
		return _creditcard;
	}
	
	public void setCreditcard(Set<CreditCard> creditcard) {
		_creditcard = creditcard;
	}
	
	public void AddCreditcard(CreditCard creditcard) {
		_creditcard.add(creditcard);
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

