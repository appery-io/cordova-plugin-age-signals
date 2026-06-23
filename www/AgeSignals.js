var exec = require('cordova/exec');

function call(action, args) {
    return new Promise(function (resolve, reject) {
        exec(resolve, reject, 'AgeSignals', action, args || []);
    });
}

module.exports = {
    checkAgeSignals: function (options) {
        return call('checkAgeSignals', [options || {}]);
    },

    checkEligibility: function () {
        return call('checkEligibility', []);
    },

    setUseFakeManager: function (options) {
        return call('setUseFakeManager', [options || {}]);
    },

    setNextAgeSignalsResult: function (options) {
        return call('setNextAgeSignalsResult', [options || {}]);
    },

    setNextAgeSignalsException: function (options) {
        return call('setNextAgeSignalsException', [options || {}]);
    }
};
