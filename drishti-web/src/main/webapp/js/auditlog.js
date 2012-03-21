$(document).ready(function() {
    var previousIndexOfMessages = 0;

    var statusDisplay = function(status) {
        $('#status').text(status);
    };
    var clearStatusDisplay = function() {
        $('#status').text("");
    };

    var displayMessage = function(node) {
       $("#extraInformation").empty();
       $("#extraInformation").append(node);
    };

    var showNewMessages = function(newMessages) {
        $(newMessages).each(function(index, elem) {
          var message = new AuditMessage(elem);

          var auditItem = $("<div class=\"audit_item\">" +
                              "<span class=\"time\">" + new Date(message.time) + "</span>" +
                              "<span class=\"message\">" + message.content + "</span>" +
                            "</div>").fadeIn(2000).delay(1000).animate({backgroundColor: "rgba(0, 200, 0, 0.2)"}, 5500);

          var actionsDiv = $("<div class=\"actions\"></div>");
          addActions(actionsDiv, message.info, message.actions);
          auditItem.append(actionsDiv);

          $("#messages").prepend(auditItem)});;
    };

    var addActions = function(auditItem, extraInfo, actions) {
      var addAction = function(auditItem, message, action) {
        var actionSpan = $("<span class=\"action\"><a href=\"javascript:\">" + message + "</a></span>");
        actionSpan.click(action);
        auditItem.append(actionSpan);
      };

      var addLink = function(auditItem, message, link) {
        auditItem.append($("<span class=\"action\"><a href=\"" + link + "\">" + message + "</a></span>"));
      };

      var createExtraInfoNode = function(info) {
        var extraInfo = "<span class=\"info\">";
        $.each(info, function(index, elem) {
          extraInfo += "<span class=\"item\">" + index + " : " + elem + "</span>";
        });
        extraInfo += "</span>";
        return $(extraInfo);
      };

      if (!$.isEmptyObject(extraInfo)) {
        addAction(auditItem, "Show more information", function() { displayMessage(createExtraInfoNode(extraInfo)); });
      }

      $(actions).each(function(index, action) {
        addLink(auditItem, action.text, (function(){return action.link})());
      });
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
