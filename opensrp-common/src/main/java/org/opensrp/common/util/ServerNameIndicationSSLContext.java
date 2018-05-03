package org.opensrp.common.util;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

public class ServerNameIndicationSSLContext extends SSLContext {

	String hostname;

	public ServerNameIndicationSSLContext(String hostname, int port) {
		super(new ServerNameIndicationSSLContextSpi(hostname, port), null, "Default");
		this.hostname = hostname;
	}

	public SSLParameters getParametersForSNI() {
		SSLParameters params = null;
		try {
			params = SSLContext.getDefault().getDefaultSSLParameters();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		List<SNIServerName> sNIServerNameList = new ArrayList<SNIServerName>();
		sNIServerNameList.add(new SNIHostName(hostname));
		params.setServerNames(sNIServerNameList);
		params.setEndpointIdentificationAlgorithm("HTTPS");
		return params;
	}

}