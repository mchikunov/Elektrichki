package com.me.elektrichki;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationManagerCompat;

public class SendNotification extends JobIntentService {

    private static final int NOTIFICATION_ID = 0;


    @Override
    protected void onHandleWork(Intent intent) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("My Title");
        builder.setContentText("This is the Body");
        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "Your_channel_id";
            NotificationChannel channel = null;

            channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title", NotificationManager.IMPORTANCE_HIGH
            );

            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        Notification notificationCompat = builder.build();
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
      managerCompat.notify(0, notificationCompat);
    }
}
