if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.EntityRelationships = function (jsonDefinition) {
    var findEntityByType = function (entities, type) {
        for (var index = 0; index < entities.length; index++) {
            if (entities[index].type === type) {
                return entities[index];
            }
        }
        return null;
    };

    var determineEntities = function () {
        var entities = [];
        jsonDefinition.forEach(function (relation) {
            var entity = findEntityByType(entities, relation.parent);
            if (!enketo.hasValue(entity)) {
                entities.push(new enketo.EntityDef(relation.parent));
            }
            entity = findEntityByType(entities, relation.child);
            if (!enketo.hasValue(entity)) {
                entities.push(new enketo.EntityDef(relation.child));
            }
        });
        return entities;
    };

    return {
        determineEntitiesAndRelations: function () {
            if (!enketo.hasValue(jsonDefinition)) {
                return [];
            }
            var entities = determineEntities();
            jsonDefinition.forEach(function (relation) {
                var parentEntity = findEntityByType(entities, relation.parent);
                if (!enketo.hasValue(parentEntity.relations)) {
                    parentEntity.removeAllRelations();
                }
                parentEntity.addRelation(new enketo.RelationDef(relation.child, relation.kind, "parent", relation.from, relation.to));

                var childEntity = findEntityByType(entities, relation.child);
                if (!enketo.hasValue(childEntity.relations)) {
                    childEntity.removeAllRelations();
                }
                childEntity.addRelation(new enketo.RelationDef(relation.parent, enketo.RelationKind[relation.kind].inverse.type, "child", relation.to, relation.from));
            });
            return entities;
        }
    };
};
