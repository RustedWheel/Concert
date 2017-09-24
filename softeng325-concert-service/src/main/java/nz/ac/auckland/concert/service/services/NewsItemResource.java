package nz.ac.auckland.concert.service.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;
import nz.ac.auckland.concert.service.domain.NewsItem;

@Path("/newsItem")
public class NewsItemResource {

	private static Logger _logger = LoggerFactory
			.getLogger(NewsItemResource.class);

	protected List<AsyncResponse> _responses = new
			ArrayList<AsyncResponse>( );

	@GET
	public synchronized void subscribe(@Suspended AsyncResponse response ) {

		_responses.add( response );
	}

	
	@POST
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public synchronized void send( NewsItemDTO dtoNewsItem ) {
		
		new Thread() {
			public void run() {
				
				EntityManager em = null;
				try {
					em = PersistenceManager.instance().createEntityManager();
					em.getTransaction().begin();

					NewsItem newsItem = NewsItemMapper.toDomainModel(dtoNewsItem);
					
					em.persist(newsItem);
					
					em.getTransaction().commit();

				} finally {
					if (em != null && em.isOpen()) {
						em.close ();
					}
				}
				
			}
		}.start( );
		
		Response res = Response.ok(dtoNewsItem).cookie().build();
		
		// Notify subscribers.
		for(AsyncResponse response : _responses) {
			response.resume( res );
		}
		_responses.clear( );
	}
	
	
	@GET
	@Path("unsubscribe")
	public synchronized void unsubscribe() {
		
		_responses.clear();
	}

}
