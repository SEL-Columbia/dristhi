package org.opensrp.connector;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.opensrp.api.domain.Address;
import org.opensrp.api.domain.BaseEntity;
import org.opensrp.api.domain.Client;
import org.opensrp.api.domain.Event;
import org.opensrp.api.domain.Obs;
import org.opensrp.common.util.DateUtil;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants.Encounter;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants.OpenmrsEntity;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants.Person;
import org.opensrp.connector.openmrs.service.EncounterService;
import org.opensrp.connector.openmrs.service.OpenmrsLocationService;
import org.opensrp.connector.openmrs.service.OpenmrsService;
import org.opensrp.connector.openmrs.service.PatientService;
import org.opensrp.connector.openmrs.service.OpenmrsUserService;
import org.opensrp.form.domain.FormField;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.domain.SubFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.StringUtils;

@Service
public class OpenmrsConnector {

	private EncounterService encounterService;
	//private HouseholdService householdService; 
	private PatientService patientService;
	private OpenmrsLocationService locationService;
	private OpenmrsUserService userService;
	private FormAttributeMapper formAttributeMapper;
		
	@Autowired
	public OpenmrsConnector(EncounterService encounterService,
			/*HouseholdService householdService,*/ PatientService patientService,
			OpenmrsLocationService locationService, OpenmrsUserService userService, FormAttributeMapper formAttributeMapper) {
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
		String encounterDateField = getFieldName(Encounter.encounter_date, fs);
		String encounterLocation = getFieldName(Encounter.location_id, fs);
		
		//TODO
		String encounterStart = getFieldName(Encounter.encounter_start, fs);
		String encounterEnd = getFieldName(Encounter.encounter_end, fs);
		
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
				String val = formAttributeMapper.getInstanceAttributesForFormFieldAndValue(fl.name(), fl.value(), null, fs);
				e.addObs(new Obs("concept", att.get("openmrs_entity_id"), 
						att.get("openmrs_entity_parent"), StringUtils.isEmptyOrWhitespaceOnly(val)?fl.value():val, null, null));
			}
		}
		return e;
	}
	
	private Event getEventForSubform(FormSubmission fs, String subform, String eventType, Map<String, String> subformInstance) throws ParseException {
		String encounterDateField = getFieldName(Encounter.encounter_date, fs);
		String encounterLocation = getFieldName(Encounter.location_id, fs);
		
		//TODO
		String encounterStart = getFieldName(Encounter.encounter_start, fs);
		String encounterEnd = getFieldName(Encounter.encounter_end, fs);
		
		List<String> a = new ArrayList<>();
		a .add("encounter_type");
		Event e = new Event()
			.withBaseEntityId(fs.entityId())
			.withEventDate(OpenmrsService.OPENMRS_DATE.parse(fs.getField(encounterDateField)))
			.withEventType(eventType)
			.withLocationId(fs.getField(encounterLocation))
			.withProviderId(fs.anmId())
			;
		
		for (Entry<String, String> fl : subformInstance.entrySet()) {
			Map<String, String> att = formAttributeMapper.getAttributesForSubform(subform, fl.getKey(), fs);
			if(att.size()>0 && att.get("openmrs_entity").equalsIgnoreCase("concept")){
				String val = formAttributeMapper.getInstanceAttributesForFormFieldAndValue(fl.getKey(), fl.getValue(), subform, fs);
				e.addObs(new Obs("concept", att.get("openmrs_entity_id"), 
						att.get("openmrs_entity_parent"), StringUtils.isEmptyOrWhitespaceOnly(val)?fl.getValue():val, null, null));
			}
		}
		return e;
	}
	
	public String getFieldName(OpenmrsEntity en, FormSubmission fs) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("openmrs_entity" , en.entity());
		m.put("openmrs_entity_id" , en.entityId());
		return formAttributeMapper.getFieldName(m , fs);
	}
	
	public String getFieldName(OpenmrsEntity en, String subform, FormSubmission fs) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("openmrs_entity" , en.entity());
		m.put("openmrs_entity_id" , en.entityId());
		return formAttributeMapper.getFieldName(m , subform, fs);
	}
	
	public String getFieldName(String entity, String entityId, String entityParentId, FormSubmission fs) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("openmrs_entity" , entity);
		m.put("openmrs_entity_id" , entityId);
		m.put("openmrs_entity_parent" , entityId);
		return formAttributeMapper.getFieldName(m , fs);
	}
	
	public Map<String, Address> extractAddresses(FormSubmission fs, String subform) throws ParseException {
		Map<String, Address> paddr = new HashMap<>();
		for (FormField fl : fs.instance().form().fields()) {
			Map<String, String> att = new HashMap<>();
			if(StringUtils.isEmptyOrWhitespaceOnly(subform)){
				formAttributeMapper.getAttributesForField(fl.name(), fs);
			}
			else {
				formAttributeMapper.getAttributesForSubform(subform, fl.name(), fs);
			}
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
				else if(addressField.equalsIgnoreCase("geopoint")){
					// example geopoint 34.044494 -84.695704 4 76 = lat lon alt prec
					String geopoint = fl.value();
					if(!StringUtils.isEmptyOrWhitespaceOnly(geopoint)){
						String[] g = geopoint.split(" ");
						ad.setLatitude(g[0]);
						ad.setLongitute(g[1]);
						ad.addAddressField(addressField, fl.value());
					}
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
		return paddr;
	}
	
	public Map<String, String> extractIdentifiers(FormSubmission fs, String subform) {
		Map<String, String> pids = new HashMap<>();
		for (FormField fl : fs.instance().form().fields()) {
			Map<String, String> att = new HashMap<>();
			if(StringUtils.isEmptyOrWhitespaceOnly(subform)){
				formAttributeMapper.getAttributesForField(fl.name(), fs);
			}
			else {
				formAttributeMapper.getAttributesForSubform(subform, fl.name(), fs);
			}
			if(att.size()>0 && att.get("openmrs_entity").equalsIgnoreCase("person_identifier")){
				pids.put(att.get("openmrs_entity_id"), fl.value());
			}
		}
		return pids;
	}
	
	public Map<String, Object> extractAttributes(FormSubmission fs, String subform) {
		Map<String, Object> pattributes = new HashMap<>();
		for (FormField fl : fs.instance().form().fields()) {
			Map<String, String> att = new HashMap<>();
			if(StringUtils.isEmptyOrWhitespaceOnly(subform)){
				formAttributeMapper.getAttributesForField(fl.name(), fs);
			}
			else {
				formAttributeMapper.getAttributesForSubform(subform, fl.name(), fs);
			}
			if(att.size()>0 && att.get("openmrs_entity").equalsIgnoreCase("person_attribute")){
				pattributes.put(att.get("openmrs_entity_id"), fl.value());
			}
		}
		return pattributes;
	}
	
	public Client getClientFromFormSubmission(FormSubmission fs) throws ParseException {
		String firstName = fs.getField(getFieldName(Person.first_name, fs));
		String middleName = fs.getField(getFieldName(Person.middle_name, fs));
		String lastName = fs.getField(getFieldName(Person.last_name, fs));
		Date birthdate = OpenmrsService.OPENMRS_DATE.parse(fs.getField(getFieldName(Person.birthdate, fs)));
		String dd = fs.getField(getFieldName(Person.deathdate, fs));
		Date deathdate = dd==null?null:OpenmrsService.OPENMRS_DATE.parse(dd);
		Boolean birthdateApprox = false;
		Boolean deathdateApprox = false;
		String gender = fs.getField(getFieldName(Person.gender, fs));
		
		List<Address> addresses = new ArrayList<>(extractAddresses(fs, null).values());
		
		Client c = new Client()
			.withBaseEntity(new BaseEntity(fs.entityId(), firstName, middleName, lastName, birthdate, deathdate, 
					birthdateApprox, deathdateApprox, gender, addresses, extractAttributes(fs, null)))
			.withIdentifiers(extractIdentifiers(fs, null));
		return c;
	}
	
	public Map<String, Map<String, Object>> getDependentClientsFromFormSubmission(FormSubmission fs) throws ParseException {
		Map<String, Map<String, Object>> map = new HashMap<>();
		for (SubFormData sbf : fs.subForms()) {
			Map<String, String> att = formAttributeMapper.getAttributesForField(sbf.name(), fs);
			if(att.size() > 0 && att.get("openmrs_entity").equalsIgnoreCase("person")){
				Map<String, Object> cne = new HashMap<>();
				for (Map<String, String> sfdata : sbf.instances()) {
					String firstName = sfdata.get(getFieldName(Person.first_name, sbf.name(), fs));
					String middleName = sfdata.get(getFieldName(Person.middle_name, sbf.name(), fs));
					String lastName = sfdata.get(getFieldName(Person.last_name, sbf.name(), fs));
					Date birthdate = OpenmrsService.OPENMRS_DATE.parse(sfdata.get(getFieldName(Person.birthdate, sbf.name(), fs)));
					String dd = sfdata.get(getFieldName(Person.deathdate, sbf.name(), fs));
					Date deathdate = dd==null?null:OpenmrsService.OPENMRS_DATE.parse(dd);
					Boolean birthdateApprox = true;
					Boolean deathdateApprox = true;
					String gender = sfdata.get(getFieldName(Person.gender, sbf.name(), fs));
					
					List<Address> addresses = new ArrayList<>(extractAddresses(fs, sbf.name()).values());
					
					Client c = new Client()
					.withBaseEntity(new BaseEntity(sfdata.get("id"), firstName, middleName, lastName, birthdate, deathdate, 
							birthdateApprox, deathdateApprox, gender, addresses, extractAttributes(fs, sbf.name())))
					.withIdentifiers(extractIdentifiers(fs, sbf.name()));
					
					cne.put("client", c);
					cne.put("event", getEventForSubform(fs, sbf.name(), att.get("openmrs_entity_id"), sfdata));
					
					map.put(c.getBaseEntityId(), cne);
				}
			}
		}
		return map;
	}
}
