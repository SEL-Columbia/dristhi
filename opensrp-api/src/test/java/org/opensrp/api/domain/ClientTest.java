package org.opensrp.api.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.opensrp.common.Gender;

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

public class ClientTest {
	
    @Test
    @SuppressWarnings("RedundantArrayCreation")
    public void shouldTestConstructor() {
        List<Address> addresses = new ArrayList<>();
        Map<String, String> identifiers = new HashMap<>();
        Map<String, Object> attributes = new HashMap<>();
        final Class<?> clazz = Client.class;
        final Object obj1 = getInstance(clazz, "ooo-02we-98","lieo","daow","sonn",new Date(),new Date(),
            true, true,"Male","identifierType","identifier");
        Affirm.affirmNotNull("Should have created an object", obj1);        
        final Object obj3 = getInstance(clazz,"ooo-02we-98","lieo","daow","sonn",new Date(),new Date(),
            true, true,"Male",addresses,identifiers,attributes);
        Affirm.affirmTrue("Should have created a different object", obj1 != obj3);
        Affirm.affirmNotNull("Should have created an object", obj3); 
        final Object obj4 = getInstance(clazz,"ooo-02we-98","lieo","daow","sonn",new Date(),new Date(),
                true, true,"Male");
        Affirm.affirmNotNull("Should have created an object", obj4); 
    }   
	
    @Test
    public void shouldTestEvent(){
        Client Client1 = new Client();
        Client1.withFirstName("robina");
        Assert.assertEquals("robina", Client1.getFirstName());
        Assert.assertNotSame("Not Same", "robin", Client1.getFirstName());
        Client1.withMiddleName("jack");
        Assert.assertEquals("jack", Client1.getMiddleName());
        Assert.assertNotSame("Not Same", "robin", Client1.getMiddleName());
        Client1.withLastName("mona");
        Assert.assertEquals("mona", Client1.getLastName());
        Assert.assertNotSame("Not Same", "robin", Client1.getLastName());
        Client1.withName("Robin", "jack", "mon");
        
        Assert.assertEquals("Robin", Client1.getFirstName());
        Assert.assertNotSame("Not Same", "robins", Client1.getFirstName());
        
        Client1.withBirthdate(new Date(),true);
        Assert.assertEquals(new Date(), Client1.getBirthdate());
        Date dt = new Date();
        Calendar c = Calendar.getInstance(); 
        c.setTime(dt); 
        c.add(Calendar.DATE, 1);
        dt = c.getTime();
        Assert.assertNotSame("Not Same", dt, Client1.getBirthdate());
        Client1.withDeathdate(new Date(), true);
        
        Assert.assertEquals(new Date(), Client1.getDeathdate());
        Assert.assertNotSame("Not Same", dt, Client1.getDeathdate());
        Client1.withGender("Male");
        Assert.assertEquals("Male", Client1.getGender());
        Assert.assertNotSame("FEMALE", dt, Client1.getGender());
        Client1.withGender(Gender.FEMALE);
        Assert.assertEquals("FEMALE", Client1.getGender());
        Assert.assertNotSame("male", dt, Client1.getGender());
        Client Client2 = new Client("ffrrr-0rrffff-ggg");
        
    }
    @Test
    public void shouldTestSetterAndGetter() {
        PojoClass pojoClass = PojoClassFactory.getPojoClass(Client.class);
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
