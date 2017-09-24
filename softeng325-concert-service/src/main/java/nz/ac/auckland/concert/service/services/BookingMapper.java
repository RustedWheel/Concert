package nz.ac.auckland.concert.service.services;

import java.util.HashSet;
import java.util.Set;

import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.service.domain.Seat;

public class BookingMapper {
	
	static nz.ac.auckland.concert.common.dto.BookingDTO toDto(nz.ac.auckland.concert.service.domain.Booking booking) {	
		
		Set<SeatDTO> dtoSeats = new HashSet<SeatDTO>();

		for(Seat seat : booking.getSeats()){
			dtoSeats.add(SeatMapper.toDto(seat));
		}
		
		nz.ac.auckland.concert.common.dto.BookingDTO dtoBooking = 
				new nz.ac.auckland.concert.common.dto.BookingDTO(
						booking.getConcert().getId(),
						booking.getConcert().getTitle(),
						booking.getDateTime(),
						dtoSeats,
						booking.getPriceBand()
						);
		return dtoBooking;
		
	}

}
