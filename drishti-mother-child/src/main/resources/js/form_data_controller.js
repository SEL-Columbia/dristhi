if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.FormDataController = function (entityRelationshipLoader, formDefinitionLoader, formModelMapper, formDataRepository, submissionRouter) {
    var self = this;

    var init = function (params) {
        if (!enketo.hasValue(self.entityRelationshipsJsonDefinition)) {
            self.entityRelationshipsJsonDefinition = entityRelationshipLoader.load();
        }
        if (!enketo.hasValue(self.entitiesDef)) {
            self.entitiesDef = enketo.EntityRelationships(self.entityRelationshipsJsonDefinition).determineEntitiesAndRelations();
        }
        //TODO: if entities if null, consider taking bind_type from params, or formName
        if (!enketo.hasValue(self.formDefinition)) {
            self.formDefinition = formDefinitionLoader.load(params.formName);
        }
    };

    self.get = function (params) {
        init(params);
        return formModelMapper.mapToFormModel(self.entitiesDef, self.formDefinition, params);
    };
    self.save = function (params, data) {
        if (typeof params !== 'object') {
            params = JSON.parse(params);
        }
        if (typeof data !== 'object') {
            data = JSON.parse(data);
        }
        init(params);
        if (enketo.hasValue(self.entitiesDef) && self.entitiesDef.length != 0) {
            formModelMapper.mapToEntityAndSave(self.entitiesDef, data);
            var baseEntityIdField = data.form.fields.filter(function (field) {
                return field.source === data.form.bind_type + ".id";
            })[0];
            params["entityId"] = baseEntityIdField.value;
        }
        if (enketo.hasValue(formDataRepository.saveFormSubmission(params, data))) {
            submissionRouter.route(params.instanceId);
        }
    };

    self.deleteSubmission = function (params) {
        init(params);
        //dataSource.remove(instanceId);
    };
};
