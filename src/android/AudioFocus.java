package com.maigre.cordova.plugins;

import android.content.Context;
import android.media.AudioManager;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

public class AudioFocus extends CordovaPlugin {
    private CallbackContext focusChangeCallbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("requestFocus")) {
            this.requestFocus(callbackContext);
            return true;
        }
        if (action.equals("cancelFocus")) {
            this.cancelFocus(callbackContext);
            return true;
        }
        if (action.equals("onFocusChange")) {
            this.onFocusChange(callbackContext);
            return true;
        }
        return false;
    }

    private AudioManager.OnAudioFocusChangeListener listener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChangeCallbackContext != null) {
                String focusState;
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        focusState = "AUDIOFOCUS_GAIN";
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        focusState = "AUDIOFOCUS_LOSS";
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        focusState = "AUDIOFOCUS_LOSS_TRANSIENT";
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        focusState = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
                        break;
                    default:
                        focusState = "AUDIOFOCUS_UNKNOWN";
                }
                PluginResult result = new PluginResult(PluginResult.Status.OK, focusState);
                result.setKeepCallback(true);
                focusChangeCallbackContext.sendPluginResult(result);
            }
        }
    };

    private void requestFocus(CallbackContext callbackContext) {
        // get AudioManager
        AudioManager am = (AudioManager)this.cordova.getActivity()
                                    .getApplicationContext()
                                    .getSystemService(Context.AUDIO_SERVICE);

        // request audio focus
        int result = am.requestAudioFocus(listener,
                                        AudioManager.STREAM_MUSIC,
                                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

        // return result
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            callbackContext.success("");
        } else {
            callbackContext.error("");
        }
    }

    private void cancelFocus(CallbackContext callbackContext) {

         // get AudioManager
        AudioManager am = (AudioManager) this.cordova.getActivity()
                                    .getApplicationContext()
                                    .getSystemService(Context.AUDIO_SERVICE);

        int result = am.abandonAudioFocus(listener);

        // return result
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            callbackContext.success("");
        } else {
            callbackContext.error("");
        }
    }

    private void onFocusChange(CallbackContext callbackContext) {
        this.focusChangeCallbackContext = callbackContext;
        // Keep callback alive for future events
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }
}
