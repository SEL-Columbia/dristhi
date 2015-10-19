package org.ei.drishti.web.handler;

import java.text.ParseException;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;
import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ei.drishti.form.service.FormSubmissionService;
import com.google.gson.Gson;

@Component
public class FormsubmissionHandler {
	private static Logger logger = LoggerFactory
			.getLogger(FormsubmissionHandler.class.toString());
	public static final String FORM_SUBMISSIONS = "formdata";
	private FormSubmissionService formSubmissionService;
	private final String drishtiformdataURL;
	
	private HttpAgent httpAgent;
	private DateUtil dateUtil;
        String entityidEC = null;


@Autowired
public FormsubmissionHandler(@Value("#{drishti['drishti.form.data.url']}") String drishtiformdataURL,FormSubmissionService formSubmissionService,
		HttpAgent httpAgent,DateUtil dateUtil){
	this.formSubmissionService=formSubmissionService;
	this.httpAgent = httpAgent;
	this.drishtiformdataURL=drishtiformdataURL;
	this.dateUtil=dateUtil;
}
    
public void formData(List<FormSubmissionDTO> formSubmissionsDTO) throws JSONException, ParseException{
	Iterator<FormSubmissionDTO> itr = formSubmissionsDTO.iterator();
	logger.info("***** form data received****");
	String url=drishtiformdataURL+"formdata";
	String formdetails=new Gson().toJson(formSubmissionsDTO);
	logger.info("post method url: url:"+url+"?"+formdetails);
	httpAgent.post(url,formdetails,"application/json");
        logger.info("http request to post");
        httpAgent.post("http://10.10.11.91:8080/drishti-reporting/formdatas","anm123","application/json");
        logger.info("try to call post multipart");
        httpAgent.post("http://localhost:8080/drishti-web-0.1-SNAPSHOT/form-submission", "anm123", "application/json");
	
    }
}

