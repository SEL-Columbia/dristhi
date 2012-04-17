package org.ei.drishti.integration;

import org.ei.commcare.api.contract.CommCareFormDefinition;
import org.ei.commcare.api.contract.CommCareFormDefinitions;
import org.ei.drishti.contract.*;
import org.ei.drishti.controller.DrishtiController;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.dao.MotechJsonReader;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

public class CommCareImportFormDefinitionsJSONTest {
    private final String definitionsJSONPath = "commcare-import-form-definitions.json";
    private CommCareFormDefinitions forms;

    @Before
    public void setUp() {
        forms = (CommCareFormDefinitions) new MotechJsonReader().readFromFile("/" + definitionsJSONPath, CommCareFormDefinitions.class);
    }

    @Test
    public void everyFormInTheJSONShouldHaveAllTheCorrectMappings() {
        Map<String, Class<?>> classEveryFormMappingConvertsTo = new HashMap<String, Class<?>>();

        classEveryFormMappingConvertsTo.put("registerMother", AnteNatalCareEnrollmentInformation.class);
        classEveryFormMappingConvertsTo.put("updateANCCareInformation", AnteNatalCareInformation.class);
        classEveryFormMappingConvertsTo.put("updateOutcomeOfANC", AnteNatalCareOutcomeInformation.class);
        classEveryFormMappingConvertsTo.put("closeANCCase", AnteNatalCareCloseInformation.class);
        classEveryFormMappingConvertsTo.put("registerChild", ChildRegistrationInformation.class);
        classEveryFormMappingConvertsTo.put("registerNewChild", ChildRegistrationRequest.class);

        assertEveryFormDefinitionInTheJSONHasBeenRepresentedInThisTest(classEveryFormMappingConvertsTo);
        assertThatTheControllerHasTheMethodsCorrespondingToTheseFormNames(DrishtiController.class, classEveryFormMappingConvertsTo);
    }

    private void assertEveryFormDefinitionInTheJSONHasBeenRepresentedInThisTest(Map<String, Class<?>> classEveryFormMappingConvertsTo) {
        for (CommCareFormDefinition formDefinition : forms.definitions()) {
            String formName = formDefinition.name();
            Class<?> typeUsedForMappingsInForm = classEveryFormMappingConvertsTo.get(formName);

            if (typeUsedForMappingsInForm == null) {
                fail("Missing form. Change this test to add a mapping for the form: " + formName + " above.");
            }

            assertThatRightHandSideOfEveryFormMappingMapsToAFieldInTheObject(formName, typeUsedForMappingsInForm);
        }
    }

    private void assertThatRightHandSideOfEveryFormMappingMapsToAFieldInTheObject(String formName, Class<?> typeUsedForObjectsInForm) {
        for (String fieldInObject : findForm(formName).mappings().values()) {
            try {
                typeUsedForObjectsInForm.getDeclaredField(fieldInObject);
            } catch (NoSuchFieldException e) {
                fail("Could not find field: " + fieldInObject + " in class: " + typeUsedForObjectsInForm +
                        ". Check the form: " + formName + " in " + definitionsJSONPath + ".");
            }
        }
    }

    private void assertThatTheControllerHasTheMethodsCorrespondingToTheseFormNames(Class<?> controllerClass, Map<String, Class<?>> classEveryFormMappingConvertsTo) {
        for (Map.Entry<String, Class<?>> formNameToClassEntry : classEveryFormMappingConvertsTo.entrySet()) {
            String formName = formNameToClassEntry.getKey();
            Class<?> parameterTypeOfTheMethod = formNameToClassEntry.getValue();
            try {
                controllerClass.getDeclaredMethod(formName, parameterTypeOfTheMethod);
            } catch (NoSuchMethodException e) {
                fail(MessageFormat.format("There should be a method in {0} like this: public void {1}({2}). If it is " +
                        "not present, the dispatcher will not be able to do anything for form submissions of this form: {3}.",
                        controllerClass.getSimpleName(), formName, parameterTypeOfTheMethod.getSimpleName(), formName));
            }
        }
    }

    private CommCareFormDefinition findForm(String formName) {
        for (CommCareFormDefinition definition : forms.definitions()) {
            if (definition.name().equals(formName)) {
                return definition;
            }
        }
        return null;
    }
}
