package F28DA_CW2;

public interface TripB<A extends AirportA,F extends Flight> {

	/** Returns the total time in connection of the trip */
	int connectingTime();
	
	/** Returns the total travel time of the trip */
	int totalTime();
}
