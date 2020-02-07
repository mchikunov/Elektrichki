package com.me.elektrichki;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {
private Context context;

    String string1, string2;

    int delayTime;


    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {


        Long trainTime = getInputData().getLong("timeTrain", 0);
        Long nextTime = getInputData().getLong("nextTrain", trainTime);
        delayTime = getInputData().getInt("delayTime", 1); //25 or 5 min
        Long thisTime = System.currentTimeMillis();
        Long diffToTrain = trainTime - thisTime;

        long diffMinutes0 = diffToTrain / (60 * 1000) % 60;
        Long diffHours0 = diffToTrain / (60 * 60 * 1000) % 24;
        String minute0 = String.format("%02d", diffMinutes0);

        Calendar train = Calendar.getInstance();
        train.setTimeInMillis(trainTime);
        int hour = train.get(Calendar.HOUR_OF_DAY);
        int minute = train.get(Calendar.MINUTE);
        String formattedMinutes = String.format("%02d", minute);

        train.setTimeInMillis(nextTime);


        long diff = nextTime-trainTime;
        long diffMinutes = diff / (60 * 1000) % 60;

        Long diffHours = diff / (60 * 60 * 1000) % 24;
        String minute1 = String.format("%02d", diffMinutes);



       string1 ="Отпр "+ hour + ":" + formattedMinutes+" ч-з " + diffHours0 + ":" + minute0;
       string2 = "След за этой ч-з: " + diffHours + ":" + minute1;


        Uri alarmSound1 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getPackageName() + "/" + R.raw.punch);
        Uri alarmSound2 = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getPackageName() + "/" + R.raw.sound2);

        Uri resultAlarm = delayTime==1 ? alarmSound1:alarmSound2;

        try {
            Ringtone r = RingtoneManager.getRingtone(context, resultAlarm);
            r.play();

        } catch (Exception e) { }

        sendNotification(context);

        return Result.success();
    }




    public void sendNotification(Context context) {






        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_popup_reminder)
                        .setContentTitle(string1)
                        .setContentText(string2)
                        .setWhen(System.currentTimeMillis() + 6000L)
                        .setSound(null);






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


            String channelId1 = "FirstChannel";

            NotificationChannel channel1 = null;
            channel1 = new NotificationChannel(
                    channelId1,
                    "First channel", NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setSound(null, audioAttributes);
            channel1.enableVibration(true);


                notificationManager.createNotificationChannel(channel1);
                builder.setChannelId(channelId1);


        }

        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);


        if (delayTime==1)
            managerCompat.notify(1, notificationCompat);
        if (delayTime==2)
            managerCompat.notify(2, notificationCompat);






    }


}
