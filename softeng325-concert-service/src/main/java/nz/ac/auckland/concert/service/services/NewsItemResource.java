package nz.ac.auckland.concert.service.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.service.domain.NewsItem;


@Path("/news")
public class NewsItemResource {

	private static Logger _logger = LoggerFactory
			.getLogger(NewsItemResource.class);

	protected static Map<String, AsyncResponse> _responses = new HashMap<String, AsyncResponse>( );


	@GET
	@Path("subscribe")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public void subscribe(@Suspended AsyncResponse response, @CookieParam("clientUsername") Cookie token) {

		_logger.debug("Client subscribe request receieved, Id : " + token.getValue());
		
		_responses.put(token.getValue(), response );
	}


	@POST
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public void send( NewsItemDTO dtoNewsItem ) {

		// Notify subscribers.
		for(AsyncResponse response : _responses.values()) {
			response.resume( dtoNewsItem );
		}

		_responses.clear();
	}


	@DELETE
	@Path("unsubscribe")
	public void unsubscribe(@CookieParam("clientUsername") Cookie token) {
		
		/*_responses.get(token.getValue()).resume(new InterruptedException());*/
		
		_logger.debug("User unsuscribed!");
		_responses.remove(token.getValue());
	}

}
