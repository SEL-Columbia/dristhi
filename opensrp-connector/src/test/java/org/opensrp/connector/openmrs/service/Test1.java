package org.opensrp.connector.openmrs.service;

import org.junit.Test;
import org.opensrp.connector.HttpUtil;

public class Test1 {

	//@Test
	public void test() {
		System.out.println(HttpUtil.post("http://localhost:8383/opensrp/rest/rapid/client/cv", 
				"identifier=20150211132&firstName=baba&lastName=deena&gender=male&birthdate=2015-10-12", "", "demotest", "Admin123").body());
		System.out.println(HttpUtil.post("http://localhost:8383/opensrp/rest/rapid/client/uv", 
				"clientId=20150211132&location=Korangi&vaccine=bcg&date=2015-10-12", "", "demotest", "Admin123").body());

	}
}
