var libs = {
    snapshotter: require('/lib/snapshotter'),
    portal: require('/lib/xp/portal'),
    mustache: require('/lib/mustache'),
    cron: require('/lib/cron'),
    configParser: require('/lib/config-parser')
};

function buildConfigFromMap(configAsMap) {
    var result = [];

    Object.keys(configAsMap).forEach(function (key) {
        result.push({
            key: key,
            value: configAsMap[key]
        })
    });

    return result;
}

exports.get = function (req) {

    var view = resolve('snapshotter.html');

    var appConfig = libs.snapshotter.getConfig();

    var schedulesResult = libs.configParser.parseSnapshots(appConfig);

    var cronJobDetails = {};
    schedulesResult.forEach(function (schedule) {
        cronJobDetails[schedule.name] = schedule;
    });

    var notifiers = libs.configParser.parseNotifiers(appConfig);

    notifiers.forEach(function (notifier) {
        notifier.config = buildConfigFromMap(notifier.config);
    });

    var cronJobsResult = libs.cron.list();

    var cronJobs = [];
    cronJobsResult.jobs.forEach(function (job) {
        if (cronJobDetails[job.name]) {
            job.keep = cronJobDetails[job.name].keep;

            var dateStr = job.nextExecTime.match(/^[^.]*/g)[0];
            var date = new Date(Date.parse(dateStr));
            job.readable = date.toLocaleDateString() + " " + date.toLocaleTimeString();

            cronJobs.push(job);
        }
    });

    var model = {
        jsUrl: libs.portal.assetUrl({path: "/js/snapshotter.js"}),
        assetsUrl: libs.portal.assetUrl({path: ""}),
        svcUrl: libs.portal.serviceUrl({service: 'Z'}).slice(0, -1),
        data: {
            cronJobs: cronJobs,
            notifiers: notifiers
        }
    };

    return {
        contentType: 'text/html',
        body: libs.mustache.render(view, model)
    };

};
