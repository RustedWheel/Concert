package nz.ac.auckland.concert.client.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Image;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.BookingDTO;
import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import nz.ac.auckland.concert.common.dto.PerformerDTO;
import nz.ac.auckland.concert.common.dto.ReservationDTO;
import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.UserDTO;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.common.Config;
import nz.ac.auckland.concert.service.services.ConcertApplication;

public class DefaultService implements ConcertService {

	private static Logger _logger = LoggerFactory
			.getLogger(ConcertService.class);

	private static String WEB_SERVICE_URI = "http://localhost:10000/services/concerts";
	
	private static Set<String> _cookieValues = new HashSet<String>();

	@Override
	public Set<ConcertDTO> getConcerts() throws ServiceException {
		
		Client client = ClientBuilder.newClient();

		Set<ConcertDTO> concertDTOs = new HashSet<ConcertDTO>();

		Builder builder = client.target(WEB_SERVICE_URI).request();
		addCookieToInvocation(builder);
		Response response = builder.get();

		// Check that the expected Concert is returned.
		concertDTOs = new HashSet<ConcertDTO>(response.readEntity(new GenericType<List<nz.ac.auckland.concert.common.dto.ConcertDTO>>(){}));

		response.close();
		client.close();

		return concertDTOs;
	}

	@Override
	public Set<PerformerDTO> getPerformers() throws ServiceException {
		
		Client client = ClientBuilder.newClient();
		
		Set<PerformerDTO> PerformerDTOs = new HashSet<PerformerDTO>();

		Builder builder = client.target(WEB_SERVICE_URI + "/performers").request();
		addCookieToInvocation(builder);
		Response response = builder.get();

		// Check that the expected Concert is returned.
		PerformerDTOs = new HashSet<PerformerDTO>(response.readEntity(new GenericType<List<nz.ac.auckland.concert.common.dto.PerformerDTO>>(){}));

		response.close();
		client.close();

		return PerformerDTOs;
	}

	@Override
	public UserDTO createUser(UserDTO newUser) throws ServiceException {
		
		Client client = ClientBuilder.newClient();
		
		Response response = client
				.target(WEB_SERVICE_URI + "/users").request()
				.post(Entity.xml(newUser));
		
		switch (response.getStatus()){
		case 400:{
				String errorMessage = response.readEntity (String.class);
				throw new ServiceException(errorMessage);
			 }
		}
		
		processCookieFromResponse(response);
		
		response.close();
		client.close();
		
		return newUser;
	}

	@Override
	public UserDTO authenticateUser(UserDTO user) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImageForPerformer(PerformerDTO performer) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReservationDTO reserveSeats(ReservationRequestDTO reservationRequest) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void confirmReservation(ReservationDTO reservation) throws ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerCreditCard(CreditCardDTO creditCard) throws ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<BookingDTO> getBookings() throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void subscribeForNewsItems(NewsItemListener listener) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void cancelSubscription() {
		throw new UnsupportedOperationException();
	}

	private void addCookieToInvocation(Builder builder) {
		if(!_cookieValues.isEmpty()) {
			builder.cookie(Config.CLIENT_COOKIE, _cookieValues.iterator().next());
		}
	}
	
	private void processCookieFromResponse(Response response) {
		Map<String, NewCookie> cookies = response.getCookies();
		
		if(cookies.containsKey(Config.CLIENT_COOKIE)) {
			String cookieValue = cookies.get(Config.CLIENT_COOKIE).getValue();
			_cookieValues.add(cookieValue);
		}
	}
}
