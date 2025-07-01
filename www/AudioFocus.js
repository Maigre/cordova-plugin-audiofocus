var exec = require('cordova/exec');

exports.requestFocus = function(success, error) {
    success = success || function() {};
    error = error || function() {};

    exec(success, error, "AudioFocus", "requestFocus", []);
};

exports.cancelFocus = function (success, error) {
  success = success || function () {};
  error = error || function () {};

  exec(success, error, 'AudioFocus', 'cancelFocus', []);
};

exports.onFocusChange = function(callback) {
    exec(callback, null, 'AudioFocus', 'onFocusChange', []);
};