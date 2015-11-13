/**
  * Contributors: muhammad.ahmed@ihsinformatics.com
 */
package org.opensrp.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;

 
public class NetClientGet {
 
	// http://localhost:8080/RESTfulExample/json/product/get
	public String  convertToString(String dataurl ,String username , String formId ) {
 
	  try {

		URL url = new URL("https://enketo.formhub.org/transform/get_html_form");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
                String url1="server_url=https://ona.io/"+username+"&form_id="+formId;  //+dataurl;
  //String url2="server_url=https://ona.io/"+username+"/forms/"+formId+"/form.json&callback=CALLBACKFN";            
                
          //    System.out.println(url2);
//"server_url=https://ona.io/ahmedihs&form_id=crvs_birth_notification"
        conn.getOutputStream().write(url1.getBytes());
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}
              //      System.out.println(conn.getResponseCode());
		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
 
		StringBuilder sb = new StringBuilder();
		String output = null;
	//	System.out.println("Output from Server .... \n");
                
		while ((output = br.readLine()) != null) {
		//	System.out.println(output);
			sb.append(output);
		}
 
		/*FileOutputStream fop = new FileOutputStream("d:\\tlocal201312161608.xml");
		fop.write(sb.toString().getBytes());
		conn.disconnect();*/
                return sb.toString();
 
	  } catch (MalformedURLException e) {
 
		e.printStackTrace();
 
	  } catch (IOException e) {
 
		e.printStackTrace();
 
	  }
          return null;
	}
        
        public byte[] downloadJson(String username,String password, String formPk) throws IOException {
		 
        	try{
			String authString = username + ":" + password;
			System.out.println("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
			System.out.println("Base64 encoded auth string: " + authStringEnc);
		  //The file that you want to download
		 String url2="https://ona.io/api/v1/forms/"+formPk+"/form.json";            
           URL link = new URL(url2);
           URLConnection urlConnection = link.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			String result = sb.toString();

			System.out.println("*** BEGIN ***");
			System.out.println(result);
			System.out.println("*** END ***");
			
			return result.getBytes();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
                    return null;
	}

        public String getModel(String data){
                     
            return data.substring(data.indexOf("<model>") , data.indexOf("</model>")+8);
            
        }
        
        public String getForm(String data){
       
        return data.substring(data.indexOf("<form ") , data.indexOf("</form>")+7);
        }
        
 
}