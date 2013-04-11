if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.EntityDef = function (type) {
    var self = this;

    var findRelativesWhoAre = function (relationAs) {
        return self.relations.filter(function (relation) {
            return relation.as === relationAs;
        });
    };

    self.type = type;
    //Do not modify this by doing EntityDef.relations
    self.relations = [];
    self.fields = [];

    self.addRelation = function (rel) {
        self.relations.push(rel);
        return self;
    };

    self.removeAllRelations = function () {
        self.relations = [];
    };

    self.createInstance = function () {
        var instance = new enketo.EntityDef(self.type);
        self.relations.forEach(function (rel) {
            instance.relations.push(rel.createInstance())
        });
        return instance;
    };

    self.addField = function (field) {
        self.fields.push(field);
        return self;
    };

    self.findParents = function () {
        return findRelativesWhoAre("child");
    };

    self.findChildren = function () {
        return findRelativesWhoAre("parent");
    };

    self.getFieldByPersistenceName = function (name) {
        return self.fields.filter(function (field) {
            return field.persistenceName === name;
        })[0];
    };

    self.iterateThroughFields = function (mapFunction) {
        return self.fields.forEach(mapFunction);
    };
};

enketo.RelationDef = function (type, kind, as, from, to) {
    var self = this;

    self.type = type;
    self.kind = kind;
    self.as = as;
    self.from = from;
    self.to = to;

    self.createInstance = function () {
        return new enketo.RelationDef(self.type, self.kind, self.as, self.from, self.to);
    };
};
