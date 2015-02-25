define([], function () {
    'use strict';
    var formJsonToList = function (item, parent, toplevel) {
        if (!item.children) {
            return item;
        }
        var ls = [], rs, chls;
        if (parent == undefined) {
            parent = "/model/instance";
        }
        if (item.name != undefined) {
            if (item.type != 'group') {
                rs = {
                    name: item.name
                };
                if (toplevel + "/" + item.name  != parent + "/" + item.name) {
                    rs.bind = parent + "/" + item.name;
                }
                ls.push(rs);
            }
            if (parent == "/model/instance") {
                toplevel = parent + "/" + item.name;
            }
            parent = parent + "/" + item.name;
        }
        chls = [];
        item.children.forEach(function (child) {
            if (child.type == undefined) {
                return;
            }
            var tmp = formJsonToList(child, parent, toplevel);
            rs = tmp.name == undefined ? tmp : {
                name: tmp.name
            };
            if (tmp.name != undefined && toplevel + "/" + item.name  != parent + "/" + item.name) {
                rs.bind = parent + "/" + tmp.name;
            }
            chls.push(rs);
        });
        if (chls.length) {
            ls.push(chls);
        }
        return ls;
    }, flattenFields = function (alist, targetList) {
        if (alist instanceof Array) {
            alist.forEach(function (item) {
                if (item instanceof Array) {
                    flattenFields(item, targetList);
                } else {
                    targetList.push(item);
                }
            });
        }
        return targetList;
    };

    return {
        generateFormDefinition: function (fJson) {
            var k = formJsonToList(fJson), f_def = {};
            if (k.length > 1) {
                f_def.bind_type = k[0].name;
                f_def.default_bind_path = k[0].bind + "/";
                f_def.fields = [];
                flattenFields(k[1], f_def.fields);
            }
            return f_def;
        }
    };
});
