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
        logger.info("try to call post multipart");
        httpAgent.post("http://localhost:8080/drishti-web-0.1-SNAPSHOT/multimedia-file", "anm123", "application/json");
	
	while (itr.hasNext()) {
		Object object = (Object) itr.next();
		String jsonstr = object.toString();

		JSONObject dataObject = new JSONObject(jsonstr);

		String visittype = dataObject.getString("formName");
		logger.info("value of formname " + visittype);
	
		if (visittype.equalsIgnoreCase("anc_visit")
				|| visittype.equalsIgnoreCase("pnc_visit")
				|| visittype.equalsIgnoreCase("child_illness")) {

			JSONArray fieldsJsonArray = dataObject
					.getJSONObject("formInstance")
					.getJSONObject("form").getJSONArray("fields");

			String visitentityid = dataObject.getString("entityId");

			String anmid = dataObject.getString("anmId");
			

			for (int i = 0; i < fieldsJsonArray.length(); i++) {

				JSONObject jsonObject = fieldsJsonArray
						.getJSONObject(i);

				if ((jsonObject.has("name"))
						&& jsonObject.getString("name").equals("ecId")) {

					entityidEC = (jsonObject.has("value") && jsonObject
							.getString("value") != null) ? jsonObject
							.getString("value") : "";
				}

				if ((jsonObject.has("name"))
						&& jsonObject.getString("name").equals(
								"isConsultDoctor")) {

					String isCon = (jsonObject.has("value") && jsonObject
							.getString("value") != null) ? jsonObject
							.getString("value") : "";

					logger.info("res1+++++" + isCon);
					if (isCon.equalsIgnoreCase("yes")) {
						

						logger.info(" invoking a service");
						logger.info("res2+++++" + isCon);
						logger.info("anmid+++++" + anmid);

						formSubmissionService.requestConsultationTest(
								visittype, visitentityid, entityidEC,
								anmid);

						logger.info("invoking a service method");
						
					}

				}
			}
		}

	}

    }
}

