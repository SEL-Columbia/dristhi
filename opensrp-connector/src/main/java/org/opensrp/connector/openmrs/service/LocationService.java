package org.opensrp.connector.openmrs.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.LocationTree;
import org.opensrp.common.util.HttpResponse;
import org.opensrp.connector.HttpUtil;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.StringUtils;

@Service
public class LocationService extends OpenmrsService{
	private static final String LOCATION_URL = "ws/rest/v1/location";

	public LocationService() {	}

	public LocationService(String openmrsUrl, String user, String password) {
    	super(openmrsUrl, user, password);
	}

	public Location getLocation(String locationIdOrName) throws JSONException {
		HttpResponse op = HttpUtil.get(HttpUtil.removeEndingSlash(OPENMRS_BASE_URL)+"/"+LOCATION_URL+"/"+(locationIdOrName.replaceAll(" ", "%20")), "v=full", OPENMRS_USER, OPENMRS_PWD);
		
		if(!StringUtils.isEmptyOrWhitespaceOnly(op.body())){
			return makeLocation(op.body());
		}
		return null;
	}
	
	private Location makeLocation(String locationJson) throws JSONException{
		JSONObject obj = new JSONObject(locationJson);
		JSONObject parentL = (obj.has("parentLocation")&&!obj.isNull("parentLocation"))?obj.getJSONObject("parentLocation"):null;
		Location p = null;
		if(parentL != null){
			p = new Location(parentL.getString("uuid"), parentL.getString("display"), null, null);
		}
		Location l = new Location(obj.getString("uuid"), obj.getString("name"), 
				null, null, p , null, null);
		JSONArray t = obj.getJSONArray("tags");
		
		for (int i = 0; i < t.length(); i++) {
			l.addTag(t.getJSONObject(i).getString("display"));
		}
		
		JSONArray a = obj.getJSONArray("attributes");
		
		for (int i = 0; i < a.length(); i++) {
			String ad = a.getJSONObject(i).getString("display");
			l.addAttribute(ad.substring(0,ad.indexOf(":")), ad.substring(ad.indexOf(":")+2));
		}
		
		return l;
	}
	
	private Location makeLocation(JSONObject location) throws JSONException{
		return makeLocation(location.toString());
	}
	
	public LocationTree getLocationTree() throws JSONException {
		LocationTree ltr = new LocationTree();
		HttpResponse op = HttpUtil.get(HttpUtil.removeEndingSlash(OPENMRS_BASE_URL)+"/"+LOCATION_URL, "v=full", OPENMRS_USER, OPENMRS_PWD);
		
		JSONArray res = new JSONObject(op.body()).getJSONArray("results");
		if(res.length() == 0){
			return ltr;
		}
		
		for (int i = 0; i < res.length(); i++) {
			ltr.addLocation(makeLocation(res.getJSONObject(i)));
		}
		return ltr;
	}
	
	public LocationTree getLocationTreeOf(String locationIdOrName) throws JSONException {
		LocationTree ltr = new LocationTree();
		
		fillTreeWithHierarchy(ltr, locationIdOrName);
		
		return ltr;
	}
	
	private void fillTreeWithHierarchy(LocationTree ltr, String locationIdOrName) throws JSONException{
		HttpResponse op = HttpUtil.get(HttpUtil.removeEndingSlash(OPENMRS_BASE_URL)+"/"+LOCATION_URL+"/"+(locationIdOrName.replaceAll(" ", "%20")), "v=full", OPENMRS_USER, OPENMRS_PWD);

		JSONObject lo = new JSONObject(op.body());
		Location l = makeLocation(op.body());
		ltr.addLocation(l);
		
		if(!lo.has("childLocations")){
			return;
		}
		JSONArray lch = lo.getJSONArray("childLocations");

		for (int i = 0; i < lch.length(); i++) {

			JSONObject cj = lch.getJSONObject(i);
			fillTreeWithHierarchy(ltr, cj.getString("uuid"));
		}
	}

}
