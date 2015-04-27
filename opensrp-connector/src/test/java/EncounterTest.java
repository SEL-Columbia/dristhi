import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.api.domain.Client;
import org.opensrp.api.domain.Event;
import org.opensrp.connector.FormAttributeMapper;
import org.opensrp.connector.OpenmrsConnector;
import org.opensrp.connector.openmrs.service.EncounterService;
import org.opensrp.connector.openmrs.service.PatientService;
import org.opensrp.connector.openmrs.service.UserService;
import org.opensrp.form.domain.FormSubmission;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import com.google.gson.Gson;


public class EncounterTest extends TestResourceLoader{
	public EncounterTest() throws IOException {
		super();
	}

	EncounterService s;
	OpenmrsConnector oc;
	PatientService ps;
	UserService us;

	@Before
	public void setup() throws IOException{
		ps = new PatientService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		us = new UserService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		s = new EncounterService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		s.setPatientService(ps);
		s.setUserService(us);
		FormAttributeMapper fam = new FormAttributeMapper(formDirPath);
		oc = new OpenmrsConnector(s, ps, null, null, fam);
	}
	
	@Test
	public void testEncounter() throws JSONException, ParseException, IOException {
		FormSubmission fs = getFormSubmissionFor("basic_reg");
		
		JSONObject p = ps.getPatientByIdentifier(fs.entityId());
		if(p == null){
			Client c = oc.getClientFromFormSubmission(fs);
			//System.out.println(ps.createPatient(c));
		}
		Event e = oc.getEventFromFormSubmission(fs);
		
		//System.out.println(s.createEncounter(e));
	}
	
	@Test
	public void shouldMapAddressWithClient() throws IOException, ParseException, JSONException{
		String field = "birthplace_street";

		FormSubmission fs = getFormSubmissionFor("basic_reg");

		System.out.println(oc.isOpenmrsForm(fs));
		
		JSONObject p = ps.getPatientByIdentifier(fs.entityId());
		if(p == null){
			Client c = oc.getClientFromFormSubmission(fs);
			//System.out.println(ps.createPatient(c));
		}
		Event e = oc.getEventFromFormSubmission(fs);
		
		//System.out.println(s.createEncounter(e));
	}
	
}
