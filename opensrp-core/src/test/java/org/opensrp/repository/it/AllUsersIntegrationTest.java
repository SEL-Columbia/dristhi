package org.opensrp.repository.it;

import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.repository.AllBaseEntities;
import org.opensrp.repository.AllUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class AllUsersIntegrationTest {


	@Autowired
	private AllUsers allUsers;
	@Autowired
	private AllBaseEntities allBaseEntities;

	@Before
	public void setUp() throws Exception {
		initMocks(this);
	}
	
	@Test
	public void shouldAddUser()throws Exception
	{
		List<String> permissions = new ArrayList<>();
		permissions.add("add");
		permissions.add("edit");
		permissions.add("delete");
		
		List<String> roles = new ArrayList<>();
		roles.add("tlp");

		org.opensrp.domain.BaseEntity baseEntity = allBaseEntities.findByBaseEntityId("0001");
		
	/*	org.opensrp.domain.User domainUser = new org.opensrp.domain.User(
				"0001", "FWA1", "77ba08ff5832b6c4143c51160b7e9bab7b72d98a",
				"1efa26b9-9cc9-40df-aecb-14836069a8f8",
				baseEntity.getFirstName(),baseEntity.getMiddleName(),baseEntity.getLastName(),
				baseEntity.getBirthdate(),baseEntity.getBirthdateApprox(),baseEntity.getGender())
				.withStatus("active")
				.withPermissions(permissions)
				.withRoles(roles);*/
		
		org.opensrp.domain.User domainUser = new org.opensrp.domain.User()
											.withBaseEntityId("0001")
											.withUsername("FWA2")
											.withPassword("77ba08ff5832b6c4143c51160b7e9bab7b72d98a")
											.withSalt("1efa26b9-9cc9-40df-aecb-14836069a8f8")
											.withStatus("active")
											.withPermissions(permissions)
											.withRoles(roles);
		
		//domainUser.isDefaultAdmin();
		
		allUsers.add(domainUser);
		
	}
}
