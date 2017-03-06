package org.opensrp.connector.dhis2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
		vaccineCountObj.put("period", 201611);
		vaccineCountObj.put("orgUnit", "IDc0HEyjhvL");
		vaccineCountObj.put("dataValues", vaccineCountArray);
	 return vaccineCountObj;
	
	}
	public JSONObject aggredateDataSendToDHIS2(JSONObject aggregateData) throws JSONException{		
		return new JSONObject(Dhis2HttpUtils.post(DHIS2_BASE_URL.replaceAll("\\s+","")+"dataValueSets", "", aggregateData.toString(),DHIS2_USER.replaceAll("\\s+",""), DHIS2_PWD.replaceAll("\\s+","")).body());
	}
}
