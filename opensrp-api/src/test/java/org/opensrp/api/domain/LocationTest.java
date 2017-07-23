package org.opensrp.api.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

public class LocationTest {
	
    @Test
    @SuppressWarnings("RedundantArrayCreation")
    public void shouldTestConstructorUsingDefaultConstructor() {
        String locationId = "locationId";
        String name = "name";
        Address	address = new Address();
        Location parentLocation = new Location();
        Map<String, String> identifiers = new HashMap<>();
        Set<String> tags = new HashSet<>();
        Map<String, Object> attributes = new HashMap<>();
        final Class<?> clazz = Location.class;
        final Object obj1 = getInstance(clazz, locationId,name,address,identifiers,parentLocation,tags,attributes);
        Affirm.affirmNotNull("Should have created an object", obj1);
        final Location obj2 = (Location) getInstance(clazz,locationId,name,address,identifiers,parentLocation,tags,attributes);
        final Object obj3 = getInstance(clazz,locationId,name,address,parentLocation);
        Affirm.affirmTrue("Should have created a different object", obj1 != obj3);	    
        obj2.removeAttribute("name");
        obj2.setAttributes(null);
        obj2.addAttribute("name", "value");	   
        Assert.assertEquals(obj2.getAttribute("name"), "value");	   
        obj2.setIdentifiers(null);
        obj2.addIdentifier("identifierType", "identifier");
        Assert.assertEquals(obj2.getIdentifier("identifierType"), "identifier");
        obj2.removeIdentifier("identifierType");	    
        obj2.setTags(null);
        obj2.addTag("tag");
        Assert.assertEquals(obj2.getTags().size(), 1);
        obj2.removeTag("tag");	    
        obj2.withAddress(address);
        obj2.withIdentifiers(identifiers);
        obj2.withTags(tags);
        obj2.withAttributes(attributes);        
        Assert.assertEquals(obj2.getAttributes(), new HashMap<>());
	    
    }   
    
    @Test
    public void shouldTestSetterAndGetter() {
        PojoClass pojoClass = PojoClassFactory.getPojoClass(Location.class);
        Validator pojoValidator = ValidatorBuilder.create()
            .with(new SetterMustExistRule())
            .with(new GetterMustExistRule())
            .with(new GetterTester())
            .with(new SetterTester())
            .build();

        pojoValidator.validate(pojoClass);
    }

    private Object getInstance(final Class<?> clazz, final Object... parameters) {
        final PojoClass pojoClass = PojoClassFactory.getPojoClass(clazz);
        return InstanceFactory.getInstance(pojoClass, parameters);
    }

}
