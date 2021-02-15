package com.example.fitastic.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleService;

import com.example.fitastic.LoginActivity;
import com.example.fitastic.NavPage;
import com.example.fitastic.R;

public class TrackingService extends LifecycleService {

    /* Foreground tracking service means it can't be killed by the system when resources are low */

    private final String ACTION_START_OR_RESUME = "ACTION_START_OR_RESUME";
    private final String ACTION_PAUSE = "ACTION_PAUSE";
    private final String ACTION_STOP = "ACTION_STOP";
    private final String ACTION_SHOW_STARTFRAG = "ACTION_SHOW_STARTFRAG";

    private final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";
    private final String NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME";
    private final int NOTIFICATION_ID = 1;

    private boolean isFirstRun = true;

    Object Object;

    // called whenever intent sent to this service
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        // get action from intent
        if (intent != null) {
            switch (intent.getExtras().getString("action")) {
                case ACTION_START_OR_RESUME:
                    if (isFirstRun) {
                        Log.i("Tracking Action", "Action start/resume: ");
                        startForegroundService();
                        isFirstRun = false;
                    }
                    break;
                case ACTION_PAUSE: Log.i("TrackingAction", "Action pause: ");
                    break;
                case ACTION_STOP: Log.i("TrackingAction", "Action stop: ");
                    break;
                default:
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    // starts the service
    private void startForegroundService() {
        // this needed to show notification on android system
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // notification channels unsupported for android versions below andriod O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }

        // create notification with notification channel
        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.runningman)
                .setContentTitle("Fitastic - Run")
                .setContentText("00:00:00")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(getActivityPendingIntent()).build();

        // start foreground service with notification
        startForeground(NOTIFICATION_ID, notification);
    }

    // create activity pending intent
    private PendingIntent getActivityPendingIntent() {
        Intent i = new Intent(this, NavPage.class);
        i.putExtra("action", ACTION_SHOW_STARTFRAG);
        return PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // create notification channel
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager) {
        // create notification
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW);


        notificationManager.createNotificationChannel(channel);
    }
}
