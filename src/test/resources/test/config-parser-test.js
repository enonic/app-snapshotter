var t = require('/lib/xp/testing');
var parser = require('/lib/config-parser');

var appConfig = {
    "snapshot.hourly.cron": "1 * * * *",
    "snapshot.hourly.keep": "PT48H",
    "snapshot.hourly.enabled": "true",
    "snapshot.daily.cron": "0 1 * * *",
    "snapshot.daily.keep": "P7D",
    "snapshot.daily.enabled": "true",
    "snapshot.weekly.cron": "0 4 * * 1",
    "snapshot.weekly.keep": "P30D",
    "snapshot.weekly.enabled": "true",

    "cleanup.cron": "0 * * * *",

    "notifier.slack.slackWebhook": "slackWebhookUrl",
    "notifier.slack.project": "Snapshotter",
    "notifier.slack.reportOnSuccess": "false",
    "notifier.slack.reportOnFailure": "true",

    "notifier.mail.onSuccess": "false",
    "notifier.mail.onFailure": "true",
    "notifier.mail.from": "from@domain.com",
    "notifier.mail.to": "to@domain.com"
};

var expectedSnapshotConfig = [
    {
        "name": "hourly",
        "keep": "PT48H",
        "cron": "1 * * * *"
    },
    {
        "name": "daily",
        "keep": "P7D",
        "cron": "0 1 * * *"
    },
    {
        "name": "weekly",
        "keep": "P30D",
        "cron": "0 4 * * 1"
    }
];

var expectedNotifiersConfig = [
    {
        "name": "slack",
        "config": {
            "url": "slackWebhookUrl",
            "project": "Snapshotter",
            "reportOnSuccess": false,
            "reportOnFailure": true
        }
    },
    {
        "name": "mail",
        "config": {
            "onSuccess": false,
            "onFailure": true,
            "from": [
                "from@domain.com"
            ],
            "to": [
                "to@domain.com"
            ],
            "hostname": "hostname"
        }
    }
];

var expectedCleanupCron = "0 * * * *";

exports.testParseSnapshots = function () {
    t.assertJsonEquals(expectedSnapshotConfig, parser.parseSnapshots(appConfig));
};

exports.testParseNotifiers = function () {
    try {
        parser.parseNotifiers(appConfig);
    } catch (e) {
        t.assertEquals('Missing property: [mail.hostname]', e.message);
    }

    appConfig['notifier.mail.hostname'] = 'hostname';

    t.assertJsonEquals(expectedNotifiersConfig, parser.parseNotifiers(appConfig));
};

exports.testParseCleanupCron = function () {
    t.assertJsonEquals(expectedCleanupCron, parser.parseCleanupCron(appConfig));
};
