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
    	 
		JSONObject aggregatedDataSet=null;
		try{
			 aggregatedDataSet = dHIS2AggregateConnector.getAggregatedDataCount(list);
			dHIS2AggregateConnector.aggredateDataSendToDHIS2(aggregatedDataSet);
		}catch(Exception e){
			System.out.println("Aggregated Data Message:"+e.getMessage());
		}
    	return new ResponseEntity<>(new Gson().toJson(""+aggregatedDataSet.toString()), HttpStatus.OK);
        
    }
    
}

