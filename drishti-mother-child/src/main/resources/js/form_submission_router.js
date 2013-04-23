if (typeof enketo == "undefined" || !enketo) {
    var enketo = {};
}

enketo.FormSubmissionRouter = function () {
    var submissionRouter;
    if (typeof formSubmissionRouter !== "undefined") {
        submissionRouter = formSubmissionRouter;
    }

    return {
        route: function (instanceId) {
            return submissionRouter.route(instanceId);
        }
    };
};