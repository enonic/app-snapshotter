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

exports.register = function (notifier) {
    notifiers.register(notifier);
};

exports.notify = function (notifierName) {
    notifiers.notify(notifierName)
};

exports.success = function (text) {
    notifiers.success(text);
};

exports.failed = function (text, errorMessage) {
    notifiers.failed(text, errorMessage);
};
