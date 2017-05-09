package org.opensrp.web.controller;

import static org.opensrp.web.rest.RestUtils.getStringFilter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.opensrp.service.OpenmrsIDService;
import org.opensrp.web.utils.PdfUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

@Controller
@RequestMapping("/uniqueids")
public class UniqueIdController {
	
	private static Logger logger = LoggerFactory.getLogger(UniqueIdController.class.toString());
	
	
	
	@Autowired
	OpenmrsIDService openmrsIdService;
	
	@RequestMapping(method = RequestMethod.GET, value = "/print")
	@ResponseBody
	public ResponseEntity<String> thisMonthDataSendTODHIS2(HttpServletRequest request,HttpServletResponse response) throws JSONException {
		
		String message = "";
		try {
			Integer numberToGenerate = Integer.valueOf(getStringFilter("numberToPrint", request));

			openmrsIdService.downloadAndSaveIds(numberToGenerate);
			List<String> data= new ArrayList<String>();
			data.add("1000001");
			data.add("1000002");
			data.add("1000003");
			data.add("1000004");
			data.add("1000005");
			data.add("1000006");
			data.add("1000007");
			ByteArrayOutputStream byteArrayOutputStream = PdfUtil.generatePdf(data,140, 140, 1, 5);
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + "report");

			OutputStream os = response.getOutputStream();
			byteArrayOutputStream.writeTo(os);
			os.flush();
			os.close();
		}
		catch (Exception e) {
			logger.error("", e);
		}
		
		return new ResponseEntity<>(new Gson().toJson("" + message), HttpStatus.OK);
		
	}
	
}
