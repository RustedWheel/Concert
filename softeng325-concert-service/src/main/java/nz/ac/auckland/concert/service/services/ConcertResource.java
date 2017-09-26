package nz.ac.auckland.concert.service.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
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
import nz.ac.auckland.concert.common.message.Messages;
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

	/**
	 * Retrieves all Concerts. The HTTP response message 
	 * has a status code of either 200 or 500, if there is an error processing the
	 * request
	 * 
	 * This method maps to the URI pattern <base-uri>/concerts.
	 * 
	 * @return a Response object containing all the Concerts.
	 */
	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response retrieveConcerts() {

		EntityManager em = null;
		ResponseBuilder response = null;
		try {
			em = PersistenceManager.instance().createEntityManager();
			em.getTransaction().begin();

			//Query the database to retrieve all concerts. Use a read lock so that the records cannot be altered when the query is made
			//to avoid inconsistent result
			TypedQuery<Concert> concertQuery = em.createQuery("select c from " + Concert.class.getName() +  " c", Concert.class)
					.setLockMode( LockModeType.PESSIMISTIC_READ )
					.setHint( "javax.persistence.lock.timeout", 5000 );
			List<Concert> concerts = concertQuery.getResultList();

			List<nz.ac.auckland.concert.common.dto.ConcertDTO> concertDTOs = new ArrayList<nz.ac.auckland.concert.common.dto.ConcertDTO>();

			//Converts concerts from domain model class to DTO class
			for(Concert concert :concerts){
				concertDTOs.add(ConcertMapper.toDto(concert));
			}

			em.getTransaction().commit();

			//Add the list of concert to the entity and return it in a response object
			GenericEntity<List<nz.ac.auckland.concert.common.dto.ConcertDTO>> entity = new GenericEntity<List<nz.ac.auckland.concert.common.dto.ConcertDTO>>(concertDTOs) {};
			response = Response.ok(entity);

		} catch (ProcessingException e) {

			throwServiceCommunitcationErrorException();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}

	
	/**
	 * Retrieves all Performers. The HTTP response message 
	 * has a status code of either 200 or 500, if there is an error processing the
	 * request
	 * 
	 * This method maps to the URI pattern <base-uri>/concerts/performers.
	 * 
	 * @return a Response object containing all the Performers.
	 */
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

		} catch (ProcessingException e) {

			throwServiceCommunitcationErrorException();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}

	
	/**
	 * Creates a user and an associated token and persist it in the database. 
	 * If the expected UserDTO attributes are not set, throws a service exception with the 
	 * message CREATE_USER_WITH_MISSING_FIELDS.
	 * If the supplied user name is already taken, also throws an exception
	 * 
	 * 
	 * 
	 * This method maps to the URI pattern <base-uri>/concerts/users.
	 * 
	 * @param dtoUser the DTO class object for the user to be created.
	 * 
	 * @return a Response object containing the server issued cookie
	 * which serve as an authentication token.
	 */
	@POST
	@Path("users")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response createUser(nz.ac.auckland.concert.common.dto.UserDTO dtoUser) {

		//Checks whether the required attributes are set
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

			//Find to user to see if it is already taken
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

			//Store the user with its token
			em.persist(user);

			em.getTransaction().commit();

			_logger.debug("Created User with username: " + user.getUsername());

			response = Response.created(URI.create("/concerts/users/" + user.getUsername()));

			response.cookie(cookie);

		} catch (ProcessingException e) {

			throwServiceCommunitcationErrorException();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}


	/**
	 * Authenticates the user by checking its credentials (username, password)
	 * and issues a cookie as an authentication token to the user.  
	 * This authentication token is required to make some requests.
	 * The user DTO class is also filled up with all attributes and returned
	 * to the user.
	 * 
	 * This method maps to the URI pattern <base-uri>/concerts/authenticate.
	 * 
	 * @param dtoUser the DTO class object containing only the user credentials.
	 * 
	 * @return a Response object containing the server issued cookie
	 * and complete up user DTO class object.
	 */
	@POST
	@Path("authenticate")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response authenticateUser(nz.ac.auckland.concert.common.dto.UserDTO dtoUser) {

		//Checks if the credential values are set
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

			//Attempts to find the user
			User searchUser = em.find(User.class, dtoUser.getUsername(), LockModeType.PESSIMISTIC_WRITE);

			em.getTransaction().commit();

			//If cannot find the user, throw exception
			if(searchUser == null){
				throw new NotFoundException(Response
						.status (Status.NOT_FOUND)
						.entity (Messages.AUTHENTICATE_NON_EXISTENT_USER)
						.build());
			}

			//If the password of the credential is incorrect, throw exception
			if(!searchUser.getPassword().equals(dtoUser.getPassword())){
				throw new BadRequestException(Response
						.status (Status.BAD_REQUEST)
						.entity (Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD)
						.build());
			}

			response = Response.ok();

			//Make the cookie token
			response.cookie(makeCookie(searchUser.getToken().getTokenValue()));
			_logger.debug("Authentication token value:" + searchUser.getToken().getTokenValue());

			response.entity(UserMapper.toDto(searchUser));

		} catch (ProcessingException e) {

			throwServiceCommunitcationErrorException();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}


	/**
	 * Register a credit card with a specific user.
	 * 
	 * This method maps to the URI pattern <base-uri>/concerts/users/creditcard.
	 * 
	 * @param creditCardDTO the DTO class object that represents a credit card.
	 * @param token the cookie token that is to be used for user authentication.
	 * 
	 * @return a Response object containing the noContent status code (user updated)
	 */
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

			//Find teh user from the token
			User user = storedToken.getUser();

			_logger.debug("Found user with username " + user.getUsername());

			for(CreditCard card: user.getCreditcard()){
				_logger.debug("Found user with user credit card: " + card.getNumber());
			}

			//Add the credit card to the user credit cards collection
			user.addCreditcard(CreditCardMapper.toDomainModel(creditCardDTO));

			//Update user
			em.merge(user);

			em.getTransaction().commit();

			response = Response.noContent();

		} catch (ProcessingException e) {

			throwServiceCommunitcationErrorException();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}


	/**
	 * Attempts to reserve seats for a concert. The reservation is valid for a
	 * short period that is determine by the remote service.
	 *  
	 * @param reservationRequest a description of the reservation, including 
	 * number of seats, price band, concert identifier, and concert date. All 
	 * fields are expected to be filled.
	 * @param token the cookie token that is to be used for user authentication.
	 * 
	 * @return a ReservationDTO object that describes the reservation. This 
	 * includes the original ReservationDTO parameter plus the seats (a Set of
	 * SeatDTO objects) that have been reserved.
	 * 
	 * 
	 */
	@POST
	@Path("reservation")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response makeReservation(nz.ac.auckland.concert.common.dto.ReservationRequestDTO dtoReservationRequest, @CookieParam("clientUsername") Cookie token) {

		//Authenticates the token/user request
		Token storedToken = authenticateToken(token);

		EntityManager em = null;
		ResponseBuilder response = null;
		try {

			em = PersistenceManager.instance().createEntityManager();

			//Check whether the expected fields are filled
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

			//Get all the bookings for the concert specified by the reservation request (with a specific date and priceband)
			TypedQuery<Booking> bookingQuery = em.createQuery("select c from " + Booking.class.getName() +  
					" c where CONCERT_CID = (:concertID) and DATE = (:date) and PRICEBAND = (:priceband)", Booking.class);
			bookingQuery.setParameter("concertID", dtoReservationRequest.getConcertId());
			bookingQuery.setParameter("date", dtoReservationRequest.getDate());
			bookingQuery.setParameter("priceband", dtoReservationRequest.getSeatType().toString());
			bookingQuery.setLockMode( LockModeType.PESSIMISTIC_READ ).setHint( "javax.persistence.lock.timeout", 5000 );
			List<Booking> bookings = bookingQuery.getResultList();

			//Get the user
			User user = storedToken.getUser();

			em.getTransaction().commit();

			//Checks whether there is a concert scheduled on the requested date
			if(!concert.getDates().contains(dtoReservationRequest.getDate())){
				throw new BadRequestException(Response
						.status (Status.BAD_REQUEST)
						.entity (Messages.CONCERT_NOT_SCHEDULED_ON_RESERVATION_DATE)
						.build());
			}

			//Get all the booked seats
			for(Booking booking : bookings){
				for(Seat bookedSeat : booking.getSeats()){
					bookedSeats.add(bookedSeat);
				}
			}

			Set<SeatRow> seatRows = TheatreLayout.getRowsForPriceBand(dtoReservationRequest.getSeatType());

			//Create all the seats for the requested concert
			for(SeatRow row : seatRows){
				int number_of_rows = TheatreLayout.getNumberOfSeatsForRow(row);

				for(int i = 1; i < number_of_rows + 1; i++){
					
					//Create the seat object
					Seat seat = new Seat(row, new SeatNumber(i));

					//Add to the available seats set if the seat doesn't exist in the already booked seats set
					if(!bookedSeats.contains(seat)){
						avilableSeats.add(seat);
					}
				}
			}

			//Checks whether the number of remaining seats satisfied the request
			if(avilableSeats.size() < dtoReservationRequest.getNumberOfSeats()){
				throw new BadRequestException(Response
						.status (Status.BAD_REQUEST)
						.entity (Messages.INSUFFICIENT_SEATS_AVAILABLE_FOR_RESERVATION)
						.build());
			}

			//Add the seats to be reserved
			int seatsToReserve = dtoReservationRequest.getNumberOfSeats();
			for(Seat seat : avilableSeats){
				reservedSeats.add(seat);
				seatsToReserve--;
				if(seatsToReserve == 0){
					break;
				}
			}

			//Make the booking object
			Booking newBooking = new Booking(concert, dtoReservationRequest.getDate(),reservedSeats ,dtoReservationRequest.getSeatType());

			em.getTransaction().begin();

			//Persist the booking and reservation in the database
			
			em.persist(newBooking);

			Reservation reservation = new Reservation(dtoReservationRequest.getSeatType(), concert, dtoReservationRequest.getDate() , reservedSeats, newBooking.getId());

			em.persist(reservation);

			user.addReservation(reservation);

			em.merge(user);

			em.getTransaction().commit();

			ReservationDTO dtoReservation = ReservationMapper.toDto(reservation, dtoReservationRequest);

			response = Response.ok().entity(dtoReservation);

			//Start a timer thread that checks if the reservation is confirmed by the user by the time it should expire (after 5 seconds). 
			//If not then the reservation is expired and deleted along with the corresponding booking.
			deleteReservationUponExpiry(reservation.getId(), newBooking.getId(), user.getUsername());

		} catch (ProcessingException e) {

			throwServiceCommunitcationErrorException();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}


	/**
	 * Confirms a reservation. Prior to calling this method, a successful 
	 * reservation request should have been made.
	 *  
	 * @param reservation a description (DTO) of the reservation to confirm.
	 * @param token the cookie token that is to be used for user authentication.
	 * 
	 * @return a Response object containing the noContent status code (reservation updated/confirmed)
	 * 
	 */
	@POST
	@Path("reservation/confirm")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response confirmReservation(nz.ac.auckland.concert.common.dto.ReservationDTO reservation, @CookieParam("clientUsername") Cookie token) {

		_logger.debug("Start to confirm reservation: " + reservation.getId());

		//Authenticates the token/user request
		Token storedToken = authenticateToken(token);

		EntityManager em = null;
		ResponseBuilder response = null;
		try {

			em = PersistenceManager.instance().createEntityManager();

			em.getTransaction().begin();

			Reservation storedReservation = em.find(Reservation.class, reservation.getId());

			Set<CreditCard> card = storedToken.getUser().getCreditcard();

			//Checks if the reservation has already been deleted
			if(storedReservation == null){
				_logger.debug(Messages.EXPIRED_RESERVATION);
				throw new NotFoundException(Response
						.status (Status.NOT_FOUND)
						.entity (Messages.EXPIRED_RESERVATION)
						.build());
			}

			//Checks if the user is registered with a credit card
			if(card.size() <= 0){
				_logger.debug(Messages.CREDIT_CARD_NOT_REGISTERED);

				Long bookingId = storedReservation.getBookingId();

				deleteReservation(storedReservation.getId(), bookingId,storedToken.getUser().getUsername());

				throw new BadRequestException(Response
						.status (Status.BAD_REQUEST)
						.entity (Messages.CREDIT_CARD_NOT_REGISTERED)
						.build());
			}

			//Update the confirm field of the reservation
			storedReservation.setStatus(true);

			em.merge(storedReservation);

			em.getTransaction().commit();

			_logger.debug("Reservation " + reservation.getId() + " confirmed!");

			response = Response.noContent();

		} catch (ProcessingException e) {

			throwServiceCommunitcationErrorException();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}


	/**
	 * Get all bookings that are made by the current user
	 * 
	 * This method maps to the URI pattern <base-uri>/concerts/bookings.
	 * 
	 * @param token the cookie token that is to be used for user authentication.
	 * 
	 * @return a Response object containing a entity that is a collection of 
	 * DTO booking objects
	 */
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

			//Find all the reservations made by the user
			Set<Reservation> reservations = user.getReservations();

			Set<BookingDTO> dtoBookings = new HashSet<BookingDTO>();

			//Uses the booking id stored inside the reservation object to find all the bookings
			for(Reservation reservation : reservations){

				Booking booking = em.find(Booking.class, reservation.getBookingId(), LockModeType.PESSIMISTIC_READ);

				//Map it to DTO class
				dtoBookings.add(BookingMapper.toDto(booking));

			}

			em.getTransaction().commit();

			GenericEntity<Set<BookingDTO>> entity = new GenericEntity<Set<BookingDTO>>(dtoBookings) {};

			response = Response.ok().entity(entity);

		} catch (ProcessingException e) {

			throwServiceCommunitcationErrorException();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return response.build();
	}

	/**
	 * Helper method that can be called from every service method to generate a 
	 * NewCookie instance, if necessary, based on the clientUsername parameter.
	 * 
	 * @param cookieValue a string extracted from a cookie in a HTTP request message.
	 * This can be null if there was no cookie named Config.CLIENT_COOKIE 
	 * present in the HTTP request message. 
	 * 
	 * @return a NewCookie object, with a generated UUID value, if the cookieValue 
	 * parameter is null. If the cookieValue parameter is non-null (i.e. The user 
	 * is registered and has he/she's cookie value is persisted in the database), 
	 * the returned cookie will contain the persisted cookieValue instead.
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


	/**
	 * Helper method that authenticates the token that the user is sending to the server. 
	 * It checks the value of the cookie contained in the HTTP request message. 
	 * If a token record exists with its key matching the cookie value, the user is 
	 * authenticated. If not the user is not authenticated or the token is not 
	 * recognised by the remote service (i.e. fake token).
	 * 
	 * @param Cookie a cookie extracted from a HTTP request message. It can be null but this
	 * will throw exceptions as the user is not authenticated (not carrying server issued token).
	 * 
	 * @return a Token object, which contains the user domain object and the cookie value.
	 */
	private Token authenticateToken(Cookie cookieToken){

		EntityManager em = null;
		Token storedToken = null;
		try {

			em = PersistenceManager.instance().createEntityManager();

			if(cookieToken == null){
				_logger.debug("Token is null");
				//Through unauthenticated request exception if there is no server issued authentication cookie
				throw new NotAuthorizedException(Response
						.status (Status.UNAUTHORIZED)
						.entity (Messages.UNAUTHENTICATED_REQUEST)
						.build());
			}

			em.getTransaction().begin();

			//Find the token
			storedToken = em.find(Token.class, cookieToken.getValue(), LockModeType.PESSIMISTIC_READ);

			if(!cookieToken.getName().equals(Config.CLIENT_COOKIE) || storedToken == null){
				//If the cookie cannot be recognised or the token is null, throw exception
				throw new NotAuthorizedException(Response
						.status (Status.UNAUTHORIZED)
						.entity (Messages.BAD_AUTHENTICATON_TOKEN)
						.build());
			}

			em.getTransaction().commit();

		} catch (ProcessingException e) {

			throwServiceCommunitcationErrorException();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

		return storedToken;
	}


	/**
	 * Helper method that calls the deleteReservation method after 5 seconds to 
	 * check whether the user has confirmed the reservation when it should expire.
	 * The deleteReservation method will decide if the reservation should be deleted.
	 * 
	 * @param reservationID The id of the newly made reservation. 
	 * @param bookingID The id of the newly made booking record. 
	 * @param username The name of the current user.
	 * 
	 */
	private void deleteReservationUponExpiry(Long reservationID, Long bookingID, String username){

		//Set up the timer
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				_logger.debug("Checking whether reservation is confirmed!");

				//Check the reservation record and delete if necessary
				deleteReservation(reservationID, bookingID, username);

			}
		}, ConcertApplication.RESERVATION_EXPIRY_TIME_IN_SECONDS * 1000); //Make the delay 5 seconds before running the task

	}


	/**
	 * Helper method that checks whether the user has confirmed a specific
	 * reservation and delete it if not.
	 * 
	 * @param reservationID The id of the newly made reservation. 
	 * @param bookingID The id of the newly made booking record. 
	 * @param username The name of the current user.
	 * 
	 */
	private void deleteReservation(Long reservationID, Long bookingID, String username){

		EntityManager em = null;
		try {
			em = PersistenceManager.instance().createEntityManager();

			em.getTransaction().begin();

			//Find the booking using the bookingID
			Booking booking = em.find(Booking.class, bookingID, LockModeType.PESSIMISTIC_WRITE);

			//Find the stored reservation using the reservationID
			Reservation storedReservation = em.find(Reservation.class, reservationID, LockModeType.PESSIMISTIC_WRITE);

			//Checks whether the user has confirmed the reservation
			if(storedReservation != null){
				if(!storedReservation.getStatus()){

					//Delete the booking and reservation if not
					em.remove(storedReservation);

					em.remove(booking);

					User user = em.find(User.class, username, LockModeType.PESSIMISTIC_WRITE);

					user.removeReservation(storedReservation);

					em.persist(user);

					_logger.debug("Deleted reservation with Id = " + reservationID);

				}
			}

			em.getTransaction().commit();

		} catch (ProcessingException e) {

			throwServiceCommunitcationErrorException();

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}

	}

	
	/**
	 * Helper method that throws exception with the service communication error
	 * message
	 * 
	 */
	private void throwServiceCommunitcationErrorException(){
		throw new InternalServerErrorException(Response
				.status (Status.INTERNAL_SERVER_ERROR)
				.entity (Messages.SERVICE_COMMUNICATION_ERROR)
				.build());
	}

}
