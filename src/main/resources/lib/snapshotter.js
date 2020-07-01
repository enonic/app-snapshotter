var libs = {
    notifiers: require('/lib/notifiers')
};

var defaultConfig = {
    "snapshot.hourly.cron": "1 * * * *",
    "snapshot.hourly.keep": "PT48H",
    "snapshot.hourly.enabled": "true",

    "snapshot.daily.cron": "0 1 * * *",
    "snapshot.daily.keep": "P7D",
    "snapshot.daily.enabled": "true",

    "snapshot.weekly.cron": "0 4 * * 1",
    "snapshot.weekly.keep": "P30D",
    "snapshot.weekly.enabled": "true",

    "cleanup.cron": "0 * * * *"
};

function required(params, name) {
    var value = params[name];
    if (value === undefined) {
        throw 'Parameter \'' + name + '\' is required';
    }

    return value;
}

var appConfig = defaultConfig;

var mailConfigExists = false;

Object.keys(app.config).forEach(function (key) {
    if (key.startsWith('notifier.mail.')) {
        mailConfigExists = true;
    }
    appConfig[key] = app.config[key];
});

var bean = __.newBean('com.enonic.app.snapshotter.handler.SnapshotterHandler');

if (mailConfigExists) {
    appConfig['notifier.mail.hostname'] = bean.getDefaultHost();
}

exports.testNotify = function (notifierName) {
    libs.notifiers.notify(notifierName);
};

exports.snapshot = function (params) {
    return __.toNativeObject(bean.snapshot(required(params, 'scheduleName'), __.nullOrValue(params.repositoryId)));
};

exports.deleteSnapshot = function (params) {
    bean.deleteSnapshot(required(params, 'scheduleName'), required(params, 'keep'));
};

exports.getConfig = function () {
    return appConfig;
};
