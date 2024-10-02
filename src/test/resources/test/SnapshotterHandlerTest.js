var t = require('/lib/xp/testing');
var snapshotter = require('/lib/snapshotter');

var expectedDefaultConfig = {
    "snapshot.hourly.cron": "1 * * * *",
    "snapshot.hourly.keep": "PT48H",
    "snapshot.hourly.enabled": "true",
    "snapshot.daily.cron": "0 1 * * *",
    "snapshot.daily.keep": "P7D",
    "snapshot.daily.enabled": "true",
    "snapshot.weekly.cron": "0 4 * * 1",
    "snapshot.weekly.keep": "P30D",
    "snapshot.weekly.enabled": "true",
    "cleanup.cron": "30 * * * *"
};

exports.snapshot = function () {
    var result = snapshotter.snapshot({
        scheduleName: 'name',
        repositoryId: 'repo'
    });

    t.assertEquals('SUCCESS', result);
};

exports.deleteSnapshot = function () {
    snapshotter.deleteSnapshot({
        scheduleName: 'com.enonic.app.snapshotter--name',
        appPrefix: 'com.enonic.app.snapshotter--',
        keep: 'PT1H'
    });
};

exports.getConfig = function () {
    t.assertJsonEquals(expectedDefaultConfig, snapshotter.getConfig());
};
