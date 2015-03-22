import org.json.JSONException;
import org.junit.Test;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.LocationTree;
import org.opensrp.connector.openmrs.LocationService;


public class LocationTest {
	String openmrsOpenmrsUrl = "http://46.101.51.199:8080/openmrs/";
	String openmrsUsername = "admin";
	String openmrsPassword = "5rpAdmin";

	@Test
	public void testLocation() throws JSONException {
		LocationService ls = new LocationService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		Location l = ls.getLocation("testloc4");
		System.out.println(l);
	}
	
	@Test
	public void testLocationTree() throws JSONException {
		LocationService ls = new LocationService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		LocationTree l = ls.getLocationTree();
		System.out.println(l);
	}
	
	@Test
	public void testLocationTreeOf() throws JSONException {
		LocationService ls = new LocationService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		LocationTree l = ls.getLocationTreeOf("testloc");
		System.out.println(l);
	}
}
