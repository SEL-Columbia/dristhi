
package org.opensrp.connector.helper;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * The Class ExceptionUtil.
 */
public class ExceptionUtil {

    /**
     * Gets the stack trace.
     *
     * @param e the e
     * @return the stack trace
     */
    public static String getStackTrace(Exception e)
    {
    	try{
	    	String errorMsg=e.getMessage();
	        StringWriter sw = new StringWriter();
	        PrintWriter pw = new PrintWriter(sw, true);
	        e.printStackTrace(pw);
	        pw.flush();
	        sw.flush();
	        return errorMsg+"\n"+sw.toString();
	    	}catch (Exception e1) {
				return e1.toString();
			}
	  }
}
