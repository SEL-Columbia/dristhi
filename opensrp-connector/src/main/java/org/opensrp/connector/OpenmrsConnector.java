package org.opensrp.connector;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensrp.api.domain.Event;
import org.opensrp.api.domain.Obs;
import org.opensrp.connector.openmrs.service.EncounterService;
import org.opensrp.connector.openmrs.service.LocationService;
import org.opensrp.connector.openmrs.service.OpenmrsService;
import org.opensrp.connector.openmrs.service.PatientService;
import org.opensrp.connector.openmrs.service.UserService;
import org.opensrp.form.domain.FormField;
import org.opensrp.form.domain.FormSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenmrsConnector {

	private EncounterService encounterService;
	//private HouseholdService householdService; 
	private PatientService patientService;
	private LocationService locationService;
	private UserService userService;
	private FormAttributeMapper formAttributeMapper;
		
	@Autowired
	public OpenmrsConnector(EncounterService encounterService,
			/*HouseholdService householdService,*/ PatientService patientService,
			LocationService locationService, UserService userService, FormAttributeMapper formAttributeMapper) {
		this.encounterService = encounterService;
		//this.householdService = householdService;
		this.patientService = patientService;
		this.locationService = locationService;
		this.userService = userService;
		this.formAttributeMapper = formAttributeMapper;
	}
	
	public Event convertFormSubmissionToEvent(FormSubmission fs) throws ParseException {
		Map<String, String> m = new HashMap<String, String>();
		m.put("openmrs_entity" , "encounter");
		m.put("openmrs_entity_id" , "encounter_date");
		String encounterDateField = formAttributeMapper.getFieldName(m , fs);

		m.clear();
		List<String> a = new ArrayList<>();
		a .add("openmrsForm");
		String eventType = formAttributeMapper.getUniqueAttributeValue(a , fs).get("openmrsForm");
		Event e = new Event()
			.withBaseEntityId(fs.entityId())
			.withEventDate(OpenmrsService.OPENMRS_DATE.parse(fs.getField(encounterDateField)))
			.withEventType(eventType)
			.withLocationId("testloc")
			.withProviderId(fs.anmId())
			;
		
		for (FormField fl : fs.instance().form().fields()) {
			Map<String, String> att = formAttributeMapper.getAttributesForField(fl.name(), fs);
			if(att.size()>0 && att.get("openmrs_entity").equalsIgnoreCase("concept"))
				e.addObs(new Obs("concept", att.get("openmrs_entity_id"), 
						att.get("openmrs_entity_parent"), fl.value(), null, null));
		}
		return e;
	}
}
