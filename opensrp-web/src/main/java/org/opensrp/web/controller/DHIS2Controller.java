package org.opensrp.web.controller;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.connector.dhis2.DHIS2AggregateConnector;
import org.opensrp.domain.Event;
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
    	
    	 
		JSONObject aggregatedDataSet=null;
		String message="";
		try{			
			aggregatedDataSet = dHIS2AggregateConnector.getAggregatedDataCount();
			dHIS2AggregateConnector.aggredateDataSendToDHIS2(aggregatedDataSet);
			message = aggregatedDataSet.toString();		
				
		}catch(Exception e){
			System.out.println("Aggregate Data Count Error Message"+e.getMessage());
			message = "No Data Found";
		}
		
    	return new ResponseEntity<>(new Gson().toJson(""+message), HttpStatus.OK);
        
    }
    
}

