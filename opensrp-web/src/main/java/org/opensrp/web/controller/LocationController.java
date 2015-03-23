package org.opensrp.web.controller;

import org.json.JSONException;
import org.opensrp.api.util.LocationTree;
import org.opensrp.connector.openmrs.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/location/")
public class LocationController {
	
	private LocationService openmrsLocationService;
	
	@Autowired
	public LocationController(LocationService openmrsLocationService) {
		this.openmrsLocationService = openmrsLocationService;
	}
	
	@RequestMapping("location-tree")
	@ResponseBody
	public ResponseEntity<LocationTree> getLocationTree() throws JSONException {
		return new ResponseEntity<>(openmrsLocationService.getLocationTree(),HttpStatus.OK);
	}
	
	@RequestMapping("location-tree/{uuid}")
	@ResponseBody
	public ResponseEntity<LocationTree> getLocationTree(@PathVariable("uuid") String uuid) throws JSONException {
		return new ResponseEntity<>(openmrsLocationService.getLocationTreeOf(uuid),HttpStatus.OK);
	}
}
