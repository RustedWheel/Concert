package nz.ac.auckland.concert.common.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nz.ac.auckland.concert.common.types.PriceBand;
import nz.ac.auckland.concert.common.types.SeatRow;

/**
 * Utility class that models the layout of seats at the concert venue.
 *
 */
public class TheatreLayout {
	
	private static Set<SeatRow> _rowsInPriceBandA;
	private static Set<SeatRow> _rowsInPriceBandB;
	private static Set<SeatRow> _rowsInPriceBandC;
	
	static {
		_rowsInPriceBandA = new HashSet<SeatRow>();
		_rowsInPriceBandB = new HashSet<SeatRow>();
		_rowsInPriceBandC = new HashSet<SeatRow>();
		
		_rowsInPriceBandA.add(SeatRow.E);
		_rowsInPriceBandA.add(SeatRow.F);
		_rowsInPriceBandA.add(SeatRow.G);
		_rowsInPriceBandA.add(SeatRow.J);
		_rowsInPriceBandA.add(SeatRow.K);
		_rowsInPriceBandA.add(SeatRow.L);
		_rowsInPriceBandA.add(SeatRow.M);
		
		_rowsInPriceBandB.add(SeatRow.A);
		_rowsInPriceBandB.add(SeatRow.B);
		_rowsInPriceBandB.add(SeatRow.C);
		_rowsInPriceBandB.add(SeatRow.D);
		
		_rowsInPriceBandC.add(SeatRow.H);
		_rowsInPriceBandC.add(SeatRow.N);
		_rowsInPriceBandC.add(SeatRow.O);
		_rowsInPriceBandC.add(SeatRow.P);
		_rowsInPriceBandC.add(SeatRow.R);
		
		_rowsInPriceBandA = Collections.unmodifiableSet(_rowsInPriceBandA);
		_rowsInPriceBandB = Collections.unmodifiableSet(_rowsInPriceBandB);
		_rowsInPriceBandC = Collections.unmodifiableSet(_rowsInPriceBandC);
		
	}
	
	// This is utility class, so hide the constructor to prevent instantiation.
	private TheatreLayout() {}
	
	/**
	 * Returns the number of seats in a specified row at the concert venue.
	 *
	 */
	public static int getNumberOfSeatsForRow(SeatRow row) {
		int seats = 0;
		
		if(row == SeatRow.A) {
			seats = 19;
		} else if(row == SeatRow.B) {
			seats = 20;
		} else if(row == SeatRow.C || row == SeatRow.D || row == SeatRow.E) {
			seats = 21;
		} else if(row == SeatRow.F || row == SeatRow.H) {
			seats = 22;
		} else if(row == SeatRow.G) {
			seats = 23;
		} else if(row == SeatRow.J || row == SeatRow.K || row == SeatRow.L) {
			seats = 25;
		} else {
			seats = 26;
		}
		
		return seats;
	}
	
	/**
	 * Returns the rows that are within the specified price band.
	 * 
	 */
	public static Set<SeatRow> getRowsForPriceBand(PriceBand priceBand) {
		Set<SeatRow> rows = null;
		
		if(priceBand == PriceBand.PriceBandA) {
			rows = _rowsInPriceBandA;
		} else if(priceBand == PriceBand.PriceBandB) {
			rows = _rowsInPriceBandB;
		} else if(priceBand == PriceBand.PriceBandC) {
			rows = _rowsInPriceBandC;
		}
		return rows;
	}
	
}
