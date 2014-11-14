package com.cmpsoft.mobile.plugin.pushnotification;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class PushNotification extends CordovaPlugin
{
	private BroadcastReceiver receiver = null;
    private CallbackContext pushCallbackContext = null;
    
    public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
	public static final String RESPONSE_METHOD = "method";
	public static final String RESPONSE_CONTENT = "content";
	public static final String RESPONSE_ERRCODE = "errcode";
	
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
	{
		if(action.equals("init"))
		{
			
			this.pushCallbackContext = callbackContext;
			super.initialize(cordova, webView);
	        IntentFilter intentFilter = new IntentFilter();
	        intentFilter.addAction(PushConstants.ACTION_RECEIVE);
	        if (this.receiver == null)
	        {
	            this.receiver = new BroadcastReceiver()
	            {
	                @Override
	                public void onReceive(Context context, Intent intent)
	                {
	            		if (intent.getAction().equals(PushConstants.ACTION_RECEIVE))
	            		{
	            			sendPushInfo(context, intent);
	            		}
	                }
	            };
	            cordova.getActivity().registerReceiver(this.receiver, intentFilter);
	        }

	        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
	        PushManager.startWork(cordova.getActivity().getApplicationContext(), 0, args.getString(0));
            return true;
		}else if (action.equals("getInfo")) {
            JSONObject r = new JSONObject();
            SharedPreferences sp = PreferenceManager
    				.getDefaultSharedPreferences(cordova.getActivity());
    		//appId = sp.getString("appid", "");
    		/*channelId = sp.getString("channel_id", "");
    		clientId = sp.getString("user_id", "");*/	
            r.put("appId", sp.getString("appid", ""));
            r.put("channelId",sp.getString("channel_id", ""));
            r.put("clientId", sp.getString("user_id", ""));
            callbackContext.success(r);
            return true;
        }
		return false;
	}
	
	private void sendPushInfo(Context context, Intent intent)
	{
		String content = "";
		JSONObject info = null;
		if (intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT) != null)
		{
			content = new String(intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));
			try
			{
				info = (JSONObject)new JSONObject(content).get("response_params");
				info.put("deviceType", 3);
				
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(cordova.getActivity());
				Editor editor = sp.edit();
				editor.putString("appid", info.getString("appid"));
				editor.putString("channel_id", info.getString("channel_id"));
				editor.putString("user_id", info.getString("user_id"));

				editor.commit();
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		if (this.pushCallbackContext != null) 
		{
	        PluginResult result = new PluginResult(PluginResult.Status.OK, info);
	        result.setKeepCallback(false);
	        this.pushCallbackContext.sendPluginResult(result);
		}
        if (this.receiver != null) {
            try {
                this.cordova.getActivity().unregisterReceiver(this.receiver);
                this.receiver = null;
            } catch (Exception e) {
                //
            }
        }
    }
	/**
	 * ����Intent
	 * 
	 * @param intent
	 *            intent
	 */
	private void handleIntent(Intent intent) {
		String action = intent.getAction();

		if (ACTION_RESPONSE.equals(action)) {

			String method = intent.getStringExtra(RESPONSE_METHOD);

			if (PushConstants.METHOD_BIND.equals(method)) {
				String toastStr = "";
				int errorCode = intent.getIntExtra(RESPONSE_ERRCODE, 0);
				if (errorCode == 0) {
					String content = intent
							.getStringExtra(RESPONSE_CONTENT);
					String appid = "";
					String channelid = "";
					String userid = "";

					try {
						JSONObject jsonContent = new JSONObject(content);
						JSONObject params = jsonContent
								.getJSONObject("response_params");
						appid = params.getString("appid");
						channelid = params.getString("channel_id");
						userid = params.getString("user_id");
					} catch (JSONException e) {
						
					}

					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(cordova.getActivity());
					Editor editor = sp.edit();
					editor.putString("appid", appid);
					editor.putString("channel_id", channelid);
					editor.putString("user_id", userid);
					editor.commit();

					toastStr = "Bind Success";
				} else {
					toastStr = "Bind Fail, Error Code: " + errorCode;
					if (errorCode == 30607) {
						Log.d("Bind Fail", "update channel token-----!");
					}
				}
			}
		} 
	}
}
