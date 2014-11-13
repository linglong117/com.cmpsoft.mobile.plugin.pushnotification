var cordova = require('cordova'),
    exec = require('cordova/exec');

var FGPushNotification = function() {
	this.registered = false;
};
FGPushNotification.prototype.init = function(api_key)
{
    exec(fastgoPushNotification.successFn, fastgoPushNotification.failureFn, 'FGPushNotification', 'init', [api_key]);
};

FGPushNotification.prototype.successFn = function(info)
{
	if(info){
		fastgoPushNotification.registered = true;
		cordova.fireDocumentEvent("cloudPushRegistered", info);
	}
};

FGPushNotification.prototype.failureFn = function(info)
{
	fastgoPushNotification.registered = false;
};

var fastgoPushNotification = new FGPushNotification();

module.exports = fastgoPushNotification;