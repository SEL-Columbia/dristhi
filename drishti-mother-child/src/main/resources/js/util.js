if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.hasValue = function (object) {
    return !(typeof object == "undefined" || !object);
};

// String format
if (!String.prototype.format) {
    String.prototype.format = function () {
        var args = arguments;
        return this.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] != 'undefined'
                ? args[number]
                : match
                ;
        });
    };
}