package org.opensrp.api.domain;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.construct.InstanceFactory;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.affirm.Affirm;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

public class UserTest {

    @Test
    @SuppressWarnings("RedundantArrayCreation")
    public void shouldTestConstructor() {
        List<Address> addresses = new ArrayList<>();
        Map<String, String> identifiers = new HashMap<>();
        Map<String, Object> attributes = new HashMap<>();
        final Class<?> clazz = User.class;        
        final Object user1 = getInstance(clazz, "ooo-02we-98","lieo","daow","sonn");
        Affirm.affirmNotNull("Should have created an object", user1);  
        List<String> roles = new ArrayList<>();
        roles.add("Admin");
        List<String> permissions = new ArrayList<>();
        permissions.add("Login");
        final Object user2 = getInstance(clazz, "ooo-02we-98","lieo","userpassword","sonn","Active",roles,permissions);
        Affirm.affirmNotNull("Should have created an object", user2); 
        final Object user3 = getInstance(clazz, "ooo-02we-98","lieo","preferredName","password","sonn","Active",roles,permissions);
        Affirm.affirmNotNull("Should have created an object", user3); 

    }   
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
		
        User newUser =new User();
        List<String> roles = new ArrayList<>();
        roles.add("Admin");
        roles.add("FWA");
        newUser.withRoles(roles);
        Assert.assertEquals(roles, newUser.getRoles());
        User newUser1 =new User();
        List<String> permissions = new ArrayList<>();
        permissions.add("Login");
        permissions.add("user creation");
        newUser1.withPermissions(permissions);
        Assert.assertEquals(permissions, newUser1.getPermissions());
        u.withPassword("12345678");
        Assert.assertEquals("12345678", u.getPassword());
        Assert.assertNotSame("12345679", u.getPassword());
        u.withSalt("saltValue");
        Assert.assertEquals("saltValue", u.getSalt());
        Assert.assertNotSame("12345679", u.getSalt());      

	}

    @Test
    public void shouldTestSetterAndGetter() {
        PojoClass pojoClass = PojoClassFactory.getPojoClass(User.class);
        Validator pojoValidator = ValidatorBuilder.create()
            .with(new SetterMustExistRule())
            .with(new GetterMustExistRule())
            .with(new GetterTester())
            .with(new SetterTester())
            .build();

        pojoValidator.validate(pojoClass);
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
        User newUser = new User();
        newUser.addRole("newRole");
        Assert.assertEquals(asList("newRole"), newUser.getRoles());
        User newUser1 = new User();
        newUser1.addPermission("Login");
        Assert.assertEquals(asList("Login"), newUser1.getPermissions());
    }

    @Test
    public void testUserActiveness() {
        User user = new User("entity id 1")
            .withStatus("active");
        assertEquals("active", user.getStatus() );
        user.setStatus("inactive");
        assertEquals("inactive", user.getStatus());

    }
    
    @Test
    public void shouldAddPermission(){
    	
    	
    }
    private Object getInstance(final Class<?> clazz, final Object... parameters) {
        final PojoClass pojoClass = PojoClassFactory.getPojoClass(clazz);
        return InstanceFactory.getInstance(pojoClass, parameters);
    }
	
}
