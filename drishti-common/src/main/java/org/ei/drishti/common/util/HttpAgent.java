package org.ei.drishti.common.util;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Security;

@Component
public class HttpAgent {

    private final DefaultHttpClient httpClient;

    public HttpAgent() {
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, 30000);
        HttpConnectionParams.setSoTimeout(basicHttpParams, 60000);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", sslSocketFactoryWithDrishtiCertificate(), 443));

        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(basicHttpParams, registry);
        httpClient = new DefaultHttpClient(connectionManager, basicHttpParams);
    }

    public HttpResponse post(String url, String data, String contentType) {
        HttpPost request = new HttpPost(url);
        try {
            request.setHeader("Content-Type", contentType);
            request.setEntity(new StringEntity(data));
            org.apache.http.HttpResponse response = httpClient.execute(request);
            return new HttpResponse(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK, IOUtils.toString(response.getEntity().getContent()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse get(String url) {
        HttpGet request = new HttpGet(url);
        try {
            org.apache.http.HttpResponse response = httpClient.execute(request);
            return new HttpResponse(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK, IOUtils.toString(response.getEntity().getContent()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SocketFactory sslSocketFactoryWithDrishtiCertificate() {
        try {
            Security.addProvider(new BouncyCastleProvider());
            KeyStore trustedKeystore = KeyStore.getInstance("BKS");
            InputStream inputStream = this.getClass().getResourceAsStream("/drishti_client.keystore");
            try {
                trustedKeystore.load(inputStream, "phone red pen".toCharArray());
            } finally {
                inputStream.close();
            }

            SSLSocketFactory socketFactory = new SSLSocketFactory(trustedKeystore);
            final X509HostnameVerifier oldVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            socketFactory.setHostnameVerifier(oldVerifier);
            return socketFactory;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
