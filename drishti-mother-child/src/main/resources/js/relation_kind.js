if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.RelationKind = {
    one_to_one: {type: "one_to_one"},
    one_to_many: {type: "one_to_many"},
    many_to_one: {type: "many_to_one"}
};

enketo.RelationKind.one_to_one["inverse"] = enketo.RelationKind.one_to_one;
enketo.RelationKind.one_to_many["inverse"] = enketo.RelationKind.many_to_one;
enketo.RelationKind.many_to_one["inverse"] = enketo.RelationKind.one_to_many;