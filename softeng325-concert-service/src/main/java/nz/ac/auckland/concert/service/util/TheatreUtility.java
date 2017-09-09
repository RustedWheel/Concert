package nz.ac.auckland.concert.service.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import nz.ac.auckland.concert.common.dto.SeatDTO;
import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.common.types.SeatNumber;
import nz.ac.auckland.concert.common.types.SeatRow;
import nz.ac.auckland.concert.common.util.TheatreLayout;

/**
 * Utility class with a search method that identifies seats that are available
 * to reserve.
 *
 */
public class TheatreUtility {

	/**
	 * Attempts to find a specified number of seats, within a given priceband,
	 * that aren't currently booked.
	 * 
	 * @param numberOfSeats
	 *            the number of seats required.
	 * @param price
	 *            the priceband to search.
	 * @param bookedSeats
	 *            the set of seats that is currently booked.
	 * 
	 * @return a set of seats that are available to book. When successful the
	 *         set is non-empty and contains numberOfSeats seats that are within
	 *         the specified priceband. When not successful (i.e. when there are
	 *         not enough seats available in the required priceband, this method
	 *         returns the empty set.
	 * 
	 */
	public static Set<SeatDTO> findAvailableSeats(int numberOfSeats,
			PriceBand price, Set<SeatDTO> bookedSeats) {
		List<SeatDTO> openSeats = getAllAvailableSeatsByPrice(price,
				bookedSeats);

		if (openSeats.size() < numberOfSeats) {
			return new HashSet<SeatDTO>();
		}

		return getSpecificAvailableSeats(
				new Random().nextInt(openSeats.size()), numberOfSeats,
				openSeats);
	}


	protected static Set<SeatDTO> getSpecificAvailableSeats(int startIndex,
			int numberOfSeats, List<SeatDTO> openSeats) {
		Set<SeatDTO> availableSeats = new HashSet<SeatDTO>();
		while (numberOfSeats > 0) {
			if (startIndex > openSeats.size() - 1) {
				startIndex = 0;
			}
			availableSeats.add(openSeats.get(startIndex));
			startIndex++;
			numberOfSeats--;
		}
		return availableSeats;

	}

	protected static List<SeatDTO> getAllAvailableSeatsByPrice(PriceBand price,
			Set<SeatDTO> bookedSeats) {
		List<SeatDTO> openSeats = new ArrayList<SeatDTO>();
		Set<SeatRow> rowsInPriceBand = TheatreLayout
				.getRowsForPriceBand(price);

		for (SeatRow row : rowsInPriceBand) {
			for (int i = 1; i <= TheatreLayout.getNumberOfSeatsForRow(row); i++) {
				SeatDTO seat = new SeatDTO(row, new SeatNumber(i));
				if (!bookedSeats.contains(seat)) {
					openSeats.add(seat);
				}
			}
		}
		return openSeats;
	}
}
