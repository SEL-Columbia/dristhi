import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.LocationTree;
import org.opensrp.connector.openmrs.service.LocationService;

import com.google.gson.Gson;


public class LocationTest {
	String openmrsOpenmrsUrl = "http://46.101.51.199:8080/openmrs/";
	String openmrsUsername = "admin";
	String openmrsPassword = "5rpAdmin";
	LocationService ls;

	@Before
	public void setup(){
		ls = new LocationService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
	}
	
	@Test
	public void testLocation() throws JSONException {
		Location l = ls.getLocation("testloc4");
		System.out.println(l);
	}
	
	@Test
	public void testLocationTree() throws JSONException {
		LocationTree l = ls.getLocationTree();
		String tj = new Gson().toJson(l, LocationTree.class);
		System.out.println(tj);
		LocationTree l2 = new Gson().fromJson(tj, LocationTree.class);
		String tj1 = new Gson().toJson(l2, LocationTree.class);
		System.out.println(tj1);
	}
	
	@Test
	public void testLocationTreeOf() throws JSONException {
		System.out.println("/////////////////////testloc");
		LocationTree l = ls.getLocationTreeOf("testloc");
		String tj = new Gson().toJson(l, LocationTree.class);
		System.out.println(tj);
		LocationTree l2 = new Gson().fromJson(tj, LocationTree.class);
		String tj1 = new Gson().toJson(l2, LocationTree.class);
		System.out.println(tj1);
	}
}
