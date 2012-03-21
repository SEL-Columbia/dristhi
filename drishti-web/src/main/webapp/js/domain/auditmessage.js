function AuditMessage(auditItem) {
  var handleSMS = function(item, messageToPutStuffInto) {
    messageToPutStuffInto.type = "SMS";

    var smsNotSentMessage = item.data.smsIsSent == "true" ? " " : " NOT ";
    messageToPutStuffInto.content = "SMS" + smsNotSentMessage + "sent to " + item.data.recipient + ": " + item.data.message;
    messageToPutStuffInto.actions = [{
      text : "Resend SMS",
      link : "javascript:alert('Not implemented yet.');"
    }]
  };

  var handleFormSubmission = function(item, messageToPutStuffInto) {
    messageToPutStuffInto.type = "Form Submission";

    var formData = JSON.parse(item.data.formData);
    for(var key in formData) {
      messageToPutStuffInto.info[key] = formData[key];
    }

    messageToPutStuffInto.content = "Form for '" + item.data.formType + "' submitted for case: " + messageToPutStuffInto.info.caseId + ".";

    messageToPutStuffInto.actions = [{
      text : "Go to CommCareHQ form",
      link : "https://www.commcarehq.org/a/frhs-who-columbia/reports/form_data/" + item.data.formId + "/"
    }]
  };

  this.time = auditItem.time;
  this.info = {};
  if (auditItem.type === "SMS") {
    handleSMS(auditItem, this);
  }
  else if (auditItem.type === "FORM_SUBMISSION") {
    handleFormSubmission(auditItem, this);
  }
};