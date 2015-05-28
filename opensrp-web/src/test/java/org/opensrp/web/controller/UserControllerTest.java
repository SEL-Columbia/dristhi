package org.opensrp.web.controller;

import java.io.IOException;

import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.MockitoAnnotations.initMocks;

import org.opensrp.api.domain.BaseEntity;
import org.opensrp.api.domain.User;
import org.opensrp.api.util.LocationTree;
import org.opensrp.connector.openmrs.service.OpenmrsLocationService;
import org.opensrp.connector.openmrs.service.OpenmrsUserService;
import org.opensrp.web.security.DrishtiAuthenticationProvider;
import org.springframework.security.core.Authentication;

import com.google.gson.Gson;

public class UserControllerTest extends TestResourceLoader{

	public UserControllerTest() throws IOException {
		super();
	}

	private OpenmrsLocationService locationservice;
	
	@Mock
	private OpenmrsUserService userservice;
	@Mock
	private UserController controller;
	@Mock
	private DrishtiAuthenticationProvider auth;
	
	@Mock
	PasswordEncoder au;

	@Before
    public void setUp() throws Exception {
		initMocks(this);
		this.locationservice = new OpenmrsLocationService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
		this.userservice = new OpenmrsUserService(openmrsOpenmrsUrl, openmrsUsername, openmrsPassword);
        this.controller = new UserController(locationservice, userservice, auth);
	}
	
	@Test
	public void test() throws JSONException{
		User u = new User().withBaseEntity(new BaseEntity().withAttribute("Location", "cd4ed528-87cd-42ee-a175-5e7089521ebd"));
		when(auth.getDrishtiUser(any(Authentication.class))).thenReturn(u);
		when(controller.currentUser()).thenReturn(u);
		JSONObject resp = new JSONObject(controller.authenticate().getBody());
		Assert.assertTrue(new Gson().fromJson(resp.getJSONObject("locations").toString(), LocationTree.class).hasLocation("cd4ed528-87cd-42ee-a175-5e7089521ebd"));
		//System.out.println(new Gson().toJson(locationservice.getLocationTreeOf(new String[]{/*"karachi","sindh",*/"sripur","faridpur"})));
	}
}
