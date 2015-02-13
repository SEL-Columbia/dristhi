package org.opensrp.connector.helper;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.ei.drishti.common.util.HttpResponse;
import org.springframework.stereotype.Component;

@Component
public class HttpUtil {

    public HttpUtil() {
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, 30000);
        HttpConnectionParams.setSoTimeout(basicHttpParams, 60000);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(basicHttpParams, registry);
    }

    public HttpResponse post(String url, String payload, String data, String contentParamName) {
        try {
            String charset = "UTF-8";;

            if(url.endsWith("/")){
            	url = url.substring(0, url.lastIndexOf("/"));
            }
            
			URL urlo = new URL(url+"?"+payload);
			java.net.HttpURLConnection conn = (HttpURLConnection) urlo.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
	        String CRLF = "\r\n"; // Line separator required by multipart/form-data.

            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			conn.setRequestProperty("Accept-Charset", charset);

            //con.setFixedLengthStreamingMode(request.toString().length());
			//con.addRequestProperty("Referer", "http://blog.dahanne.net");
			// Start the query
//			conn.connect();
			//OutputStream output = con.getOutputStream();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), charset), true); // true = autoFlush, important!

			// Send normal param.
		    writer.append("--" + boundary).append(CRLF);
		    writer.append("Content-Disposition: form-data; name=\""+contentParamName+"\"").append(CRLF);
		    writer.append("Content-Type: text/plain; charset=" + charset ).append(CRLF);
		    writer.append(CRLF);
		    System.out.println(data);
		    writer.append(data).append(CRLF).flush();
		    // End of multipart/form-data.
		    writer.append("--" + boundary + "--").append(CRLF);
		    if (writer != null) writer.close();

		    //if(output != null) output.close(); 
		    	
            return new HttpResponse(conn.getResponseCode() == HttpStatus.SC_OK, IOUtils.toString(conn.getInputStream()));
			
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
