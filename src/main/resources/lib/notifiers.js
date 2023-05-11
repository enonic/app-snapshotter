const libs = {
    slackNotifier: require('/lib/slack-notifier'),
    mailNotifier: require('/lib/mail-notifier'),
    configParser: require('/lib/config-parser'),
};

function Notifiers() {
    this.notifiers = [];
}

Notifiers.prototype.register = function (notifier) {
    this.notifiers.push(notifier);
};

Notifiers.prototype.notify = function (notifierName) {
    this.notifiers.forEach(function (notifier) {
        if (notifierName === notifier.name()) {
            notifier.test('testing notifier');
        }
    });
};

Notifiers.prototype.success = function (text) {
    this.notifiers.forEach(function (notifier) {
        notifier.success(text);
    });
};

Notifiers.prototype.failed = function (text, errorMessage) {
    this.notifiers.forEach(function (notifier) {
        notifier.failed(text, errorMessage);
    });
};

var notifiers = new Notifiers();

exports.notify = function (notifierName) {
    notifiers.notify(notifierName)
};

exports.success = function (text) {
    notifiers.success(text);
};

exports.failed = function (text, errorMessage) {
    notifiers.failed(text, errorMessage);
};

function registerNotifier (notifier) {
    notifiers.register(notifier);
}

exports.initNotifiers = function (appConfig) {
    libs.configParser.parseNotifiers(appConfig).forEach(function (notifier) {
        if (notifier.name === 'slack') {
            registerNotifier(libs.slackNotifier.newInstance(notifier.config));
        } else if (notifier.name === 'mail') {
            registerNotifier(libs.mailNotifier.newInstance(notifier.config));
        } else {
            // do nothing
        }
    });
}
