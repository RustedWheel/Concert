package nz.ac.auckland.concert.client.service;

import java.awt.Image;
import java.util.Set;

import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;


/**
 * Service interface for the Concert service. 
 * 
 * Use of this interface simplifies development of concert clients, and hides
 * the choice of remote communication technology. This interface is to be 
 * implemented by a class that acts as a REST Web service client. 
 * 
 * The service is defined using several DTO classes, data types and messages.
 * These are defined in the following packages: 
 * - nz.ac.auckland.concert.common.dto
 * - nz.ac.auckland.concert.common.types
 * - nz.ac.auckland.concert.common.message
 *
 */
public interface ConcertService {

	/**
	 * Returns a Set of ConcertDTO objects, where each ConcertDTO instance
	 * describes a concert.
	 * 
	 * @throws ServiceException if there's an error communicating with the
	 * service. The exception's message is Messages.SERVICE_COMMUNICATION_ERROR.
	 * 
	 */
	Set<ConcertDTO> getConcerts() throws ServiceException;
	
	/**
	 * Returns a Set of PerformerDTO objects. Each member of the Set describes
	 * a Performer.
	 * 
	 * @throws ServiceException if there's an error communicating with the
	 * service. The exception's message is Messages.SERVICE_COMMUNICATION_ERROR.
	 * 
	 */
	Set<PerformerDTO> getPerformers() throws ServiceException;
	
	/**
	 * Attempts to create a new user. When successful, the new user is 
	 * automatically authenticated and logged into the remote service.
	 * 
	 * @param newUser a description of the new user. The following 
	 * properties are expected to be set: username, password, firstname
	 * and lastname. 
	 * 
	 * @return a new UserDTO object, whose identity property is also set.
	 * 
	 * @throws ServiceException in response to any of the following conditions.
	 * The exception's message is defined in 
	 * class nz.ac.auckland.concert.common.Messages.
	 * 
	 * Condition: the expected UserDTO attributes are not set.
	 * Messages.CREATE_USER_WITH_MISSING_FIELD
	 * 
	 * Condition: the supplied username is already taken.
	 * Messages.CREATE_USER_WITH_NON_UNIQUE_NAME
	 * 
	 * Condition: there is a communication error.
	 * Messages.SERVICE_COMMUNICATION_ERROR
	 * 
	 */
	UserDTO createUser(UserDTO newUser) throws ServiceException;
	
	/**
	 * Attempts to authenticate an existing user and log them into the remote
	 * service.
	 * 
	 * @param user stores the user's authentication credentials. Properties 
	 * username and password must be set.
	 * 
	 * @return a UserDTO whose properties are all set. 
	 * 
	 * @throws ServiceException in response to any of the following conditions.
	 * The exception's message is defined in 
	 * class nz.ac.auckland.concert.common.Messages.
	 * 
	 * Condition: the UserDTO parameter doesn't have values for username and/or
	 * password.
	 * Messages.AUTHENTICATE_USER_WITH_MISSING_FIELDS
	 * 
	 * Condition: the remote service doesn't have a record of a user with the
	 * specified username.
	 * Messages.AUTHENTICATE_NON_EXISTENT_USER
	 * 
	 * Condition: the given user can't be authenticated because their password
	 * doesn't match what's stored in the remote service.
	 * Messages.AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD
	 * 
	 * Condition: there is a communication error.
	 * Messages.SERVICE_COMMUNICATION_ERROR
	 * 
	 */
	UserDTO authenticateUser(UserDTO user) throws ServiceException;
	
	/**
	 * Returns an Image for a given performer.
	 * 
	 * @param performer the performer for whom an Image is required.
	 * 
	 * @return an Image instance.
	 * 
	 * @throws ServiceException in response to any of the following conditions.
	 * The exception's message is defined in 
	 * class nz.ac.auckland.concert.common.Messages.
	 * 
	 * Condition: there is no image for the specified performer.
	 * Messages.NO_IMAGE_FOR_PERFORMER
	 * 
	 * Condition: there is a communication error.
	 * Messages.SERVICE_COMMUNICATION_ERROR
	 * 
	 */
	Image getImageForPerformer(PerformerDTO performer) throws ServiceException;

	/**
	 * Attempts to reserve seats for a concert. The reservation is valid for a
	 * short period that is determine by the remote service.
	 * 
	 * @param reservationRequest a description of the reservation, including 
	 * number of seats, price band, concert identifier, and concert date. All 
	 * fields are expected to be filled.
	 * 
	 * @return a ReservationDTO object that describes the reservation. This 
	 * includes the original ReservationDTO parameter plus the seats (a Set of
	 * SeatDTO objects) that have been reserved.
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
	 * Condition: the ReservationRequestDTO parameter is incomplete.
	 * Messages.RESERVATION_REQUEST_WITH_MISSING_FIELDS
	 * 
	 * Condition: the ReservationRequestDTO parameter specifies a reservation
	 * date/time for when the concert is not scheduled.
	 * Messages.CONCERT_NOT_SCHEDULED_ON_RESERVATION_DATE
	 * 
	 * Condition: the reservation request is unsuccessful because the number of
	 * seats within the required price band are unavailable.
	 * Messages.INSUFFICIENT_SEATS_AVAILABLE_FOR_RESERVATION
	 * 
	 * Condition: there is a communication error.
	 * Messages.SERVICE_COMMUNICATION_ERROR
	 * 
	 */
	ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException;
	
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
	void confirmReservation(ReservationDTO reservation) throws ServiceException;
	
	/**
	 * Registers a credit card for the currently logged in user.
	 * 
	 * @param creditCard a description of the credit card.
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
	 * Condition: there is a communication error.
	 * Messages.SERVICE_COMMUNICATION_ERROR
	 * 
	 */
	void registerCreditCard(CreditCardDTO creditCard) throws ServiceException;
	
	/**
	 * Retrieves the bookings (confirmed reservations) for the currently 
	 * authenticated (logged in) user.
	 * 
	 * @return a Set of BookingDTOs describing the bookings. Each BookingDTO 
	 * includes concert-identifying information, booking date, seats booked and 
	 * their price band.
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
	 * Condition: there is a communication error.
	 * Messages.SERVICE_COMMUNICATION_ERROR
	 * 
	 */
	Set<BookingDTO> getBookings() throws ServiceException;
	
	/**
	 * Subscribes for news items. Once subscribed, the supplied 
	 * NewsItemListener will be notified of any news items. The listener is
	 * repeatedly notified in a background thread of new news items until the
	 * subscription is cancelled. 
	 * 
	 * This is an optional method.
	 * 
	 * @param listener the NewsItemListener that is notified of new items.
	 * 
	 * @throws ServiceException if there is a communication error. In this case
	 * the exception's message is Messages.COMMUNICATION_ERROR.
	 * 
	 * @throws UnsupportedOperationException if the method isn't implemented.
	 * 
	 */
	void subscribeForNewsItems(NewsItemListener listener) throws ServiceException;
	
	/**
	 * Cancels a subscription previously made by a subscribeForNewsItems() 
	 * call.
	 * 
	 * This is an optional method.
	 * 
	 * @throws ServiceException if there is a communication error. In this case
	 * the exception's message is Messages.COMMUNICATION_ERROR.
	 * 
	 * @throws UnsupportedOperationException if the method isn't implemented.
	 * 
	 */
	void cancelSubscription() throws ServiceException;
	
	/**
	 * Interface to be implemented by clients that are interested in receiving
	 * news items. A NewsItemListener is expected to process the incoming new 
	 * item.
	 *
	 */
	interface NewsItemListener {
		void newsItemReceived(NewsItemDTO newsItem);
	}
}
