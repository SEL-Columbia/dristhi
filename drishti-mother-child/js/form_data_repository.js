if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.FormDataRepository = function () {
    return {
        getFormInstanceByFormTypeAndId: function (formID, formName) {
            return null;
        },
        queryUniqueResult: function (sql) {
            return repository.queryUniqueResult(sql);
        },
        queryList: function (sql) {
            return repository.queryList(sql);
        },
        saveFormSubmission: function (params, data) {
            repository.saveFormSubmission(JSON.stringify(params), JSON.stringify(data));
        },
        saveEntity: function (entityType, entity) {
            return repository.saveEntity(entityType, JSON.stringify(entity));
        }
    };
}
;