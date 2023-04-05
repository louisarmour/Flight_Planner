package F28DA_CW2;

public interface Flight {

	String getFlightCode();

	AirportImpl getTo();

	AirportImpl getFrom();
	
	String getFromGMTime();
	
	String getToGMTime();

	int getCost();

}