package nz.ac.auckland.concert.common.message;

/**
 * Class to defines service status/error messages.
 *
 */
public class Messages {
	public static final String CREATE_USER_WITH_NON_UNIQUE_NAME = "Unable to create new user - username already taken";
	public static final String CREATE_USER_WITH_MISSING_FIELDS = "Unable to create new user - incomplete user information";
	
	public static final String AUTHENTICATE_USER_WITH_MISSING_FIELDS = "Unable to authenticate user - missing username and/or password";
	public static final String AUTHENTICATE_USER_WITH_ILLEGAL_PASSWORD = "Unable to authenticate user - incorrect password";
	public static final String AUTHENTICATE_NON_EXISTENT_USER = "Unable to authenticate user - unrecognised username";
	
	public static final String SERVICE_COMMUNICATION_ERROR = "Unable to contact remote Service";
	
	public static final String UNAUTHENTICATED_REQUEST = "Unable to process request - missing authentication token";
	public static final String BAD_AUTHENTICATON_TOKEN = "Unable to process request - unrecognised authentication token";
	
	public static final String RESERVATION_REQUEST_WITH_MISSING_FIELDS = "Unable to process reservation - missing fields in the request";
	public static final String CONCERT_NOT_SCHEDULED_ON_RESERVATION_DATE = "Unable to process reservation - concert isn't scheduled on spcecified date";
	public static final String INSUFFICIENT_SEATS_AVAILABLE_FOR_RESERVATION = "Unable to make reservation - seats of the required type are not available";
	
	public static final String CREDIT_CARD_NOT_REGISTERED = "Unable to confirm reservation - credit card not registered";
	public static final String EXPIRED_RESERVATION = "Unable to confirm reservation - reservation has expired";

	public static final String NO_IMAGE_FOR_PERFORMER = "Unable to download image - no image associated with requested performer";
}
