package org.opensrp.api.domain;

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

public class PhotoTest {
	
    @Test
    @SuppressWarnings("RedundantArrayCreation")
    public void shouldTestConstructorUsingDefaultConstructor() {       
        final Class<?> clazz = Photo.class;
        final Object obj1 = getInstance(clazz);
        Affirm.affirmNotNull("Should have created an object", obj1);        
       
    }   
    
    @Test
    public void shouldTestSetterAndGetter() {
        PojoClass pojoClass = PojoClassFactory.getPojoClass(Photo.class);
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
