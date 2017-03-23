package org.opensrp.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

@Service
public class OpenmrsIDService {
	@Value("#{opensrp['openmrs.url']}")
	private String openmrsUrl;

	@Value("#{opensrp['openmrs.username']}")
	private String openmrsUserName;
	
	@Value("#{opensrp['openmrs.password']}")
	private String openmrsPassword;
	
	@Value("#{opensrp['jdbc.driverClassName']}")
	private String mysqlDriverClassName;
	
	@Value("#{opensrp['jdbc.url']}")
	private String mysqlDatabaseUrl;
	
	@Value("#{opensrp['jdbc.import-db']}")
	private String mysqlDatabaseName;
	
	@Value("#{opensrp['jdbc.username']}")
	private String mysqlUserName;
	
	@Value("#{opensrp['jdbc.password']}")
	private String mysqlPassword;
	
	
	private JdbcTemplate jdbcTemplate;
	
	// Client identifiers constant
	public static final String ZEIR_IDENTIFIER = "ZEIR_ID";
	
	// OPENMRS constants
	public static final String DATABASE_TABLE_NAME = "unique_ids";
	public static final String TEST_DATABASE_TABLE_NAME = "unique_ids_test";
	public static final String OPENMRS_IDGEN_URL = "/module/idgen/exportIdentifiers.form";
	public static final int OPENMRS_UNIQUE_ID_SOURCE = 2;
	public static final String ID_COLUMN = "_id";
    public static final String OPENMRS_ID_COLUMN = "openmrs_id";
    public static final String STATUS_COLUMN = "status";
    private static final String USED_BY_COLUMN = "used_by";
    public static final String CREATED_AT_COLUMN = "created_at";
    public static final String UPDATED_AT_COLUMN = "updated_at";
    public static String STATUS_USED = "used";
    public static String STATUS_NOT_USED = "not_used";
    
    private static Logger logger = LoggerFactory.getLogger(OpenmrsIDService.class.toString());
	

	private HttpClient client;
	
	public OpenmrsIDService() {
		this.client = HttpClientBuilder.create().build();
	}
	
	public List<String> downloadOpenmrsIds(int numberToGenerate) {
		List<String> ids = new ArrayList<String>();
		String openmrsQueryUrl = this.openmrsUrl + OPENMRS_IDGEN_URL;
		// Add query parameters
		openmrsQueryUrl += "?source=" + OPENMRS_UNIQUE_ID_SOURCE + "&numberToGenerate=" + numberToGenerate;
		openmrsQueryUrl += "&username=" + this.openmrsUserName + "&password=" + this.openmrsPassword;

		HttpGet get = new HttpGet(openmrsQueryUrl);
		try {
			HttpResponse response = client.execute(get);
			String jsonResponse = EntityUtils.toString(response.getEntity());
			JSONObject responseJson= new JSONObject(jsonResponse);
			JSONArray jsonArray= responseJson.getJSONArray("identifiers");
			
			if(jsonArray != null && jsonArray.length() > 0){
	            for(int i=0; i < jsonArray.length(); i++){
	                ids.add(jsonArray.getString(i));
	            }
	        }
		} catch (IOException | JSONException e) {
			logger.error("", e);
			return null;
		}
		// import IDs and client data to database together with assignments 
		return ids;
	}
	
	public void clearRecords(boolean testMode) {
		String databaseNameToUse = testMode ? TEST_DATABASE_TABLE_NAME : DATABASE_TABLE_NAME;
		String deleteRecordsSql = "DELETE FROM " + databaseNameToUse;
		DataSource dataSource = this.createDataSource();
		this.jdbcTemplate = this.initializeJdbcTemplate(dataSource);
		
		this.jdbcTemplate.execute(deleteRecordsSql);
	}
	
	public Client assignOpenmrsIdToClient(String zeirID, Client client, boolean testMode) throws SQLException {
		// create jdbc template to persist the ids
		String insertSql = "INSERT INTO " + DATABASE_TABLE_NAME;
		insertSql += "(" + OPENMRS_ID_COLUMN + ", " + STATUS_COLUMN + ", " + USED_BY_COLUMN +",";
		insertSql += CREATED_AT_COLUMN + ", " + UPDATED_AT_COLUMN + " ) values (?, ?, ?, ?, ?)";

		initializeImportTable(testMode);
		
		DateTime now = new DateTime();
		
		client.addIdentifier(ZEIR_IDENTIFIER, zeirID);
		this.jdbcTemplate.update(insertSql, zeirID, STATUS_USED, client.fullName(), now.toDate(), now.toDate());
		logger.info("Assigned " + ZEIR_IDENTIFIER + " to " + client.fullName());
		
		return client; 
	}
	
	private DataSource createDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(this.mysqlDatabaseUrl);
		dataSource.setDriverClassName(this.mysqlDriverClassName);
		dataSource.setUsername(this.mysqlUserName);
		dataSource.setPassword(this.mysqlPassword);
		
		return dataSource;
	}
	
	private JdbcTemplate initializeJdbcTemplate(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		
		return jdbcTemplate;
	}
	
	private void initializeImportTable(boolean testMode) throws SQLException {
		String databaseNameToUse = testMode ? TEST_DATABASE_TABLE_NAME : DATABASE_TABLE_NAME;
		String createTableSql = "CREATE TABLE " + databaseNameToUse + "(";
		createTableSql += ID_COLUMN + " INT PRIMARY KEY AUTO_INCREMENT, ";
		createTableSql += OPENMRS_ID_COLUMN + " VARCHAR(255), ";
		createTableSql += STATUS_COLUMN + " VARCHAR(20), ";
		createTableSql += USED_BY_COLUMN + " VARCHAR(255), ";
		createTableSql += CREATED_AT_COLUMN + " DATE, ";
		createTableSql += UPDATED_AT_COLUMN + " DATE";
		createTableSql += ")";
		
		String showTablesQuery = "SELECT count(TABLE_NAME) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'opensrp' AND TABLE_NAME = '" + databaseNameToUse + "'";
		DataSource dataSource = this.createDataSource();
		this.jdbcTemplate = this.initializeJdbcTemplate(dataSource);
		// check if table exists before creating it
		int rowCount = this.jdbcTemplate.queryForObject(showTablesQuery, Integer.class);
		logger.info("Check if import table is created.");
		
		if(rowCount == 0) {
			logger.info("No Import table present. Creating one.");
			// create unique_ids table
			jdbcTemplate.execute(createTableSql);
			logger.info("Import Table Created.");
		}
		
		logger.info("Import table present.");
	}
	
}
