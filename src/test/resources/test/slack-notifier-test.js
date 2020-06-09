var t = require('/lib/xp/testing');

t.mock('/lib/http-client', {
    request: function (params) {
        return {
            "status": 200
        }
    }
});

exports.testNotify = function () {
    var slackNotifier = require('/lib/slack-notifier');

    var config = {
        url: 'url',
        project: 'test',
        reportOnSuccess: false,
        reportOnFailure: false
    };

    slackNotifier.newInstance(config).test('test');
};

exports.testSuccess = function () {
    var slackNotifier = require('/lib/slack-notifier');

    var config = {
        url: 'url',
        project: 'test',
        reportOnSuccess: true,
        reportOnFailure: false
    };

    slackNotifier.newInstance(config).success('SUCCESS');
};

exports.testFailed = function () {
    var slackNotifier = require('/lib/slack-notifier');

    var config = {
        url: 'url',
        project: 'test',
        reportOnSuccess: false,
        reportOnFailure: false
    };

    slackNotifier.newInstance(config).failed('FAILED');
};
