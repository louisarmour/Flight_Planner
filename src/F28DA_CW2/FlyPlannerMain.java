package F28DA_CW2;

import java.io.FileNotFoundException;

public class FlyPlannerMain {

	public static void main(String[] args) {

		// Your implementation should be in FlyPlannerImpl.java, this class is only to
		// run the user interface of your programme.

		FlyPlannerImpl fi;
		fi = new FlyPlannerImpl();
		try {
			fi.populate(new FlightsReader());

			// Implement here your user interface using the methods of Part A. You could
			// optionally expand it to use the methods of Part B.
			fi.gettingUserInput();
		} catch (FileNotFoundException | FlyPlannerException e) {
			e.printStackTrace();
		}

	}

}
