# cordova-plugin-audiofocus
AudioFocus is a Cordova plugin for Android platform that allows requesting of audio focus.
> Android uses audio focus to moderate audio playback—only apps that hold the audio focus should play audio.

### Installation
Using Cordova/Ionic

```sh
$ cordova plugin add https://github.com/Maigre/cordova-plugin-audiofocus.git
```

### Usage
```js
cordova.plugins.audiofocus.requestFocus(function() {
    // succeeded to gain focus
}, function() {
    // failed to gain focus
});

cordova.plugins.audiofocus.cancelFocus(function() {
    // succeeded to release focus
}, function() {
    // failed to release focus
});

cordova.plugins.audiofocus.onFocusChange(function(focusState) {
    // focusState is a string: "AUDIOFOCUS_GAIN", "AUDIOFOCUS_LOSS", etc.
    if (focusState === "AUDIOFOCUS_LOSS" || focusState === "AUDIOFOCUS_LOSS_TRANSIENT") {
        // Pause your audio
    } else if (focusState === "AUDIOFOCUS_GAIN") {
        // Resume your audio
    } else if (focusState === "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK") {
        // Optionally lower your audio volume
    }
});
```

License
----
Apache License, Version 2.0
