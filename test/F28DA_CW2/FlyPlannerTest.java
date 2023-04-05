package F28DA_CW2;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class FlyPlannerTest {

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

	// Add your own tests here!
	//
	// You can get inspiration from the tests in FlyPlannerProvidedTest
	// that uses the provided data set but also from the
	// leastCostCustomTest test that uses a custom-made graph
	@Test
	public void betterConnectedInOrderDXBTest() {
		fi.setDirectlyConnected();
		fi.setDirectlyConnectedOrder();
		AirportImpl lhr = fi.airport("LHR");
		Set<AirportImpl> betterConnected = fi.getBetterConnectedInOrder(lhr);
		assertEquals(8, betterConnected.size());
	}

	@Test
	public void leastCostTest() {
		try {
			TripImpl i = fi.leastCost("EDI", "DAL");
			assertEquals(3, i.totalHop());
			assertEquals(504, i.totalCost());
		} catch (FlyPlannerException e) {
			fail();
		}
	}

	@Test
	public void directlyConnectedTest() {
		AirportImpl lhr = fi.airport("EDI");
		Set<AirportImpl> s = fi.directlyConnected(lhr);
		System.out.println("s " + s);
		assertEquals(9, s.size());
	}

	@Test
	public void leastTimeMeetUpTest() {
		try {
			System.out.println("This takes about 15 minutes to run.l");
			String meetUp = fi.leastTimeMeetUp("EDI", "DAL", "0930");
			assertEquals("CMH", meetUp);
		} catch (FlyPlannerException e) {
			fail();
		}
	}

}
