var abstractFn = function () {
    throw new Error('This function should be implemented');
};

exports.NotifierInterface = {
    name: abstractFn,
    success: function (text) {
        throw new Error('This function should be implemented');
    },
    failed: function (text, errorMessage) {
        throw new Error('This function should be implemented');
    },
    test: function (message) {
        throw new Error('This function should be implemented');
    },
    config: abstractFn
};
