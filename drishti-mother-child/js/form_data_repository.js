if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.FormDataRepository = function () {
    var repository;

        var fakeRepository = {
            queryUniqueResult: function (sql) {
            },
            queryList: function (sql) {
            },
            saveFormSubmission: function (params, data) {
            },
            saveEntity: function (entityType, entity) {
            }
        };
        repository = fakeRepository

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