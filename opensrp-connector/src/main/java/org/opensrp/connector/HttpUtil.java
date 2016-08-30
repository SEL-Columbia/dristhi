package org.opensrp.connector;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.opensrp.common.util.HttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mysql.jdbc.StringUtils;

@Component
public class HttpUtil {

    private final static DefaultHttpClient httpClient = init();

    @SuppressWarnings("deprecation")
	public static DefaultHttpClient init()  {
    	try {
	    	//TODO add option to ignore cetificate validation in opensrp.prop
	    	KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			CustomCertificateSSLSocketFactory sf = new CustomCertificateSSLSocketFactory(trustStore);
			sf.setHostnameVerifier(CustomCertificateSSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			
	        BasicHttpParams basicHttpParams = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(basicHttpParams, 30000);
	        HttpConnectionParams.setSoTimeout(basicHttpParams, 60000);
	
	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));
	
	        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(basicHttpParams, registry);
	        return new DefaultHttpClient(connectionManager, basicHttpParams);
    	} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }

    public static HttpResponse post(String url, String payload, String data, String username,String password) {
        return post(url, payload, data, username, password, "application/json");
    }
    
    public static HttpResponse post(String url, String payload, String data, String username,String password, String contentType) {
        try {
        	HttpPost request = (HttpPost) makeConnection(url, payload, RequestMethod.POST, true, username, password);
            request.setHeader(HTTP.CONTENT_TYPE, contentType);
            StringEntity entity = new StringEntity(data);
            System.out.println(data);
            entity.setContentEncoding(contentType);
            request.setEntity(entity);
            org.apache.http.HttpResponse response = httpClient.execute(request);
            return new HttpResponse(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK, IOUtils.toString(response.getEntity().getContent()));
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static HttpResponse get(String url, String payload, String username, String password) {
        try {
        	HttpGet request = (HttpGet) makeConnection(url, payload, RequestMethod.GET, true, username, password);
            org.apache.http.HttpResponse response = httpClient.execute(request);
            return new HttpResponse(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK, IOUtils.toString(response.getEntity().getContent()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static HttpRequestBase makeConnection(String url, String payload, RequestMethod method, boolean useBasicAuth, String username, String password) throws URISyntaxException {
    	String charset = "UTF-8";

        if(url.endsWith("/")){
        	url = url.substring(0, url.lastIndexOf("/"));
        }
        url = (url+(StringUtils.isEmptyOrWhitespaceOnly(payload)?"":("?"+payload))).replaceAll(" ", "%20");
    	URI urlo = new URI(url);
    	
    	HttpRequestBase requestBase = null;
    	if(method.equals(RequestMethod.GET)){
    		requestBase = new HttpGet(urlo);
    	}
    	else if(method.equals(RequestMethod.POST)){
    		requestBase = new HttpPost(urlo);
    	}
    	else if(method.equals(RequestMethod.PUT)){
    		requestBase = new HttpPut(urlo);
    	}
    	requestBase.setURI(urlo);
    	requestBase.addHeader("Accept-Charset", charset);
		
		if(useBasicAuth){
			String encoded = new String(Base64.encodeBase64((username+":"+password).getBytes()));
			requestBase.addHeader("Authorization", "Basic "+encoded);
		}
		System.out.println(url);
		return requestBase;
	}
    
    public static String removeEndingSlash(String str){
		return str.endsWith("/")?str.substring(0, str.lastIndexOf("/")):str;
	}
    public static String removeTrailingSlash(String str){
		return str.startsWith("/")?str.substring(1):str;
	}
}
