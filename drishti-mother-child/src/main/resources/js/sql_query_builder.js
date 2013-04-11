if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.SQLQueryBuilder = function (formDataRepository) {
    var findEntityByType = function (entities, type) {
        for (var index = 0; index < entities.length; index++) {
            if (entities[index].type === type) {
                return entities[index];
            }
        }
        return null;
    };

    var loadEntityObjectAndItsRelatives = function (entitiesDefn, parentInstance, parentType, contextRelation) {
        var baseEntity = findEntityByType(entitiesDefn, contextRelation.type);
        var column = contextRelation.from.split(".")[1];
        var sql = "select * from {0} where {1} = '{2}'".format(contextRelation.type, contextRelation.to, parentInstance[column]);
        var baseInstance = JSON.parse(queryMethod(contextRelation)(sql));

        if (!enketo.hasValue(baseInstance)) return null;
        if (!enketo.hasValue(baseEntity.relations) || baseEntity.relations.length === 0) {
            return baseInstance;
        }
        //TODO: When baseEntity is a list, relatives have to be loaded for each instance
        baseEntity.relations.forEach(function (relation) {
            if (relation.type !== parentType) {
                var relative = loadEntityObjectAndItsRelatives(entitiesDefn, baseInstance, baseEntity.type, relation);
                if (enketo.hasValue(relative))
                    baseInstance[relation.type] = relative;
            }
        });
        return baseInstance;
    };

    var queryMethod = function (contextRelation) {
        if (enketo.RelationKind[contextRelation.kind] === enketo.RelationKind.one_to_many) {
            return formDataRepository.queryList;
        }
        else {
            return formDataRepository.queryUniqueResult;
        }
    };

    return {
        loadEntityHierarchy: function (entitiesDefn, baseEntityType, baseEntityId) {
            var baseEntityDefn = findEntityByType(entitiesDefn, baseEntityType);
            //TODO : Need to format the sql as per the data type
            var sql = "select * from {0} where id = '{1}'".format(baseEntityType, baseEntityId);
            var baseEntity = JSON.parse(formDataRepository.queryUniqueResult(sql));
            if (!enketo.hasValue(baseEntityDefn.relations) || baseEntityDefn.relations.length === 0) {
                return baseEntity;
            }
            baseEntityDefn.relations.forEach(function (relation) {
                baseEntity[relation.type] = loadEntityObjectAndItsRelatives(entitiesDefn, baseEntity, baseEntityType, relation);
            });
            var entityWithRelatives = {};
            entityWithRelatives[baseEntityType] = baseEntity;
            return entityWithRelatives;
        }
    };
};
