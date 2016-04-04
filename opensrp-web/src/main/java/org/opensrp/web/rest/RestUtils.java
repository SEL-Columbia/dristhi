package org.opensrp.web.rest;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;

import com.mysql.jdbc.StringUtils;

public class RestUtils {
	public static final String DATE_FORMAT = "dd-MM-yyyy";
	public static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MM-yyyy");
	public static final String DATETIME_FORMAT = "dd-MM-yyyy HH:mm";
	public static final SimpleDateFormat SDTF = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	
	
	public static String getStringFilter(String filter, HttpServletRequest req)
	{
	  return StringUtils.isEmptyOrWhitespaceOnly(req.getParameter(filter)) ? null : req.getParameter(filter);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Enum getEnumFilter(String filter, Class cls, HttpServletRequest req)
	{
	  String filterVal = getStringFilter(filter, req);
	  if (filterVal != null) {
	    return Enum.valueOf(cls, filterVal);
	  }
	  return null;
	}
	
	public static Integer getIntegerFilter(String filter, HttpServletRequest req)
	{
	  String strval = getStringFilter(filter, req);
	  return strval == null ? null : Integer.parseInt(strval);
	}
	
	public static Float getFloatFilter(String filter, HttpServletRequest req)
	{
	  String strval = getStringFilter(filter, req);
	  return strval == null ? null : Float.parseFloat(strval);
	}
	
	public static DateTime getDateFilter(String filter, HttpServletRequest req) throws ParseException
	{
	  String strval = getStringFilter(filter, req);
	  return strval == null ? null : new DateTime(strval);
	}
	
	public static DateTime[] getDateRangeFilter(String filter, HttpServletRequest req) throws ParseException
	{
	  String strval = getStringFilter(filter, req);
	  if(strval == null){
		  return null;
	  }
	  DateTime d1 = new DateTime(strval.substring(0, strval.indexOf(":")));
	  DateTime d2 = new DateTime(strval.substring(strval.indexOf(":")+1));
	  return new DateTime[]{d1,d2};
	}
	
	
	public static void main(String[] args) {
		System.out.println(new DateTime("​1458932400000"));
	}
	
	public static String setDateFilter(Date date) throws ParseException
	{
	  return date == null ? null : SDF.format(date);
	}
	
	public static <T> void verifyRequiredProperties(List<String> properties, T entity) {
		if(properties != null)
		for (String p : properties) {
			Field[] aaa = entity.getClass().getDeclaredFields();
			for (Field field : aaa) {
				if(field.getName().equals(p)){
					field.setAccessible(true);
					try {
						if(field.get(entity) == null || field.get(entity).toString().trim().equalsIgnoreCase("")){
							throw new RuntimeException("A required field "+p+" was found empty");
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
						throw new RuntimeException("A required field "+p+" was not found in resource class");
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
