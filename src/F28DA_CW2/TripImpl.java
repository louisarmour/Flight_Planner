package F28DA_CW2;

import org.jgrapht.GraphPath;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TripImpl implements TripA<AirportImpl, FlightImpl>, TripB<AirportImpl, FlightImpl> {

    //Private fields
    private GraphPath<AirportImpl,FlightImpl> gp;
    private int longestAiportName;


    public TripImpl(GraphPath<AirportImpl, FlightImpl> gp, int longestAiportName) {
        this.gp = gp;
        this.longestAiportName = longestAiportName;
    }

    //Gets stops for the final trip
    public List<String> getStops() {
        List<AirportImpl> listOfAirportCodes = this.gp.getVertexList();
        LinkedList<String> llOfAirportCodes = new LinkedList<>();
        for(int i = 0; i < listOfAirportCodes.size(); i++){
            AirportImpl currentAirport = listOfAirportCodes.get(i);
            String currentAirportCode = currentAirport.getCode();
            llOfAirportCodes.add(currentAirportCode);
        }
        return llOfAirportCodes;
    }

    //Gets the formatted version for each hop on the final trip
    public List<String> getFlights() {

        List<FlightImpl> flightsList  = this.gp.getEdgeList();

        LinkedList<String> flightsListString = new LinkedList<String>();

        for(int i = 0; i < flightsList.size(); i++) {
            FlightImpl currentFlight = flightsList.get(i);
            flightsListString.add(currentFlight.toString(longestAiportName));
        }

        return flightsListString;
    }

    //Returns the total hops for the trip
    public int totalHop() {
        int totalHops = this.gp.getEdgeList().size();
        return totalHops;
    }

    //Gets the total cost for the trip
    public int totalCost() {
        List<FlightImpl> flights = this.gp.getEdgeList();
        Iterator<FlightImpl> i = flights.iterator();
        int cost = 0;
        while(i.hasNext()){
            FlightImpl currentFlight = i.next();
            cost += currentFlight.getCost();
        }
        return cost;
    }

    //Gets the total time spent in the air for the final trip
    public int airTime() {
        List<FlightImpl> flights = this.gp.getEdgeList();
        Iterator<FlightImpl> i = flights.iterator();
        int totalAirTime = 0;
        while(i.hasNext()){
            FlightImpl currentFlight = i.next();
            totalAirTime += differenceForFlight(currentFlight.getFromGMTime(), currentFlight.getToGMTime());

        }
        return totalAirTime;
    }

    //Calculates the difference between to two 24hour times
    private int differenceForFlight(String from, String to){

        String departureTime = from;
        String arrivalTime = to;
        int half;
        half = departureTime.length() / 2;
        String departureTimeHours = departureTime.substring(0, half);
        String departureTimeMinutes = arrivalTime.substring(0, half);
        String arrivalTimeHours = departureTime.substring(half);
        String arrivalTimeMinutes = arrivalTime.substring(half);

        int hoursDeparture = Integer.parseInt(departureTimeHours);
        int hoursArrival = Integer.parseInt(departureTimeMinutes);
        int minutesDeparture = Integer.parseInt(arrivalTimeHours);
        int minutesArrival = Integer.parseInt(arrivalTimeMinutes);

        if(minutesDeparture > minutesArrival){
            if(hoursDeparture < hoursArrival){
                --hoursArrival;
            }
            minutesArrival += 60;
        }

        if(hoursDeparture > hoursArrival){
            hoursArrival += 24;
        }

        int totalDifferenceMins = (minutesArrival - minutesDeparture) + ((hoursArrival - hoursDeparture) * 60);

        return totalDifferenceMins;
    }

    //Gets the total time waiting between each connection
    public int connectingTime() {
        int incrementer = 2;
        int arrivingTime = 0;
        int departingTime = 1;
        int totalConnectingTime = 0;
        List<FlightImpl> flights = this.gp.getEdgeList();
        List<String> flightTimes = new LinkedList<>();
        Iterator<FlightImpl> flightIterator = flights.iterator();
        while(flightIterator.hasNext()){
            FlightImpl currentFlight = flightIterator.next();
            flightTimes.add(currentFlight.getFromGMTime());
            flightTimes.add(currentFlight.getToGMTime());
        }
        if(flightTimes.size() > 2){
            flightTimes.remove(0);
            flightTimes.remove(flightTimes.size()-1);
            for(int i=0; i<flightTimes.size(); i+=incrementer){
                totalConnectingTime += differenceForFlight(flightTimes.get(arrivingTime), flightTimes.get(departingTime));
                arrivingTime += incrementer;
                departingTime += incrementer;
            }
        }
        else{
            return 0;
        }

        return totalConnectingTime;
    }

    //Gets the total time spent on the final trip
    public int totalTime() {
        return connectingTime() + airTime();
    }

    //Prints out the final trip in a formatted way
    @Override
    public String toString()
    {
        List<FlightImpl> finalFlights  = this.gp.getEdgeList();

        //Flight indexes
        int IndexOfFinalFlight = finalFlights.size() - 1;
        FlightImpl firstFlight = finalFlights.get(0);

        FlightImpl lastFlight = finalFlights.get(IndexOfFinalFlight);

        AirportImpl departureAirport = firstFlight.getFrom();
        AirportImpl destinationAirport = lastFlight.getTo();

        String stringToPrint = "";

        System.out.println();

        System.out.println("Journey from " + departureAirport + " to " + destinationAirport);

        //Gets the formatted version of each part for the final trip
        List<String> flightStringList = this.getFlights();

        //Formats the spacing for the final trip that is printed to the screen
        System.out.println(String.format("Leg %-"+longestAiportName+"s  %-6s%-8s%-"+longestAiportName+"s  At", "Leave", "At", "On","Arrive"));

        for (int i = 0; i < flightStringList.size(); i++) {
            stringToPrint += ( i + 1 ) + "   " + flightStringList.get(i) + "\n";
        }

        return stringToPrint;
    }
}
