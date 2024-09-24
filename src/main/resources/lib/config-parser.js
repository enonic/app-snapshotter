function SnapshotterConfigParser(config) {

    var self = this;

    this.subConfig = function (prefix) {
        var result = {};

        Object.keys(config).forEach(function (key) {
            if (key.startsWith(prefix) === true) {
                result[key.substring(prefix.length())] = config[key];
            }
        });

        return result;
    };

    this.parseSnapshots = function () {
        var snapshotConfigs = this.subConfig('snapshot.');

        var snapshotJobNames = [];

        Object.keys(snapshotConfigs).forEach(function (key) {
            snapshotJobNames.push(key.substring(0, key.indexOf(".", 1)));
        });

        // remove duplicates
        snapshotJobNames = snapshotJobNames.filter(function (a, b) {
            return snapshotJobNames.indexOf(a) === b;
        });

        var snapshotJobs = [];

        snapshotJobNames.forEach(function (snapshotJobName) {
            snapshotJobs.push({
                name: snapshotJobName,
                keep: self.getProperty(snapshotConfigs, snapshotJobName, "keep"),
                cron: self.getProperty(snapshotConfigs, snapshotJobName, "cron"),
                timezone: self.getProperty(snapshotConfigs, snapshotJobName, "timezone", "UTC"),
                enabled: self.getBooleanProperty(snapshotConfigs, snapshotJobName, "enabled"),
            });
        });

        return snapshotJobs;
    };

    this.parseNotifiers = function () {
        var notifierConfigs = this.subConfig('notifier.');

        var notifierNames = [];

        Object.keys(notifierConfigs).forEach(function (key) {
            notifierNames.push(key.substring(0, key.indexOf(".", 1)));
        });

        // remove duplicates
        notifierNames = notifierNames.filter(function (a, b) {
            return notifierNames.indexOf(a) === b;
        });

        var notifiers = [];

        notifierNames.forEach(function (notifierName) {
            if (notifierName === 'slack') {
                notifiers.push({
                    name: notifierName,
                    config: {
                        url: self.getProperty(notifierConfigs, notifierName, "slackWebhook"),
                        project: self.getProperty(notifierConfigs, notifierName, "project"),
                        reportOnSuccess: self.getProperty(notifierConfigs, notifierName, "reportOnSuccess") === "true",
                        reportOnFailure: self.getProperty(notifierConfigs, notifierName, "reportOnFailure") === "true"
                    }
                });
            } else if (notifierName === 'mail') {
                notifiers.push({
                    name: notifierName,
                    config: {
                        onSuccess: self.getProperty(notifierConfigs, notifierName, "onSuccess") === "true",
                        onFailure: self.getProperty(notifierConfigs, notifierName, "onFailure") === "true",
                        from: self.splitByComma(self.getProperty(notifierConfigs, notifierName, "from")),
                        to: self.splitByComma(self.getProperty(notifierConfigs, notifierName, "to")),
                        hostname: self.getProperty(notifierConfigs, notifierName, "hostname")
                    }
                });
            } else {
                throw new Error("Unsupported notifier type " + notifierName);
            }
        });

        return notifiers;
    };

    this.parseCleanupCron = function () {
        return {
            cron: this.getProperty(config, "cleanup", "cron"),
            timezone: this.getProperty(config, "cleanup", "timezone", "UTC"),
        };
    };

    this.getProperty = function (configs, name, propName, defaultValue) {
        var property = name + '.' + propName;

        if (!configs.hasOwnProperty(property)) {
            if (defaultValue !== undefined) {
                return defaultValue
            }

            throw new Error("Missing property: [" + property + "]");
        }

        return configs[property];
    };

    this.getBooleanProperty = function (configs, name, propName) {
        const property = this.getProperty(configs, name, propName);

        return property === true || property === "true";
    };

    this.splitByComma = function (value) {
        var values = value.split(",");

        return values.filter(function (v) {
            return v && v.length > 0;
        });
    };

}

exports.parseSnapshots = function (config) {
    return new SnapshotterConfigParser(config).parseSnapshots();
};

exports.parseNotifiers = function (config) {
    return new SnapshotterConfigParser(config).parseNotifiers();
};

exports.parseCleanupCron = function (config) {
    return new SnapshotterConfigParser(config).parseCleanupCron();
};
