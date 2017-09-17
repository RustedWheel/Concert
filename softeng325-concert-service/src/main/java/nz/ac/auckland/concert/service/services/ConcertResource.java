package nz.ac.auckland.concert.service.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Performer;
import nz.ac.auckland.concert.service.domain.User;



@Path("/concerts")
@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
public class ConcertResource {
	
	private static Logger _logger = LoggerFactory
			.getLogger(ConcertResource.class);
	private EntityManager _em = PersistenceManager.instance().createEntityManager();
	
	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public Response retrieveConcerts(/*@CookieParam("clientId") Cookie clientId*/) {

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

/*		if(clientId == null || !clientId.getName().equals(Config.CLIENT_COOKIE)){
			response.cookie(makeCookie(clientId));
		}*/

		return response.build();
	}
	
	@GET
	@Path("allPerformers")
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

/*		if(clientId == null || !clientId.getName().equals(Config.CLIENT_COOKIE)){
			response.cookie(makeCookie(clientId));
		}*/

		return response.build();
	}
	
	@POST
	/*@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)*/
	public Response createUser(nz.ac.auckland.concert.common.dto.UserDTO dtoUser /*@CookieParam("clientId") Cookie clientId*/) {

		User user = UserMapper.toDomainModel(dtoUser);
		
		_em.getTransaction().begin();
		
		_em.persist(user);
		
		_em.getTransaction().commit();
		
		_logger.debug("Created User with username: " + user.getUsername());
		
		
		ResponseBuilder response = Response.created(URI.create("/concerts/user/" + user.getUsername()));

/*		if(clientId == null || !clientId.getName().equals(Config.CLIENT_COOKIE)){
			response.cookie(makeCookie(clientId));
		}*/

		return response.build();
	}

}
