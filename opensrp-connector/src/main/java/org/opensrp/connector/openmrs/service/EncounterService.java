package org.opensrp.connector.openmrs.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.common.util.HttpResponse;
import org.opensrp.connector.HttpUtil;
import org.opensrp.domain.Event;
import org.opensrp.domain.Obs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.StringUtils;

@Service
public class EncounterService extends OpenmrsService{
	private static final String ENCOUNTER_URL = "ws/rest/v1/encounter";
	private static final String ENCOUNTER__TYPE_URL = "ws/rest/v1/encountertype";
	private PatientService patientService;
	private OpenmrsUserService userService;

	@Autowired
	public EncounterService(PatientService patientService, OpenmrsUserService userService) {
		this.patientService = patientService;
		this.userService = userService;
	}
	
	public EncounterService(String openmrsUrl, String user, String password) {
    	super(openmrsUrl, user, password);
	}
	
	public PatientService getPatientService() {
		return patientService;
	}

	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	public OpenmrsUserService getUserService() {
		return userService;
	}

	public void setUserService(OpenmrsUserService userService) {
		this.userService = userService;
	}

	public JSONObject getEncounterType(String encounterType) throws JSONException
    {
    	// we have to use this ugly approach because identifier not found throws exception and 
    	// its hard to find whether it was network error or object not found or server error
    	JSONArray res = new JSONObject(HttpUtil.get(getURL()+"/"+ENCOUNTER__TYPE_URL, "v=full", 
    			OPENMRS_USER, OPENMRS_PWD).body()).getJSONArray("results");
    	for (int i = 0; i < res.length(); i++) {
			if(res.getJSONObject(i).getString("display").equalsIgnoreCase(encounterType)){
				return res.getJSONObject(i);
			}
		}
    	return null;
    }
	
    public JSONObject createEncounterType(String name, String description) throws JSONException{
		JSONObject o = convertEncounterToOpenmrsJson(name, description);
		return new JSONObject(HttpUtil.post(getURL()+"/"+ENCOUNTER__TYPE_URL, "", o.toString(), OPENMRS_USER, OPENMRS_PWD).body());
	}
    
    public JSONObject convertEncounterToOpenmrsJson(String name, String description) throws JSONException {
		JSONObject a = new JSONObject();
		a.put("name", name);
		a.put("description", description);
		return a;
	}
	
	public JSONObject createEncounter(Event e) throws JSONException{
		JSONObject pt = patientService.getPatientByIdentifier(e.getBaseEntityId());
		JSONObject enc = new JSONObject();
		
		JSONObject pr = userService.getPersonByUser(e.getProviderId());
		
		enc.put("encounterDatetime", OPENMRS_DATE.format(e.getEventDate().toDate()));
		// patient must be existing in OpenMRS before it submits an encounter. if it doesnot it would throw NPE
		enc.put("patient", pt.getString("uuid"));
		enc.put("encounterType", e.getEventType());
		enc.put("location", e.getLocationId());
		enc.put("provider", pr.getString("uuid"));

		List<Obs> ol = e.getObs();
		Map<String, JSONArray> p = new HashMap<>();
		Map<String, JSONArray> pc = new HashMap<>();
		
		if(ol != null)
		for (Obs obs : ol) {
			//if no parent simply make it root obs
			if(StringUtils.isEmptyOrWhitespaceOnly(obs.getParentCode())){
				p.put(obs.getFieldCode(), convertObsToJson(obs));
			}
			else {
				//find parent obs if not found search and fill or create one
				JSONArray parentObs = p.get(obs.getParentCode());
				if(parentObs == null){
					p.put(obs.getParentCode(), convertObsToJson(getOrCreateParent(ol, obs)));
				}
				// find if any other exists with same parent if so add to the list otherwise create new list
				JSONArray obl = pc.get(obs.getParentCode());
				if(obl == null){
					obl = new JSONArray();
				}
				JSONArray addobs = convertObsToJson(obs);
				for (int i = 0; i < addobs.length(); i++) {
					obl.put(addobs.getJSONObject(i));
				}
				pc.put(obs.getParentCode(), obl);
			}
		}
		
		JSONArray obar = new JSONArray();
		for (String ok : p.keySet()) {
			for (int i = 0; i < p.get(ok).length(); i++) {
				JSONObject obo = p.get(ok).getJSONObject(i);
				
				JSONArray cob = pc.get(ok);
				if(cob != null && cob.length() > 0) {
					obo.put("groupMembers", cob);
				}
				
				obar.put(obo);
			}
		}
		enc.put("obs", obar);
		
		HttpResponse op = HttpUtil.post(HttpUtil.removeEndingSlash(OPENMRS_BASE_URL)+"/"+ENCOUNTER_URL, "", enc.toString(), OPENMRS_USER, OPENMRS_PWD);
		return new JSONObject(op.body());
	}
	
	private JSONArray convertObsToJson(Obs o) throws JSONException{
		JSONArray arr = new JSONArray();
		if(o.getValues() == null || o.getValues().size()==0){//must be parent of some obs
			JSONObject obo = new JSONObject();
			obo.put("concept", o.getFieldCode());

			arr.put(obo);
		}
		else {
			//OpenMRS can not handle multivalued obs so add obs with multiple values as two different obs
			for (Object v : o.getValues()) {
				JSONObject obo = new JSONObject();
				obo.put("concept", o.getFieldCode());
				obo.put("value", v);
	
				arr.put(obo);
			}
		}
		return arr;
	}
	
	private Obs getOrCreateParent(List<Obs> obl, Obs o){
		for (Obs obs : obl) {
			if(o.getParentCode().equalsIgnoreCase(obs.getFieldCode())){
				return obs;
			}
		}
		return new Obs("concept", "parent", o.getParentCode(), null, null, null, null);
	}
}
