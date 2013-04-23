if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.FormDefinitionLoader = function () {
    return {
        load: function (formName) {
            return JSON.parse(ziggyFileLoader.loadAppData(formName + "/form_definition.json"));
        }
    };
};