package com.maigre.cordova.plugins;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;

public class AudioFocus extends CordovaPlugin {

    private CallbackContext focusChangeCallbackContext;
    private AudioFocusRequest focusRequest; // API 26+

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

    private final AudioManager.OnAudioFocusChangeListener listener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChangeCallbackContext == null) return;
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
    };

    @SuppressWarnings("deprecation")
    private void requestFocus(CallbackContext callbackContext) {
        AudioManager am = (AudioManager) this.cordova.getActivity()
                .getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .setOnAudioFocusChangeListener(listener)
                    .build();
            result = am.requestAudioFocus(focusRequest);
        } else {
            result = am.requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            startAudioFocusService();
            callbackContext.success("");
        } else {
            callbackContext.error("");
        }
    }

    @SuppressWarnings("deprecation")
    private void cancelFocus(CallbackContext callbackContext) {
        AudioManager am = (AudioManager) this.cordova.getActivity()
                .getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && focusRequest != null) {
            result = am.abandonAudioFocusRequest(focusRequest);
        } else {
            result = am.abandonAudioFocus(listener);
        }
        stopAudioFocusService();
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            callbackContext.success("");
        } else {
            callbackContext.error("");
        }
    }

    private void startAudioFocusService() {
        Context ctx = this.cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(ctx, AudioFocusService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx.startForegroundService(intent);
        } else {
            ctx.startService(intent);
        }
    }

    private void stopAudioFocusService() {
        Context ctx = this.cordova.getActivity().getApplicationContext();
        ctx.stopService(new Intent(ctx, AudioFocusService.class));
    }

    private void onFocusChange(CallbackContext callbackContext) {
        this.focusChangeCallbackContext = callbackContext;
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }
}
