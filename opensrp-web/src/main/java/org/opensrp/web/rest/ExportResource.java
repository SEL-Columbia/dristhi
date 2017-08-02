package org.opensrp.web.rest;

import static org.opensrp.web.rest.RestUtils.getStringFilter;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.opensrp.register.thrivepk.FormSubmissionView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/export")
public class ExportResource {
	private FormSubmissionView fsv;
	
	@Autowired
	public ExportResource(FormSubmissionView fsv) {
		this.fsv = fsv;
	}

	@RequestMapping(value = "dumpcsv" , method = RequestMethod.GET)
	public @ResponseBody String dumpCsv(HttpServletRequest request) throws ParseException, IOException {
		String formName = getStringFilter("formName", request);
		return IOUtils.toString(fsv.dump_csv(formName));
	}
	
}
