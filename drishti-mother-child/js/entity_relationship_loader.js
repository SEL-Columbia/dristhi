if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.EntityRelationshipLoader = function () {
    return {
        load: function () {
            return JSON.parse(jsonFileLoader.loadFormData("entity_relationship.json"));
        }
    };
};