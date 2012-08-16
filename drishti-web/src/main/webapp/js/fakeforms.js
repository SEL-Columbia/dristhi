$(document).ready(function() {
    var statusDisplay = function(status) {
        $('#status').text(status);
    };

    var formStatusDisplay = function(formName, status) {
        $('#' + formName + 'status').text(status).css('opacity', '100');
    };

    var clearStatusDisplay = function(formName) {
        $('#' + formName + 'status').text('.').css('opacity', '0');
    };

    var createObjectFrom = function(formFields) {
        var data = {};
        $.each(formFields, function(i, field) {
            data[field.id.slice(field.id.indexOf('-') + 1)] = field.value;
        });
        return data;
    };

    var handleSubmit = function(formName, form) {
        var mappings = $(form).children(".mapping");
        var extraDataPieces = {};
        $(form).children(".extraData").each(function() { extraDataPieces[$(this).data('key')] = createObjectFrom($(this).children(".extramapping")); });

        $.post('../form/submit', { formName: formName, formData: JSON.stringify(createObjectFrom(mappings)),
                extraFormData: JSON.stringify(extraDataPieces) })
            .success(function(responseFromServer) { formStatusDisplay(formName, "Success!"); })
            .error(function(jqXHR, textStatus) { formStatusDisplay(formName, "Failed!"); });
    };

    var displayForms = function(formDefinitions) {
        $("#forms").html(Handlebars.compile($("#forms_template").html())(formDefinitions));

        $(".mapping").change(function() { clearStatusDisplay($(this).data('name')); });
        $(".extramapping").change(function() { clearStatusDisplay($(this).data('name')); });

        $(".form").submit(function() {
            handleSubmit($(this).data('name'), this);
            return false;
        });
    };

    var init = function() {
        $.getJSON('../form/definitions')
            .success(function(responseFromServer) {
                displayForms(responseFromServer);
            })
            .error(function(jqXHR, textStatus) {
                statusDisplay("Failed!");
            });
    };

    init();
});