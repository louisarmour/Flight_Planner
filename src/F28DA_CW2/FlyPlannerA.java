package F28DA_CW2;

import java.util.HashSet;
import java.util.List;

public interface FlyPlannerA<A extends AirportA, F extends Flight> {

	/**
	 * Populates the graph with the airports and flights information from a flight
	 * reader object. Returns true if the operation was successful.
	 */
	boolean populate(FlightsReader fr);

	/**
	 * Populates the graph with the airports and flights information. Returns true
	 * if the operation was successful.
	 */
	boolean populate(HashSet<String[]> airports, HashSet<String[]> flights);

	/** Returns the airport object of the given airport code. */
	A airport(String code);

	/** Returns the flight object of the given flight code. */
	F flight(String code);


	/**
	 * Returns a cheapest flight trip from one airport (airport code) to another
	 */
	TripImpl leastCost(String from, String to) throws FlyPlannerException;

	/**
	 * Returns a least connections flight trip from one airport (airport code) to
	 * another
	 */
	TripImpl leastHop(String from, String to) throws FlyPlannerException;

	/**
	 * Returns a cheapest flight trip from one airport (airport code) to another,
	 * excluding a list of airport (airport codes)
	 */
	TripImpl leastCost(String from, String to, List<String> excluding) throws FlyPlannerException;

	/**
	 * Returns a least connections flight trip from one airport (airport code) to
	 * another, excluding a list of airport (airport codes)
	 */
	TripImpl leastHop(String from, String to, List<String> excluding) throws FlyPlannerException;

}
