package org.ei.drishti.web.controller;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ei.commcare.api.contract.CommCareFormDefinition;
import org.ei.commcare.api.util.CommCareImportProperties;
import org.ei.commcare.listener.CommCareFormSubmissionRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        for (CommCareFormDefinition definition : properties.definitions().definitions()) {
            newDefinitions.add(FormDefinition.from(definition));
        }

        return newDefinitions;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/form/submit")
    @ResponseBody
    public String submitFakeCommCareForm(@RequestParam("formName") String formName, @RequestParam("formData") String formData) throws Exception {
        try {
            dispatcher.dispatch("FAKE-FORM", formName, formData);
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

        public static FormDefinition from(CommCareFormDefinition formDefinition) {
            FormDefinition definition = new FormDefinition();
            definition.name = formDefinition.name();
            definition.mappings = formDefinition.mappings().entrySet();
            return definition;
        }
    }
}
