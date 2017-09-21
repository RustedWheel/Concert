package nz.ac.auckland.concert.service.services;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private EntityManager _em = PersistenceManager.instance().createEntityManager();
	
	// AWS S3 access credentials for concert images.
	private static final String AWS_ACCESS_KEY_ID = "AKIAIDYKYWWUZ65WGNJA";
	private static final String AWS_SECRET_ACCESS_KEY = "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";

	// Name of the S3 bucket that stores images.
	private static final String AWS_BUCKET = "concert.aucklanduni.ac.nz";

	private static final String FILE_SEPARATOR = System
			.getProperty("file.separator");
	private static final String USER_DIRECTORY = System
			.getProperty("user.home");
	private static final String DOWNLOAD_DIRECTORY = USER_DIRECTORY
			+ FILE_SEPARATOR + "images";

	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response retrieveConcerts() {

		_em.getTransaction().begin();
		TypedQuery<Concert> concertQuery = _em.createQuery("select c from " + Concert.class.getName() +  " c", Concert.class);
		List<Concert> concerts = concertQuery.getResultList();
		_em.getTransaction().commit();

		List<nz.ac.auckland.concert.common.dto.ConcertDTO> concertDTOs = new ArrayList<nz.ac.auckland.concert.common.dto.ConcertDTO>();

		for(Concert concert :concerts){
			concertDTOs.add(ConcertMapper.toDto(concert));
		}

		GenericEntity<List<nz.ac.auckland.concert.common.dto.ConcertDTO>> entity = new GenericEntity<List<nz.ac.auckland.concert.common.dto.ConcertDTO>>(concertDTOs) {};
		ResponseBuilder response = Response.ok(entity);

		return response.build();
	}

	@GET
	@Path("performers")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response retrievePerformers() {

		_em.getTransaction().begin();
		TypedQuery<Performer> performerQuery = _em.createQuery("select p from " + Performer.class.getName() +  " p", Performer.class);
		List<Performer> performers = performerQuery.getResultList();
		_em.getTransaction().commit();

		List<nz.ac.auckland.concert.common.dto.PerformerDTO> performerDTOs = new ArrayList<nz.ac.auckland.concert.common.dto.PerformerDTO>();

		for(Performer performer :performers){
			performerDTOs.add(PerformerMapper.toDto(performer));
		}

		GenericEntity<List<nz.ac.auckland.concert.common.dto.PerformerDTO>> entity = new GenericEntity<List<nz.ac.auckland.concert.common.dto.PerformerDTO>>(performerDTOs){};
		ResponseBuilder response = Response.ok(entity);

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

		_em.getTransaction().begin();

		User searchUser = _em.find(User.class, dtoUser.getUsername());

		_em.getTransaction().commit();

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

		_em.getTransaction().begin();

		_em.persist(user);

		_em.getTransaction().commit();

		_logger.debug("Created User with username: " + user.getUsername());

		ResponseBuilder response = Response.created(URI.create("/concerts/users/" + user.getUsername()));

		response.cookie(cookie);

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

		_em.getTransaction().begin();

		User searchUser = _em.find(User.class, dtoUser.getUsername());

		_em.getTransaction().commit();

		if(searchUser == null){
			throw new BadRequestException(Response
					.status (Status.BAD_REQUEST)
					.entity (Messages.AUTHENTICATE_NON_EXISTENT_USER)
					.build());
		}

		if(!searchUser.getPassword().equals(dtoUser.getPassword())){
			throw new BadRequestException(Response
					.status (Status.BAD_REQUEST)
					.entity (Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD)
					.build());
		}

		ResponseBuilder response = Response.ok();

		response.cookie(makeCookie(searchUser.getToken().getTokenValue()));
		System.out.print("Authentication token value:" + searchUser.getToken().getTokenValue());

		return response.entity(UserMapper.toDto(searchUser)).build();
	}


	@POST
	@Path("users/creditcard")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response registerCreditCard(nz.ac.auckland.concert.common.dto.CreditCardDTO creditCardDTO, @CookieParam("clientUsername") Cookie token) {

		Token storedToken = authenticateToken(token);
		
		_em.getTransaction().begin();
		
		User user = storedToken.getUser();
		
		_logger.debug("Found user with username " + user.getUsername());
		
		for(CreditCard card: user.getCreditcard()){
			_logger.debug("Found user with user credit card: " + card.getNumber());
		}
		
		user.addCreditcard(CreditCardMapper.toDomainModel(creditCardDTO));
		
		_em.persist(user);
		
		_em.getTransaction().commit();

		ResponseBuilder response = Response.noContent();

		return response.build();
	}
	
	
	@POST
	@Path("reservation")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response makeReservation(nz.ac.auckland.concert.common.dto.ReservationRequestDTO dtoReservationRequest, @CookieParam("clientUsername") Cookie token) {

		Token storedToken = authenticateToken(token);
		
		if(dtoReservationRequest.getConcertId() == null || dtoReservationRequest.getDate() == null || dtoReservationRequest.getNumberOfSeats() <= 0 || dtoReservationRequest.getSeatType() == null){
			throw new BadRequestException(Response
					.status (Status.BAD_REQUEST)
					.entity (Messages.RESERVATION_REQUEST_WITH_MISSING_FIELDS)
					.build());
		}
		
		Set<Seat> bookedSeats = new HashSet<Seat>();
		Set<Seat> avilableSeats = new HashSet<Seat>();
		Set<Seat> reservedSeats = new HashSet<Seat>();
		
		_em.getTransaction().begin();
		
		Concert concert = _em.find(Concert.class, dtoReservationRequest.getConcertId());
		
		TypedQuery<Booking> bookingQuery = _em.createQuery("select c from " + Booking.class.getName() +  " c where CONCERT_CID = (:concertID)", Booking.class);
		bookingQuery.setParameter("concertID", dtoReservationRequest.getConcertId());
		List<Booking> bookings = bookingQuery.getResultList();
		
		User user = storedToken.getUser();
		
		_em.getTransaction().commit();
		
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
				_logger.debug("Created seat, row: " + row.name() + " number: " + i);
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
		
		/*Reservation reservation = new Reservation(Long id, dtoReservationRequest.getSeatType(), concert, dtoReservationRequest.getDate() , reservedSeats);*/
		
		
		
		
		/*_em.getTransaction().begin();
		
		User user = storedToken.getUser();
		
		_logger.debug("Found user with username " + user.getUsername());
		
		for(CreditCard card: user.getCreditcard()){
			_logger.debug("Found user with user credit card: " + card.getNumber());
		}
		
		user.addCreditcard(CreditCardMapper.toDomainModel(creditCardDTO));
		
		_em.persist(user);
		
		_em.getTransaction().commit();*/

		ResponseBuilder response = Response.ok();

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

		if(cookieToken == null){
			_logger.debug("Token is null");
			
			throw new NotAuthorizedException(Response
					.status (Status.UNAUTHORIZED)
					.entity (Messages.UNAUTHENTICATED_REQUEST)
					.build());
		}

		_em.getTransaction().begin();

		Token storedToken = _em.find(Token.class, cookieToken.getValue());

		_em.getTransaction().commit();

		if(!cookieToken.getName().equals(Config.CLIENT_COOKIE) || storedToken == null){

			throw new NotAuthorizedException(Response
					.status (Status.UNAUTHORIZED)
					.entity (Messages.BAD_AUTHENTICATON_TOKEN)
					.build());
		}
		
		return storedToken;
	}

}
