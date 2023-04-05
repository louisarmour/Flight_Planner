package F28DA_CW2;

import java.util.Set;

public interface AirportB {
	
	Set<AirportImpl> getDicrectlyConnected();

	void setDicrectlyConnected(Set<AirportImpl> dicrectlyConnected);

	void setDicrectlyConnectedOrder(int order);

	int getDirectlyConnectedOrder();


}
