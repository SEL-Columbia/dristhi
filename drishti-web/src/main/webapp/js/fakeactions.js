$(document).ready(function() {
    var statusDisplay = function(status) {
        $('#status').text(status);
    };

    var formStatusDisplay = function(formName, status) {
        $('#' + formName + '-status').text(status).css('opacity', '100');
    };

    var clearStatusDisplay = function(formName) {
        $('#' + formName + '-status').text('.').css('opacity', '0');
    };

    var createObjectFrom = function(formFields) {
        var data = {};
        $.each(formFields, function(i, field) {
            data[field.id.slice(field.id.indexOf('-') + 1)] = field.value;
        });
        return data;
    };

    var handleSubmit = function(form) {
        var payload = createObjectFrom($(form).children(".field"));
        var data = createObjectFrom($(form).children(".data"));
        var anmId = $(form).children(".anm")[0].value;
        payload['data'] = data;
        payload['actionType'] = form.id;

        $.post('../action/submit', { formData: JSON.stringify(payload), anmIdentifier: anmId })
            .success(function(responseFromServer) { formStatusDisplay(form.id, "Success!"); })
            .error(function(responseFromServer, textStatus) {
                formStatusDisplay(form.id, "Failed!");
            });
    };

    var hookUpAllForms = function() {
        $('form').each(function(formIndex, form) {
            $(form).children('.mapping').each(function(mappingIndex, mapping) {
                $(mapping).change(function() { clearStatusDisplay(form.id); });
            });

            $(form).submit(function() {
                handleSubmit(form);
                return false;
            });
        });

    };

    var init = function() {
        hookUpAllForms();
    };

    init();
});