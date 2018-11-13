"use strict";
(function ($) {

        var alertHideTimer;
        var svcUrl = document.currentScript.getAttribute('data-svcurl');

        var listenToNotifierTest = function () {
            $(".btnTestNotifier").click(function () {
                testNotifier($(this).data('name'));
            });
        };

        var testNotifier = function (notifierName) {

            var data = {
                notifierName: notifierName
            };

            $.ajax({
                url: svcUrl + 'notifier-test-service',
                method: 'GET',
                cache: false,
                data: data
            }).then(function (data) {
                showAlertMessage('Testing notifier', 'success');
            }).fail(function (jqXHR) {
                showAlertMessage('Notifier failed', 'error');
            });

        };

        var showAlertMessage = function (text, type) {
            type = type || 'warning';
            $('#alertMessageText').text(text);
            $('#alertMessage')
                .show()
                .toggleClass('alert-warning', type === 'warning')
                .toggleClass('alert-danger', type === 'error')
                .toggleClass('alert-info', type === 'info')
                .toggleClass('alert-success', type === 'success');

            clearTimeout(alertHideTimer);
            alertHideTimer = setTimeout(function () {
                $('#alertMessage').hide();
            }, 5000);
        };

        listenToNotifierTest();

    }(jQuery)
);
