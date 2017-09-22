package nz.ac.auckland.concert.service.services;

import java.util.Set;

import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;

public class SeatMapper {

	static nz.ac.auckland.concert.common.dto.SeatDTO toDto(nz.ac.auckland.concert.service.domain.Seat seat) {	
	
		nz.ac.auckland.concert.common.dto.SeatDTO dtoSeat = 
				new nz.ac.auckland.concert.common.dto.SeatDTO(
						seat.getRow(),
						seat.getNumber()
						);
		return dtoSeat;
		
	}
	
}
