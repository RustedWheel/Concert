package nz.ac.auckland.concert.service.services;

import java.util.HashSet;
import java.util.Set;

import nz.ac.auckland.concert.common.dto.ReservationRequestDTO;
import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.service.domain.Seat;


public class ReservationMapper {
	
	static nz.ac.auckland.concert.common.dto.ReservationDTO toDto(nz.ac.auckland.concert.service.domain.Reservation reservation, ReservationRequestDTO request) {
		
		Set<SeatDTO> dtoSeats = new HashSet<SeatDTO>();
		
		for(Seat seat : reservation.getSeats()){
			dtoSeats.add(SeatMapper.toDto(seat));
		}
		
		nz.ac.auckland.concert.common.dto.ReservationDTO dtoReservation = 
				new nz.ac.auckland.concert.common.dto.ReservationDTO(
						reservation.getId(),
						request,
						dtoSeats
						);
		return dtoReservation;
		
	}

}
