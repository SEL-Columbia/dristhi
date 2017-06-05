package org.opensrp.api.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.opensrp.api.domain.User;

public class UserTest {

	@Test
	public void testUserAttributes(){
		User u = new User("entity id 1");
		u.withAttribute("attr1", "strval")
			.withAttribute("attr2", 2)
			.withAttribute("attr3", true);
		
		assertEquals("strval", (String)u.getAttribute("attr1"));
		assertEquals(2, (int)u.getAttribute("attr2"));
		assertEquals(true, (boolean)u.getAttribute("attr3"));
		assertNull(u.getAttribute("attr8"));
		u.removeAttribute("attr3");
		assertNull(u.getAttribute("attr3"));

	}


	@Test
    public void testUserPermissions() {
        User user = new User("entity id 1")
                .withUsername("username11")
                .withPermission("view")
                .withPermission("edit")
                .withPermission("add")
                .withPermission("delete");

        assertEquals(true, user.hasPermission("view"));
        assertEquals(true, user.hasPermission("edit"));
        assertEquals(true, user.hasPermission("add"));
        assertEquals(true, user.hasPermission("delete"));

        user.removePermission("delete");
        user.removePermission("dsfda");

        assertEquals(false, user.hasPermission("delete"));
    }

    @Test
    public void testUserRole() {
        User user = new User("entity id 1")
                .withUsername("username11")
                .withRole("r1")
                .withRole("r2");

        assertEquals(true, user.hasRole("r1"));
        assertEquals(true, user.hasRole("r2"));
        assertEquals(false, user.hasRole("r4"));
        assertEquals(false, user.hasAdminRights());
        assertEquals(false, user.isDefaultAdmin());

        user.addRole("admin");
        assertEquals(true, user.hasAdminRights());
        assertEquals(false, user.isDefaultAdmin());

        user.addRole("administrator");
        assertEquals(true, user.hasAdminRights());
        assertEquals(false, user.isDefaultAdmin());

        user.removeRole("admin");
        user.setUsername("admin");
        assertEquals(true, user.hasAdminRights());
        assertEquals(true, user.isDefaultAdmin());

        user.removeRole("administrator");
        assertEquals(false, user.hasAdminRights());
        assertEquals(false, user.isDefaultAdmin());
    }

    @Test
    public void testUserActiveness() {
        User user = new User("entity id 1")
                .withStatus("active");

        assertEquals("active", user.getStatus() );

        user.setStatus("inactive");

        assertEquals("inactive", user.getStatus());

    }
	
}
