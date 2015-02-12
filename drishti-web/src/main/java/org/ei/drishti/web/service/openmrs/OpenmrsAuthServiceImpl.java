package org.ei.drishti.web.service.openmrs;

import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.web.service.AuthenticationService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class OpenmrsAuthServiceImpl implements AuthenticationService {

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		// TODO Auto-generated method stub
		return null;
	}
/*
	@Autowired
	HttpAgent http;

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		HttpResponse response = http.post(url, data, contentType);
		if (!response.isSuccess()) {
			throw new BadCredentials();
		}
		
		JSONObject openmrsUser = parseJson(response.body());
		for (JsonObject openmrsRole : openmrsUser.get("roles")) {
			authentication.getAuthorities().add(new OpenmrsAuthority(openmrsRole.get("name")));
		}
		
		return authentication;
	}*/
	
}
