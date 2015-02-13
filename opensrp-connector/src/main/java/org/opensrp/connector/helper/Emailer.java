/*package org.opensrp.connector.helper;

import java.io.InputStream;
import java.util.Properties;

import org.ihs.emailer.EmailEngine;

public class Emailer {
	public static boolean EMAILER_INSTANTIATED;
	
	public static boolean intantiateEmailer() {
		try{
			System.out.println(">>>>LOADING SYSTEM PROPERTIES...");
			InputStream f = Thread.currentThread().getContextClassLoader().getResourceAsStream("emailer.properties");
			Properties prop = new Properties();
			prop.load(f);
			System.out.println("......PROPERTIES LOADED SUCCESSFULLY......");
			
			EmailEngine.instantiateEmailEngine(prop);
			System.out.println("......EMAIL ENGINE LOADED SUCCESSFULLY......");
			
			return EMAILER_INSTANTIATED = true;	
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return EMAILER_INSTANTIATED = false;
	}
}
*/