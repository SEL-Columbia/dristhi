package org.opensrp.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.opensrp.api.domain.User;

public class UserTest {

	@Test
	public void userTest(){
		User u = new User("entity id 1")
			.withUsername("username11")
			.withPermission("view")
			.withPermission("edit")
			.withPermission("add")
			.withRole("r1")
			.withRole("r2")
			.withStatus("active");
		u.withAttribute("attr1", "strval")
			.withAttribute("attr2", 2)
			.withAttribute("attr3", true);
		
		assertEquals("", (String)u.getAttribute("attr1"), "strval");
		assertEquals("", (int)u.getAttribute("attr2"), 2);
		assertEquals("", (boolean)u.getAttribute("attr3"), true);
		assertNull("", u.getAttribute("attr8"));
		u.removeAttribute("attr3");
		assertNull("", u.getAttribute("attr3"));
		assertEquals("", u.getStatus(), "active");
		assertEquals("", u.hasPermission("view"), true);
		assertEquals("", u.hasPermission("edit"), true);
		assertEquals("", u.hasPermission("add"), true);
		assertEquals("", u.hasPermission("delete"), false);
		assertEquals("", u.hasRole("r1"), true);
		assertEquals("", u.hasRole("r2"), true);
		assertEquals("", u.hasRole("r4"), false);
		assertEquals("", u.hasAdminRights(), false);
		assertEquals("", u.isDefaultAdmin(), false);

		u.addRole("admin");
		assertEquals("", u.hasAdminRights(), true);
		assertEquals("", u.isDefaultAdmin(), false);

		u.addRole("administrator");
		assertEquals("", u.hasAdminRights(), true);
		assertEquals("", u.isDefaultAdmin(), false);
		
		u.removeRole("admin");
		u.setUsername("admin");
		assertEquals("", u.hasAdminRights(), true);
		assertEquals("", u.isDefaultAdmin(), true);
		
		u.removeRole("administrator");
		assertEquals("", u.hasAdminRights(), false);
		assertEquals("", u.isDefaultAdmin(), false);
		
	}
	
	
}
