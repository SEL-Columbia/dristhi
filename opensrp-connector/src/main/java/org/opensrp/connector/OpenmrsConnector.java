package org.opensrp.connector;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensrp.api.domain.Address;
import org.opensrp.api.domain.BaseEntity;
import org.opensrp.api.domain.Client;
import org.opensrp.api.domain.Event;
import org.opensrp.api.domain.Obs;
import org.opensrp.common.util.DateUtil;
import org.opensrp.connector.openmrs.service.EncounterService;
import org.opensrp.connector.openmrs.service.LocationService;
import org.opensrp.connector.openmrs.service.OpenmrsService;
import org.opensrp.connector.openmrs.service.PatientService;
import org.opensrp.connector.openmrs.service.UserService;
import org.opensrp.form.domain.FormField;
import org.opensrp.form.domain.FormSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.StringUtils;

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
	
	public boolean isOpenmrsForm(FormSubmission fs) {
		List<String> a = new ArrayList<>();
		a .add("encounter_type");
		String eventType = formAttributeMapper.getUniqueAttributeValue(a , fs).get("encounter_type");
		return !StringUtils.isEmptyOrWhitespaceOnly(eventType);
	}
	
	public Event getEventFromFormSubmission(FormSubmission fs) throws ParseException {
		String encounterDateField = getFieldName("encounter", "encounter_date", fs);
		String encounterLocation = getFieldName("encounter", "location_id", fs);
		
		//TODO
		String encounterStart = getFieldName("encounter", "encounter_start", fs);
		String encounterEnd = getFieldName("encounter", "encounter_end", fs);
		
		List<String> a = new ArrayList<>();
		a .add("encounter_type");
		String eventType = formAttributeMapper.getUniqueAttributeValue(a , fs).get("encounter_type");
		Event e = new Event()
			.withBaseEntityId(fs.entityId())
			.withEventDate(OpenmrsService.OPENMRS_DATE.parse(fs.getField(encounterDateField)))
			.withEventType(eventType)
			.withLocationId(fs.getField(encounterLocation))
			.withProviderId(fs.anmId())
			;
		
		for (FormField fl : fs.instance().form().fields()) {
			Map<String, String> att = formAttributeMapper.getAttributesForField(fl.name(), fs);
			if(att.size()>0 && att.get("openmrs_entity").equalsIgnoreCase("concept")){
				String val = formAttributeMapper.getInstanceAttributesForFormFieldAndValue(fl.name(), fl.value(), fs);
				e.addObs(new Obs("concept", att.get("openmrs_entity_id"), 
						att.get("openmrs_entity_parent"), StringUtils.isEmptyOrWhitespaceOnly(val)?fl.value():val, null, null));
			}
		}
		return e;
	}
	
	public String getFieldName(String entity, String entityId, FormSubmission fs) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("openmrs_entity" , entity);
		m.put("openmrs_entity_id" , entityId);
		return formAttributeMapper.getFieldName(m , fs);
	}
	
	public String getFieldName(String entity, String entityId, String entityParentId, FormSubmission fs) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("openmrs_entity" , entity);
		m.put("openmrs_entity_id" , entityId);
		m.put("openmrs_entity_parent" , entityId);
		return formAttributeMapper.getFieldName(m , fs);
	}
	
	public Client getClientFromFormSubmission(FormSubmission fs) throws ParseException {
		String firstName = fs.getField(getFieldName("person", "first_name", fs));
		String middleName = fs.getField(getFieldName("person", "middle_name", fs));
		String lastName = fs.getField(getFieldName("person", "last_name", fs));
		Date birthdate = OpenmrsService.OPENMRS_DATE.parse(fs.getField(getFieldName("person", "birthdate", fs)));
		String dd = fs.getField(getFieldName("person", "deathdate", fs));
		Date deathdate = dd==null?null:OpenmrsService.OPENMRS_DATE.parse(dd);
		Boolean birthdateApprox = true;
		Boolean deathdateApprox = true;
		String gender = fs.getField(getFieldName("person", "gender", fs));
		
		Map<String, Address> paddr = new HashMap<>();
		for (FormField fl : fs.instance().form().fields()) {
			Map<String, String> att = formAttributeMapper.getAttributesForField(fl.name(), fs);
			if(att.size()>0 && att.get("openmrs_entity").equalsIgnoreCase("person_address")){
				String addressType = att.get("openmrs_entity_parent");
				String addressField = att.get("openmrs_entity_id");
				Address ad = paddr.get(addressType);
				if(ad == null){
					ad = new Address(addressType, null, null, null, null, null, null, null, null);
				}

				if(addressField.equalsIgnoreCase("startDate")||addressField.equalsIgnoreCase("start_date")){
					ad.setStartDate(DateUtil.parseDate(fl.value()));
				}
				else if(addressField.equalsIgnoreCase("endDate")||addressField.equalsIgnoreCase("end_date")){
					ad.setEndDate(DateUtil.parseDate(fl.value()));
				}
				else if(addressField.equalsIgnoreCase("latitude")){
					ad.setLatitude(fl.value());
				}
				else if(addressField.equalsIgnoreCase("longitute")){
					ad.setLongitute(fl.value());
				}
				else if(addressField.equalsIgnoreCase("postalCode")||addressField.equalsIgnoreCase("postal_code")){
					ad.setPostalCode(fl.value());
				}
				else if(addressField.equalsIgnoreCase("state")||addressField.equalsIgnoreCase("state_province")){
					ad.setState(fl.value());
				}
				else if(addressField.equalsIgnoreCase("country")){
					ad.setCountry(fl.value());
				}
				else {
					ad.addAddressField(addressField, fl.value());
				}
				
				paddr.put(addressType, ad);
			}
		}

		List<Address> addresses = new ArrayList<>(paddr.values());
		
		Map<String, Object> pattributes = new HashMap<>();
		for (FormField fl : fs.instance().form().fields()) {
			Map<String, String> att = formAttributeMapper.getAttributesForField(fl.name(), fs);
			if(att.size()>0 && att.get("openmrs_entity").equalsIgnoreCase("person_attribute")){
				pattributes.put(att.get("openmrs_entity_id"), fl.value());
			}
		}
		Map<String, String> pids = new HashMap<>();
		for (FormField fl : fs.instance().form().fields()) {
			Map<String, String> att = formAttributeMapper.getAttributesForField(fl.name(), fs);
			if(att.size()>0 && att.get("openmrs_entity").equalsIgnoreCase("person_identifier")){
				pids.put(att.get("openmrs_entity_id"), fl.value());
			}
		}
		Client c = new Client()
			.withBaseEntity(new BaseEntity(fs.entityId(), firstName, middleName, lastName, birthdate, deathdate, birthdateApprox, deathdateApprox, gender, addresses, pattributes))
			.withIdentifiers(pids);
		return c;
	}
}
