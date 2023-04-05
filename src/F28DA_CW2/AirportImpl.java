package F28DA_CW2;

import java.util.Set;

public class AirportImpl implements AirportA, AirportB {

    private String airportCode; //Used to store the airport code passed in
    private String airportName; //Used to store the airport name passed in
    private String airportCity; //Used to store the airport city passed in
    //Used to store all the airports directly connected
    private Set<AirportImpl> directlyConnected;
    //Used to store the order of a current airport
    private int order;

    /** Class constructor storing parameters passed in to appropriate local variables
     * @param code the code of the current airport
     * @param city the city the current airport is in
     * @param name the name of the current airport
     */
    public AirportImpl(String code, String city, String name)
    {
        this.airportCode = code;
        this.airportCity = city;
        this.airportName = name;
    }

    /** Returns the airport code
     * @return the local variable airportCode
     */
    public String getCode()
    {
        return this.airportCode;
    }

    /** Return the airport name
     * @return the local variable airportName
     */
    public String getName()
    {
        return this.airportName;
    }

    /** Return the current airport city
     * @return tbe local variable airportName
     */
    public String getCity()
    {
        return this.airportCity;
    }

    public void setDicrectlyConnectedOrder(int order)
    {
        this.order = order;
    }

    public int getDirectlyConnectedOrder()
    {
        return this.order;
    }

    public Set<AirportImpl> getDicrectlyConnected()
    {
        return this.directlyConnected;
    }

    public void setDicrectlyConnected(Set<AirportImpl> dicrectlyConnected)
    {
        this.directlyConnected = dicrectlyConnected;
    }

    /** Creates a string combining the airport city and airport code
     * @return the string combining the airport city and airport code
     */
    public String toString() {
        return this.airportCity + " (" + this.airportCode + ")";
    }

}
