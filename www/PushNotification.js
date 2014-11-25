var argscheck = require('cordova/argscheck'), channel = require('cordova/channel'), utils = require('cordova/utils'), exec = require('cordova/exec'), cordova = require('cordova');

var FGPushNotification = function() {
	this.registered = false;
	//
	this.appId = null;
	this.channelId = null;
	this.clientId = null;

	var me = this;

	me.getInfo(function(info) {
		me.appId = info.appId;
		me.channelId = info.channelId;
		me.clientId = info.clientId;
	});

	//alert("me >>>> " + JSON.stringify(me));
};

FGPushNotification.prototype.customSuccess = {};
FGPushNotification.prototype.customFail = {};

FGPushNotification.prototype.init = function(api_key, success, fail) {
	//alert(api_key);
	customSuccess = success;
	customFail = fail;
	exec(fastgoPushNotification.successFn, fastgoPushNotification.failureFn, 'FGPushNotification', 'init', [api_key]);
};

FGPushNotification.prototype.register = function(options, successCallback, errorCallback) {

	//alert("options" + JSON.stringify(options));

	// customSuccess = success;
	// customFail = fail;
	// exec(fastgoPushNotification.successFn, fastgoPushNotification.failureFn, 'FGPushNotification', 'init', [api_key]);

	//alert("PushNotification.prototype.register");
	alert("opt  >>>> " + JSON.stringify(options));

	if (errorCallback == null) {
		errorCallback = function() {
		}
	}

	if ( typeof errorCallback != "function") {
		console.log("PushNotification.register failure: failure parameter not a function");
		return
	}

	if ( typeof successCallback != "function") {
		console.log("PushNotification.register failure: success callback parameter must be a function");
		return
	}

	cordova.exec(successCallback, errorCallback, "PushPlugin", "register", [options]);

};

FGPushNotification.prototype.successFn = function(info) {
	//alert(JSON.stringify(info));
	if (info) {
		customSuccess(info);
		fastgoPushNotification.registered = true;
		cordova.fireDocumentEvent("cloudPushRegistered", info);
	}
};

FGPushNotification.prototype.failureFn = function(info) {
	customFail(info);
	fastgoPushNotification.registered = false;
};

FGPushNotification.prototype.getInfo = function(successCallback, errorCallback) {
	argscheck.checkArgs('fF', 'FGPushNotification.getInfo', arguments);
	exec(successCallback, errorCallback, "FGPushNotification", "getInfo", []);
};
var fastgoPushNotification = new FGPushNotification();

module.exports = fastgoPushNotification;
