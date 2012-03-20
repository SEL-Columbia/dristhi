$(document).ready(function() {
    var previousIndexOfMessages = 0;

    var statusDisplay = function(status) {
        $('#status').text(status);
    };
    var clearStatusDisplay = function() {
        $('#status').text("");
    };

    var showNewMessages = function(newMessages) {
        $(newMessages).each(function(index, elem) { $("#messages").prepend(
        $("<div class=\"audit_item\">" +
            "<span class=\"time\">" + new Date(elem.time) + "</span>" +
            "<span class=\"message\">" + elem.message.replace(/","/g, "\", \"").replace(/":"/g, "\": \"") + "</span>" +
          "</div>").fadeIn(2000).delay(1000).animate({backgroundColor: "rgba(0, 200, 0, 0.2)"}, 5500)
        )});;
    };

    var updateMessageIndex = function(newMessages) {
        if (newMessages.length == 0) return;
        previousIndexOfMessages = newMessages[newMessages.length - 1].index;
    };

    var displayNewMessages = function() {
        $.getJSON('../audit/messages', {previousAuditMessageIndex: previousIndexOfMessages})
            .success(function(responseFromServer) {
                clearStatusDisplay();
                updateMessageIndex(responseFromServer);
                showNewMessages(responseFromServer);
            })
            .error(function(jqXHR, textStatus) {
                statusDisplay("Failed!");
            });

    };

    var init = function() {
        setInterval(displayNewMessages, 2000);
    };

    init();
});