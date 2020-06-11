var libs = {
    httpClient: require('/lib/http-client'),
    notifierBase: require('/lib/notifier-base')
};

// The SlackMessageAttachment definition

function SlackMessageAttachment(title, text, color) {
    this.title = title || "";
    this.text = text || "";
    this.color = color || "#888";
    this.markdown = [];
}

SlackMessageAttachment.prototype.addMarkdown = function (args) {
    var values = args || [];

    var self = this;

    values.forEach(function (value) {
        self.markdown.push(value);
    })
};

SlackMessageAttachment.prototype.toJson = function () {
    var obj = {};

    if (this.title) {
        obj.title = this.title;
    }
    if (this.text) {
        obj.text = this.text;
    }
    if (this.color) {
        obj.color = this.color;
    }
    if (this.markdown.length) {
        obj.mrkdwn_in = this.markdown;
    }

    return obj;
};

// The SlackMessage definition

function SlackMessage() {
    this.text = null;
    this.attachments = [];
}

SlackMessage.prototype.addAttachment = function (attachment) {
    this.attachments.push(attachment);
};

SlackMessage.prototype.toJson = function () {
    var json = {};

    if (this.text) {
        json.text = this.text;
    }

    if (this.attachments.length) {
        var attachments = [];

        this.attachments.forEach(function (attachment) {
            attachments.push(attachment.toJson());
        });

        json.attachments = attachments;
    }

    return json;
};

// The SlackNotifier definition

function SlackNotifier(config) {
    if (!config) {
        throw new Error('The configuration of Slack notifier should be provided');
    }
    if (!config.url) {
        throw new Error('The config property url is required');
    }

    this.config = {
        url: config.url,
        project: config.project || 'UNKNOWN',
        reportOnSuccess: config.reportOnSuccess || false,
        reportOnFailure: config.reportOnFailure || true
    };
}

SlackNotifier.prototype = Object.create(libs.notifierBase.NotifierInterface);

SlackNotifier.prototype.name = function () {
    return 'slack';
};

SlackNotifier.prototype.test = function (message) {
    this.send(message, false);
};

SlackNotifier.prototype.success = function (text) {
    if (this.config.reportOnSuccess === true || this.config.reportOnSuccess === "true") {
        this.send(text, false);
    }
};

SlackNotifier.prototype.failed = function (text) {
    if (this.config.reportOnFailure === true || this.config.reportOnFailure === "true") {
        this.send(text, true);
    }
};

SlackNotifier.prototype.config = function () {
    return this.config;
};

SlackNotifier.prototype.send = function (textMessage, failed) {
    var text = "Snapshot [" + textMessage + "] " + (failed ? "failed" : "success");
    var color = failed ? "#d1252e" : "#7ec982";

    var slackMessageAttachment = new SlackMessageAttachment(this.config.project, text, color);
    slackMessageAttachment.addMarkdown(["text", "title"]);

    var slackMessage = new SlackMessage();
    slackMessage.addAttachment(slackMessageAttachment);

    var self = this;

    libs.httpClient.request({
        url: self.config.url,
        method: 'POST',
        contentType: 'application/json',
        body: JSON.stringify(slackMessage.toJson())
    });
};

exports.newInstance = function (config) {
    return new SlackNotifier(config);
};
