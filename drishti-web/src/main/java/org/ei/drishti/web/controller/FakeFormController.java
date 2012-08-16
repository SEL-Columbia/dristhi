package org.ei.drishti.web.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ei.commcare.api.contract.CommCareFormDefinition;
import org.ei.commcare.api.contract.CommCareModuleDefinition;
import org.ei.commcare.api.util.CommCareImportProperties;
import org.ei.commcare.listener.CommCareFormSubmissionRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.*;

@Controller
public class FakeFormController {
    private final CommCareImportProperties properties;
    private final CommCareFormSubmissionRouter dispatcher;

    @Autowired
    public FakeFormController(CommCareImportProperties properties, CommCareFormSubmissionRouter dispatcher) {
        this.properties = properties;
        this.dispatcher = dispatcher;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/form/definitions")
    @ResponseBody
    public List<FormDefinition> getFormDefinitions() throws IOException {
        List<FormDefinition> newDefinitions = new ArrayList<FormDefinition>();

        for (CommCareModuleDefinition commCareModuleDefinition : properties.moduleDefinitions().modules()) {
            for (CommCareFormDefinition definition : commCareModuleDefinition.definitions()) {
                newDefinitions.add(FormDefinition.from(definition));
            }
        }

        return newDefinitions;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/form/submit")
    @ResponseBody
    public String submitFakeCommCareForm(@RequestParam("formName") String formName, @RequestParam("formData") String formData,
                                         @RequestParam("extraFormData") String extraFormData) throws Exception {
        try {
            Map<String, Map<String, String>> extraData = new Gson().fromJson(extraFormData, new TypeToken<Map<String, Map<String, String>>>() {}.getType());
            dispatcher.dispatch("FAKE-FORM", formName, formData, !extraData.isEmpty(), extraData);
        } catch (Exception e) {
            return "Failed: " + e.getMessage();
        }

        return "Success!";
    }

    private static class FormDefinition {
        @JsonProperty
        private String name;
        @JsonProperty
        private Set<Map.Entry<String, String>> mappings;
        @JsonProperty
        private Set<Map.Entry<String, Set<Map.Entry<String, String>>>> extraMappings;

        public static FormDefinition from(CommCareFormDefinition formDefinition) {
            FormDefinition definition = new FormDefinition();
            definition.name = formDefinition.name();
            definition.mappings = formDefinition.mappings().entrySet();
            definition.extraMappings = extraDataMappingsOf(formDefinition);
            return definition;
        }

        private static Set<Map.Entry<String,Set<Map.Entry<String,String>>>> extraDataMappingsOf(CommCareFormDefinition formDefinition) {
            Set<Map.Entry<String, Set<Map.Entry<String, String>>>> extraMappings = new HashSet<>();

            Map<String, Map<String, String>> extraData = formDefinition.extraMappings();
            if (extraData != null && !extraData.isEmpty()) {
                for (Map.Entry<String, Map<String, String>> extraDataTypeToExtraDataFields : extraData.entrySet()) {
                    String typeOfExtraData = extraDataTypeToExtraDataFields.getKey();
                    Set<Map.Entry<String, String>> entriesOfExtraData = extraDataTypeToExtraDataFields.getValue().entrySet();

                    extraMappings.add(new AbstractMap.SimpleEntry<>(typeOfExtraData, entriesOfExtraData));
                }
            }

            return extraMappings;
        }
    }
}
