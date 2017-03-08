package org.opensrp.connector.dhis2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Event;
import org.opensrp.domain.Obs;
import org.springframework.stereotype.Service;
/*import static org.opensrp.common.AllConstants.DHIS2.*;
*/
@Service
public class DHIS2AggregateConnector extends DHIS2Service {
	
	
	public DHIS2AggregateConnector(){
		
	}
	public DHIS2AggregateConnector(String dhis2Url, String user, String password) {
		super(dhis2Url, user, password);
	}
	public JSONObject getAggregateDataCount() throws JSONException{
		JSONObject vaccineCountObj =	new JSONObject();
		JSONArray vaccineCountArray =	new JSONArray();		
		
		Date date = new Date();
		String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
		
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH);
		String periodTime =  Integer.toString(year)+Integer.toString(month);
		
		JSONObject vaccineAttrObj1 = new JSONObject();
		vaccineAttrObj1.put("dataElement", "bDl4fsu1QIj");//Bcg given (0-11m)
		//vaccineAttrObj1.put("period", "201701");
		//vaccineAttrObj1.put("orgUnit", "IDc0HEyjhvL");
		vaccineAttrObj1.put("value", 53);
		
		JSONObject vaccineAttrObj2 = new JSONObject();
		vaccineAttrObj2.put("dataElement", "Ar5v2MYP3EU");//Penta 1given (0-11m)
		//vaccineAttrObj2.put("period", "201701");
		//vaccineAttrObj2.put("orgUnit", "IDc0HEyjhvL");
		vaccineAttrObj2.put("value", 45);
		
		vaccineCountArray.put(vaccineAttrObj1);
	    vaccineCountArray.put(vaccineAttrObj2);
	    
	    vaccineCountObj.put("dataSet", "wn53Io9MM6B");
		vaccineCountObj.put("completeData", modifiedDate);
		vaccineCountObj.put("period", 201610);
		vaccineCountObj.put("orgUnit", "IDc0HEyjhvL");
		vaccineCountObj.put("dataValues", vaccineCountArray);
	 return vaccineCountObj;
	
	}
	
	public JSONObject getAggregatedDataCount( List<Event> eventList) throws JSONException{
			
		
		Date date = new Date();
		String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
		
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH);
		String periodTime =  Integer.toString(year)+Integer.toString(month);
		Integer health=0;
		System.out.println("periodTime:"+periodTime);
	   	Integer birthPlaceInHealthFacilityCount=0;
	   	Integer birthPlaceInHome=0;
	   	Integer birthUnderWeightCount = 0;
	   	Integer brtc = 0;
	   	for (Event event : eventList) {
			List<Obs> obs = event.getObs();
				for (Obs obs2 : obs) {
					if(obs2.getFormSubmissionField().equalsIgnoreCase("Place_Birth")){
						List<Object> values = obs2.getHumanReadableValues();
						for (Object object : values) {
							if(object.toString().equalsIgnoreCase("Health facility")){
								birthPlaceInHealthFacilityCount++;
							}else if(object.toString().equalsIgnoreCase("Home")){
								birthPlaceInHome++;
							}else{
								
							}
						}
						
					}
					
					if(obs2.getFormSubmissionField().equalsIgnoreCase("Birth_Weight")){
						List<Object> values = obs2.getValues();
						for (Object object : values) {
							try{
								Double value = Double.parseDouble((String) object);
								double conditionValue =value;
								System.out.println("conditionValue:"+conditionValue);
								if(conditionValue <3.5){
									birthUnderWeightCount++;
								}else{
									
								}
							}catch(Exception e){
								System.out.println("Birth_Weight Message:"+e.getMessage());
							}
						}
					}
					
					
				}
				
				
				
				
				
				brtc++;
		 }
   	 	
   	 	JSONArray vaccineDataValues =	new JSONArray();		
		
		
		/**
		 * Count for Birth place of Health_Facility of current month
		 * */
		JSONObject birhtPlaceInHealthFacility = new JSONObject();
		birhtPlaceInHealthFacility.put("dataElement", "ii7lOGQqEq5");
		birhtPlaceInHealthFacility.put("value", birthPlaceInHealthFacilityCount);
		/**
		 * Count for Birth place of Home of current month
		 * */
		JSONObject birhtPlaceInHome = new JSONObject();
		birhtPlaceInHome.put("dataElement", "yNWOJ0OOOQD");
		birhtPlaceInHome.put("value", birthPlaceInHome);
		
		
		/**
		 * Count Birth Under WeightCount of current month
		 * */
		JSONObject birthUnderWeight = new JSONObject();
		birthUnderWeight.put("dataElement", "Wtf7iSiQdUJ");
		birthUnderWeight.put("value", birthUnderWeightCount);
		
		
		/**
		 *Count for Total new birth registrations  of current month
		 * */
		JSONObject brtcObject = new JSONObject();
		brtcObject.put("dataElement", "xMlVHstzOgC");
		brtcObject.put("value", brtc);
		
		vaccineDataValues.put(brtcObject);
		vaccineDataValues.put(birhtPlaceInHome);
		vaccineDataValues.put(birhtPlaceInHealthFacility);
		vaccineDataValues.put(birthUnderWeight);
		
	    JSONObject vaccineDataSet =	new JSONObject();
	    vaccineDataSet.put("dataSet", "fDoHorjO5Sr");
	    vaccineDataSet.put("completeData", modifiedDate);
	    vaccineDataSet.put("period", 201702);
	    vaccineDataSet.put("orgUnit", "IDc0HEyjhvL");
	    vaccineDataSet.put("dataValues", vaccineDataValues);
	 return vaccineDataSet;
	
	}
	public JSONObject aggredateDataSendToDHIS2(JSONObject aggregateData) throws JSONException{		
		return new JSONObject(Dhis2HttpUtils.post(DHIS2_BASE_URL.replaceAll("\\s+","")+"dataValueSets", "", aggregateData.toString(),DHIS2_USER.replaceAll("\\s+",""), DHIS2_PWD.replaceAll("\\s+","")).body());
	}
}
