var t = require('/lib/xp/testing');

t.mock('/lib/xp/mail', {
    send: function (params) {
        return true;
    }
});

exports.testNotify = function () {
    var mailNotifier = require('/lib/mail-notifier');

    try {
        mailNotifier.newInstance().test('test');
    } catch (e) {
        t.assertEquals('The configuration of Mail notifier should be provided', e.message);
    }

    var config = {};

    try {
        mailNotifier.newInstance(config).test('test');
    } catch (e) {
        t.assertEquals('The config property \"from\" is required', e.message);
    }

    config.from = ["from"];

    try {
        mailNotifier.newInstance(config).test('test');
    } catch (e) {
        t.assertEquals('The config property \"to\" is required', e.message);
    }

    config.to = ["to"];

    mailNotifier.newInstance(config).test('test');
};

exports.testSuccess = function () {
    var config = {
        from: ["from"],
        to: ["to"],
        onSuccess: true,
        onFailure: true
    };

    var mailNotifier = require('/lib/mail-notifier');

    mailNotifier.newInstance(config).success("jobName")
};

exports.testFailed = function () {
    var config = {
        from: ["from"],
        to: ["to"],
        onSuccess: true,
        onFailure: true
    };

    var mailNotifier = require('/lib/mail-notifier');

    mailNotifier.newInstance(config).failed("jobName", "error message")
};
