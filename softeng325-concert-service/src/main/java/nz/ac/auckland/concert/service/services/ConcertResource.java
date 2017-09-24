package nz.ac.auckland.concert.service.services;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.common.util.TheatreLayout;
import nz.ac.auckland.concert.service.common.Config;
import nz.ac.auckland.concert.service.domain.Booking;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.CreditCard;
import nz.ac.auckland.concert.service.domain.Performer;
import nz.ac.auckland.concert.service.domain.Reservation;
import nz.ac.auckland.concert.service.domain.Seat;
import nz.ac.auckland.concert.service.domain.Token;
import nz.ac.auckland.concert.service.domain.User;




@Path("/concerts")
public class ConcertResource {

	private static Logger _logger = LoggerFactory
			.getLogger(ConcertResource.class);

	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response retrieveConcerts() {

		EntityManager em = null;
		ResponseBuilder response = null;
		try {
			em = PersistenceManager.instance().createEntityManager();
			em.getTransaction().begin();

			TypedQuery<Concert> concertQuery = em.createQuery("select c from " + Concert.class.getName() +  " c", Concert.class)
					.setLockMode( LockModeType.PESSIMISTIC_READ )
					.setHint( "javax.persistence.lock.timeout", 5000 );
			List<Concert> concerts = concertQuery.getResultList();

			List<nz.ac.auckland.concert.common.dto.ConcertDTO> concertDTOs = new ArrayList<nz.ac.auckland.concert.common.dto.ConcertDTO>();

			for(Concert concert :concerts){
				concertDTOs.add(ConcertMapper.toDto(concert));
			}
			
			em.getTransaction().commit();

			GenericEntity<List<nz.ac.auckland.concert.common.dto.ConcertDTO>> entity = new GenericEntity<List<nz.ac.auckland.concert.common.dto.ConcertDTO>>(concertDTOs) {};
			response = Response.ok(entity);

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}

	@GET
	@Path("performers")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response retrievePerformers() {

		EntityManager em = null;
		ResponseBuilder response = null;
		try {

			em = PersistenceManager.instance().createEntityManager();

			em.getTransaction().begin();

			TypedQuery<Performer> performerQuery = em.createQuery("select p from " + Performer.class.getName() +  " p", Performer.class)
					.setLockMode( LockModeType.PESSIMISTIC_READ )
					.setHint( "javax.persistence.lock.timeout", 5000 );
			List<Performer> performers = performerQuery.getResultList();
			
			List<nz.ac.auckland.concert.common.dto.PerformerDTO> performerDTOs = new ArrayList<nz.ac.auckland.concert.common.dto.PerformerDTO>();

			for(Performer performer :performers){
				performerDTOs.add(PerformerMapper.toDto(performer));
			}
			
			em.getTransaction().commit();

			GenericEntity<List<nz.ac.auckland.concert.common.dto.PerformerDTO>> entity = new GenericEntity<List<nz.ac.auckland.concert.common.dto.PerformerDTO>>(performerDTOs){};
			response = Response.ok(entity);

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}

	@POST
	@Path("users")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response createUser(nz.ac.auckland.concert.common.dto.UserDTO dtoUser) {

		if(dtoUser.getUsername() == null || dtoUser.getPassword() == null || dtoUser.getFirstname() == null || dtoUser.getLastname() == null){
			_logger.debug(Messages.CREATE_USER_WITH_MISSING_FIELDS);
			throw new BadRequestException(Response
					.status (Status.BAD_REQUEST)
					.entity (Messages.CREATE_USER_WITH_MISSING_FIELDS)
					.build());
		}

		EntityManager em = null;
		ResponseBuilder response = null;
		try {

			em = PersistenceManager.instance().createEntityManager();

			em.getTransaction().begin();

			User searchUser = em.find(User.class, dtoUser.getUsername(), LockModeType.PESSIMISTIC_READ);

			em.getTransaction().commit();

			if(searchUser != null){
				_logger.debug(Messages.CREATE_USER_WITH_NON_UNIQUE_NAME);
				throw new BadRequestException(Response
						.status (Status.BAD_REQUEST)
						.entity (Messages.CREATE_USER_WITH_NON_UNIQUE_NAME)
						.build());
			}

			User user = UserMapper.toDomainModel(dtoUser);

			NewCookie cookie = makeCookie(null);

			Token userToken = new Token(cookie.getValue(), user);

			user.setToken(userToken);

			em.getTransaction().begin();

			em.persist(user);

			em.getTransaction().commit();

			_logger.debug("Created User with username: " + user.getUsername());

			response = Response.created(URI.create("/concerts/users/" + user.getUsername()));

			response.cookie(cookie);

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}


	@POST
	@Path("authenticate")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response authenticateUser(nz.ac.auckland.concert.common.dto.UserDTO dtoUser) {

		if(dtoUser.getUsername() == null || dtoUser.getPassword() == null){
			_logger.debug(Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS);
			throw new BadRequestException(Response
					.status (Status.BAD_REQUEST)
					.entity (Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS)
					.build());
		}

		EntityManager em = null;
		ResponseBuilder response = null;
		try {

			em = PersistenceManager.instance().createEntityManager();

			em.getTransaction().begin();

			User searchUser = em.find(User.class, dtoUser.getUsername(), LockModeType.PESSIMISTIC_WRITE);

			em.getTransaction().commit();

			if(searchUser == null){
				throw new NotFoundException(Response
						.status (Status.NOT_FOUND)
						.entity (Messages.AUTHENTICATE_NON_EXISTENT_USER)
						.build());
			}

			if(!searchUser.getPassword().equals(dtoUser.getPassword())){
				throw new BadRequestException(Response
						.status (Status.BAD_REQUEST)
						.entity (Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD)
						.build());
			}

			response = Response.ok();

			response.cookie(makeCookie(searchUser.getToken().getTokenValue()));
			System.out.print("Authentication token value:" + searchUser.getToken().getTokenValue());

			response.entity(UserMapper.toDto(searchUser));

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}


	@POST
	@Path("users/creditcard")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response registerCreditCard(nz.ac.auckland.concert.common.dto.CreditCardDTO creditCardDTO, @CookieParam("clientUsername") Cookie token) {

		Token storedToken = authenticateToken(token);

		EntityManager em = null;
		ResponseBuilder response = null;
		try {

			em = PersistenceManager.instance().createEntityManager();
			em.getTransaction().begin();

			User user = storedToken.getUser();

			_logger.debug("Found user with username " + user.getUsername());

			for(CreditCard card: user.getCreditcard()){
				_logger.debug("Found user with user credit card: " + card.getNumber());
			}

			user.addCreditcard(CreditCardMapper.toDomainModel(creditCardDTO));

			em.merge(user);

			em.getTransaction().commit();

			response = Response.noContent();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}


	@POST
	@Path("reservation")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response makeReservation(nz.ac.auckland.concert.common.dto.ReservationRequestDTO dtoReservationRequest, @CookieParam("clientUsername") Cookie token) {

		Token storedToken = authenticateToken(token);

		EntityManager em = null;
		ResponseBuilder response = null;
		try {

			em = PersistenceManager.instance().createEntityManager();


			if(dtoReservationRequest.getConcertId() == null || dtoReservationRequest.getDate() == null || dtoReservationRequest.getNumberOfSeats() <= 0 || dtoReservationRequest.getSeatType() == null){
				throw new BadRequestException(Response
						.status (Status.BAD_REQUEST)
						.entity (Messages.RESERVATION_REQUEST_WITH_MISSING_FIELDS)
						.build());
			}

			Set<Seat> bookedSeats = new HashSet<Seat>();
			Set<Seat> avilableSeats = new HashSet<Seat>();
			Set<Seat> reservedSeats = new HashSet<Seat>();

			em.getTransaction().begin();

			Concert concert = em.find(Concert.class, dtoReservationRequest.getConcertId(), LockModeType.PESSIMISTIC_READ);

/*			TypedQuery<Booking> bookingQuery = em.createQuery("select c from " + Booking.class.getName() +  
					" c where CONCERT_CID = (:concertID) and DATE = (:date) and PRICEBAND = (:priceband)", Booking.class);
			bookingQuery.setParameter("concertID", dtoReservationRequest.getConcertId());
			bookingQuery.setParameter("date", dtoReservationRequest.getDate());
			bookingQuery.setParameter("priceband", dtoReservationRequest.getSeatType().toString());
			List<Booking> bookings = bookingQuery.getResultList();*/
			
			TypedQuery<Booking> bookingQuery = em.createQuery("select c from " + Booking.class.getName() +  
					" c where CONCERT_CID = (:concertID) and DATE = (:date) and PRICEBAND = (:priceband)", Booking.class);
			bookingQuery.setParameter("concertID", dtoReservationRequest.getConcertId());
			bookingQuery.setParameter("date", dtoReservationRequest.getDate());
			bookingQuery.setParameter("priceband", dtoReservationRequest.getSeatType().toString());
			bookingQuery.setLockMode( LockModeType.PESSIMISTIC_READ ).setHint( "javax.persistence.lock.timeout", 5000 );
			List<Booking> bookings = bookingQuery.getResultList();

			User user = storedToken.getUser();

			em.getTransaction().commit();

			if(!concert.getDates().contains(dtoReservationRequest.getDate())){
				throw new BadRequestException(Response
						.status (Status.BAD_REQUEST)
						.entity (Messages.CONCERT_NOT_SCHEDULED_ON_RESERVATION_DATE)
						.build());
			}

			for(Booking booking : bookings){
				for(Seat bookedSeat : booking.getSeats()){
					bookedSeats.add(bookedSeat);
				}
			}

			Set<SeatRow> seatRows = TheatreLayout.getRowsForPriceBand(dtoReservationRequest.getSeatType());

			for(SeatRow row : seatRows){
				int number_of_rows = TheatreLayout.getNumberOfSeatsForRow(row);

				for(int i = 1; i < number_of_rows + 1; i++){
					/*_logger.debug("Created seat, row: " + row.name() + " number: " + i);*/
					Seat seat = new Seat(row, new SeatNumber(i));

					if(!bookedSeats.contains(seat)){
						avilableSeats.add(seat);
					}
				}
			}

			if(avilableSeats.size() < dtoReservationRequest.getNumberOfSeats()){
				throw new BadRequestException(Response
						.status (Status.BAD_REQUEST)
						.entity (Messages.INSUFFICIENT_SEATS_AVAILABLE_FOR_RESERVATION)
						.build());
			}

			int seatsToReserve = dtoReservationRequest.getNumberOfSeats();
			for(Seat seat : avilableSeats){
				reservedSeats.add(seat);
				seatsToReserve--;
				if(seatsToReserve == 0){
					break;
				}
			}
			
			Booking newBooking = new Booking(concert, dtoReservationRequest.getDate(),reservedSeats ,dtoReservationRequest.getSeatType());

			em.getTransaction().begin();
			
			em.persist(newBooking);

			Reservation reservation = new Reservation(dtoReservationRequest.getSeatType(), concert, dtoReservationRequest.getDate() , reservedSeats, newBooking.getId());
			
			em.persist(reservation);

			user.addReservation(reservation);

			em.merge(user);

			em.getTransaction().commit();

			ReservationDTO dtoReservation = ReservationMapper.toDto(reservation, dtoReservationRequest);

			response = Response.ok().entity(dtoReservation);

			deleteReservationUponExpiry(reservation.getId(), newBooking.getId(), user.getUsername());

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}


	/**
	 * Confirms a reservation. Prior to calling this method, a successful 
	 * reservation request should have been made via a call to reserveSeats(),
	 * returning a ReservationDTO. 
	 *  
	 * @param reservation a description of the reservation to confirm.
	 * 
	 * @throws ServiceException in response to any of the following conditions.
	 * The exception's message is defined in 
	 * class nz.ac.auckland.concert.common.Messages.
	 * 
	 * Condition: the request is made by an unauthenticated user.
	 * Messages.UNAUTHENTICATED_REQUEST
	 * 
	 * Condition: the request includes an authentication token but it's not
	 * recognised by the remote service.
	 * Messages.BAD_AUTHENTICATON_TOKEN
	 * 
	 * Condition: the reservation has expired.
	 * Messages.EXPIRED_RESERVATION
	 * 
	 * Condition: the user associated with the request doesn't have a credit
	 * card registered with the remote service.
	 * Messages.CREDIT_CARD_NOT_REGISTERED
	 * 
	 * Condition: there is a communication error.
	 * Messages.SERVICE_COMMUNICATION_ERROR
	 * 
	 */

	@POST
	@Path("reservation/confirm")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response confirmReservation(nz.ac.auckland.concert.common.dto.ReservationDTO reservation, @CookieParam("clientUsername") Cookie token) {

		_logger.debug("Start to confirm reservation: " + reservation.getId());

		Token storedToken = authenticateToken(token);

		EntityManager em = null;
		ResponseBuilder response = null;
		try {

			em = PersistenceManager.instance().createEntityManager();

			em.getTransaction().begin();

			Reservation storedReservation = em.find(Reservation.class, reservation.getId());

			Set<CreditCard> card = storedToken.getUser().getCreditcard();

/*			em.getTransaction().commit();*/

			if(storedReservation == null){
				_logger.debug(Messages.EXPIRED_RESERVATION);
				throw new NotFoundException(Response
						.status (Status.NOT_FOUND)
						.entity (Messages.EXPIRED_RESERVATION)
						.build());
			}

			if(card.size() <= 0){
				_logger.debug(Messages.CREDIT_CARD_NOT_REGISTERED);
				
				Long bookingId = storedReservation.getBookingId();
				
				deleteReservation(storedReservation.getId(), bookingId,storedToken.getUser().getUsername());
				
				throw new BadRequestException(Response
						.status (Status.BAD_REQUEST)
						.entity (Messages.CREDIT_CARD_NOT_REGISTERED)
						.build());
			}

			storedReservation.setStatus(true);

			/*em.getTransaction().begin();*/

			em.merge(storedReservation);

			em.getTransaction().commit();

			_logger.debug("Reservation " + reservation.getId() + " confirmed!");

			response = Response.noContent();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}


	@GET
	@Path("bookings")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response getBookings(@CookieParam("clientUsername") Cookie token) {

		Token storedToken = authenticateToken(token);
		EntityManager em = null;
		ResponseBuilder response = null;
		try {

			em = PersistenceManager.instance().createEntityManager();

			em.getTransaction().begin();

			User user = storedToken.getUser();

			Set<Reservation> reservations = user.getReservations();

			Set<BookingDTO> dtoBookings = new HashSet<BookingDTO>();

			
			
			for(Reservation reservation : reservations){
				
				Booking booking = em.find(Booking.class, reservation.getBookingId(), LockModeType.PESSIMISTIC_READ);

				dtoBookings.add(BookingMapper.toDto(booking));
			
			}
			
			em.getTransaction().commit();

			GenericEntity<Set<BookingDTO>> entity = new GenericEntity<Set<BookingDTO>>(dtoBookings) {};

			response = Response.ok().entity(entity);

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}

	/**
	 * Helper method that can be called from every service method to generate a 
	 * NewCookie instance, if necessary, based on the clientId parameter.
	 * 
	 * @param userId the Cookie whose name is Config.CLIENT_COOKIE, extracted 
	 * from a HTTP request message. This can be null if there was no cookie 
	 * named Config.CLIENT_COOKIE present in the HTTP request message. 
	 * 
	 * @return a NewCookie object, with a generated UUID value, if the clientId 
	 * parameter is null. If the clientId parameter is non-null (i.e. the HTTP 
	 * request message contained a cookie named Config.CLIENT_COOKIE), this 
	 * method returns null as there's no need to return a NewCookie in the HTTP
	 * response message. 
	 */
	private NewCookie makeCookie(String cookieValue){

		String value;

		if(cookieValue != null){
			value = cookieValue;
		} else {

			value = UUID.randomUUID().toString();
		}

		NewCookie newCookie = new NewCookie(Config.CLIENT_COOKIE, value);

		_logger.info("Generated cookie: " + newCookie.getValue());

		return newCookie;
	}



	private Token authenticateToken(Cookie cookieToken){

		EntityManager em = null;
		Token storedToken = null;
		try {

			em = PersistenceManager.instance().createEntityManager();

			if(cookieToken == null){
				_logger.debug("Token is null");

				throw new NotAuthorizedException(Response
						.status (Status.UNAUTHORIZED)
						.entity (Messages.UNAUTHENTICATED_REQUEST)
						.build());
			}

			em.getTransaction().begin();

			storedToken = em.find(Token.class, cookieToken.getValue(), LockModeType.PESSIMISTIC_READ);

			if(!cookieToken.getName().equals(Config.CLIENT_COOKIE) || storedToken == null){

				throw new NotAuthorizedException(Response
						.status (Status.UNAUTHORIZED)
						.entity (Messages.BAD_AUTHENTICATON_TOKEN)
						.build());
			}
			
			em.getTransaction().commit();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return storedToken;
	}

	

	private void deleteReservationUponExpiry(Long reservationID, Long bookingID, String username){

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				System.out.println("Checking whether reservation is confirmed!");

				deleteReservation(reservationID, bookingID, username);

			}
		}, ConcertApplication.RESERVATION_EXPIRY_TIME_IN_SECONDS * 1000);

	}
	


	private void deleteReservation(Long reservationID, Long bookingID, String username){

		EntityManager em = null;
		try {
			em = PersistenceManager.instance().createEntityManager();

			em.getTransaction().begin();

			Booking booking = em.find(Booking.class, bookingID, LockModeType.PESSIMISTIC_WRITE);

			Reservation storedReservation = em.find(Reservation.class, reservationID, LockModeType.PESSIMISTIC_WRITE);

			if(storedReservation == null){
				System.out.println("Reservation already deleted!");
			}
			
			if(storedReservation != null){
				if(!storedReservation.getStatus()){

					em.remove(storedReservation);

					em.remove(booking);

					User user = em.find(User.class, username, LockModeType.PESSIMISTIC_WRITE);

					user.removeReservation(storedReservation);

					em.persist(user);

					System.out.println("Deleted reservation with Id = " + reservationID);

				}
			}
			
			em.getTransaction().commit();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

	}

}
