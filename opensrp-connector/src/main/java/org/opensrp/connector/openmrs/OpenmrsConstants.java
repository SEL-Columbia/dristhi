package org.opensrp.connector.openmrs;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.ei.drishti.form.domain.FormSubmission;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenmrsConstants {

	public static SimpleDateFormat OPENMRS_DATE = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat OPENMRS_DATETIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static enum PersonField{
		IS_NEW_PERSON("is_new_person", null),
		IDENTIFIER("identifier", "case_id"),
		IDENTIFIER_TYPE("identifier_type", null),
		FIRST_NAME("given_name", "name"),
		LAST_NAME("family_name", "family_name"),
		GENDER("gender", "sex"),
		AGE("age", "age"),
		BIRTHDATE("birth_date", "dob"),
		BIRTHDATE_IS_APPROX("birthdate_estimated", null),
		DEATHDATE("death_date", "death_date"),
		;
		
		private String openmrsfieldName;
		private String drishtifieldName;
		public String OMR_FIELD(){
			return openmrsfieldName;
		}
		public String OSRP_FIELD(){
			return drishtifieldName;
		}
		public String SRP_VALUE(FormSubmission fs){
			return fs.getField(drishtifieldName);
		}
		private PersonField(String openmrsfieldName, String drishtifieldName) {
			this.openmrsfieldName = openmrsfieldName;
			this.drishtifieldName = drishtifieldName;
		}
		
	}
	
	public static enum FormField {
		TB_FUP_FORM_TYPE("encounter_type", null, "TB_FOLLOWUP"),
		//TB_FUP_FORM_ID("form_id", null, "4"),
		//TB_FUP_FORM_NAME("form_name", null, "birth notification mobile"),
		
		DEATH_FORM_TYPE("encounter_type", null, "DEATH DETAILED REPORT"),
		DEATH_FORM_ID("form_id", null, "6"),
		DEATH_FORM_NAME("form_name", null, "death notification mobile"),
		
		TB_REG_FORM_TYPE("encounter_type", null, "TB_REGISTRATION"),
		//TB_REG_FORM_ID("form_id", null, "35"),
		//TB_REG_FORM_NAME("form_name", null, "pregnancy notification mobile"),

		VERBAL_AUTOPSY_FORM_TYPE("encounter_type", null, "VERBAL AUTOPSY"),
		VERBAL_AUTOPSY_FORM_ID("form_id", null, "3005"),//TODO FORMID
		VERBAL_AUTOPSY_FORM_NAME("form_name", null, "verbal autopsy mobile"),
		
		ENCOUNTER_LOCATION("location_id", "address_encounter_center", null),
		ENCOUNTER_DATE("encounter_datetime", "today", null),

		ENCOUNTER_CENTER_TYPE("164375", "address_encounter_center_type", null),
		ENCOUNTER_CENTER("164385", "address_encounter_center", null),

		ADDRESS_ENCOUNTER("164372", "address_encounter", null),
		ADDRESS_ENCOUNTER_STREET("162318", "address_encounter_street", null),
		ADDRESS_ENCOUNTER_PROVINCE("162307", "address_encounter_province", null),
		ADDRESS_ENCOUNTER_DISTRICT("162319", "address_encounter_district", null),
		ADDRESS_ENCOUNTER_TOWN("162308", "address_encounter_town", null),
		ADDRESS_ENCOUNTER_UC("163036", "address_encounter_uc", null),
		
		FORM_CREATOR("creator", "creator", null),

		FORM_START_DATETIME("164373","start",null),
		FORM_END_DATETIME("164374","end",null),
		FORM_DATE("163064","today",null),
		FORM_BACKLOG_ID("163041",null, null),
		//DEVICEID("","deviceid",null),
		//SUBSCRIBERID("","subscriberid",null),

		INFORMANT_INFORMATION("163052", "applicant_information", null),
		INFORMANT_FULL_NAME("163055", "applicant_name", null),
		INFORMANT_NIC("162849", "applicant_nic", null),
		INFORMANT_RELATIONSHIP("164384", "applicant_relationship", null),

		ADDITIONAL_NOTE("161011","additional_note",null),
		
		PERSON_FULL_NAME("163061", null, null),
		PERSON_IDENTIFIER("163121", "entityId", null),
		GENDER("163122", "gender", null),
		BIRTHDATE("163123", "birth_date", null),
		DEATHDATE("1543", "death_date", null),
		AGE("160617", "age", null),
		MARITAL_STATUS("162265", "marital_status", null),
		MARRIAGE_DATE("162275","marriage_date",null),
		CITIZENSHIP("162296", "citizenship", null),
		CITIZENSHIP_TYPE("164404", "citizenship", null),
		NIC("162849", "nic", null),
		RELIGION("163043", "religion", null),
		ETHNICITY("162294", "ethnicity", null),
		EDUCATION("1712", "education", null),
		OCCUPATION("162807", "occupation", null),
		ABILITY_READ_WRITE("162787","ability_read_write",null),
		ECONOMIC_ACTIVITY_STATUS("162851","economic_activity_status",null),
		
		BMI("1342","bmi",null),
		WEIGHT("5089","weight",null),
		HEIGHT("5090", "height", null),

		SMEAR("307","smear",null),
		PATIENT_TYPE("159990","patient_type",null),
		RESISTANCE_TYPE("162693","resistance_type",null),
		RESISTANCE_DRUGS("159956","resistance_drugs",null),
		RISK_FACTORS("162696","risk_factors",null),
		
		
		;
		
		private String openmrsfieldName;
		private String drishtifieldName;
		private Object defaultValue;
		public String OMR_FIELD(){
			return openmrsfieldName;
		}
		public String OSRP_FIELD(){
			return drishtifieldName;
		}
		public String SRP_VALUE(FormSubmission fs){
			return fs.getField(drishtifieldName);
		}
		public Object DEFAULT_VALUE(){
			return defaultValue;
		}
		private FormField(String openmrsfieldName, String drishtifieldName, Object defaultValue) {
			this.openmrsfieldName = openmrsfieldName;
			this.drishtifieldName = drishtifieldName;
			this.defaultValue = defaultValue;
		}
		
		public void createConceptObs(JSONArray obsArray, FormSubmission fs, int obsId, JSONObject parent) throws JSONException {
			if(fs.getField(this.OSRP_FIELD()) != null){
				JSONObject obs = new JSONObject();
				obs.put("obs_data_type", "concept");
				obs.put("concept_id", this.OMR_FIELD());
				obs.put("obs_id", obsId);
				if(parent != null) obs.put("obs_group_id", parent.get("obs_id"));
				
				obs.put("value", fs.getField(this.OSRP_FIELD()));
				obsArray.put(obs);
			}
		}
		
		public void createConceptObsWithValue(JSONArray obsArray, Object value, int obsId, JSONObject parent) throws JSONException {
			if(value != null){
				JSONObject obs = new JSONObject();
				obs.put("obs_data_type", "concept");
				obs.put("concept_id", this.OMR_FIELD());
				obs.put("obs_id", obsId);
				if(parent != null) obs.put("obs_group_id", parent.get("obs_id"));
				
				obs.put("value", value);
				obsArray.put(obs);
			}
		}
		
		public void createCodedObsWith(JSONArray obsArray, FormSubmission fs, int obsId, JSONObject parent) throws JSONException {
			String value = getConceptFromValue(fs);
			if(value != null){
				JSONObject obs = new JSONObject();
				obs.put("obs_data_type", "concept");
				obs.put("concept_id", this.OMR_FIELD());
				obs.put("obs_id", obsId);
				if(parent != null) obs.put("obs_group_id", parent.get("obs_id"));
				
				obs.put("value", value);
				obsArray.put(obs);
			}
		}
		
		public JSONObject createParentObs(int obsId) throws JSONException {
			JSONObject obs = new JSONObject();
			obs.put("obs_data_type", "concept");
			obs.put("concept_id", this.OMR_FIELD());
			obs.put("obs_id", obsId);
			return obs;

		}
		
		public String getConceptFromValue(FormSubmission fs) {
			return VALUE_MAP.get(SRP_VALUE(fs));
		}
	}
	
	public static enum Location{
		LOCATION_LIST("results", null, null),
		PAGER_LIST("links", null, null),
		UUID("uuid", "uuid", null),
		NAME("display", "name", null),
		DESCRIPTION("description", "description", null),
		ADDRESS1("address1", "address1", null),
		ADDRESS2("address2", "address2", null),
		CITY_VILLAGE("cityVillage", "cityVillage", null),
		STATE_PROVINCE("stateProvince", "stateProvince", null),
		COUNTRY("country", "country", null),
		POSTAL_CODE("postalCode", "postalCode", null),
		LATITUDE("latitude", "latitude", null),
		LONGITUDE("longitude", "longitude", null),
		COUNTY_DISTRICT("countyDistrict", "countyDistrict", null),
		ADDRESS3("address3", "address3", null),
		ADDRESS4("address4", "address4", null),
		ADDRESS5("address5", "address5", null),
		ADDRESS6("address6", "address6", null),
		TAGS("tags", "tags", null),
		PARENT("parentLocation", "parentLocation", null),
		CHILDREN("childLocations", "childLocations", null),
		RETIRED("retired", "retired", null),
		ATTRIBUTES("attributes", "locationAttributes", null),
		;
		private String openmrsfieldName;
		private String drishtifieldName;
		private Object defaultValue;
		public String OMR_FIELD(){
			return openmrsfieldName;
		}
		public String SRP_FIELD(){
			return drishtifieldName;
		}
		public Object DEFAULT_VALUE(){
			return defaultValue;
		}
		private Location(String openmrsfieldName, String drishtifieldName, Object defaultValue) {
			this.openmrsfieldName = openmrsfieldName;
			this.drishtifieldName = drishtifieldName;
			this.defaultValue = defaultValue;
		}
		
		public static String getNextPageLink(JSONObject jso) throws JSONException{
			JSONArray linksarr = jso.getJSONArray(PAGER_LIST.OMR_FIELD());
			System.out.println(linksarr);
			for (int i = 0; i < linksarr.length(); i++) {
				if(linksarr.getJSONObject(i).has("rel") && linksarr.getJSONObject(i).getString("rel").equalsIgnoreCase("next")){
					return linksarr.getJSONObject(i).getString("uri");
				}
			}
			return null;
		}
		
		public static void getPrevPageLink(){
			
		}
	}
	
	public static Map<String, String> VALUE_MAP = init();
	
	public static Map<String, String> init() {
		VALUE_MAP = new HashMap<String, String>();
		VALUE_MAP.put("positive","703");
		VALUE_MAP.put("negative","664");
		VALUE_MAP.put("new","159977");
		VALUE_MAP.put("relapse","160033");
		VALUE_MAP.put("normal","159958");
		VALUE_MAP.put("mdr","159345");
		VALUE_MAP.put("xdr","159346");
		VALUE_MAP.put("rhe","159851");
		VALUE_MAP.put("rhze","162695");
		VALUE_MAP.put("streptomycin","84360");
		VALUE_MAP.put("isoniazid","78280");
		VALUE_MAP.put("capreomycin","72794");
		VALUE_MAP.put("amikacin","71060");
		VALUE_MAP.put("diabetes","119481");
		VALUE_MAP.put("hiv","884");
		VALUE_MAP.put("pregnant","1434");
		VALUE_MAP.put("malnourished","162697");
		VALUE_MAP.put("other","5622");
		
		return VALUE_MAP;
	}


}
