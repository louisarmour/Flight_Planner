package F28DA_CW2;

import java.util.LinkedList;

public class FlightImpl implements Flight {

    //All variables are data of a given flight
    private String flightCode; //Will store the flight code past in
    private String departureTime; //Will store the departure code past in
    private String arrivalTime; //Will store the arrival time past in
    private AirportImpl destination; //Will store the destination airport code past in
    private AirportImpl from; //Will store the departing airport code past in
    private int cost; //Will store the cost of the flight past in

    /** Class constructor storing parameters passed in to appropriate local variables
     * @param flightCode flight code for the current flight
     * @param from destination airport code for the current flight
     * @param departureTime departing time for the current flight
     * @param destination departing airport code for the current flight
     * @param arrivalTime arrival time for the current flight
     * @param cost the ticket price for the current flight
     */
    public FlightImpl(String flightCode, AirportImpl from, String departureTime, AirportImpl destination, String arrivalTime, int cost){
        this.flightCode = flightCode;
        this.from = from;
        this.departureTime = departureTime;
        this.destination = destination;
        this.arrivalTime = arrivalTime;
        this.cost = cost;
    }

    public String getFlightCode(){
        return this.flightCode;
    }

    public AirportImpl getTo(){
        return this.destination;
    }

    public AirportImpl getFrom(){
        return this.from;
    }

    public String getFromGMTime(){
        return this.departureTime;
    }

    public String getToGMTime(){
        return this.arrivalTime;
    }

    public int getCost(){
        return this.cost;
    }

    //Formats the details of a flight for printingi
    public String toString(int x)
    {
        AirportImpl originAirport = this.getFrom();
        AirportImpl destinationAirport = this.getTo();
        String finalString;

        String originAptStr = String.format("%-"+x+"s  ", originAirport.toString());
        String fromTimeStr = String.format("%-6s", this.departureTime );
        String flightCodeStr = String.format("%-8s", this.flightCode );
        String destinationAptStr = String.format("%-"+x+"s  ", destinationAirport.toString());
        String toTimeStr = String.format("%-6s", this.arrivalTime );
        finalString = originAptStr + fromTimeStr + flightCodeStr + destinationAptStr  + toTimeStr;

        return finalString;
    }
}
