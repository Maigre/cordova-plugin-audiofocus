package com.maigre.cordova.plugins;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

public class AudioFocusService extends Service {

    static final int NOTIFICATION_ID = 7374;
    private static final String CHANNEL_ID = "flanerie_audio";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = buildNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification buildNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Flanerie Audio", NotificationManager.IMPORTANCE_MIN);
            channel.setSound(null, null);
            channel.enableVibration(false);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);

            return new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Flanerie")
                    .setContentText("Lecture audio")
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .build();
        } else {
            return new Notification.Builder(this)
                    .setContentTitle("Flanerie")
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .build();
        }
    }
}
