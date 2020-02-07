package com.me.elektrichki;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.List;

public class ShowNotification extends BroadcastReceiver {
    int get1;
    Long trainTime, nextTime;
    Calendar train;
    int minute;
    int hour;
    String formattedMinutes,  minute1;
    long diffHours;
    String string1, string2;



    @Override
    public void onReceive(Context context, Intent intent) {
        WakeLocker.acquire(context);

        get1  = intent.getIntExtra("get1", 0);


        trainTime  = intent.getLongExtra("trainTime", 0);
      train = Calendar.getInstance();
        train.setTimeInMillis(trainTime);
        hour = train.get(Calendar.HOUR_OF_DAY);
        minute = train.get(Calendar.MINUTE);
        formattedMinutes = String.format("%02d", minute);

        nextTime  = intent.getLongExtra("nextTime", trainTime);
        train.setTimeInMillis(nextTime);


        long diff = nextTime-trainTime;
        long diffMinutes = diff / (60 * 1000) % 60;

        diffHours = diff / (60 * 60 * 1000) % 24;
        minute1 = String.format("%02d", diffMinutes);


        string1 ="Отпр "+ hour + ":" + formattedMinutes+" ч-з " + get1+ "мин";
        string2 = "След за этой ч-з: " + diffHours + ":" + minute1;

        sendNotification(context);

//        if (pos<(times.size()-1))
  //      {
    //        pos++;
    //    MainActivity.startAlarmBroadcastReceiver(context, times, pos);
    //    }
    }



public void sendNotification(Context context) {
    Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.punch);




    NotificationCompat.Builder builder =
            new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.ic_dialog_email)
                    .setContentTitle(string1)
                    .setContentText(string2)
                    .setWhen(System.currentTimeMillis() + 6000L)
                    .setSound(alarmSound);


    Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.punch);






    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);




    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                //.setContentType(AudioAttributes.USAGE_NOTIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED)
                .build();



        if (notificationManager != null) {
            List<NotificationChannel> channelList = notificationManager.getNotificationChannels();

            for (int i = 0; channelList != null && i < channelList.size(); i++) {
                notificationManager.deleteNotificationChannel(channelList.get(i).getId());
            }
        }


        String channelId = "firstChannel";
        NotificationChannel channel = null;

        channel = new NotificationChannel(
                channelId,
                "First train channel", NotificationManager.IMPORTANCE_HIGH
        );
        channel.setSound(sound, audioAttributes);
        channel.enableVibration(true);
        notificationManager.createNotificationChannel(channel);
        builder.setChannelId(channelId);
    }

        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

        managerCompat.notify(1, notificationCompat);






}

}
