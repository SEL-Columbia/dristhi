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
import org.opensrp.repository.AllEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/*import static org.opensrp.common.AllConstants.DHIS2.*;
*/
@Service
public class DHIS2AggregateConnector extends DHIS2Service {
	
	@Autowired
	private AllEvents allEvents;
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
	
	public JSONObject getAggregatedDataCount() throws JSONException{
			
		
		Date date = new Date();
		String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
		
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MONTH, -1);
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH)+1;
		
		int length = (int)(Math.log10(month)+1);
		String formatted ;
		System.out.println(length);
		if(length<2){			
		 formatted = String.format("%02d", month);
		}else{
			formatted =Integer.toString(month);
		}
		
		String periodTime =  Integer.toString(year)+formatted;
		Integer health=0;
		System.out.println("periodTime:"+periodTime);
	   	Integer birthPlaceInHealthFacilityCount=0;
	   	Integer birthPlaceInHome=0;
	   	Integer birthUnderWeightCount = 0;
	   	Integer brtc = 0;
	   	
	   	List<Event> eventList = allEvents.findEventByEventTypeBetweenTwoDates("Birth Registration");
	   	if(eventList.isEmpty()){
	   		System.out.println("Empty:Data");
	   	}else{
	   		System.out.println("Not Empty:Data");
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
	   	}
	   	
	   	
   	 	JSONArray eventDataValues =	new JSONArray();		
		
		
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
		
		eventDataValues.put(brtcObject);
		eventDataValues.put(birhtPlaceInHome);
		eventDataValues.put(birhtPlaceInHealthFacility);
		eventDataValues.put(birthUnderWeight);
		/**
		 * Vaccination data count start form here
		 * **/
		
		
	    JSONObject eventDataSet =	new JSONObject();
	    eventDataSet.put("dataSet", "fDoHorjO5Sr");
	    eventDataSet.put("completeData", modifiedDate);
	    eventDataSet.put("period", periodTime);
	    eventDataSet.put("orgUnit", "IDc0HEyjhvL");
	    eventDataSet.put("dataValues", eventDataValues);
	 return eventDataSet;
	
	}
	public JSONObject aggredateDataSendToDHIS2(JSONObject aggregateData) throws JSONException{		
		return new JSONObject(Dhis2HttpUtils.post(DHIS2_BASE_URL.replaceAll("\\s+","")+"dataValueSets", "", aggregateData.toString(),DHIS2_USER.replaceAll("\\s+",""), DHIS2_PWD.replaceAll("\\s+","")).body());
	}
}
