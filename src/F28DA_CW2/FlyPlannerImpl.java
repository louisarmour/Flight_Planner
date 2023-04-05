package F28DA_CW2;

import java.util.*;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.DepthFirstIterator;


public class FlyPlannerImpl implements FlyPlannerA<AirportImpl,FlightImpl>, FlyPlannerB<AirportImpl,FlightImpl> {

    //Graphs
    private SimpleDirectedWeightedGraph<AirportImpl, FlightImpl> graph;
    private DirectedAcyclicGraph<AirportImpl, FlightImpl> dag;

    //Scanner used to get user input
    private Scanner scanner;

    //Data sets for checking data
    private Hashtable<String, String> airportNames = new Hashtable<>();
    private Hashtable<String, String> airportNamesReversed = new Hashtable<>();
    private HashSet<AirportImpl> registeredAvailableAirports = new HashSet<>();
    private Hashtable<String, AirportImpl> airportHashTable = new Hashtable<>();
    private Set<String> checkedAirports = new HashSet<>();
    private HashSet<AirportImpl> availableAirports = new HashSet<>();

    /**
     * Populates the two HashSets airports and flights with data read in from
     * the csv files airports.csv and flights.csv
     * @param fr FlightsReader object passed in to read data from the csv files
     * @return true if the graph is populated correctly
     */
    public boolean populate(FlightsReader fr) {
        HashSet<String[]> airports = fr.getAirports();
        HashSet<String[]> flights = fr.getFlights();
        return populate(airports, flights);
    }

    /**
     * Populates the class graph with the vertices as data from the airports hashset and edges
     * as data from the flights hashset.
     * @param airports hashset airports
     * @param flights hashset flights
     * @return true if the graphs are populated correctly and false if it is not
     */
    public boolean populate(HashSet<String[]> airports, HashSet<String[]> flights) {
        this.graph = new SimpleDirectedWeightedGraph<>(FlightImpl.class);
        Iterator<String[]> airportIterator = airports.iterator();
        //Add all airports as vertices to the graph
        while(airportIterator.hasNext()){
            String [] currentLine = airportIterator.next();
            AirportImpl currentAirport = new AirportImpl(currentLine[0], currentLine[1], currentLine[2]);
            graph.addVertex(currentAirport);
            String airportCodeName = currentAirport.getCity() + " (" + currentAirport.getCode() + ")";
            airportNames.put(currentAirport.getCode(),airportCodeName);
            airportNamesReversed.put(airportCodeName,currentAirport.getCode());
            airportHashTable.put(currentAirport.getCode(), currentAirport);
        }

        Iterator<String[]> flightsIterator = flights.iterator();
        //Add all flights to the edges of the graph
        while(flightsIterator.hasNext()){
            String[] currentLine = flightsIterator.next();
            String flightCode = currentLine[0];
            AirportImpl departureAirport = airportHashTable.get(currentLine[1]);
            String departureTime = currentLine[2];
            AirportImpl destinationAirport = airportHashTable.get(currentLine[3]);
            String arrivalTime = currentLine[4];
            int ticketPrice = Integer.parseInt(currentLine[5]);

            FlightImpl currentFlight = new FlightImpl(flightCode, departureAirport, departureTime, destinationAirport, arrivalTime, ticketPrice);

            graph.addEdge(departureAirport, destinationAirport, currentFlight);
        }

        if(!(graph.vertexSet().size() == airports.size() && graph.edgeSet().size() == flights.size())){
            return false;
        }

        return true;
    }

    /**
     * Searches through the graphs vertex set of airports to check if the code is there
     * @param code the specified airports code
     * @return true if the airport is found and false if not
     */
    public AirportImpl airport(String code) {
        Iterator<AirportImpl> airportIterator = this.graph.vertexSet().iterator();
        while(airportIterator.hasNext()){
            AirportImpl currentAirport = airportIterator.next();
            String currentAirportCode = currentAirport.getCode();
            if(code.equals(currentAirportCode)){
                return currentAirport;
            }
        }
        return null;
    }

    /**
     * Searches through the graphs edge set of flights to check if the code is there
     * @param code the flight code of the specified flight
     * @return true if the flight code is found and false if not
     */
    public FlightImpl flight(String code) {
        Iterator<FlightImpl> flightIterator = this.graph.edgeSet().iterator();
        while(flightIterator.hasNext()){
            FlightImpl currentFlight = flightIterator.next();
            String currentFlightCode = currentFlight.getFlightCode();
            if(code.equals(currentFlightCode)){
                return currentFlight;
            }
        }
        return null;
    }


    /**
     * Calls the leastCost method with three parameters and sets the excluding list to null
     * @param from the departing airport
     * @param to the destination airport
     * @return TripImpl object of the least costing journey
     * @throws FlyPlannerException if a trip was not found
     */
    public TripImpl leastCost(String from, String to) throws FlyPlannerException {
        return this.leastCost(from, to, null);
    }

    /**
     * Calls the leastHop method with three parameters and sets the excluding list to null
     * @param from the departing airport
     * @param to the destination airport
     * @return TripImpl object with the least amount of connections
     * @throws FlyPlannerException if a trip was not found
     */
    public TripImpl leastHop(String from, String to) throws FlyPlannerException {
        return this.leastHop(from, to, null);
    }

    /**
     * If excluding is not null, the local graph will have airports and flights
     * connected to each airport in excluding will be removed. Each edge will have
     * a weight added with a ticket price. A TripImpl object will be created off
     * the shortest path found based the weight of each edge.
     * will then be found based on cost.
     * @param from departing airport
     * @param to destination airport
     * @param excluding list of airports to avoid
     * @return finalTrip if a path is found and null if not
     * @throws FlyPlannerException if a trip was not found or from and to are the same
     */
    public TripImpl leastCost(String from, String to, List<String> excluding) throws FlyPlannerException {
        SimpleDirectedWeightedGraph<AirportImpl, FlightImpl> demoGraph = this.graph;
        if(excluding != null){
            if(excluding.size() > 0){
                for(int i=0; i<excluding.size(); i++){
                    if(excluding.get(i).equals(from) || excluding.get(i).equals(to)){
                        throw new FlyPlannerException("Cannot exclude " + excluding.get(i));
                    }
                    AirportImpl currentAirport = this.airport(excluding.get(i));
                    Set<FlightImpl> flightsToRemove = demoGraph.edgesOf(currentAirport);
                    demoGraph.removeAllEdges(flightsToRemove);
                    demoGraph.removeVertex(currentAirport);
                }
            }
        }

        Iterator<FlightImpl> flightsIterator = demoGraph.edgeSet().iterator();
        while(flightsIterator.hasNext()){
            FlightImpl currentFlight = flightsIterator.next();
            int ticketPrice = currentFlight.getCost();
            demoGraph.setEdgeWeight(currentFlight, ticketPrice);
        }
        DijkstraShortestPath<AirportImpl,FlightImpl> dijkstraPath = new DijkstraShortestPath<>(demoGraph);

        AirportImpl departingAirport = airport(from);

        AirportImpl destinationAirport = airport(to);

        GraphPath<AirportImpl, FlightImpl> sp = dijkstraPath.getPath(departingAirport, destinationAirport);

        if(sp == null){
            throw new FlyPlannerException("A trip could not be found.");
        }

        int longestAirportName = longestAirportName(sp.getVertexList());

        TripImpl finalTrip = new TripImpl(sp,longestAirportName);

        return finalTrip;
    }

    /**
     * If excluding is not null, the local graph will have airports and flights
     * connected to each airport in excluding will be removed. Each edge will have
     * a weight added with a value of 1. A TripImpl object will be created off
     * the shortest path found based the weight of each edge.
     * will then be found based on cost.
     * @param from departing airport
     * @param to destination airport
     * @param excluding list of airports to avoid
     * @return finalTrip if a path is found or null if not
     * @throws FlyPlannerException if a trip was not found or from and to are the same
     */
    public TripImpl leastHop(String from, String to, List<String> excluding) throws FlyPlannerException {
        TripImpl nullTrip = null;
        SimpleDirectedWeightedGraph<AirportImpl, FlightImpl> demoGraph = this.graph;
        if(excluding != null){
            if(excluding.size() > 0){
                for(int i=0; i<excluding.size(); i++){
                    if(excluding.get(i).equals(from) || excluding.get(i).equals(to)){
                        throw new FlyPlannerException("Cannot exclude " + excluding.get(i));
                    }
                    AirportImpl currentAirport = this.airport(excluding.get(i));
                    Set<FlightImpl> flightsToRemove = demoGraph.edgesOf(currentAirport);
                    demoGraph.removeAllEdges(flightsToRemove);
                    demoGraph.removeVertex(currentAirport);
                }
            }
        }

        Iterator<FlightImpl> flightsIterator = demoGraph.edgeSet().iterator();
        while(flightsIterator.hasNext()){
            FlightImpl currentFlight = flightsIterator.next();
            demoGraph.setEdgeWeight(currentFlight, 1d);
        }
        DijkstraShortestPath<AirportImpl,FlightImpl> dijkstraPath = new DijkstraShortestPath<>(demoGraph);

        AirportImpl departingAirport = airport(from);

        AirportImpl destinationAirport = airport(to);

        GraphPath<AirportImpl, FlightImpl> sp = dijkstraPath.getPath(departingAirport, destinationAirport);

        if(sp == null){
            return nullTrip;
        }

        else {
            int longestAirportName = longestAirportName(sp.getVertexList());

            TripImpl finalTrip = new TripImpl(sp, longestAirportName);

            return finalTrip;
        }
    }

    /**
     * If excluding is not null, the local graph will have airports and flights
     * connected to each airport in excluding will be removed. Each edge will have
     * a weight added with a value of 1. A TripImpl object will be created off
     * the shortest path found based the weight of each edge.
     * will then be found based on cost.
     * @param from departing airport
     * @param to destination airport
     * @param excluding list of airports to avoid
     * @return finalTrip if a path is found or nullTrip if not
     * @throws FlyPlannerException if a path is not found or from and to are equal
     */
    public TripImpl leastHopMeetUp(String from, String to, List<String> excluding) throws FlyPlannerException {
        TripImpl nullTrip = null;
        SimpleDirectedWeightedGraph<AirportImpl, FlightImpl> demoGraph = this.graph;

        if(excluding != null){
            if(excluding.size() > 0){
                for(int i=0; i<excluding.size(); i++){
                    if(excluding.get(i).equals(from) || excluding.get(i).equals(to)){
                        throw new FlyPlannerException("Cannot exclude " + excluding.get(i));
                    }
                    FlightImpl currentFlight = this.flight(excluding.get(i));
                    demoGraph.removeEdge(currentFlight);
                }
            }
        }

        Iterator<FlightImpl> flightsIterator = demoGraph.edgeSet().iterator();
        while(flightsIterator.hasNext()){
            FlightImpl currentFlight = flightsIterator.next();
            demoGraph.setEdgeWeight(currentFlight, 1d);
        }
        DijkstraShortestPath<AirportImpl,FlightImpl> dijkstraPath = new DijkstraShortestPath<>(demoGraph);

        AirportImpl departingAirport = airport(from);

        AirportImpl destinationAirport = airport(to);

        GraphPath<AirportImpl, FlightImpl> sp = dijkstraPath.getPath(departingAirport, destinationAirport);

        if(sp == null){
            return nullTrip;
        }

        else {
            int longestAirportName = longestAirportName(sp.getVertexList());

            TripImpl finalTrip = new TripImpl(sp, longestAirportName);

            return finalTrip;
        }
    }

    /**
     * Finds the directly connected airports from a given airport
     * @param airport starting airport
     * @return connectedAirports set
     */
    public Set<AirportImpl> directlyConnected(AirportImpl airport) {
        DepthFirstIterator<AirportImpl, FlightImpl> dfi = new DepthFirstIterator<>(this.graph, airport);
        HashSet<AirportImpl> connectedAirports = new HashSet<>();
        while (dfi.hasNext()){
            AirportImpl currentEdge = dfi.next();
            if(this.graph.containsEdge(airport, currentEdge) && this.graph.containsEdge(currentEdge, airport)){
                connectedAirports.add(currentEdge);
            }
        }
        return connectedAirports;
    }

    /**
     * Calculates the total number of directly connected airports in the graph
     * @return totalDirectlyConnected
     */
    public int setDirectlyConnected() {

        Iterator<AirportImpl> airportIterator = this.graph.vertexSet().iterator();

        int totalDirectlyConnected = 0;

        while(airportIterator.hasNext()){
            AirportImpl currentAirport = airportIterator.next();
            int directlyConnectedAirports = this.directlyConnected(currentAirport).size();
            totalDirectlyConnected += directlyConnectedAirports;

        }
        return totalDirectlyConnected;
    }

    /**
     * Removes all flights from the directed acyclic graph that do not end at
     * an airport that have more directly connected airports than the origin
     * @return number of edges in the graph
     */
    public int setDirectlyConnectedOrder() {
        this.dag = new DirectedAcyclicGraph<>(FlightImpl.class);
        Iterator<AirportImpl> airportIterator = this.graph.vertexSet().iterator();
        Hashtable<String, String> confirmedAirport = new Hashtable<>();
        List<List<AirportImpl>> confirmedAirports = new ArrayList<>();

        while(airportIterator.hasNext()){
            HashSet<Integer> tempConnectedAirportsSizes = new HashSet<>();
            HashSet<Set<AirportImpl>> tempConnectedAirportNames = new HashSet<>();
            AirportImpl currentAirport = airportIterator.next();
            dag.addVertex(currentAirport);
            tempConnectedAirportsSizes.add(directlyConnected(currentAirport).size());
            tempConnectedAirportNames.add(directlyConnected(currentAirport));
            Iterator<Set<AirportImpl>> airportsIterator2 = tempConnectedAirportNames.iterator();
            while(airportsIterator2.hasNext()){
                List<AirportImpl> nice = new ArrayList<>(airportsIterator2.next());
                for(int i=0; i<nice.size(); i++){
                    if(directlyConnected(currentAirport).size() < directlyConnected(nice.get(i)).size()){
                        List<AirportImpl> tempCurrentAirports = new LinkedList<>();
                        confirmedAirport.put(nice.get(i).toString(), currentAirport.toString());
                        tempCurrentAirports.add(currentAirport);
                        tempCurrentAirports.add(nice.get(i));
                        confirmedAirports.add(tempCurrentAirports);
                    }
                }
            }
        }

        Iterator<FlightImpl> flightIterator = this.graph.edgeSet().iterator();
        while(flightIterator.hasNext()){
            FlightImpl currentFlightLine = flightIterator.next();
            for(int x=0; x<confirmedAirports.size(); x++){
                if(currentFlightLine.getFrom().equals(confirmedAirports.get(x).get(0)) && currentFlightLine.getTo().equals(confirmedAirports.get(x).get(1))){
                    FlightImpl currentFlight = new FlightImpl(currentFlightLine.getFlightCode(),
                            currentFlightLine.getFrom(), currentFlightLine.getFromGMTime(), currentFlightLine.getTo(),
                            currentFlightLine.getToGMTime(), currentFlightLine.getCost());
                    dag.addEdge(currentFlight.getFrom(), currentFlight.getTo(), currentFlight);
                }
            }
        }

        return dag.edgeSet().size();
    }

    /**
     * Returns the name of the airports the have more direct connections and
     * are available from the starting airport
     * @param airport starting airport
     * @return the set of airports available that have more direct connections
     */
    public Set<AirportImpl> getBetterConnectedInOrder(AirportImpl airport) {
        DirectedAcyclicGraph<AirportImpl,FlightImpl> graph123 = this.dag;
        Iterator<FlightImpl> flightIterator = graph123.edgeSet().iterator();
        boolean foundAllAirports = false;
        while(flightIterator.hasNext()){
            FlightImpl currentFlight = flightIterator.next();
            if(currentFlight.getFrom().toString().equals(airportNames.get(airport.getCode()))){
                if(!checkedAirports.contains(currentFlight.getFrom().toString())) {
                    availableAirports.add(currentFlight.getTo());
                    registeredAvailableAirports.add(currentFlight.getTo());
                    checkedAirports.add(currentFlight.getTo().toString());
                }
            }
        }

        while(foundAllAirports == false){
            int prevSize = registeredAvailableAirports.size();
            registeredAvailableAirports = checkFurtherAirports();
            if(prevSize == registeredAvailableAirports.size()){
                foundAllAirports = true;
            }
        }

        return registeredAvailableAirports;
    }

    /**
     * Checks for more directly connected airports connected to
     * the already found directly airports
     * @return set of directly connected airports
     */
    private HashSet<AirportImpl> checkFurtherAirports(){
        SimpleDirectedWeightedGraph<AirportImpl, FlightImpl> graph123 = this.graph;
        Iterator<FlightImpl> furtherAirports = graph123.edgeSet().iterator();
        while(furtherAirports.hasNext()){
            FlightImpl currentFlight = furtherAirports.next();
            if(registeredAvailableAirports.contains(currentFlight.getFrom()) && !registeredAvailableAirports.contains(currentFlight.getTo())){
                if(directlyConnected(currentFlight.getFrom()).size() < directlyConnected(currentFlight.getTo()).size()){
                    registeredAvailableAirports.add(currentFlight.getTo());
                }
            }
        }
        return registeredAvailableAirports;
    }

    /**
     * Find the ideal airport for two people to meet based on
     * their starting airports and the number of hops
     * @param at1 first starting airport
     * @param at2 second starting airport
     * @return the ideal airport code
     * @throws FlyPlannerException if a trip is not found
     */
    public String leastHopMeetUp(String at1, String at2) throws FlyPlannerException {
        SimpleDirectedWeightedGraph<AirportImpl, FlightImpl> currGraph = this.graph;
        Hashtable<String, Integer> flightHops = new Hashtable<>();
        Hashtable<Integer, String> flightHopsReversed = new Hashtable<>();
        int lowestHop = 0;
        Iterator<AirportImpl> airportIteratorAt1 = currGraph.vertexSet().iterator();
        while(airportIteratorAt1.hasNext()) {
            TripImpl flightsFrom = null;
            boolean foundTrip = true;
            AirportImpl currentAirport = airportIteratorAt1.next();
            if (((currGraph.outgoingEdgesOf(currentAirport).size() + currGraph.incomingEdgesOf(currentAirport).size()) < 2) || currentAirport.getCode().equals(at1) || currentAirport.getCode().equals(at2)) {

            } else {
                try {
                    flightsFrom = this.leastHop(at1, currentAirport.getCode());
                    try {
                        flightsFrom.getStops();
                    } catch (NullPointerException e) {
                        foundTrip = false;
                    }
                    if (foundTrip == true) {
                        flightHops.put(currentAirport.getCode(), flightsFrom.totalCost());
                    }
                } catch (FlyPlannerException e) {
                }
            }
        }

        Iterator<AirportImpl> airportIteratorAt2 = currGraph.vertexSet().iterator();
        while(airportIteratorAt2.hasNext()) {
            TripImpl flightsFromAt2 = null;
            boolean foundTrip = true;
            AirportImpl currentAirport = airportIteratorAt2.next();
            if (((currGraph.outgoingEdgesOf(currentAirport).size() + currGraph.incomingEdgesOf(currentAirport).size()) < 2) || currentAirport.getCode().equals(at2) || currentAirport.getCode().equals(at1)) {

            } else {
                try {
                    flightsFromAt2 = this.leastHop(at2, currentAirport.getCode());
                    try {
                        flightsFromAt2.getStops();
                    } catch (NullPointerException e) {
                        foundTrip = false;
                    }
                    if (foundTrip == true) {
                        int totalFlightTimes = flightsFromAt2.totalCost() + flightHops.get(currentAirport.getCode());
                        lowestHop = totalFlightTimes;
                        flightHops.put(currentAirport.getCode(), totalFlightTimes);
                        flightHopsReversed.put(totalFlightTimes, currentAirport.getCode());
                    }
                } catch (FlyPlannerException e) {
                }
            }
        }

        Set<String> flightTimeKeys = flightHops.keySet();
        for(String flightTime: flightTimeKeys){
            if(lowestHop > flightHops.get(flightTime)){
                lowestHop = flightHops.get(flightTime);
            }
        }

        String idealAirport = flightHopsReversed.get(lowestHop);

        return idealAirport;
    }

    /**
     * Find the ideal airport for two people to meet based on
     * their starting airports and the cost of each journey
     * @param at1 first starting airport
     * @param at2 second starting airport
     * @return the ideal airport code
     * @throws FlyPlannerException if a journey is not found
     */
    public String leastCostMeetUp(String at1, String at2) throws FlyPlannerException {
        SimpleDirectedWeightedGraph<AirportImpl, FlightImpl> currGraph = this.graph;
        Hashtable<String, Integer> flightCosts = new Hashtable<>();
        Hashtable<Integer, String> flightCostsReversed = new Hashtable<>();
        int lowestCost = 0;
        Iterator<AirportImpl> airportIteratorAt1 = currGraph.vertexSet().iterator();
        while(airportIteratorAt1.hasNext()) {
            TripImpl flightsFrom = null;
            boolean foundTrip = true;
            AirportImpl currentAirport = airportIteratorAt1.next();
            if (((currGraph.outgoingEdgesOf(currentAirport).size() + currGraph.incomingEdgesOf(currentAirport).size()) < 2) || currentAirport.getCode().equals(at1) || currentAirport.getCode().equals(at2)) {

            } else {
                try {
                    flightsFrom = this.leastCost(at1, currentAirport.getCode());
                    try {
                        flightsFrom.getStops();
                    } catch (NullPointerException e) {
                        foundTrip = false;
                    }
                    if (foundTrip == true) {
                        flightCosts.put(currentAirport.getCode(), flightsFrom.totalCost());
                    }
                } catch (FlyPlannerException e) {
                }
            }
        }

        Iterator<AirportImpl> airportIteratorAt2 = currGraph.vertexSet().iterator();
        while(airportIteratorAt2.hasNext()) {
            TripImpl flightsFromAt2 = null;
            boolean foundTrip = true;
            AirportImpl currentAirport = airportIteratorAt2.next();
            if (((currGraph.outgoingEdgesOf(currentAirport).size() + currGraph.incomingEdgesOf(currentAirport).size()) < 2) || currentAirport.getCode().equals(at2) || currentAirport.getCode().equals(at1)) {

            } else {
                try {
                    flightsFromAt2 = this.leastCost(at2, currentAirport.getCode());
                    try {
                        flightsFromAt2.getStops();
                    } catch (NullPointerException e) {
                        foundTrip = false;
                    }
                    if (foundTrip == true) {
                        int totalFlightTimes = flightsFromAt2.totalCost() + flightCosts.get(currentAirport.getCode());
                        lowestCost = totalFlightTimes;
                        flightCosts.put(currentAirport.getCode(), totalFlightTimes);
                        flightCostsReversed.put(totalFlightTimes, currentAirport.getCode());
                    }
                } catch (FlyPlannerException e) {
                }
            }
        }

        Set<String> flightTimeKeys = flightCosts.keySet();
        for(String flightTime: flightTimeKeys){
            if(lowestCost > flightCosts.get(flightTime)){
                lowestCost = flightCosts.get(flightTime);
            }
        }

        String idealAirport = flightCostsReversed.get(lowestCost);

        return idealAirport;
    }

    /**
     * Find the ideal airport for two people to meet based on
     * their starting airports, their departing time and number
     * of hops
     * @param at1 first starting airport
     * @param at2 second starting airport
     * @param startTime departing time for both people
     * @return the ideal airport to meet at
     * @throws FlyPlannerException if a trip is not found
     */
    public String leastTimeMeetUp(String at1, String at2, String startTime) throws FlyPlannerException {

        SimpleDirectedWeightedGraph<AirportImpl, FlightImpl> currGraph = this.graph;
        Iterator<FlightImpl> flightIterator = currGraph.edgeSet().iterator();
        Hashtable<Integer, String> flightTimesReversed = new Hashtable<>();
        Hashtable<String, Integer> flightTimes = new Hashtable<>();
        List<String> excluding = new LinkedList<>();
        int shortestTime = 0;

        while(flightIterator.hasNext()){
            FlightImpl currentFlight = flightIterator.next();
            if((Integer.parseInt(currentFlight.getFromGMTime()) < Integer.parseInt(startTime)) && !excluding.contains(airportNamesReversed.get(currentFlight.getFrom().toString()))){
                List<String> flightToRemove = new LinkedList<>();
                flightToRemove.add(airportNamesReversed.get(currentFlight.getFrom().toString()));
                flightToRemove.add(airportNamesReversed.get(currentFlight.getTo().toString()));
                excluding.add(currentFlight.getFlightCode());
            }
        }

        Iterator<AirportImpl> airportIteratorAt1 = currGraph.vertexSet().iterator();
        while(airportIteratorAt1.hasNext()) {
            TripImpl flightsFrom = null;
            boolean foundTrip = true;
            AirportImpl currentAirport = airportIteratorAt1.next();
            if (((currGraph.outgoingEdgesOf(currentAirport).size() + currGraph.incomingEdgesOf(currentAirport).size()) < 2) || currentAirport.getCode().equals(at1)) {

            } else {
                try {
                    flightsFrom = this.leastHopMeetUp(at1, currentAirport.getCode(),excluding);
                    try {
                        flightsFrom.getStops();
                    } catch (NullPointerException e) {
                        foundTrip = false;
                    }
                    if (foundTrip == true) {
                        flightTimes.put(currentAirport.getCode(), flightsFrom.totalTime());
                    }
                } catch (FlyPlannerException e) {
                }
            }
        }

        Iterator<AirportImpl> airportIteratorAt2 = currGraph.vertexSet().iterator();
        while(airportIteratorAt2.hasNext()) {
            TripImpl flightsFromAt2 = null;
            boolean foundTrip = true;
            AirportImpl currentAirport = airportIteratorAt2.next();
            if (((currGraph.outgoingEdgesOf(currentAirport).size() + currGraph.incomingEdgesOf(currentAirport).size()) < 2) || currentAirport.getCode().equals(at2) || currentAirport.getCode().equals(at1)) {

            } else {
                try {
                    flightsFromAt2 = this.leastHopMeetUp(at2, currentAirport.getCode(),excluding);
                    try {
                        flightsFromAt2.getStops();
                    } catch (NullPointerException e) {
                        foundTrip = false;
                    }
                    if (foundTrip == true) {
                        int totalFlightTimes = flightsFromAt2.totalTime() + flightTimes.get(currentAirport.getCode());
                        shortestTime = totalFlightTimes;
                        flightTimes.put(currentAirport.getCode(), totalFlightTimes);
                        flightTimesReversed.put(totalFlightTimes, currentAirport.getCode());
                    }
                } catch (FlyPlannerException e) {
                }
            }
        }

        System.out.println("flightTimes " + flightTimes);
        Set<String> flightTimeKeys = flightTimes.keySet();
        for(String flightTime: flightTimeKeys){
            if(shortestTime > flightTimes.get(flightTime)){
                shortestTime = flightTimes.get(flightTime);
            }
        }

        String idealAirport = flightTimesReversed.get(shortestTime);

        return idealAirport;
    }

    private void printAllAirports(){
        Iterator<AirportImpl> airportsIterator = graph.vertexSet().iterator();
        while(airportsIterator.hasNext()){
            AirportImpl currentAirport = airportsIterator.next();
            System.out.println(currentAirport.getName() + " (" + currentAirport.getCode() + ")");
        }
    }

    public void gettingUserInput(){

        String cost = "cost";
        String hop = "hop";
        String time = "time";

        System.out.println("The following airports are available: ");
        this.printAllAirports();

        this.scanner = new Scanner(System.in);

        System.out.println("\nFinding the least costing trip Part A");
        this.userInputForLeastCostPartA();

        System.out.println("\nFinding the least costing trip");
        this.userInputForLeastCost();

        System.out.println("\nFinding the least hops trip");
        this.userInputForLeasHop();

        System.out.println("\nFinding the number of directly connected airports");
        this.userInputForDirectlyConnectedAirport();

        System.out.println("\nFinding the best place to meet based on cost");
        this.userInputForMeetUps(cost);

        System.out.println("\nFinding the best place to meet based on number of connections");
        this.userInputForMeetUps(hop);

        System.out.println("\nFinding the best place to meet based on departing time");
        this.userInputForMeetUps(time);
    }

    private void userInputForMeetUps(String var){
        String airportCode = "";
        System.out.println("Please enter the first airport code");
        String airportCode1 = scanner.nextLine();
        System.out.println("Please enter the second airport code");
        String airportCode2 = scanner.nextLine();
        if(airportCode1.equals("") || airportCode2.equals("")){
            System.out.println("\nPlease re-run the program and enter two airport codes.");
            var = "exit";
        }
        if(var.equals("cost")){
            try {
                airportCode = this.leastCostMeetUp(airportCode1, airportCode2);
            } catch(FlyPlannerException e){
                System.out.println("A trip could not be found.");
            }
            System.out.println("The best airport to meet at is: " + airportCode);
        }
        else if(var.equals("hop")){
            try {
                airportCode = this.leastHopMeetUp(airportCode1, airportCode2);
            } catch(FlyPlannerException e){
                System.out.println("A trip could not be found.");
            }
            System.out.println("The best airport to meet at is: " + airportCode);
        }
        else if(var.equals("time")){
            System.out.println("Please enter a departing time");
            String time = scanner.nextLine();
            System.out.println("Finding the best airport, this may take up to 10 minutes.\n");
            try {
                airportCode = this.leastTimeMeetUp(airportCode1, airportCode2, time);
            } catch(FlyPlannerException e){
                System.out.println("A trip could not be found.");
            }
            System.out.println("The best airport to meet at is: " + airportCode);
        }
        else{
            System.out.println("Please enter two separate airport codes when you re-run the program.");
        }
    }

    private void userInputForDirectlyConnectedAirport(){
        System.out.println("Please enter an airport code");
        String airportCodeString = scanner.nextLine();
        AirportImpl airportCode = airport(airportCodeString);
        Set<AirportImpl> directlyConnectedAirports = this.directlyConnected(airportCode);
        int size = directlyConnectedAirports.size();
        int count = 0;
        Iterator<AirportImpl> airportIterator = directlyConnectedAirports.iterator();
        System.out.println("Number of directly connected airports: " + directlyConnectedAirports.size());
        System.out.print("Directly Connected Airports: ");
        while(airportIterator.hasNext()){
            if(count == size-1){
                System.out.print(airportIterator.next() + "\n");
                break;
            }
            System.out.print(airportIterator.next() + ", ");
            count++;
        }
        System.out.println();
    }

    private void userInputForLeasHop(){

        List<String> airportsToExclude = new LinkedList<>();

        TripImpl finalTrip = null;

        boolean receivedInput = false;

        while(receivedInput != true){
            try {
                //Asks the user for the airport code they're departing from
                System.out.println("Please enter the departing airport code");

                //Stores the user input into the variable departingAirportCode
                String departingAirportCode = scanner.nextLine();

                //Asks the user for the airport code they're flying to
                System.out.println("Please enter the destination airport code");

                //Stores the user input into the variable destinationAirportCOde
                String destinationAirportCode = scanner.nextLine();

                //Asks the user if they would like to avoid any airports
                System.out.println("Are there any airports you would like to exclude? (yes/no)");

                //Stores the user input into the variable excludeAirportsInput
                String excludeAirportsInput = scanner.nextLine().toUpperCase();

                if(excludeAirportsInput.charAt(0) == 'Y'){
                    System.out.println("Please enter the airports you would like to exclude in the format: LHR,EDI,OTH...");
                    String excludedAirports = scanner.nextLine();
                    if(!excludedAirports.equals(null)){
                        String[] airports = excludedAirports.split(",");
                        airportsToExclude.addAll(Arrays.asList(airports));
                        finalTrip = this.leastHop(departingAirportCode, destinationAirportCode, airportsToExclude);
                    }
                }
                else if (excludeAirportsInput.charAt(0) == 'N'){
                    finalTrip = this.leastHop(departingAirportCode, destinationAirportCode);
                }
                else{
                    throw new FlyPlannerException("Please re-run the program and enter yes or no for excluding airports.");
                }

                receivedInput = true;
            }
            catch (FlyPlannerException e)
            {
                System.out.println("No journey was found");

                receivedInput = false;
            }
        }
        this.printFinalTrip(finalTrip);

    }

    private void userInputForLeastCostPartA(){

        List<String> airportsToExclude = new LinkedList<>();

        TripImpl finalTrip = null;

        boolean receivedInput = false;

        while(receivedInput != true){
            try
            {
                //Asks the user for the airport code they're departing from
                System.out.println("Please enter the departing airport code");

                //Stores the user input into the variable departingAirportCode
                String departingAirportCode = scanner.nextLine();

                //Asks the user for the airport code they're flying to
                System.out.println("Please enter the destination airport code");

                //Stores the user input into the variable destinationAirportCOde
                String destinationAirportCode = scanner.nextLine();

                //Asks the user if they would like to avoid any airports
                System.out.println("Are there any airports you would like to exclude? (yes/no)");

                //Stores the user input into the variable excludeAirportsInput
                String excludeAirportsInput = scanner.nextLine().toUpperCase();

                if(excludeAirportsInput.charAt(0) == 'Y'){
                    System.out.println("Please enter the airports you would like to exclude in the format: LHR,EDI,OTH...");
                    String excludedAirports = scanner.nextLine();
                    if(!excludedAirports.equals(null)){
                        String[] airports = excludedAirports.split(",");
                        airportsToExclude.addAll(Arrays.asList(airports));
                        finalTrip = this.leastCost(departingAirportCode, destinationAirportCode, airportsToExclude);
                    }
                    else{
                        throw new FlyPlannerException("Please re-run the program and enter the airports you would like to exclude");
                    }
                }
                else if (excludeAirportsInput.charAt(0) == 'N'){
                    finalTrip = this.leastCost(departingAirportCode, destinationAirportCode);
                }
                else{
                    throw new FlyPlannerException("Please re-run the program and enter yes or no for excluding airports.");
                }
                receivedInput = true;
            }
            catch (FlyPlannerException e)
            {
                System.out.println("No journey was found");
                receivedInput = false;
            }
        }

        this.printFinalTripPartA(finalTrip);
    }

    private void userInputForLeastCost(){

        List<String> airportsToExclude = new LinkedList<>();

        TripImpl finalTrip = null;

        boolean receivedInput = false;

        while(receivedInput != true){
            try
            {
                //Asks the user for the airport code they're departing from
                System.out.println("Please enter the departing airport code");

                //Stores the user input into the variable departingAirportCode
                String departingAirportCode = scanner.nextLine();

                //Asks the user for the airport code they're flying to
                System.out.println("Please enter the destination airport code");

                //Stores the user input into the variable destinationAirportCOde
                String destinationAirportCode = scanner.nextLine();

                //Asks the user if they would like to avoid any airports
                System.out.println("Are there any airports you would like to exclude? (yes/no)");

                //Stores the user input into the variable excludeAirportsInput
                String excludeAirportsInput = scanner.nextLine().toUpperCase();

                if(excludeAirportsInput.charAt(0) == 'Y'){
                    System.out.println("Please enter the airports you would like to exclude in the format: LHR,EDI,OTH...");
                    String excludedAirports = scanner.nextLine();
                    if(!excludedAirports.equals(null)){
                        String[] airports = excludedAirports.split(",");
                        airportsToExclude.addAll(Arrays.asList(airports));
                        finalTrip = this.leastCost(departingAirportCode, destinationAirportCode, airportsToExclude);
                    }
                    else{
                        throw new FlyPlannerException("Please re-run the program and enter the airports you would like to exclude");
                    }
                }
                else if (excludeAirportsInput.charAt(0) == 'N'){
                    finalTrip = this.leastCost(departingAirportCode, destinationAirportCode);
                }
                else{
                    throw new FlyPlannerException("Please re-run the program and enter yes or no for excluding airports.");
                }
                receivedInput = true;
            }
            catch (FlyPlannerException e)
            {
                System.out.println("No journey was found");
                receivedInput = false;
            }
        }

        this.printFinalTrip(finalTrip);
    }

    //Prints all final data for the found trip
    private void printFinalTrip(TripImpl finalTrip) {
        System.out.println(finalTrip);
        System.out.println("Total Hops: " + finalTrip.totalHop());
        System.out.println("Total Trip Cost: £" + finalTrip.totalCost());
        System.out.println("Total Time in the air: " + finalTrip.totalTime() + " minutes");
    }

    //Prints all final data for the found trip
    private void printFinalTripPartA(TripImpl finalTrip) {
        System.out.println(finalTrip);
        System.out.println("Total Trip Cost: £" + finalTrip.totalCost());
        System.out.println("Total Time: " + finalTrip.airTime() + " minutes");
    }


    //Gets the length of the longest airport name in the airports set
    private int longestAirportName(List<AirportImpl> airports){
        String longestString = airports.get(0).toString();
        for(int i=0; i<airports.size(); i++){
            if(airports.get(i).toString().length() > longestString.length()){
                longestString = airports.get(i).toString();
            }
        }
        return longestString.length();
    }
}
