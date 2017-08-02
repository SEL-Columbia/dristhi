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

public class BaseEntityTest {
	
	
    @Test
    public void shouldTestBaseEntity(){
        BaseEntity baseEntity = new BaseEntity();
        Assert.assertNull(baseEntity.getIdentifier(null));
        Assert.assertNull(baseEntity.getAttribute(null));
        BaseEntity baseEntity1= new BaseEntity("oioi-ojhghhg-88777ddd");
        Map<String, String> identifiers = new HashMap<>();
        identifiers.put("identifier", "value");
        BaseEntity baseEntity2 = new BaseEntity("oioi-ojhghhg-88777ddd", identifiers);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("Home_Facility", "5bf3b4ca-9482-4e85-ab7a-0c44e4edb329");
        BaseEntity baseEntity3 = new BaseEntity("oioi-ojhghhg-88777ddd", identifiers, attributes);
        List<Address> addresses = new ArrayList<>();
        Address address = new Address();
        address.setAddressType("usual_residence");
        addresses.add(address);
        BaseEntity baseEntity4 = new BaseEntity("oioi-ojhghhg-88777ddd", identifiers, attributes, addresses);
        
        baseEntity.addAttribute("CHW_Phone_Number", "0193456667");
        baseEntity.addAddress(address);
        
        Assert.assertEquals("0193456667", baseEntity.getAttribute("CHW_Phone_Number"));
        Assert.assertNull(baseEntity.getAttribute(null));
        baseEntity.removeAttribute("CHW_Phone_Number");
        baseEntity.addIdentifier("ZEIR_ID", "101304-4");
        Assert.assertEquals("101304-4", baseEntity.getIdentifier("ZEIR_ID"));
        Assert.assertEquals("101304-4", baseEntity.getIdentifierMatchingRegex("ZEIR_ID"));
        baseEntity.removeIdentifier("ZEIR_ID");
        baseEntity.withBaseEntityId("fff-uuur-8utt");        
		
    }
	
    @Test
    public void shouldTestIdentifier(){
        BaseEntity baseEntity = new BaseEntity();
        baseEntity.withIdentifier("Zeir", "1234");
        Assert.assertEquals("1234", baseEntity.getIdentifier("Zeir"));
        Assert.assertNotSame("1234s", baseEntity.getIdentifier("Zeir"));
        
        Map<String, String> identifiers = new HashMap<>();
        identifiers.put("ZEIR_ID", "101304-4");
        baseEntity.withIdentifiers(identifiers);
        Assert.assertEquals("101304-4", baseEntity.getIdentifier("ZEIR_ID"));
        Assert.assertNotSame("1234s", baseEntity.getIdentifier("ZEIR_ID"));
		
    }
    @Test
    public void shouldTestAttributeAndAttribute(){
        BaseEntity baseEntity = new BaseEntity();
        List<Address> addresses = new ArrayList<>();
        Address address = new Address();
        address.setAddressType("usual_residence");
        addresses.add(address);
        baseEntity.withAddress(address);
        Assert.assertEquals(addresses, baseEntity.getAddresses());
        baseEntity.withAddresses(addresses);
        BaseEntity baseEntityForAttribute = new BaseEntity();
        baseEntityForAttribute.withAttribute("CHW_Phone_Number", "n/a");
        
        Assert.assertEquals("n/a", baseEntityForAttribute.getAttribute("CHW_Phone_Number"));
        Assert.assertNotSame("n/aa", baseEntityForAttribute.getAttribute("CHW_Phone_Number"));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("CHW_Phone_Number", "1234");
        baseEntityForAttribute.withAttributes(attributes);
        Assert.assertEquals("1234", baseEntityForAttribute.getAttribute("CHW_Phone_Number"));
        
    }
    @Test
    public void shouldTestSetterAndGetter() {
        PojoClass pojoClass = PojoClassFactory.getPojoClass(BaseEntity.class);
        Validator pojoValidator = ValidatorBuilder.create()
            .with(new SetterMustExistRule())
            .with(new GetterMustExistRule())
            .with(new GetterTester())
            .with(new SetterTester())
            .build();

        pojoValidator.validate(pojoClass);
    }
   
}
