package nz.ac.auckland.concert.service.services;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nz.ac.auckland.concert.common.dto.NewsItemDTO;

@Path("/newsItem")
public class NewsItemResource {

	private static Logger _logger = LoggerFactory
			.getLogger(NewsItemResource.class);

	protected List<AsyncResponse> _responses = new
			ArrayList<AsyncResponse>( );

	@GET
	@Path("subscribe")
	public synchronized void subscribe(@Suspended AsyncResponse response ) {

		_responses.add( response );
	}

	@POST
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public synchronized void send( NewsItemDTO dtoNewsItem) {
		// Notify subscribers.
		for(AsyncResponse response : _responses) {
			response.resume( dtoNewsItem );
		}
		_responses.clear( );
	}

	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_XML)
	public void process( final @Suspended AsyncResponse response) {
		new Thread() {
			public void run( ) {

				String result = "ss";
				response.resume( result);
			}
		}.start( );
	}

	@GET
	@Path("unsubscribe")
	public synchronized void unsubscribe() {

		_responses.clear();
	}

}
