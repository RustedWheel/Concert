package nz.ac.auckland.concert.service.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nz.ac.auckland.concert.common.dto.ConcertDTO;
import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Performer;

/**
 * Helper class to convert between domain-model and DTO objects representing
 * concerts.
 *
 */
public class ConcertMapper {

/*	static Concert toDomainModel(nz.ac.auckland.concert.common.dto.ConcertDTO dtoConcert) {
		
		Map<PriceBand, BigDecimal> ticketPrices = new HashMap<PriceBand, BigDecimal>();
		ticketPrices.put(nz.ac.auckland.concert.common.types.PriceBand.PriceBandA,
				dtoConcert.getTicketPrice(nz.ac.auckland.concert.common.types.PriceBand.PriceBandA));
		ticketPrices.put(nz.ac.auckland.concert.common.types.PriceBand.PriceBandB,
				dtoConcert.getTicketPrice(nz.ac.auckland.concert.common.types.PriceBand.PriceBandB));
		ticketPrices.put(nz.ac.auckland.concert.common.types.PriceBand.PriceBandC,
				dtoConcert.getTicketPrice(nz.ac.auckland.concert.common.types.PriceBand.PriceBandC));
		
		Set<Performer> _performers = new HashSet<Performer>();
		for(){
			
		}
		
		Concert concert = new Concert(dtoConcert.getId(),
				dtoConcert.getTitle(),
				dtoConcert.getDates(), 
				ticketPrices,
				null,
				null);
		return concert;
	}*/
	
	static nz.ac.auckland.concert.common.dto.ConcertDTO toDto(nz.ac.auckland.concert.service.domain.Concert concert) {
		
		Set<Performer> performers = concert.getPerformers();
		Set<Long> performerIDs = new HashSet<Long>();
		for(Performer performer : performers){
			performerIDs.add(performer.getId());
		}
		
		nz.ac.auckland.concert.common.dto.ConcertDTO dtoConcert = 
				new nz.ac.auckland.concert.common.dto.ConcertDTO(
						concert.getId(),
						concert.getTitle(),
						concert.getDates(),
						concert.getTicketPrice(),
						performerIDs);
		return dtoConcert;
		
	}
}

