import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.connector.openmrs.service.PatientService;


public class PatientTest {
	String openmrsOpenmrsUrl = "http://46.101.51.199:8080/openmrs/";
	String openmrsUsername = "admin";
	String openmrsPassword = "5rpAdmin";
	PatientService s;

	@Before
	public void setup(){
		s = new PatientService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
	}
	
	@Test
	public void testLocation() throws JSONException {
		String l = s.getPatientByIdentifier("abcd").toString();
		System.out.println(l);
	}
}
