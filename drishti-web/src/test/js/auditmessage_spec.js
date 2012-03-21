describe("AuditMessage", function() {
  it("should create audit message for an unsent SMS, from audit log item of SMS type", function() {
    var smsAuditItem = {
        time  : 1332302355150,
        index : 43,
        type  : "SMS",
        data  : {
            message   : "The real SMS message.",
            smsIsSent : "false",
            recipient : "9845700000"
        }
    };

    var actualAuditMessage = new AuditMessage(smsAuditItem);

    var expectedAuditMessage = {
        time    : 1332302355150,
        type    : "SMS",
        content : "SMS NOT sent to 9845700000: The real SMS message.",
        info    : {},
        actions : [
            {
                "text" : "Resend SMS.",
                "link" : "javascript:alert('Not implemented yet.');"
            }
        ]
    };

    expect(actualAuditMessage).toEqual(expectedAuditMessage);
  });

  it("should create audit message for a sent SMS, from audit log item of SMS type", function() {
    var smsAuditItem = {
        time  : 1332302355150,
        index : 43,
        type  : "SMS",
        data  : {
            message   : "The real SMS message.",
            smsIsSent : "true",
            recipient : "9845700000"
        }
    };

    var actualAuditMessage = new AuditMessage(smsAuditItem);

    var expectedAuditMessage = {
        time    : 1332302355150,
        type    : "SMS",
        content : "SMS sent to 9845700000: The real SMS message.",
        info    : {},
        actions : [
            {
                "text" : "Resend SMS.",
                "link" : "javascript:alert('Not implemented yet.');"
            }
        ]
    };

    expect(actualAuditMessage).toEqual(expectedAuditMessage);
  });

  it("should create audit message for a form submission, from audit log item of FORM_SUBMISSION type", function() {
    var formSubmissionAuditItem = {
        time  : 1332302355150,
        index : 43,
        type  : "FORM_SUBMISSION",
        data  : {
            formType: "registerMother",
            formData: "{\"lmp\":\"2012-02-29\",\"anmPhoneNumber\":\"\",\"caseId\":\"839RUGI9CVN7348CZGLMQ6NUH\"}",
            formId: "FORM-ID-HERE"
        }
    };

    var actualAuditMessage = new AuditMessage(formSubmissionAuditItem);

    var expectedAuditMessage = {
        time    : 1332302355150,
        type    : "Form Submission",
        content : "Form for 'registerMother' submitted for case: 839RUGI9CVN7348CZGLMQ6NUH.",
        info    : {
          "lmp" : "2012-02-29",
          "anmPhoneNumber" : "",
          "caseId" : "839RUGI9CVN7348CZGLMQ6NUH"
        },
        actions : [
            {
                "text" : "Go to CommCareHQ form.",
                "link" : "https://www.commcarehq.org/a/frhs-who-columbia/reports/form_data/FORM-ID-HERE/"
            }
        ]
    };

    expect(actualAuditMessage).toEqual(expectedAuditMessage);
  });
});