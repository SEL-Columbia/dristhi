package org.opensrp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class DataQuery {
	public static final String LUCENE_CLIENT_QUERY = new String("lucene-client-query");

	static {
		for (Field field : DataQuery.class.getDeclaredFields()) {
			if(field.getName().toLowerCase().endsWith("_query")){
				try {
					field.setAccessible(true);
			        Field modifiers = field.getClass().getDeclaredField("modifiers");
			        modifiers.setAccessible(true);
					modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
					field.set(null, getQuery(field.get(null).toString()));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static String getQuery(String fileName){
		try{
			InputStream in = DataQuery.class.getResourceAsStream("/queries/"+fileName);
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			StringBuilder stringJson = new StringBuilder();
	
			int chunksize = 1024;
			char[] charBuffer = new char[chunksize];
		    int count = 0;
	
		    do {
		    	count = r.read(charBuffer, 0, chunksize);
	
		    	if (count >= 0) {
		    		stringJson.append(charBuffer, 0, count);
		    	}
		    } while (count>0);
		    
		    r.close();
		        
			return stringJson.toString();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(LUCENE_CLIENT_QUERY);
	}
}
