package nz.ac.auckland.concert.service.services;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import nz.ac.auckland.concert.service.domain.Concert;
import nz.ac.auckland.concert.service.domain.Performer;

/**
 * Helper class to convert between domain-model and DTO objects representing
 * concerts.
 *
 */
public class ConcertMapper {

/*	static Concert toDomainModel(nz.ac.auckland.concert.common.dto.ConcertDTO dtoConcert) {
		Concert concert = new Concert(dtoParolee.getId(),
				dtoParolee.getLastname(),
				dtoParolee.getFirstname(),
				dtoParolee.getGender(),
				dtoParolee.getDateOfBirth(),
				dtoParolee.getHomeAddress(),
				dtoParolee.getCurfew());
		return concert;
	}*/
	
	static nz.ac.auckland.concert.common.dto.ConcertDTO toDto(Concert concert) {
		
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
						concert.getTariff(),
						performerIDs);
		return dtoConcert;
		
	}
}

