package nz.ac.auckland.concert.service.services;

import javax.ws.rs.core.Application;

/**
 * JAX-RS Application subclass for the Concert Web service.
 * 
 * 
 *
 */
public class ConcertApplication extends Application {

	// This property should be used by your Resource class. It represents the 
	// period of time, in seconds, that reservations are held for. If a
	// reservation isn't confirmed within this period, the reserved seats are
	// returned to the pool of seats available for booking.
	//
	// This property is used by class ConcertServiceTest.
	public static final int RESERVATION_EXPIRY_TIME_IN_SECONDS = 5;
}
