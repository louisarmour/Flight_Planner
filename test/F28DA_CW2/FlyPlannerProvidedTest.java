package F28DA_CW2;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class FlyPlannerProvidedTest {

	FlyPlannerImpl fi;

	@Before
	public void initialize() {
		fi = new FlyPlannerImpl();
		try {
			fi.populate(new FlightsReader());
		} catch (FileNotFoundException | FlyPlannerException e) {
			e.printStackTrace();
		}
	}

	// Example test cases for Part A

	@Test
	public void leastCostTest() {
		try {
			TripImpl i = fi.leastCost("EDI", "DXB");
			assertEquals(3, i.totalHop());
			assertEquals(374, i.totalCost());
		} catch (FlyPlannerException e) {
			fail();
		}
	}

	@Test
	public void leastHopTest() {
		try {
			TripImpl i = fi.leastHop("EDI", "DXB");
			assertEquals(2, i.totalHop());
		} catch (FlyPlannerException e) {
			fail();
		}
	}

	@Test
	public void leastCostExclTest() {
		try {
			LinkedList<String> exclude = new LinkedList<String>();
			exclude.add("LHR");
			exclude.add("PRG");
			exclude.add("LGW");
			exclude.add("LCY");
			exclude.add("DUS");
			exclude.add("FRA");
			exclude.add("WAW");
			exclude.add("AMS");
			exclude.add("CDG");
			exclude.add("IST");
			exclude.add("GLA");
			exclude.add("CWL");
			exclude.add("EWR");
			exclude.add("BOS");
			TripImpl i = fi.leastCost("DXB", "EDI", exclude);
			System.out.println(i.getStops());
			fail();
		} catch (FlyPlannerException e) {
			assertTrue(true);
		}
	}

	// Example test cases for Part B

	@Test
	public void directlyConnectedTest() {
		AirportImpl lhr = fi.airport("LHR");
		Set<AirportImpl> s = fi.directlyConnected(lhr);
		System.out.println("s " + s);
		assertEquals(138, s.size());
	}

	@Test
	public void setDirectlyConnectedTest() {
		int sum = fi.setDirectlyConnected();
		assertEquals(9436, sum);
	}

	@Test
	public void betterConnectedInOrderEDITest() {
		fi.setDirectlyConnected();
		fi.setDirectlyConnectedOrder();
		AirportImpl edi = fi.airport("EDI");
		Set<AirportImpl> betterConnected = fi.getBetterConnectedInOrder(edi);
		assertEquals(30, betterConnected.size());
	}

	@Test
	public void betterConnectedInOrderDXBTest() {
		fi.setDirectlyConnected();
		fi.setDirectlyConnectedOrder();
		AirportImpl dxb = fi.airport("DXB");
		Set<AirportImpl> betterConnected = fi.getBetterConnectedInOrder(dxb);
		assertEquals(11, betterConnected.size());
	}

	@Test
	public void leastCostTimeTest() {
		try {
			TripImpl i = fi.leastCost("EDI", "DXB");
			System.out.println("airTime " + i.airTime());
			assertEquals(436, i.airTime());
			assertEquals(1495, i.connectingTime());
			assertEquals(1931, i.totalTime());
		} catch (FlyPlannerException e) {
			fail();
		}
	}

	@Test
	public void leastCostMeetUpTest() {
		try {
			String meetUp = fi.leastCostMeetUp("EDI", "DXB");
			assertEquals("IST", meetUp);
		} catch (FlyPlannerException e) {
			fail();
		}
	}

	@Test
	public void leastTimeMeetUpTest() {
		try {
			String meetUp = fi.leastTimeMeetUp("NCL", "NTL", "0600");
			assertEquals("CDG", meetUp);
		} catch (FlyPlannerException e) {
			fail();
		}
	}
	
	// Example of advanced test case using a custom graph
	@Test
	public void leastCostCustomTest() {
		FlyPlannerImpl fp = new FlyPlannerImpl();
		HashSet<String[]> airports = new HashSet<String[]>();
		String[] a1= {"A1","City1","AirportName1"}; airports.add(a1);
		String[] a2= {"A2","City2","AirportName2"}; airports.add(a2);
		String[] a3= {"A3","City2","AirportName3"}; airports.add(a3);
		HashSet<String[]>  flights = new HashSet<String[]>();
		String[] f1= {"F1","A1","1000","A2","1100","500"}; flights.add(f1);
		String[] f2= {"F2","A1","1000","A3","1100","50"}; flights.add(f2);
		String[] f3= {"F3","A3","1000","A2","1100","50"}; flights.add(f3);
		fp.populate(airports, flights);
		try {
			System.out.println(fp);
			TripImpl lc = fp.leastCost("A1", "A2");
			assertEquals(100,lc.totalCost());
		} catch (FlyPlannerException e) {
			e.printStackTrace();
			fail();
		}
	}
}
