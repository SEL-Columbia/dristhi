if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.FormDataController = function (entityRelationshipLoader, formDefinitionLoader, formModelMapper, formDataRepository) {
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
        var mapToFormModel = formModelMapper.mapToFormModel(self.entitiesDef, self.formDefinition, params);
        return mapToFormModel;
    };
    self.save = function (params, data) {
        init(JSON.parse(params));
        if (enketo.hasValue(self.entitiesDef) && self.entitiesDef.length != 0) {
            formModelMapper.mapToEntityAndSave(self.entitiesDef, JSON.parse(data));
        }
        formDataRepository.saveFormSubmission(JSON.parse(params), JSON.parse(data));
    };
    self.deleteSubmission = function (params) {
        init(params);
        //dataSource.remove(instanceId);
    };
};
