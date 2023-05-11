const libs = {
    snapshotter: require('/lib/snapshotter'),
    portal: require('/lib/xp/portal'),
    mustache: require('/lib/mustache'),
    snapshotJobScheduler: require('/lib/snapshot-job-scheduler'),
    configParser: require('/lib/config-parser')
};

function buildConfigFromMap(configAsMap) {
    const result = [];

    Object.keys(configAsMap).forEach(function (key) {
        result.push({
            key: key,
            value: configAsMap[key]
        })
    });

    return result;
}

exports.get = function (req) {
    const view = resolve('snapshotter.html');
    const appConfig = libs.snapshotter.getConfig();

    const notifiers = makeNotifiers(appConfig);
    const cronJobs = makeCronJobsData(appConfig);

    const model = {
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

function makeNotifiers(appConfig) {
    const notifiers = libs.configParser.parseNotifiers(appConfig);

    notifiers.forEach(function (notifier) {
        notifier.config = buildConfigFromMap(notifier.config);
    });

    return notifiers;
}

function makeCronJobsData(appConfig) {
    const snapshotsToSchedule = libs.configParser.parseSnapshots(appConfig);

    const cronJobDetails = {};
    snapshotsToSchedule.forEach(function (schedule) {
        const key = libs.snapshotJobScheduler.makeScheduledSnapshotJobName(schedule.name);
        cronJobDetails[key] = schedule;
    });

    const scheduledJobs = libs.snapshotJobScheduler.list();

    const cronJobs = [];
    scheduledJobs.forEach(function (job) {
        if (cronJobDetails[job.name]) {
            job.displayName = cronJobDetails[job.name].name;
            job.keep = cronJobDetails[job.name].keep;
            job.readable = makeLastRunString(job);

            cronJobs.push(job);
        }
    });

    return cronJobs;
}

function makeLastRunString(job) {
    if (!job.lastRun) {
        return '-----'
    }

    const dateStr = job.lastRun.match(/^[^.]*/g)[0];
    const date = new Date(Date.parse(dateStr));
    return date.toLocaleDateString() + " " + date.toLocaleTimeString();
}
