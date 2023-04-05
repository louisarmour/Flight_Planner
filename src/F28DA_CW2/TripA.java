package F28DA_CW2;

import java.util.List;

public interface TripA<A extends AirportA,F extends Flight> {

	/** Returns the list of airports codes composing the trip */
	List<String> getStops();
	
	/** Returns the list of flight codes composing the trip */
	List<String> getFlights();
	
	/** Returns the number of connections of the trip */
	int totalHop();
	
	/** Returns the total cost of the trip */
	int totalCost();
	
	/** Returns the total time (number of minutes) in flight of the trip */
	int airTime();
	
}
