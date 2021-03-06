package nz.ac.auckland.concert.service.services;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import nz.ac.auckland.concert.service.domain.Booking;
import nz.ac.auckland.concert.service.domain.NewsItem;
import nz.ac.auckland.concert.service.domain.Reservation;
import nz.ac.auckland.concert.service.domain.Token;
import nz.ac.auckland.concert.service.domain.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JAX-RS Application subclass for the Concert Web service.
 * 
 * 
 *
 */
@ApplicationPath("/services")
public class ConcertApplication extends Application {

	// This property should be used by your Resource class. It represents the 
	// period of time, in seconds, that reservations are held for. If a
	// reservation isn't confirmed within this period, the reserved seats are
	// returned to the pool of seats available for booking.
	//
	// This property is used by class ConcertServiceTest.
	public static final int RESERVATION_EXPIRY_TIME_IN_SECONDS = 5;

	private Set<Class<?>> _classes = new HashSet<Class<?>>();
	private Set<Object> _singletons = new HashSet<Object>();
	EntityManager em = null;

	public ConcertApplication() {

		try {
			em = PersistenceManager.instance().createEntityManager();
			em.getTransaction().begin();
			
			TypedQuery<Token> tokenQuery = em.createQuery("select t from " + Token.class.getName() +  " t", Token.class);
			List<Token> tokens = tokenQuery.getResultList();
			
			for(Token token : tokens){
				em.remove(token);
			}
			
			TypedQuery<Booking> bookingQuery = em.createQuery("select b from " + Booking.class.getName() +  " b", Booking.class);
			List<Booking> bookings = bookingQuery.getResultList();
			
			for(Booking booking : bookings){
				em.remove(booking);
			}
			
			TypedQuery<User> userQuery = em.createQuery("select u from " + User.class.getName() +  " u", User.class);
			List<User> users = userQuery.getResultList();
			
			for(User user : users){
				em.remove(user);
			}
			
			TypedQuery<Reservation> reservationQuery = em.createQuery("select r from " + Reservation.class.getName() +  " r", Reservation.class);
			List<Reservation> reservations = reservationQuery.getResultList();
			
			for(Reservation reservation : reservations){
				em.remove(reservation);
			}
			
			TypedQuery<NewsItem> newsItemQuery = em.createQuery("select n from " + NewsItem.class.getName() +  " n", NewsItem.class);
			List<NewsItem> newsItems = newsItemQuery.getResultList();
			
			for(NewsItem newsItem : newsItems){
				em.remove(newsItem);
			}
			
			em.flush();
			em.clear();
			em.getTransaction (). commit();

		} catch(Exception e) {
			
		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		_classes.add(ConcertResource.class);
		_classes.add(NewsItemResource.class);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return _classes;
	}

	@Override
	public Set<Object> getSingletons()
	{
		_singletons.add(new PersistenceManager());
		return _singletons;
	}

}
