package org.opensrp.api.domain;

import java.util.ArrayList;
import java.util.List;

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

public class ObsTest {	
    @Test
    @SuppressWarnings("RedundantArrayCreation")
    public void shouldTestConstructor() {    	
        List<Object> values = new ArrayList<>();       
        final Class<?> clazz = Obs.class;
        final Object obj1 = getInstance(clazz, "concept","163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            "daow","886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",values,"no comments","start");
        Affirm.affirmNotNull("Should have created an object", obj1);
        Obs ob = (Obs) obj1;
        Assert.assertNull(ob.getValue());        
    }   
	
    @Test(expected=Exception.class)
    public void shouldReturnNullForgetValue(){
        Obs obs = new Obs();        
        obs.getValue();
    }
    
    @Test(expected=RuntimeException.class)
    public void shouldReturnRuntimeExceptionForgetValue(){
        Obs obs = new Obs();
        List<Object> values = new ArrayList<>();
        values.add(1);
        values.add(2);
        values.add(4);
        obs.withValues(values);
        obs.getValue();    	
    }
    @Test
    public void shouldTestObs(){
        Obs obs = new Obs();
        obs.withFieldType("concept");
        Assert.assertEquals("concept", obs.getFieldType());
        Assert.assertNotSame("concept1", obs.getFieldType());        
        obs.withFieldCode("1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Assert.assertEquals("1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", obs.getFieldCode());
        Assert.assertNotSame("1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB", obs.getFieldCode());
        obs.withFieldDataType("date");
        Assert.assertEquals("date", obs.getFieldDataType());
        Assert.assertNotSame("concept", obs.getFieldDataType());
        obs.withParentCode("162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Assert.assertEquals("162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", obs.getParentCode());
        Assert.assertNotSame("162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAB", obs.getParentCode());
        List<Object> values = new ArrayList<>();
        values.add(1);
        obs.withValues(values);
        Assert.assertEquals(1,obs.getValue());
        obs.withComments("No Comments");
        obs.withFormSubmissionField("pcv1_retro");
        Assert.assertEquals("pcv1_retro", obs.getFormSubmissionField());
        Assert.assertNotSame("pcv1_retro1", obs.getFormSubmissionField());       
        obs.setValue(4);        
    }
    
    @Test
    public void shouldTestAddToValueList(){
        Obs obs = new Obs();
        obs.withValue(null);
        Assert.assertNull(obs.getValue()); 
        
        
    }
    @Test
    public void shouldTestSetterAndGetter() {
        PojoClass pojoClass = PojoClassFactory.getPojoClass(Obs.class);
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
