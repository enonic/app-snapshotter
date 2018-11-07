var sendSlackMessage = function (bodyMsg, slackWebhook) {
    slackWebhook = slackWebhook || config.getSlackWebhook();
    if (!slackWebhook) {
        log.info('Slack notifications not configured. Ignoring message: ' + JSON.stringify(bodyMsg, null, 2));
        return;
    }
    try {
        var response = httpClient.request({
            url: slackWebhook,
            method: 'POST',
            body: JSON.stringify(bodyMsg),
            connectionTimeout: 2000,
            readTimeout: 4000,
            contentType: 'application/json'
        });
        return response.status >= 200 && response.status < 300;
    } catch (e) {
        log.error('Error in Slack request [' + slackWebhook + ']: ' + e);
        return false;
    }
};