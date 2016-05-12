package org.opensrp.util;

import static java.lang.String.valueOf;
import static org.opensrp.common.AllConstants.Form.ANM_ID;
import static org.opensrp.common.AllConstants.Form.CLIENT_VERSION;
import static org.opensrp.common.AllConstants.Form.ENTITY_ID;
import static org.opensrp.common.AllConstants.Form.FORM_NAME;
import static org.opensrp.common.AllConstants.Form.INSTANCE_ID;
import static org.opensrp.common.AllConstants.Form.SERVER_VERSION;
import static org.opensrp.common.util.EasyMap.create;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.form.domain.FormSubmission;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mysql.jdbc.StringUtils;

public class Utils {

	public static Map<String, String> getStringMapFromJSON(String fields) {
		return new Gson().fromJson(fields, new TypeToken<Map<String, String>>() {
		}.getType());
	}

	public static List<String> getFieldsAsList(Class<?> cls) {
		List<String> fieldList = new ArrayList<>();
		Field[] fieldSet = cls.getDeclaredFields();
		for (Field field : fieldSet) {
			fieldList.add(field.getName());
		}
		return fieldList;
	}

	public static String getZiggyParams(FormSubmission formSubmission) {
		return new Gson().toJson(create(ANM_ID, formSubmission.anmId()).put(INSTANCE_ID, formSubmission.instanceId()).put(ENTITY_ID, formSubmission.entityId())
				.put(FORM_NAME, formSubmission.formName()).put(CLIENT_VERSION, valueOf(formSubmission.clientVersion()))
				.put(SERVER_VERSION, valueOf(formSubmission.serverVersion())).map());
	}

	public static JSONArray getXlsToJson(String path) throws JSONException, IOException {
		FileInputStream inp = new FileInputStream(new File(path));
		// Get the workbook instance for XLS file
		HSSFWorkbook workbook = new HSSFWorkbook(inp);

		// Get first sheet from the workbook
		HSSFSheet sheet = workbook.getSheetAt(0);

		int hrn = getHeaderRowNum(sheet);
		List<String> hr = getRowContent(sheet, hrn);
		// Start constructing JSON.
		JSONArray jarr = new JSONArray();

		for (int i = hrn + 1; i <= sheet.getLastRowNum(); i++) {
			List<String> rc = getRowContent(sheet, i);
			if (!isRowEmpty(rc)) {
				JSONObject row = new JSONObject();
				for (int j = 0; j < hr.size(); j++) {
					row.put(hr.get(j), rc.get(j));
				}
				jarr.put(row);
			}
		}

		workbook.close();
		return jarr;
	}

	private static int getHeaderRowNum(HSSFSheet sheet) {
		Iterator<Row> i = sheet.iterator();
		while (i.hasNext()) {
			Row r = i.next();
			for (Cell c : r) {
				if (!StringUtils.isEmptyOrWhitespaceOnly(c.getStringCellValue())) {
					return r.getRowNum();
				}
			}
		}
		return -1;
	}

	private static boolean isRowEmpty(List<String> rcontent) {
		for (String r : rcontent) {
			if (!StringUtils.isEmptyOrWhitespaceOnly(r)) {
				return false;
			}
		}
		return true;
	}

	private static List<String> getRowContent(HSSFSheet sheet, int rowNum) {
		List<String> hc = new ArrayList<>();
		HSSFRow r = sheet.getRow(rowNum);
		if (r != null && r.getPhysicalNumberOfCells() > 0) {
			for (int i = 0; i < r.getLastCellNum(); i++) {
				Cell c = r.getCell(i);
				hc.add(c == null ? "" : c.getStringCellValue());
			}
		}

		/*
		 * Iterator<Cell> it = sheet.getRow(rowNum).cellIterator(); while
		 * (it.hasNext()) { Cell c = it.next(); hc.add(c.getStringCellValue());
		 * }
		 */
		return hc;
	}

	/**
	 * Connect to the database specified by DatabaseConnectionParams
	 * 
	 * @param dbParams
	 * @return
	 * @throws MalformedURLException
	 */
	public static CouchDbConnector connectToDB(DatabaseConnectionParams dbParams) throws MalformedURLException {
		HttpClient authenticatedHttpClient = new StdHttpClient.Builder().url(dbParams.url.concat(":").concat(dbParams.portNumber)).username(dbParams.userName)
				.password(dbParams.password).build();
		CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);

		CouchDbConnector db = new StdCouchDbConnector(dbParams.dbName, dbInstance);
		return db;

	}

	public class DatabaseConnectionParams {
		String url;
		String portNumber;
		String userName;
		String password;
		String dbName;
	}
}
