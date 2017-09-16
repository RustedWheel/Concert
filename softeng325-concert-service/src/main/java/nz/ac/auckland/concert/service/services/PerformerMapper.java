package nz.ac.auckland.concert.service.services;

import java.util.HashSet;
import java.util.Set;

import nz.ac.auckland.concert.service.domain.Concert;

public class PerformerMapper {
	
	static nz.ac.auckland.concert.common.dto.PerformerDTO toDto(nz.ac.auckland.concert.service.domain.Performer performer) {
		
		Set<Concert> concerts = performer.getConcerts();
		Set<Long> ConcertIDs = new HashSet<Long>();
		
		if(concerts != null && concerts.size() > 0){
			
			for(Concert concert : concerts){
				ConcertIDs.add(concert.getId());
			}
			
		}
		
		nz.ac.auckland.concert.common.dto.PerformerDTO dtoPerformer = 
				new nz.ac.auckland.concert.common.dto.PerformerDTO(
						performer.getId(),
						performer.getName(),
						performer.getImageName(),
						performer.getGenre(),
						ConcertIDs);
		return dtoPerformer;
		
	}

}
