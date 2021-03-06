package com.abrenoch.hyperiongrabber;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

public class HyperionNotification {
    private static final String NOTIFICATION_CHANNEL_ID = "com.abrenoch.hyperiongrabber.notification";
    private static final String NOTIFICATION_CHANNEL_LABEL = "Hyperion Grabber Notifications";
    private static final String NOTIFICATION_TITLE = "Hyperion Grabber";
    private static final String NOTIFICATION_DESCRIPTION = "Currently grabbing screen content";
    private final NotificationManager mNotificationManager;
    private final Context mContext;
    private Notification.Action mAction = null;


    HyperionNotification (Context ctx, NotificationManager manager) {
        mNotificationManager = manager;
        mContext = ctx;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(makeChannel());
        }
    }

    public void setAction(int code, String label, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getService(mContext, code,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAction = new Notification.Action.Builder(
                    Icon.createWithResource(mContext, R.drawable.ic_launcher_foreground),
                    label,
                    pendingIntent
            ).build();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mAction = new Notification.Action.Builder(
                    R.drawable.ic_launcher_foreground,
                    label,
                    pendingIntent
            ).build();
        }
    }

    public Notification buildNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(mContext, NOTIFICATION_CHANNEL_ID)
                    .setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(NOTIFICATION_TITLE)
                    .setContentText(NOTIFICATION_DESCRIPTION);
            if (mAction != null) {
                builder.addAction(mAction);
            }
            return builder.build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                    .setVibrate(null)
                    .setSound(null)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(NOTIFICATION_TITLE)
                    .setContentText(NOTIFICATION_DESCRIPTION);
            return builder.build();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel makeChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_LABEL, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(NOTIFICATION_CHANNEL_LABEL);
        notificationChannel.enableVibration(false);
        notificationChannel.setSound(null,null);
        return notificationChannel;
    }
}
