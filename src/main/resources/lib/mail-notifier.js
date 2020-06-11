var libs = {
    mail: require('/lib/xp/mail'),
    notifierBase: require('/lib/notifier-base')
};

function MailNotifier(config) {
    if (!config) {
        throw new Error('The configuration of Mail notifier should be provided');
    }
    if (!config.from || !config.from.length) {
        throw new Error('The config property \"from\" is required');
    }
    if (!config.to || !config.to.length) {
        throw new Error('The config property \"to\" is required');
    }

    this.config = {
        from: config.from,
        to: config.to,
        hostname: config.hostname || 'UNKNOWN',
        onFailure: config.onFailure,
        onSuccess: config.onSuccess
    };

    this.createSubject = function (description, state) {
        var result = "com.enonic.app.snapshotter";

        result += " - ";
        result += "Host: " + this.config.hostname;
        result += " - ";
        result += " job " + description;
        result += " - ";
        result += " Status: " + state;

        return result;
    };
}

MailNotifier.prototype = Object.create(libs.notifierBase.NotifierInterface);

MailNotifier.prototype.name = function () {
    return 'mail';
};

MailNotifier.prototype.failed = function (jobDescription, errorText) {
    var self = this;

    if (self.config.onFailure === true) {
        libs.mail.send({
            from: self.config.from,
            to: self.config.to,
            subject: self.createSubject(jobDescription, "FAILED"),
            body: errorText
        });
    }
};

MailNotifier.prototype.success = function (jobDescription) {
    var self = this;

    if (self.config.onSuccess === true) {
        libs.mail.send({
            from: self.config.from,
            to: self.config.to,
            subject: self.createSubject(jobDescription, "OK")
        });
    }
};

MailNotifier.prototype.test = function (message) {
    var self = this;

    libs.mail.send({
        from: self.config.from,
        to: self.config.to,
        subject: message
    });
};

MailNotifier.prototype.config = function () {
    return this.config;
};

exports.newInstance = function (config) {
    return new MailNotifier(config);
};
