package org.opensrp.web.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.connector.dhis2.DHIS2AggregateConnector;
import org.opensrp.domain.Event;
import org.opensrp.domain.Obs;
import org.opensrp.repository.AllEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

@Controller
public class DHIS2Controller {
   
	
    private AllEvents allEvents;
    @Autowired
    private DHIS2AggregateConnector dHIS2AggregateConnector;
    @Autowired
    public DHIS2Controller(AllEvents allEvents) {
    	this.allEvents = allEvents;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/this-month-client-to-dhis2")
    @ResponseBody
    public ResponseEntity<String> thisMonthDataSendTODHIS2() throws JSONException{
    	 List<Event> list = allEvents.findEventByEventTypeBetweenTwoDates("Birth Registration");
    	 Integer health=0;
    	 Integer birthPlace=0;
    	 Integer brtc = 0;
    	 for (Event event : list) {
			List<Obs> obs = event.getObs();
			for (Obs obs2 : obs) {
				if(obs2.getFormSubmissionField().equalsIgnoreCase("Place_Birth")){
					List<Object> values = obs2.getHumanReadableValues();
					for (Object object : values) {
						if(object.toString().equalsIgnoreCase("Health facility")){
							birthPlace++;
						}
					}
					
				}
			}
			
			
			brtc++;
		}
    	System.out.println("Datas:"+birthPlace);
    	JSONArray vaccineCountArray =	new JSONArray();		
		
		Date date = new Date();
		String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
		
		
		JSONObject vaccineAttrObj2 = new JSONObject();
		vaccineAttrObj2.put("dataElement", "ii7lOGQqEq5");
		vaccineAttrObj2.put("value", birthPlace);
		
		JSONObject brtcObject = new JSONObject();
		brtcObject.put("dataElement", "xMlVHstzOgC");
		brtcObject.put("value", brtc);
		
		vaccineCountArray.put(brtcObject);
	    vaccineCountArray.put(vaccineAttrObj2);
	    JSONObject vaccineCountObj =	new JSONObject();
	    vaccineCountObj.put("dataSet", "fDoHorjO5Sr");
		vaccineCountObj.put("completeData", modifiedDate);
		vaccineCountObj.put("period", 201703);
		vaccineCountObj.put("orgUnit", "IDc0HEyjhvL");
		vaccineCountObj.put("dataValues", vaccineCountArray);
		System.out.println("vaccineCountObj:"+vaccineCountObj.toString());
		dHIS2AggregateConnector.aggredateDataSendToDHIS2(vaccineCountObj);
    	return new ResponseEntity<>(new Gson().toJson(""), HttpStatus.OK);
        
    }
    
}

