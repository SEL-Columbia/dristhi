package org.opensrp.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.opensrp.api.domain.Location;
import org.opensrp.api.domain.User;
import org.opensrp.connector.openmrs.LocationService;
import org.opensrp.connector.openmrs.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/security/")
public class SecurityManagerController {

	private UserService openmrsUserService;
	private LocationService openmrsLocationService;

	@Autowired
	public SecurityManagerController(UserService openmrsUserService, LocationService openmrsLocationService) {
		this.openmrsUserService = openmrsUserService;
		this.openmrsLocationService = openmrsLocationService;
	}
	
	@RequestMapping("is-authenticated")
	@ResponseBody
	public ResponseEntity<Boolean> isAuthenticated(@RequestParam(value="username", required=true) String username, 
			@RequestParam(value="password", required=true) String password) throws JSONException {
		boolean isAuth = openmrsUserService.authenticate(username, password);
		return new ResponseEntity<>(isAuth?HttpStatus.OK:HttpStatus.UNAUTHORIZED);
	}

	@RequestMapping("authenticate")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> authenticate(@RequestParam(value="username", required=true) String username, 
			@RequestParam(value="password", required=true) String password) throws JSONException {
		boolean isAuth;
		isAuth = openmrsUserService.authenticate(username, password);
		User u = openmrsUserService.getUser(username);
		Location l = openmrsLocationService.getLocation((String) u.getBaseEntity().getAttribute("Location"));
		Map<String, Object> map = new HashMap<>();
		map.put("user", u);
		map.put("location", l);
		return new ResponseEntity<>(map, isAuth?HttpStatus.OK:HttpStatus.UNAUTHORIZED);
	}

}
