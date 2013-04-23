if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.FormModelMapper = function (formDataRepository, queryBuilder, idFactory) {

    var findEntityByType = function (entitiesDef, type) {
        for (var index = 0; index < entitiesDef.length; index++) {
            if (entitiesDef[index].type === type) {
                return entitiesDef[index];
            }
        }
        return null;
    };

    var addFieldToEntityInstance = function (source, value, entityInstance) {
        var pathVariables = source.split(".");
        var base = pathVariables[0];
        if (!enketo.hasValue(entityInstance[base])) {
            entityInstance[base] = {};
        }
        if (pathVariables.length > 2) {
            pathVariables.shift();
            entityInstance[base] = addFieldToEntityInstance(pathVariables.join("."), value, entityInstance[base]);
        } else {
            entityInstance[base][pathVariables[1]] = value;
        }
        return entityInstance;
    };

    var shouldPersistEntity = function (entitiesToSave, entityType, updatedEntities) {
        var entityToBeSaved = findEntityByType(entitiesToSave, entityType);
        var isEntityAlreadySaved = enketo.hasValue(findEntityByType(updatedEntities, entityType));
        return enketo.hasValue(entityToBeSaved) && !isEntityAlreadySaved;
    };

    var addParentReferenceFieldToChildEntity = function (childRelation, entitiesToSave, entityId) {
        var parentReferenceField = childRelation.to.split(".")[1];
        var childEntityToSave = findEntityByType(entitiesToSave, childRelation.type);

        childEntityToSave.addField({
            "name": childEntityToSave.source + "." + parentReferenceField,
            "source": childEntityToSave.source + "." + parentReferenceField,
            "persistenceName": parentReferenceField,
            "value": entityId
        });
    };

    var identify = function (entitiesToSave, formModel) {
        entitiesToSave.forEach(function (entity) {
            var idField = entity.getFieldByPersistenceName("id");
            if (!enketo.hasValue(idField)) {
                idField = {
                    "name": entity.source + ".id",
                    "source": entity.source + ".id",
                    "persistenceName": "id",
                    "value": idFactory.generateIdFor(entity.type)
                };
                entity.addField(idField);
                var idFormField = formModel.form.fields.filter(function (formField) {
                    return formField.source === idField.source;
                })[0];
                if (!enketo.hasValue(idFormField)) {
                    formModel.form.fields.push(idField);
                }
                else if (!enketo.hasValue(idFormField.value)) {
                    idFormField.value = idField.value;
                }
            }
            else if (!enketo.hasValue(idField.value)) {
                idField.value = idFactory.generateIdFor(entity.type);
            }
        });
    };

    var persist = function (entitiesDef, entityType, entitiesToSave, updatedEntities) {
        var entityTypeDef = findEntityByType(entitiesDef, entityType);
        var parentRelations = entityTypeDef.findParents();
        parentRelations.forEach(function (parentRelation) {
            if (shouldPersistEntity(entitiesToSave, parentRelation.type, updatedEntities)) {
                persist(entitiesDef, parentRelation.type, entitiesToSave, updatedEntities);
            }
        });

        var entityId;
        if (shouldPersistEntity(entitiesToSave, entityType, updatedEntities)) {
            var entityToSave = findEntityByType(entitiesToSave, entityType);
            var entityFields = {};
            entityToSave.iterateThroughFields(function (field) {
                entityFields[field.persistenceName] = field.value;
            });
            formDataRepository.saveEntity(entityType, entityFields);
            entityId = entityToSave.getFieldByPersistenceName("id").value;
            updatedEntities.push(entityToSave);
        }

        var childRelations = entityTypeDef.findChildren();
        childRelations.forEach(function (childRelation) {
            if (shouldPersistEntity(entitiesToSave, childRelation.type, updatedEntities)) {
                addParentReferenceFieldToChildEntity(childRelation, entitiesToSave, entityId);
                persist(entitiesDef, childRelation.type, entitiesToSave, updatedEntities);
            }
        });
    };

    return {
        mapToFormModel: function (entities, formDefinition, params) {
            //TODO: Handle errors, savedFormInstance could be null!
            var savedFormInstance = JSON.parse(formDataRepository.getFormInstanceByFormTypeAndId(params.id, params.formName));
            if (enketo.hasValue(savedFormInstance)) {
                return savedFormInstance;
            }
            if (!enketo.hasValue(entities)) {
                return formDefinition;
            }
            //TODO: not every case entityId maybe applicable.
            if (!enketo.hasValue(params.entityId)) {
                return formDefinition;
            }
            //TODO: pass all the params to the query builder and let it decide what it wants to use for querying.
            var entityHierarchy = queryBuilder.loadEntityHierarchy(entities, formDefinition.form.bind_type, params.entityId);
            formDefinition.form.fields.forEach(function (field) {
                var fieldValue;
                var entity;
                var fieldName;
                if (enketo.hasValue(field.source)) {
                    var pathVariables = field.source.split(".");
                    fieldValue = entityHierarchy;
                    for (var index in pathVariables) {
                        var pathVariable = pathVariables[index];
                        if (enketo.hasValue(fieldValue[pathVariable])) {
                            fieldValue = fieldValue[pathVariable];
                        } else {
                            fieldValue = undefined;
                            break;
                        }
                    }
                } else {
                    entity = formDefinition.form.bind_type;
                    fieldName = field.name;
                    fieldValue = entityHierarchy[entity][fieldName];
                    field["source"] = entity + "." + fieldName;
                }
                if (enketo.hasValue(fieldValue)) {
                    field.value = fieldValue;
                }
            });

            return formDefinition;
        },
        mapToEntityAndSave: function (entitiesDef, formModel) {
            var entitiesToSave = [];
            formModel.form.fields.forEach(function (field) {
                var pathVariables = field.source.split(".");
                var entityTypeOfField = pathVariables[pathVariables.length - 2];
                var entityInstance = findEntityByType(entitiesToSave, entityTypeOfField);
                if (!enketo.hasValue(entityInstance)) {
                    entityInstance = findEntityByType(entitiesDef, entityTypeOfField).createInstance();
                    entityInstance.source = field.source.substring(0, field.source.lastIndexOf("."));
                    entitiesToSave.push(entityInstance);
                }
                var entityField = {
                    "name": field.name,
                    "source": field.source,
                    "persistenceName": pathVariables[pathVariables.length - 1],
                    "value": field.value
                };
                entityInstance.addField(entityField);
            });
            identify(entitiesToSave, formModel);
            var updatedEntities = [];
            persist(entitiesDef, formModel.form.bind_type, entitiesToSave, updatedEntities);
        }
    };
};