package nz.ac.auckland.concert.service.services;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.common.message.Messages;
import nz.ac.auckland.concert.service.domain.NewsItem;


@Path("/news")
public class NewsItemResource {

	private static Logger _logger = LoggerFactory
			.getLogger(NewsItemResource.class);

	protected static Map<String, AsyncResponse> _responses = new HashMap<String, AsyncResponse>( );
	protected static Long _latestNewsItemId = new Long(0);
	

	@GET
	@Path("subscribe")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public void subscribe(@Suspended AsyncResponse response, @CookieParam("clientUsername") Cookie token) {

		String[] cookieValues = token.getValue().split(" ");
		_logger.debug("Client subscribe request received, Id : " + cookieValues[1]);
		_responses.put(cookieValues[1], response ); //Get the user cookie token value
		
		Long clientLatestNewsItemId = Long.valueOf(cookieValues[0]).longValue(); //Get the id of the latest news item that the user has received
		
		if(clientLatestNewsItemId == 0){
			//Dont't do anything since it much be the first time that the client has subscribed
		} else if (clientLatestNewsItemId < _latestNewsItemId){ //If the user is behind in news item (could be black out situation or re-subscribing),
																//send up to 5 missed news items.
			_logger.debug("The user is behind in news item content!");
			
			if(_latestNewsItemId - clientLatestNewsItemId >= 5){
				clientLatestNewsItemId = _latestNewsItemId - 5;
			}
			
			EntityManager em = null;
			NewsItemDTO dtoLatestNews = new NewsItemDTO();
			try {
				em = PersistenceManager.instance().createEntityManager();
				em.getTransaction().begin();
				
				//Find the latest news item
				NewsItem latestNews = em.find(NewsItem.class, clientLatestNewsItemId + 1); 
				dtoLatestNews = NewsItemMapper.totoDto(latestNews);

				em.getTransaction().commit();

			} catch (ProcessingException e) {

				throw new InternalServerErrorException(Response
						.status (Status.INTERNAL_SERVER_ERROR)
						.entity (Messages.SERVICE_COMMUNICATION_ERROR)
						.build());

			} finally {
				if (em != null && em.isOpen()) {
					em.close ();
				}
			}
			
			//Send it to user and make the user to re-subscribe
			response.resume( dtoLatestNews );
			_responses.remove(cookieValues[1]);
		}
	}


	@POST
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public void send( NewsItemDTO dtoNewsItem ) {
		
		//Check whether any news item has been posted before. Assuming the first id is 1.
		if(_latestNewsItemId == 0){
			_latestNewsItemId = dtoNewsItem.getId();   //Assign the latest news item id to the posted one if none has been posted before
		} else if(_latestNewsItemId < dtoNewsItem.getId()){
			_latestNewsItemId = dtoNewsItem.getId();	//Assign the latest news item id to the posted one if the posted one has a higher id value
		}

		EntityManager em = null;
		try {
			em = PersistenceManager.instance().createEntityManager();
			em.getTransaction().begin();
			
			em.persist(NewsItemMapper.toDomainModel(dtoNewsItem)); //Store the news item on the database

			em.getTransaction().commit();

		} catch (ProcessingException e) {

			throw new InternalServerErrorException(Response
					.status (Status.INTERNAL_SERVER_ERROR)
					.entity (Messages.SERVICE_COMMUNICATION_ERROR)
					.build());

		} finally {
			if (em != null && em.isOpen()) {
				em.close ();
			}
		}
		
		// Notify subscribers.
		for(AsyncResponse response : _responses.values()) {
			
			response.resume( dtoNewsItem );
		}

		_responses.clear();
	}


	@DELETE
	@Path("unsubscribe")
	public void unsubscribe(@CookieParam("clientUsername") Cookie token) {
		
		_logger.debug("User unsuscribed!");
		String[] cookieValues = token.getValue().split(" ");
		_responses.remove(cookieValues[1]);
	}

}
