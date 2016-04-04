package org.opensrp.web.utils;

import java.text.ParseException;

import org.junit.Test;
import org.opensrp.common.AllConstants.BaseEntity;
import org.opensrp.common.AllConstants.Client;
import org.opensrp.web.rest.RestUtils;
import org.springframework.mock.web.MockHttpServletRequest;

public class RestUtilsTest {
	@Test
	public void test() throws ParseException {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(Client.BIRTH_DATE, "2015-02-01:2016-03-01");
		
		System.out.println(RestUtils.getDateRangeFilter(Client.BIRTH_DATE, req));
	}
}
