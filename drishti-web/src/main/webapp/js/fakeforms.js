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

        $.post('../form/submit', { formName: formName, formData: JSON.stringify(createObjectFrom(mappings)) })
            .success(function(responseFromServer) { formStatusDisplay(formName, "Success!"); })
            .error(function(jqXHR, textStatus) { formStatusDisplay(formName, "Failed!"); });
    };

    var displayForms = function(formDefinitions) {
        $.each(formDefinitions, function(i, formDefinition) {
            var name = formDefinition.name;

            var form = $(document.createElement('form')).attr('id', name + "Form").attr('class', 'form');
            $.each(formDefinition.mappings, function(i, mapping) {
                form.append($(document.createElement('label')).attr('for', name + "-" + mapping.value).text(mapping.value));
                form.append($(document.createElement('input')).attr('type', 'text').attr('id', name + "-" + mapping.value)
                    .attr('class', 'mapping').change(function() { clearStatusDisplay(name); }));
            });

            form.append($(document.createElement('input')).attr('type', 'submit').attr('id', name + 'FormSubmit').attr('value', 'Submit form!'));
            form.submit(function() {
                handleSubmit(name, this);
                return false;
            });

            var title = $(document.createElement('span')).attr('class', 'title').text(name);
            var status = $(document.createElement('span')).attr('class', 'formStatus').attr('id', name + 'status').text('.').css('opacity', '0');
            var div = $(document.createElement('div')).attr('id', name).attr('class', 'formDiv').append(title).append(form).append(status);
            $('#forms').append(div);
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