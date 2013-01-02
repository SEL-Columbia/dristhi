package org.ei.drishti.integration;

import org.ei.commcare.api.contract.CommCareFormDefinition;
import org.ei.commcare.api.contract.CommCareModuleDefinition;
import org.ei.commcare.api.util.CommCareImportProperties;
import org.ei.drishti.contract.*;
import org.ei.drishti.controller.DrishtiController;
import org.junit.Before;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.*;

import static org.junit.Assert.fail;

public class CommCareImportFormDefinitionsJSONTest {
    private final String definitionsJSONPath = "commcare-import-form-definitions.json";
    private List<CommCareFormDefinition> forms = new ArrayList<>();

    @Before
    public void setUp() {
        Properties properties = new Properties();
        properties.put(CommCareImportProperties.COMMCARE_IMPORT_DEFINITION_FILE, "/" + definitionsJSONPath);
        CommCareImportProperties importProperties = new CommCareImportProperties(properties);

        for (CommCareModuleDefinition moduleDefinition : importProperties.moduleDefinitions().modules()) {
            forms.addAll(moduleDefinition.definitions());
        }
    }

    @Test
    public void everyFormInTheJSONShouldHaveAllTheCorrectMappings() {
        Map<String, Class<?>> classEveryFormMappingConvertsTo = new HashMap<>();

        classEveryFormMappingConvertsTo.put("registerEligibleCouple", EligibleCoupleRegistrationRequest.class);
        classEveryFormMappingConvertsTo.put("updateFamilyPlanningMethod", FamilyPlanningUpdateRequest.class);
        classEveryFormMappingConvertsTo.put("reportFPComplications", FPComplicationsRequest.class);
        classEveryFormMappingConvertsTo.put("registerOutOfAreaANC", OutOfAreaANCRegistrationRequest.class);
        classEveryFormMappingConvertsTo.put("registerMother", AnteNatalCareEnrollmentInformation.class);
        classEveryFormMappingConvertsTo.put("updateANCCareInformation", AnteNatalCareInformation.class);
        classEveryFormMappingConvertsTo.put("updateSubsetOfANCInformation", AnteNatalCareInformationSubset.class);
        classEveryFormMappingConvertsTo.put("updateOutcomeOfANC", AnteNatalCareOutcomeInformation.class);
        classEveryFormMappingConvertsTo.put("updateChildImmunization", ChildImmunizationUpdationRequest.class);
        classEveryFormMappingConvertsTo.put("updatePNCAndChildInformation", PostNatalCareInformation.class);
        classEveryFormMappingConvertsTo.put("updateBirthPlanning", BirthPlanningRequest.class);
        classEveryFormMappingConvertsTo.put("closePNCCase", PostNatalCareCloseInformation.class);
        classEveryFormMappingConvertsTo.put("closeChildCase", ChildCloseRequest.class);
        classEveryFormMappingConvertsTo.put("closeANCCase", AnteNatalCareCloseInformation.class);
        classEveryFormMappingConvertsTo.put("closeEligibleCouple", EligibleCoupleCloseRequest.class);

        assertEveryFormDefinitionInTheJSONHasBeenRepresentedInThisTest(classEveryFormMappingConvertsTo);
        assertThatTheControllerHasTheMethodsCorrespondingToTheseFormNames(DrishtiController.class, classEveryFormMappingConvertsTo);
    }

    private void assertEveryFormDefinitionInTheJSONHasBeenRepresentedInThisTest(Map<String, Class<?>> classEveryFormMappingConvertsTo) {
        for (CommCareFormDefinition formDefinition : forms) {
            String formName = formDefinition.name();
            Class<?> typeUsedForMappingsInForm = classEveryFormMappingConvertsTo.get(formName);

            if (typeUsedForMappingsInForm == null) {
                fail("Missing form. Change this test to add a mapping for the form: " + formName + " above.");
            }

            assertThatRightHandSideOfEveryFormMappingMapsToAFieldInTheObject(formName, typeUsedForMappingsInForm);
        }
    }

    private void assertThatRightHandSideOfEveryFormMappingMapsToAFieldInTheObject(String formName, Class<?> typeUsedForObjectsInForm) {
        ArrayList<CommCareFormDefinition> formDefinitions = findForms(formName);
        for (CommCareFormDefinition formDefinition : formDefinitions) {
            for (String fieldInObject : formDefinition.mappings().values()) {
                try {
                    typeUsedForObjectsInForm.getDeclaredField(fieldInObject);
                } catch (NoSuchFieldException e) {
                    fail("Could not find field: " + fieldInObject + " in class: " + typeUsedForObjectsInForm +
                            ". Check the form: " + formName + " in " + definitionsJSONPath + ".");
                }
            }
        }
    }

    private void assertThatTheControllerHasTheMethodsCorrespondingToTheseFormNames(Class<?> controllerClass, Map<String, Class<?>> classEveryFormMappingConvertsTo) {
        for (Map.Entry<String, Class<?>> formNameToClassEntry : classEveryFormMappingConvertsTo.entrySet()) {
            String formName = formNameToClassEntry.getKey();
            Class<?> parameterTypeOfTheMethod = formNameToClassEntry.getValue();
            ArrayList<CommCareFormDefinition> definition = findForms(formName);
            for (CommCareFormDefinition formDefinition : definition) {
                if (formDefinition.extraMappings().isEmpty()) {
                    ensureMethodPresent(controllerClass, formName, parameterTypeOfTheMethod);
                }
                else {
                    ensureMethodWithMapPresent(controllerClass, formName, parameterTypeOfTheMethod);
                }
            }
        }
    }

    private void ensureMethodWithMapPresent(Class<?> controllerClass, String formName, Class<?> parameterTypeOfTheMethod) {
        try {
            controllerClass.getDeclaredMethod(formName, parameterTypeOfTheMethod, Map.class);
        } catch (NoSuchMethodException e) {
            fail(MessageFormat.format("There should be a method in {0} like this: public void {1}({2} request, Map<String, Map<String, String>> extraData). If it is " +
                    "not present, the dispatcher will not be able to do anything for form submissions of this form: {3}.",
                    controllerClass.getSimpleName(), formName, parameterTypeOfTheMethod.getSimpleName(), formName));
        }
    }

    private void ensureMethodPresent(Class<?> controllerClass, String formName, Class<?> parameterTypeOfTheMethod) {
        try {
            controllerClass.getDeclaredMethod(formName, parameterTypeOfTheMethod);
        } catch (NoSuchMethodException e) {
            fail(MessageFormat.format("There should be a method in {0} like this: public void {1}({2} request). If it is " +
                    "not present, the dispatcher will not be able to do anything for form submissions of this form: {3}.",
                    controllerClass.getSimpleName(), formName, parameterTypeOfTheMethod.getSimpleName(), formName));
        }
    }

    private ArrayList<CommCareFormDefinition> findForms(String formName) {
        ArrayList<CommCareFormDefinition> definitions = new ArrayList<>();
        for (CommCareFormDefinition definition : forms) {
            if (definition.name().equals(formName)) {
                definitions.add(definition);
            }
        }
        return definitions;
    }
}
