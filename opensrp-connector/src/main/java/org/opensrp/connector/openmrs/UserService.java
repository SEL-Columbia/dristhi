package org.opensrp.connector.openmrs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.domain.User;
import org.opensrp.common.util.HttpResponse;
import org.opensrp.connector.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	private static final String AUTHENTICATION_URL = "ws/rest/v1/session";
	private static final String USER_URL = "ws/rest/v1/user";
	private String openmrsWebUrl;
	private String openmrsUser;
	private String openmrsPwd;
	
	@Autowired
    public UserService(@Value("#{opensrp['openmrs.url']}") String openmrsOpenmrsUrl,
    		@Value("#{opensrp['openmrs.username']}") String openmrsUsername,
    		@Value("#{opensrp['openmrs.password']}") String openmrsPassword) {
        openmrsWebUrl = openmrsOpenmrsUrl;
        openmrsUser = openmrsUsername;
        openmrsPwd = openmrsPassword;
    }

	public boolean authenticate(String username, String password) throws JSONException {
		HttpResponse op = HttpUtil.get(HttpUtil.removeEndingSlash(openmrsWebUrl)+"/"+AUTHENTICATION_URL, "", username, password);
		return new JSONObject(op.body()).getBoolean("authenticated");
	}

	public User getUser(String username) throws JSONException {
		HttpResponse op = HttpUtil.get(HttpUtil.removeEndingSlash(openmrsWebUrl)+"/"+USER_URL, "v=full&username="+username, openmrsUser, openmrsPwd);
		JSONArray res = new JSONObject(op.body()).getJSONArray("results");
		if(res.length() == 0){
			return null;
		}
		JSONObject obj = res.getJSONObject(0);
		JSONObject p = obj.getJSONObject("person");
		User u = new User(obj.getString("uuid"), obj.getString("username"), null, null,
				p.getString("display"), "", "", null, null, p.getString("gender"));
		//Object ploc;
		JSONArray a = p.getJSONArray("attributes");
		
		for (int i = 0; i < a.length(); i++) {
			String ad = a.getJSONObject(i).getString("display");
			u.getBaseEntity().addAttribute(ad.substring(0,ad.indexOf("=")-1), ad.substring(ad.indexOf("=")+2));
		}
		
		JSONArray per = obj.getJSONArray("privileges");
		
		for (int i = 0; i < per.length(); i++) {
			u.addPermission(per.getJSONObject(i).getString("name"));
		}
		
		JSONArray rol = obj.getJSONArray("roles");
		
		for (int i = 0; i < rol.length(); i++) {
			u.addRole(rol.getJSONObject(i).getString("name"));
		}
		
		return u;
	}
}
