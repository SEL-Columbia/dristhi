if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.IdFactory = function (idFactoryBridge) {
    return{
        generateIdFor: function (entityType) {
            return idFactoryBridge.generateIdFor(entityType);
        }
    }
};

enketo.IdFactoryBridge = function () {
    var idFactoryContext;
    if (typeof formDataRepositoryContext !== "undefined") {
        idFactoryContext = formDataRepositoryContext;
    }

    return {
        generateIdFor: function (entityType) {
            return idFactoryContext.generateIdFor(entityType);
        }
    };
};
