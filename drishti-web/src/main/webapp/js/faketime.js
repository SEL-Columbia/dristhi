$(document).ready(function() {
    var timeToSet = function() {
        return new Date($('#year').val(), $('#month').val() - 1, $('#day').val(),
            $('#hour').val(), $('#minute').val(), $('#second').val());
    };

    var changeTimeToSetDisplay = function() {
        $('#timeToSet').text(timeToSet().toString());
        $('#status').text('');
    };

    var setupCurrentTimeDisplay = function() {
        var currentTime = new Date();

        $('#year').val(currentTime.getFullYear());
        $('#month').val(currentTime.getMonth() + 1);
        $('#day').val(currentTime.getDate());

        $('#hour').val(currentTime.getHours());
        $('#minute').val(currentTime.getMinutes());
        $('#second').val(currentTime.getSeconds());

        changeTimeToSetDisplay();
    };

    var statusDisplay = function(status) {
        $('#status').text(status);
    };

    var setTimeTo = function(offsetInSeconds) {
        $.post('../time/set', { offset: offsetInSeconds })
            .success(function(responseFromServer) {
              var timeOnServer = new Date(parseInt(responseFromServer));
              var offsetOnServerInSeconds = (timeOnServer - new Date()) / 1000;
              var differenceBetweenExpectedAndActualOffset = offsetInSeconds - offsetOnServerInSeconds;

              var statusAccordingToExpectedOffset = differenceBetweenExpectedAndActualOffset > 10 ? "Failed to set time. Is faketime running?" : "Success!";

              statusDisplay(statusAccordingToExpectedOffset + " Time on server: " + timeOnServer);
            })
            .error(function(jqXHR, textStatus) { statusDisplay("Failed!"); });
    };

    var init = function() {
        setupCurrentTimeDisplay();

        $('input#setTime').click(function() {
            var currentTime = new Date();
            var offsetInSeconds = Math.ceil((timeToSet() - currentTime) / 1000);
            setTimeTo(offsetInSeconds);
        });

        $('input[type="text"]').change(function() {
            changeTimeToSetDisplay();
        });
    };

    init();
});
