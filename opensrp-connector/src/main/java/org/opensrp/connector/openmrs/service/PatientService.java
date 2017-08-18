
package org.opensrp.connector.openmrs.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.common.util.HttpResponse;
import org.opensrp.common.util.HttpUtil;
import org.opensrp.connector.MultipartUtility;
import org.opensrp.connector.openmrs.schedule.OpenmrsSyncerListener;
import org.opensrp.domain.Address;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.domain.Multimedia;
import org.opensrp.domain.Obs;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;

@Service
public class PatientService extends OpenmrsService {
	
	private static Logger logger = LoggerFactory.getLogger(OpenmrsSyncerListener.class.toString());
	
	//TODO include everything for patient registration. i.e. person, person name, patient identifier
	// include get for patient on different params like name, identifier, location, uuid, attribute,etc
	//person methods should be separate
	private static final String PERSON_URL = "ws/rest/v1/person";
	
	private static final String PATIENT_URL = "ws/rest/v1/patient";
	
	private static final String PATIENT_IMAGE_URL = "ws/rest/v1/patientimage/uploadimage";
	
	private static final String PATIENT_IDENTIFIER_URL = "identifier";
	
	private static final String PERSON_ATTRIBUTE_URL = "attribute";
	
	private static final String PERSON_ATTRIBUTE_TYPE_URL = "ws/rest/v1/personattributetype";
	
	private static final String PATIENT_IDENTIFIER_TYPE_URL = "ws/rest/v1/patientidentifiertype";
	
	private static final String PATIENT_CREATE_RELATIONSHIP_URL = "ws/rest/v1/relationship";
	
	// This ID should start with opensrp and end with uid. As matched by atomefeed module`s patient service
	public static final String OPENSRP_IDENTIFIER_TYPE = "OpenSRP Thrive UID";
	
	public static final String OPENSRP_IDENTIFIER_TYPE_MATCHER = "(?i)opensrp.*uid";
	
	public static final String OPENMRS_UUID_IDENTIFIER_TYPE = "OPENMRS_UUID";
	
	private ClientService clientService;
	
	private EventService eventService;
	
	private OpenmrsLocationService openmrsLocationService;
	
	public PatientService() {
	}
	
	@Autowired
	public PatientService(ClientService clientService, OpenmrsLocationService openmrsLocationService,
	    EventService eventService) {
		this.clientService = clientService;
		this.openmrsLocationService = openmrsLocationService;
		this.eventService = eventService;
	}
	
	public PatientService(String openmrsUrl, String user, String password) {
		super(openmrsUrl, user, password);
	}
	
	public JSONObject getPatientByIdentifier(String identifier) throws JSONException {
		JSONArray p = new JSONObject(HttpUtil
		        .get(getURL() + "/" + PATIENT_URL, "v=full&identifier=" + identifier, OPENMRS_USER, OPENMRS_PWD).body())
		                .getJSONArray("results");
		return p.length() > 0 ? p.getJSONObject(0) : null;
	}
	
	public JSONObject getPatientByUuid(String uuid, boolean noRepresentationTag) throws JSONException {
		return new JSONObject(HttpUtil.get(getURL() + "/" + PATIENT_URL + "/" + uuid, noRepresentationTag ? "" : "v=full",
		    OPENMRS_USER, OPENMRS_PWD).body());
	}
	
	public JSONObject getIdentifierType(String identifierType) throws JSONException {
		// we have to use this ugly approach because identifier not found throws exception and 
		// its hard to find whether it was network error or object not found or server error
		JSONArray res = new JSONObject(
		        HttpUtil.get(getURL() + "/" + PATIENT_IDENTIFIER_TYPE_URL, "v=full", OPENMRS_USER, OPENMRS_PWD).body())
		                .getJSONArray("results");
		for (int i = 0; i < res.length(); i++) {
			if (res.getJSONObject(i).getString("display").equalsIgnoreCase(identifierType)) {
				return res.getJSONObject(i);
			}
		}
		return null;
	}
	
	public JSONObject createIdentifierType(String name, String description) throws JSONException {
		JSONObject o = convertIdentifierToOpenmrsJson(name, description);
		return new JSONObject(HttpUtil
		        .post(getURL() + "/" + PATIENT_IDENTIFIER_TYPE_URL, "", o.toString(), OPENMRS_USER, OPENMRS_PWD).body());
	}
	
	public JSONObject createPatientRelationShip(String personB, String personA, String relationshipType)
	    throws JSONException {
		JSONObject o = convertRaleationsShipToOpenmrsJson(personB, personA, relationshipType);
		return new JSONObject(HttpUtil
		        .post(getURL() + "/" + PATIENT_CREATE_RELATIONSHIP_URL, "", o.toString(), OPENMRS_USER, OPENMRS_PWD).body());
	}
	
	public JSONObject convertIdentifierToOpenmrsJson(String name, String description) throws JSONException {
		JSONObject a = new JSONObject();
		a.put("name", name);
		a.put("description", description);
		return a;
	}
	
	public JSONObject convertRaleationsShipToOpenmrsJson(String personB, String personA, String relationshipType)
	    throws JSONException {
		JSONObject relation = new JSONObject();
		relation.put("personB", personB);
		relation.put("personA", personA);
		relation.put("relationshipType", relationshipType);
		return relation;
	}
	
	public void createRealationShip(List<Client> cl) throws JSONException {
		for (Client c : cl) {
			if (!c.getRelationships().isEmpty() && c.getRelationships().containsKey("mother")) {
				String motherBaseId = c.getRelationships().get("mother").get(0).toString();
				JSONObject motherJson = getPatientByIdentifier(motherBaseId);
				JSONObject person = motherJson.getJSONObject("person");
				if (person.getString("uuid") != null) {
					createPatientRelationShip(c.getIdentifier("OPENMRS_UUID"), person.getString("uuid"),
					    "8d91a210-c2cc-11de-8d13-0010c6dffd0f");
					logger.info("RelationshipsCreated check openrs" + c.getIdentifier("OPENMRS_UUID"));
				}
				List<Client> siblings = clientService.findByRelationship(motherBaseId);
				if (!siblings.isEmpty() || siblings != null) {
					JSONObject siblingJson;
					JSONObject sibling;
					for (Client client : siblings) {
						if (!c.getBaseEntityId().equals(client.getBaseEntityId())) {
							siblingJson = getPatientByIdentifier(client.getBaseEntityId());
							sibling = siblingJson.getJSONObject("person");
							createPatientRelationShip(c.getIdentifier("OPENMRS_UUID"), sibling.getString("uuid"),
							    "8d91a01c-c2cc-11de-8d13-0010c6dffd0f");
						}
						
					}
				}
			}
		}
	}
	
	public JSONObject getPersonAttributeType(String attributeName) throws JSONException {
		JSONArray p = new JSONObject(HttpUtil
		        .get(getURL() + "/" + PERSON_ATTRIBUTE_TYPE_URL, "v=full&q=" + attributeName, OPENMRS_USER, OPENMRS_PWD)
		        .body()).getJSONArray("results");
		return p.length() > 0 ? p.getJSONObject(0) : null;
	}
	
	public JSONObject createPerson(Client be) throws JSONException {
		JSONObject per = convertBaseEntityToOpenmrsJson(be);
		logger.info("PERSON TO CREATE " + per.toString());
		String response = HttpUtil.post(getURL() + "/" + PERSON_URL, "", per.toString(), OPENMRS_USER, OPENMRS_PWD).body();
		return new JSONObject(response);
	}
	
	public JSONObject convertBaseEntityToOpenmrsJson(Client be) throws JSONException {
		JSONObject per = new JSONObject();
		per.put("gender", be.getGender());
		per.put("birthdate", OPENMRS_DATE.format(be.getBirthdate().toDate()));
		per.put("birthdateEstimated", be.getBirthdateApprox());
		if (be.getDeathdate() != null) {
			per.put("deathDate", OPENMRS_DATE.format(be.getDeathdate().toDate()));
		}
		
		String fn = be.getFirstName() == null || be.getFirstName().isEmpty() ? "-" : be.getFirstName();
		if (!fn.equals("-")) {
			fn = fn.replaceAll("[^A-Za-z0-9\\s]+", "");
		}
		
		String mn = be.getMiddleName() == null ? "" : be.getMiddleName();
		
		if (!mn.equals("-")) {
			mn = mn.replaceAll("[^A-Za-z0-9\\s]+", "");
		}
		
		String ln = (be.getLastName() == null || be.getLastName().equals(".")) ? "-" : be.getLastName();
		if (!ln.equals("-")) {
			ln = ln.replaceAll("[^A-Za-z0-9\\s]+", "");
		}
		
		per.put("names",
		    new JSONArray("[{\"givenName\":\"" + fn + "\",\"middleName\":\"" + mn + "\", \"familyName\":\"" + ln + "\"}]"));
		per.put("attributes", convertAttributesToOpenmrsJson(be.getAttributes()));
		per.put("addresses", convertAddressesToOpenmrsJson(be));
		return per;
	}
	
	public JSONArray convertAttributesToOpenmrsJson(Map<String, Object> attributes) throws JSONException {
		if (CollectionUtils.isEmpty(attributes)) {
			return null;
		}
		JSONArray attrs = new JSONArray();
		for (Entry<String, Object> at : attributes.entrySet()) {
			JSONObject a = new JSONObject();
			a.put("attributeType", getPersonAttributeType(at.getKey()).getString("uuid"));
			a.put("value", at.getValue());
			attrs.put(a);
		}
		
		return attrs;
	}
	
	public JSONArray convertAddressesToOpenmrsJson(Client client) throws JSONException {
		List<Address> adl = client.getAddresses();
		if (CollectionUtils.isEmpty(adl)) {
			return null;
		}
		JSONArray jaar = new JSONArray();
		for (Address ad : adl) {
			JSONObject jao = new JSONObject();
			if (ad.getAddressFields() != null) {
				jao.put("address1",
				    ad.getAddressFieldMatchingRegex("(?i)(ADDRESS1|HOUSE_NUMBER|HOUSE|HOUSE_NO|UNIT|UNIT_NUMBER|UNIT_NO)"));
				jao.put("address2", ad.getAddressFieldMatchingRegex("(?i)(ADDRESS2|STREET|STREET_NUMBER|STREET_NO|LANE)"));
				String address3 = ad.getAddressFieldMatchingRegex("(?i)(ADDRESS3|SECTOR|AREA)");
				if (!address3.equals("Other") && address3 != null && !StringUtils.isEmptyOrWhitespaceOnly(address3)) {
					address3 = openmrsLocationService.getLocation(address3).getName();
				}
				jao.put("address3", address3);
				jao.put("address5", ad.getAddressFieldMatchingRegex("(?i)(ADDRESS5|OTHER_RESIDENTIAL_AREA)"));
				
				List<Event> registrationEvents = eventService.findByBaseEntityId(client.getBaseEntityId());
				for (Event event : registrationEvents) {
					if (event.getEventType().equals("Birth Registration")) {
						
						List<Obs> obs = event.getObs();
						for (Obs obs2 : obs) {
							
							if (obs2 != null && obs2.getFieldType().equals("formsubmissionField")
							        && obs2.getFormSubmissionField().equals("Home_Facility") && obs2.getValue() != null) {
								
								ArrayList<String> locationsHierarchy = openmrsLocationService.getLocationsHierarchy(
								    new Gson().toJson(openmrsLocationService.getLocationTreeOf(obs2.getValue().toString())));
								
								jao.put("address4",
								    openmrsLocationService.getLocation(obs2.getValue().toString()).getName());
								jao.put("countyDistrict", locationsHierarchy.get(0));
								jao.put("stateProvince", locationsHierarchy.get(1));
								jao.put("country", locationsHierarchy.get(2));
								
								break;
							}
						}
						
					}
				}
				
				jao.put("cityVillage", ad.getCityVillage());
				
			}
			jao.put("address6", ad.getAddressType());
			jao.put("postalCode", ad.getPostalCode());
			jao.put("latitude", ad.getLatitude());
			jao.put("longitude", ad.getLongitude());
			if (ad.getStartDate() != null) {
				jao.put("startDate", OPENMRS_DATE.format(ad.getStartDate().toDate()));
			}
			if (ad.getEndDate() != null) {
				jao.put("endDate", OPENMRS_DATE.format(ad.getEndDate().toDate()));
			}
			
			jaar.put(jao);
		}
		
		return jaar;
	}
	
	public JSONObject createPatient(Client c) throws JSONException {
		JSONObject p = new JSONObject();
		
		p.put("person", createPerson(c).getString("uuid"));
		JSONArray ids = new JSONArray();
		if (c.getIdentifiers() != null) {
			for (Entry<String, String> id : c.getIdentifiers().entrySet()) {
				JSONObject jio = new JSONObject();
				JSONObject idobj = getIdentifierType(id.getKey());
				if (idobj == null) {
					idobj = createIdentifierType(id.getKey(), id.getKey() + " - FOR THRIVE OPENSRP");
				}
				jio.put("identifierType", idobj.getString("uuid"));
				jio.put("identifier", id.getValue());
				Object cloc = c.getAttribute("Location");
				jio.put("location", cloc == null ? "Unknown Location" : cloc);
				//jio.put("preferred", true);
				
				ids.put(jio);
			}
		}
		
		JSONObject jio = new JSONObject();
		JSONObject ido = getIdentifierType(OPENSRP_IDENTIFIER_TYPE);
		if (ido == null) {
			ido = createIdentifierType(OPENSRP_IDENTIFIER_TYPE, OPENSRP_IDENTIFIER_TYPE + " - FOR THRIVE OPENSRP");
		}
		jio.put("identifierType", ido.getString("uuid"));
		jio.put("identifier", c.getBaseEntityId());
		Object cloc = c.getAttribute("Location");
		jio.put("location", cloc == null ? "Unknown Location" : cloc);
		jio.put("preferred", true);
		
		ids.put(jio);
		
		p.put("identifiers", ids);
		String response = HttpUtil.post(getURL() + "/" + PATIENT_URL, "", p.toString(), OPENMRS_USER, OPENMRS_PWD).body();
		return new JSONObject(response);
	}
	
	public JSONObject updatePatient(Client c, String uuid) throws JSONException {
		JSONObject p = new JSONObject();
		p.put("person", convertBaseEntityToOpenmrsJson(c));
		JSONArray ids = new JSONArray();
		if (c.getIdentifiers() != null) {
			for (Entry<String, String> id : c.getIdentifiers().entrySet()) {
				JSONObject jio = new JSONObject();
				JSONObject idobj = getIdentifierType(id.getKey());
				if (idobj == null) {
					idobj = createIdentifierType(id.getKey(), id.getKey() + " - FOR THRIVE OPENSRP");
				}
				jio.put("identifierType", idobj.getString("uuid"));
				jio.put("identifier", id.getValue());
				Object cloc = c.getAttribute("Location");
				jio.put("location", cloc == null ? "Unknown Location" : cloc);
				//jio.put("preferred", true);
				
				ids.put(jio);
			}
		}
		
		JSONObject jio = new JSONObject();
		JSONObject ido = getIdentifierType(OPENSRP_IDENTIFIER_TYPE);
		if (ido == null) {
			ido = createIdentifierType(OPENSRP_IDENTIFIER_TYPE, OPENSRP_IDENTIFIER_TYPE + " - FOR THRIVE OPENSRP");
		}
		jio.put("identifierType", ido.getString("uuid"));
		jio.put("identifier", c.getBaseEntityId());
		Object cloc = c.getAttribute("Location");
		jio.put("location", cloc == null ? "Unknown Location" : cloc);
		jio.put("preferred", true);
		
		ids.put(jio);
		
		p.put("identifiers", ids);
		return new JSONObject(HttpUtil
		        .post(getURL() + "/" + PATIENT_URL + "/" + uuid, "", p.toString(), OPENMRS_USER, OPENMRS_PWD).body());
	}
	
	public JSONObject addThriveId(String baseEntityId, JSONObject patient) throws JSONException {
		JSONObject jio = new JSONObject();
		JSONObject ido = getIdentifierType(OPENSRP_IDENTIFIER_TYPE);
		if (ido == null) {
			ido = createIdentifierType(OPENSRP_IDENTIFIER_TYPE, OPENSRP_IDENTIFIER_TYPE + " - FOR THRIVE OPENSRP");
		}
		jio.put("identifierType", ido.getString("uuid"));
		jio.put("identifier", baseEntityId);
		jio.put("location", "Unknown Location");
		jio.put("preferred", true);
		
		return new JSONObject(
		        HttpUtil.post(getURL() + "/" + PATIENT_URL + "/" + patient.getString("uuid") + "/" + PATIENT_IDENTIFIER_URL,
		            "", jio.toString(), OPENMRS_USER, OPENMRS_PWD).body());
	}
	
	public Client convertToClient(JSONObject patient) throws JSONException {
		Client c = new Client(null);
		JSONArray ar = patient.getJSONArray("identifiers");
		for (int i = 0; i < ar.length(); i++) {
			JSONObject ji = ar.getJSONObject(i);
			if (ji.getJSONObject("identifierType").getString("display").equalsIgnoreCase(OPENSRP_IDENTIFIER_TYPE)) {
				c.setBaseEntityId(ji.getString("identifier"));
			} else {
				c.addIdentifier(ji.getJSONObject("identifierType").getString("display"), ji.getString("identifier"));
			}
		}
		
		c.addIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, patient.getString("uuid"));
		
		JSONObject pr = patient.getJSONObject("person");
		
		String mn = pr.getJSONObject("preferredName").has("middleName")
		        ? pr.getJSONObject("preferredName").getString("middleName") : null;
		DateTime dd = pr.has("deathDate") && !pr.getString("deathDate").equalsIgnoreCase("null")
		        ? new DateTime(pr.getString("deathDate")) : null;
		c.withFirstName(pr.getJSONObject("preferredName").getString("givenName")).withMiddleName(mn)
		        .withLastName(pr.getJSONObject("preferredName").getString("familyName")).withGender(pr.getString("gender"))
		        .withBirthdate(new DateTime(pr.getString("birthdate")), pr.getBoolean("birthdateEstimated"))
		        .withDeathdate(dd, false);
		
		if (pr.has("attributes")) {
			for (int i = 0; i < pr.getJSONArray("attributes").length(); i++) {
				JSONObject at = pr.getJSONArray("attributes").getJSONObject(i);
				if (at.optJSONObject("value") == null) {
					c.addAttribute(at.getJSONObject("attributeType").getString("display"), at.getString("value"));
				} else {
					c.addAttribute(at.getJSONObject("attributeType").getString("display"),
					    at.getJSONObject("value").getString("display"));
				}
			}
		}
		
		if (pr.has("addresses")) {
			for (int i = 0; i < pr.getJSONArray("addresses").length(); i++) {
				JSONObject ad = pr.getJSONArray("addresses").getJSONObject(i);
				DateTime startDate = ad.has("startDate") && !ad.getString("startDate").equalsIgnoreCase("null")
				        ? new DateTime(ad.getString("startDate")) : null;
				DateTime endDate = ad.has("startDate") && !ad.getString("endDate").equalsIgnoreCase("null")
				        ? new DateTime(ad.getString("endDate")) : null;
				;
				Address a = new Address(ad.getString("address6"), startDate, endDate, null, ad.getString("latitude"),
				        ad.getString("longitude"), ad.getString("postalCode"), ad.getString("stateProvince"),
				        ad.getString("country"));
				//a.setGeopoint(geopoint);
				a.setSubTown(ad.getString("address2"));//TODO
				a.setTown(ad.getString("address3"));
				a.setSubDistrict(ad.getString("address4"));
				a.setCountyDistrict(ad.getString("countyDistrict"));
				a.setCityVillage(ad.getString("cityVillage"));
				
				c.addAddress(a);
			}
			
		}
		return c;
	}
	
	public void patientImageUpload(Multimedia multimedia) throws IOException {
		//String requestURL =  "http://46.101.51.199:8080/openmrs/ws/rest/v1/patientimage/uploadimage";
		
		try {
			File convFile = new File("/opt" + multimedia.getFilePath());
			MultipartUtility multipart = new MultipartUtility(getURL() + "/" + PATIENT_IMAGE_URL, OPENMRS_USER, OPENMRS_PWD);
			multipart.addFormField("patientidentifier", multimedia.getCaseId());
			multipart.addFormField("category", multimedia.getFileCategory());
			multipart.addFilePart("file", convFile);
			
			List<String> response = multipart.finish();
			
			System.out.println("SERVER REPLIED:");
			
			for (String line : response) {
				System.out.println(line);
			}
		}
		catch (IOException ex) {
			System.err.println(ex);
		}
	}
	
	public JSONObject updatePersonAsDeceased(Event deathEvent) throws JSONException {
		JSONObject patientObject = getPatientByIdentifier(deathEvent.getBaseEntityId());
		JSONObject requestBody = new JSONObject();
		
		String patientUUID = patientObject.getString("uuid");
		
		requestBody.put("dead", true);
		requestBody.put("deathDate", OPENMRS_DATE.format(deathEvent.getEventDate().toDate()));
		requestBody.put("causeOfDeath", PROBABLE_CAUSE_PARENT_CONCEPT);
		
		HttpResponse op = HttpUtil.post(HttpUtil.removeEndingSlash(OPENMRS_BASE_URL) + "/" + PERSON_URL + "/" + patientUUID,
		    "", requestBody.toString(), OPENMRS_USER, OPENMRS_PWD);
		
		return new JSONObject(op.body());
		
	}
}
