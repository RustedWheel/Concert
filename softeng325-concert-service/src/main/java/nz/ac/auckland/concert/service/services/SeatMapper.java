package nz.ac.auckland.concert.service.services;

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
