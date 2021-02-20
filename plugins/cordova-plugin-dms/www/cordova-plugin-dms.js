var exec = require('cordova/exec');

exports.startReading = function (success, error) {
    exec(success, error, 'DMSPlugin', 'startReading');
};

exports.stopReading = function (success, error) {
    exec(success, error, 'DMSPlugin', 'stopReading');
}

exports.startSignActive = function (args, success, error) {
    exec(success, error, 'DMSPlugin', 'startSignActive', [args]);
}