"use strict";
(function () {

        const svcUrl = document.currentScript.getAttribute('data-svcurl');

        const listenToNotifierTest = function () {
            const btns = document.getElementsByClassName('btnTestNotifier');

            for (let btn of btns) {
                btn.addEventListener('click', function () {
                    testNotifier(btn.getAttribute('data-name'));
                })
            }
        };

        const testNotifier = function (notifierName) {
            const xhr = new XMLHttpRequest();
            xhr.open('GET', `${svcUrl}?notifierName=${notifierName}`);
            xhr.send();

            xhr.onload = function() {
                if (xhr.status !== 200) {
                    showAlertMessage('Notifier failed', 'error');
                } else { // show the result
                    showAlertMessage('Testing notifier', 'success');
                }
            };

            xhr.onerror = function() {
                showAlertMessage('Notifier failed', 'error');
            };
        };

        const showAlertMessage = function (text, type) {
            if (type === 'error') {
                console.error(text);
            } else {
                console.info(text);
            }
        };

        listenToNotifierTest();

    }()
);
