package org.opensrp.web.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

@Controller
public class UniqueIdController {
	
	private static Logger logger = LoggerFactory.getLogger(UniqueIdController.class.toString());
	
	@Value("#{opensrp['openmrs.url']}")
	protected String OPENMRS_BASE_URL;
	
	@Value("#{opensrp['openmrs.username']}")
	protected String OPENMRS_USER;
	
	@Value("#{opensrp['openmrs.password']}")
	protected String OPENMRS_PWD;
	
	@Value("#{opensrp['openmrs.idgen.url']}")
	protected String idgenUrl;
	
	@Value("#{opensrp['openmrs.idgen.initial.batchsize']}")
	protected Integer initialBatchSize;
	
	@Value("#{opensrp['openmrs.idgen.batchsize']}")
	protected Integer batchSize;
	
	@Value("#{opensrp['openmrs.idgen.idsource']}")
	protected String idSource;
	
	@RequestMapping(method = RequestMethod.GET, value = "/generate")
	@ResponseBody
	public ResponseEntity<String> thisMonthDataSendTODHIS2() throws JSONException {
		
		String message = "";
		try {
			
		}
		catch (Exception e) {
			logger.error("", e);
		}
		
		return new ResponseEntity<>(new Gson().toJson("" + message), HttpStatus.OK);
		
	}
	
	private void pullUniqueIdsFromOpenmrs(int numberToGenerate) throws Exception {
		String localUrlString = OPENMRS_BASE_URL + idgenUrl + "?source=" + idSource + "&numberToGenerate=" + numberToGenerate
		        + "&username=" + OPENMRS_USER + "&password=" + OPENMRS_PWD;
		URL localURL = new URL(localUrlString);
		/*
		 * Tries to open a connection to the URL. If an IO error occurs, this throws an
		 * IOException
		 */
		URLConnection localURLConnection = localURL.openConnection();
		
		// If the connection is an HTTP connection, continue
		if ((localURLConnection instanceof HttpURLConnection)) {
			
			// Casts the connection to a HTTP connection
			HttpURLConnection localHttpURLConnection = (HttpURLConnection) localURLConnection;
			
			// Sets the user agent for this request.
//			localHttpURLConnection.setRequestProperty("User-Agent",
//			    FileUtilities.getUserAgent(Context.getInstance().applicationContext()));
			
			// Gets a response code from the RSS server
			int responseCode = localHttpURLConnection.getResponseCode();
			
			switch (responseCode) {
				
				// If the response is OK
				case HttpURLConnection.HTTP_OK:
					// Gets the last modified data for the URL
					parseResponse(localHttpURLConnection);
					
					break;
				default:
					logger.error("", "Error when fetching unique ids from openmrs server " + localUrlString
					        + " Response code " + responseCode);
					
			}
			
			// Reports that the feed retrieval is complete.
		}
		
	}
	
	private void parseResponse(HttpURLConnection connection) throws Exception {
		String response = readInputStreamToString(connection);
		JSONObject responseJson = new JSONObject(response);
		JSONArray jsonArray = responseJson.getJSONArray("identifiers");
		if (jsonArray != null && jsonArray.length() > 0) {
			List<String> ids = new ArrayList<String>();
			for (int i = 0; i < jsonArray.length(); i++) {
				ids.add(jsonArray.getString(i));
			}
			//uniqueIdRepo.bulkInserOpenmrsIds(ids);
		}
	}
	
	/**
	 * @param connection object; note: before calling this function, ensure that the connection is
	 *            already be open, and any writes to the connection's output stream should have
	 *            already been completed.
	 * @return String containing the body of the connection response or null if the input stream
	 *         could not be read correctly
	 */
	private String readInputStreamToString(HttpURLConnection connection) {
		String result = null;
		StringBuffer sb = new StringBuffer();
		InputStream is = null;
		
		try {
			is = new BufferedInputStream(connection.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String inputLine = "";
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			result = sb.toString();
		}
		catch (Exception e) {
			logger.error("", e);
			result = null;
		}
		finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (IOException e) {
					logger.error("", e);
				}
			}
		}
		
		return result;
	}
	
}
