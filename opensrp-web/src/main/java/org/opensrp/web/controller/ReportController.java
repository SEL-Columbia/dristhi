package org.opensrp.web.controller;

import java.util.Map;

import org.json.JSONException;
import org.opensrp.connector.openmrs.service.OpenmrsReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;

@Controller
public class ReportController {

	private OpenmrsReportingService reportService;
	
	@Autowired
	public ReportController(OpenmrsReportingService reportService) {
		this.reportService = reportService;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/report/report-definitions")
    public ResponseEntity<String> reportDefinitions() throws JSONException {
		return new ResponseEntity<>(reportService.getReportDefinitions().toString(),HttpStatus.OK);
    }
	
	@RequestMapping(method = RequestMethod.GET, value = "/report/report-data")
    public ResponseEntity<String> reportData(@RequestParam("uuid") String uuid, @RequestParam Map<String, String> params) throws JSONException {
		return new ResponseEntity<>(reportService.getReportData(uuid, params).toString(),HttpStatus.OK);
    }
}
