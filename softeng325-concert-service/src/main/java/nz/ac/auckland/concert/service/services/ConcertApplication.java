package nz.ac.auckland.concert.service.services;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
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

	public ConcertApplication() {
		_classes.add(ConcertResource.class);
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
