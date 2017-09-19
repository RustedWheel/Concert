package nz.ac.auckland.concert.service.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
import nz.ac.auckland.concert.service.common.Config;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Performer;
import nz.ac.auckland.concert.service.domain.User;



@Path("/concerts")
public class ConcertResource {
	
	private static Logger _logger = LoggerFactory
			.getLogger(ConcertResource.class);
	private EntityManager _em = PersistenceManager.instance().createEntityManager();
	private Cookie _token;
	
	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response retrieveConcerts(@CookieParam("clientUsername") Cookie token) {
		
/*		if(token == null || _token == null){
			throw new NotAuthorizedException(Response
					.status (Status.UNAUTHORIZED)
					.entity (Messages.UNAUTHENTICATED_REQUEST)
					.build());
		}
		
		if(!token.getName().equals(Config.CLIENT_COOKIE) || !token.getValue().equals(_token.getValue())){
			throw new NotAuthorizedException(Response
					.status (Status.UNAUTHORIZED)
					.entity (Messages.BAD_AUTHENTICATON_TOKEN)
					.build());
		}*/
		
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
	public Response retrievePerformers(@CookieParam("clientUsername") Cookie token) {
		
		/*if(token == null || !token.getName().equals(Config.CLIENT_COOKIE)){
			throw new ServiceException(Messages.SERVICE_COMMUNICATION_ERROR);
		}*/

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
		
		_em.getTransaction().begin();
		
		_em.persist(user);
		
		_em.getTransaction().commit();
		
		_logger.debug("Created User with username: " + user.getUsername());
		
		
		ResponseBuilder response = Response.created(URI.create("/concerts/users/" + user.getUsername()));

		response.cookie(makeCookie(_token));
		
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

		response.cookie(makeCookie(_token));
		
		return response.entity(UserMapper.toDto(searchUser)).build();
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
	private NewCookie makeCookie(Cookie clientToken){
		
		NewCookie newCookie = new NewCookie(Config.CLIENT_COOKIE, UUID.randomUUID().toString());
			_token = newCookie;
			_logger.info("Generated cookie: " + newCookie.getValue());

		return newCookie;
	}

}