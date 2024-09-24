const libs = {
    scheduler: require('/lib/xp/scheduler'),
    context: require('/lib/xp/context'),
    cluster: require('/lib/xp/cluster'),
    configParser: require('/lib/config-parser'),
};

const appName = app.name;
const scheduledSnapshotJobPrefix = appName + '--'
const snapshotTaskDescriptor = appName + ':snapshot';
const cleanupTaskDescriptor = appName + ':clean';

exports.list = listScheduledJobs;
exports.makeScheduledSnapshotJobName = makeScheduledSnapshotJobName;
exports.scheduleSnapshotsJobs = scheduleSnapshotsJobs;
exports.disableScheduledJobs = disableScheduledJobs;
exports.appPrefix = scheduledSnapshotJobPrefix;

function listScheduledJobs() {
    return libs.scheduler.list();
}

function makeScheduledSnapshotJobName(name) {
    return scheduledSnapshotJobPrefix + name;
}

function scheduleJob(params) {
    const isExistingJob = !!libs.scheduler.get({name: params.name});

    runAsAdmin(function () {
        if (isExistingJob) {
            updateScheduledJob(params)
        } else {
            createScheduledJob(params);
        }
    });
}

function runAsAdmin(callback) {
    libs.context.run({
        repository: 'system.scheduler',
        branch: 'master',
        principals: ['role:system.admin'],
    }, callback);
}

function createScheduledJob(params) {
    const createParams = {
        name: params.name,
        descriptor: params.taskDescriptor,
        enabled: params.enabled,
        user: 'user:system:su',
        schedule: {
            type: 'CRON',
            value: params.cron,
            timeZone: params.timezone,
        },
    };

    if (!!params.config) {
        createParams.config = params.config;
    }

    libs.scheduler.create(createParams);
}

function updateScheduledJob(params) {
    libs.scheduler.modify({
        name: params.name,
        editor: (edit) => {
            edit.schedule = {
                type: 'CRON',
                value: params.cron,
                timeZone: params.timezone,
            };
            edit.enabled = params.enabled;

            return edit;
        }
    });
}

function scheduleSnapshotsJobs(appConfig) {
    if (libs.cluster.isMaster()) {
        scheduleCleanup(appConfig);

        const snapshotsConfigs = getSnapshotsToSchedule(appConfig);
        scheduleSnapshots(snapshotsConfigs);
        cleanupStaleJobs(snapshotsConfigs);
    }
}

function scheduleSnapshots(snapshotsConfigs) {
    snapshotsConfigs.forEach(function (snapshotConfig) {
        scheduleJob({
            name: snapshotConfig.name,
            taskDescriptor: snapshotTaskDescriptor,
            cron: snapshotConfig.cron,
            timezone: snapshotConfig.timezone,
            enabled: snapshotConfig.enabled,
            config: {
                name: snapshotConfig.name
            },
        });
    });
}

function getSnapshotsToSchedule(appConfig) {
    const snapshotsConfigs = libs.configParser.parseSnapshots(appConfig);

    snapshotsConfigs.forEach(function (snapshotConfig) {
        snapshotConfig.name = makeScheduledSnapshotJobName(snapshotConfig.name);
    });

    return snapshotsConfigs;
}

function scheduleCleanup(appConfig) {
    const params = libs.configParser.parseCleanupCron(appConfig);
    log.info('Scheduling cleanup with cron: ' + JSON.stringify(params));
    const cleanupCronName = makeCleanupCronName();

    scheduleJob({
        name: cleanupCronName,
        taskDescriptor: cleanupTaskDescriptor,
        cron: params.cron,
        timezone: params.timezone,
        enabled: true,
    });
}

function makeCleanupCronName() {
    return makeScheduledSnapshotJobName('cleanup');
}

function disableScheduledJobs(appConfig) {
    if (libs.cluster.isMaster()) {
        log.info('Disabling ' + app.name + ' scheduled jobs');
        disableScheduledSnapshots(appConfig);
    }
}

function disableScheduledSnapshots(appConfig) {
    // snapshot tasks
    getSnapshotsToSchedule(appConfig).forEach(function (snapshotConfig) {
        disableScheduledSnapshot(snapshotConfig.name);
    });

    // cleanup task
    disableScheduledSnapshot(makeCleanupCronName());
}

function disableScheduledSnapshot(snapshotJobName) {
    const isExistingJob = !!libs.scheduler.get({name: snapshotJobName});

    if (isExistingJob) {
        runAsAdmin(function () {
            doDisableScheduledSnapshot(snapshotJobName);
        });
    }
}

function doDisableScheduledSnapshot(name) {
    libs.scheduler.modify({
        name: name,
        editor: (edit) => {
            edit.enabled = false;

            return edit;
        }
    });
}

function cleanupStaleJobs(snapshotsConfigs) {
    if (libs.cluster.isMaster()) {
        const jobsToKeep = getSnapshotJobsToKeep(snapshotsConfigs);
        const scheduledJobs = listScheduledJobs();

        scheduledJobs.filter(isSnapshotterJob).map(extractJobName).forEach(function (scheduledSnapshotJobName) {
            if (jobsToKeep.indexOf(scheduledSnapshotJobName) === -1) {
                log.info('Removing stale snapshotter job: ' + scheduledSnapshotJobName);

                libs.scheduler.delete({
                    name: scheduledSnapshotJobName,
                });
            }
        });
    }
}

function getSnapshotJobsToKeep(snapshotsConfigs) {
    const actualJobsNames = [makeCleanupCronName()];

    snapshotsConfigs.forEach(function (snapshotConfig) {
        actualJobsNames.push(snapshotConfig.name);
    });

    return actualJobsNames;
}

function isSnapshotterJob(job) {
    return job.descriptor.indexOf(appName) === 0;
}

function extractJobName(job) {
    return job.name;
}