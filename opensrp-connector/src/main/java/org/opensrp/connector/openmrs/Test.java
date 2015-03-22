package org.opensrp.connector.openmrs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.domain.User;
import org.opensrp.common.util.HttpResponse;
import org.opensrp.connector.HttpUtil;

import com.google.gson.Gson;

public class Test {

	public static void main(String[] args) throws JSONException {
		HttpUtil http = new HttpUtil();
		
		String user = "admin";
		String pass = "5rpAdmin";
		HttpResponse au = http.get("http://46.101.51.199:8080/openmrs/ws/rest/v1/session", "", user, pass);
		System.out.println(au.body());
		//String encodedURL=java.net.URLEncoder.encode(url,"UTF-8");
		HttpResponse op = http.get("http://46.101.51.199:8080/openmrs/ws/rest/v1/location/testloc4", "v=full", user, pass);
		
		System.out.println(op.body());
		JSONObject res = new JSONObject(op.body());
		JSONObject j = res.getJSONArray("results").getJSONObject(0);
		JSONObject p = j.getJSONObject("person");
		User u = new User(j.getString("uuid"), j.getString("username"), null, null,
				p.getString("display"), "", "", null, null, p.getString("gender"));
		JSONArray per = j.getJSONArray("privileges");
		
		for (int i = 0; i < per.length(); i++) {
			u.addPermission(per.getJSONObject(i).getString("name"));
		}
		
		JSONArray rol = j.getJSONArray("roles");
		
		for (int i = 0; i < rol.length(); i++) {
			u.addRole(rol.getJSONObject(i).getString("name"));
		}
		
		System.out.println(u);
		
		System.out.println(new Gson().toJson(u));
		
		
	}
}
