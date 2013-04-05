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
    var idFactoryContextnew= enketo.FakeIdFactoryContext();

    return {
        generateIdFor: function (entityType) {
            return idFactoryContext.generateIdFor(entityType);
        }
    };
};

enketo.FakeIdFactoryContext = function () {
    return {
        generateIdFor: function (entityType) {
            return "new uuid : " + entityType;
        }
    }
};