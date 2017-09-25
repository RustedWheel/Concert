package nz.ac.auckland.concert.client.service;


import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.common.Config;

public class DefaultService implements ConcertService {

	private static Logger _logger = LoggerFactory
			.getLogger(ConcertService.class);

	private static String WEB_SERVICE_URI = "http://localhost:10000/services/concerts";

	// AWS S3 access credentials for concert images.
	private static final String AWS_ACCESS_KEY_ID = "";
	private static final String AWS_SECRET_ACCESS_KEY = "";

	// Name of the S3 bucket that stores images.
	private static final String AWS_BUCKET = "concert.aucklanduni.ac.nz";

	private static final String FILE_SEPARATOR = System
			.getProperty("file.separator");
	private static final String USER_DIRECTORY = System
			.getProperty("user.home");
	private static final String DOWNLOAD_DIRECTORY = USER_DIRECTORY
			+ FILE_SEPARATOR + "images";

	private String _cookieValues;

	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {

		Client client = ClientBuilder.newClient();

		Set<ConcertDTO> concertDTOs = new HashSet<ConcertDTO>();

		Builder builder = client.target(WEB_SERVICE_URI).request();

		Response response = null;
		try {
			response = builder.get();
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}
		
		String errorMessage;
		switch (response.getStatus()) {
			case 200:
				concertDTOs = new HashSet<ConcertDTO>(response.readEntity(new GenericType<List<nz.ac.auckland.concert.common.dto.ConcertDTO>>(){}));
				break;
			case 500:
				errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
		}

		response.close();
		client.close();

		return concertDTOs;
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {

		Client client = ClientBuilder.newClient();

		Set<PerformerDTO> PerformerDTOs = new HashSet<PerformerDTO>();

		Builder builder = client.target(WEB_SERVICE_URI + "/performers").request();

		Response response = null;
		try {
			response = builder.get();
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}
		
		String errorMessage;
		switch (response.getStatus()) {
			case 200:
				PerformerDTOs = new HashSet<PerformerDTO>(response.readEntity(new GenericType<List<nz.ac.auckland.concert.common.dto.PerformerDTO>>(){}));
				break;
			case 500:
				errorMessage = response.readEntity(String.class);
				throw new ServiceException(errorMessage);
		}

		response.close();
		client.close();

		return PerformerDTOs;
	}

	@Override
	public UserDTO createUser(UserDTO newUser) throws ServiceException {

		Client client = ClientBuilder.newClient();

		Builder builder = client.target(WEB_SERVICE_URI + "/users").request();
		
		Response response = null;
		try {
			response = builder.post(Entity.xml(newUser));
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}

		switch (response.getStatus()){
		case 400:
			String errorMessage = response.readEntity (String.class);
			throw new ServiceException(errorMessage);
		case 201: 
			break;
		case 500:
			errorMessage = response.readEntity(String.class);
			System.out.println(errorMessage);
			throw new ServiceException(errorMessage);
		}

		processCookieFromResponse(response);

		response.close();
		client.close();

		return newUser;
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {

		Client client = ClientBuilder.newClient();

		Builder builder = client.target(WEB_SERVICE_URI + "/authenticate").request();
		
		Response response = null;
		try {
			response = builder.post(Entity.xml(user));
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}

		String errorMessage;
		switch (response.getStatus()){
		case 400:
			errorMessage = response.readEntity (String.class);
			throw new ServiceException(errorMessage);
		case 404:
			errorMessage = response.readEntity (String.class);
			throw new ServiceException(errorMessage);
		case 200: 
			System.out.println("Authentication success");
			user = response.readEntity(UserDTO.class);
			break;
		case 500:
			errorMessage = response.readEntity(String.class);
			throw new ServiceException(errorMessage);
		}

		processCookieFromResponse(response);

		response.close();
		client.close();

		return user;
	}

	@Override
	public Image getImageForPerformer(PerformerDTO performer) throws ServiceException {

		String name = performer.getImageName();

		// Create download directory if it doesn't already exist.
		File downloadDirectory = new File(DOWNLOAD_DIRECTORY);
		downloadDirectory.mkdir();

		// Create an AmazonS3 object that represents a connection with the
		// remote S3 service.
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
				AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
		AmazonS3 s3 = AmazonS3ClientBuilder
				.standard()
				.withRegion(Regions.AP_SOUTHEAST_2)
				.withCredentials(
						new AWSStaticCredentialsProvider(awsCredentials))
				.build();

		TransferManager mgr = TransferManagerBuilder
				.standard()
				.withS3Client(s3)
				.build();

		File f = new File(DOWNLOAD_DIRECTORY + FILE_SEPARATOR + name);				
		try {
			Download xfer = mgr.download(AWS_BUCKET, name, f);
			xfer.waitForCompletion();
		} catch (AmazonServiceException e) {
			throw new ServiceException(Messages.NO_IMAGE_FOR_PERFORMER);
		} catch (AmazonClientException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

		mgr.shutdownNow();

		Image image = null;

		try {
			image = ImageIO.read(f);
		} catch (IOException e) {
			throw new ServiceException(Messages.NO_IMAGE_FOR_PERFORMER);
		}

		return image;
	}

	@Override
	public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {

		Client client = ClientBuilder.newClient();

		Builder builder = client.target(WEB_SERVICE_URI + "/reservation").request();
		addCookieToInvocation(builder);
		
		Response response = null;
		try {
			response = builder.post(Entity.xml(reservationRequest));
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}

		ReservationDTO dtoReservation = new ReservationDTO();

		switch (response.getStatus()){
		case 200:
			dtoReservation = response.readEntity(ReservationDTO.class);
			break;
		case 400:
			String errorMessage = response.readEntity (String.class);
			throw new ServiceException(errorMessage);
		case 401:
			throw new ServiceException(response.readEntity (String.class));
		case 500:
			errorMessage = response.readEntity(String.class);
			throw new ServiceException(errorMessage);	
		}

		response.close();
		client.close();

		return dtoReservation;
	}

	@Override
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {
		Client client = ClientBuilder.newClient();

		Builder builder = client.target(WEB_SERVICE_URI + "/reservation/confirm").request();
		addCookieToInvocation(builder);
		
		Response response = null;
		try {
			response = builder.post(Entity.xml(reservation));
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}
		
		String errorMessage;
		switch (response.getStatus()){
		case 204:
			break;
		case 400:
			errorMessage = response.readEntity (String.class);
			throw new ServiceException(errorMessage);
		case 401:
			errorMessage = response.readEntity(String.class);
			throw new ServiceException(errorMessage);
		case 404:
			errorMessage = response.readEntity(String.class);
			throw new ServiceException(errorMessage);
		case 500:
			errorMessage = response.readEntity(String.class);
			throw new ServiceException(errorMessage);	
		}

		response.close();
		client.close();

	}

	@Override
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {

		Client client = ClientBuilder.newClient();

		Builder builder = client.target(WEB_SERVICE_URI + "/users/creditcard").request();
		addCookieToInvocation(builder);
		
		Response response = null;
		try {
			response = builder.post(Entity.xml(creditCard));
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}

		String errorMessage;
		switch (response.getStatus()){
		case 204:
			break;
		case 401:
			errorMessage = response.readEntity (String.class);
			throw new ServiceException(errorMessage);
		case 500:
			errorMessage = response.readEntity(String.class);
			throw new ServiceException(errorMessage);
		}

		response.close();
		client.close();
	}

	
	@Override
	public Set<BookingDTO> getBookings() throws ServiceException {

		Client client = ClientBuilder.newClient();

		Builder builder = client.target(WEB_SERVICE_URI + "/bookings").request();
		addCookieToInvocation(builder);
		
		Response response = null;
		try {
			response = builder.get();
		} catch (ProcessingException e) {
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}

		Set<BookingDTO> dtoBookings = new HashSet<BookingDTO>();

		String errorMessage;
		switch (response.getStatus()){
		case 200:
			dtoBookings = response.readEntity(new GenericType<Set<nz.ac.auckland.concert.common.dto.BookingDTO>>(){});
			break;
		case 401:
			errorMessage = response.readEntity (String.class);
			throw new ServiceException(errorMessage);
		case 500:
			errorMessage = response.readEntity(String.class);
			throw new ServiceException(errorMessage);
		}

		response.close();
		client.close();

		return dtoBookings;
	}

	@Override
	public void subscribeForNewsItems(NewsItemListener listener) {

		Client client= ClientBuilder.newClient();

		final WebTarget target =client.target("newsItem");
		target.request( )
		.async( )
		.get( new InvocationCallback<Response>() {
			public void completed( Response response ) {
				NewsItemDTO newsItem = response.readEntity(NewsItemDTO.class);
				listener.newsItemReceived(newsItem);
				target.request().async().get(this);
			}

			public void failed( Throwable t ) {}
		});
		
		client.close();
	}

	
	@Override
	public void cancelSubscription() {

		Client client= ClientBuilder.newClient();

		final WebTarget target =client.target("newsItem/unsubscribe");
		target.request( )
		.async( );
		
		client.close();

	}

	private void addCookieToInvocation(Builder builder) {
		if(_cookieValues != null) {
			builder.cookie(Config.CLIENT_COOKIE, _cookieValues);
		}
	}

	private void processCookieFromResponse(Response response) {
		Map<String, NewCookie> cookies = response.getCookies();

		if(cookies.containsKey(Config.CLIENT_COOKIE)) {
			String cookieValue = cookies.get(Config.CLIENT_COOKIE).getValue();
			_cookieValues = cookieValue;
		}
	}

}
