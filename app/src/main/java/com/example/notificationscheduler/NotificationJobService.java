package com.example.notificationscheduler;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;



@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NotificationJobService extends JobService {

    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    NotificationManager notificationManager;


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        //Create the notification channel
        createNotificationChannel();

        //Set up the notification content intent to launch the app when clicked
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_job_running);
        builder.setContentTitle(getString(R.string.Not_Title));
        builder.setContentText("Your Job is running!");
        builder.setContentIntent(contentPendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        notificationManager.notify(0, builder.build());

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public void createNotificationChannel(){
        //Define notification manager object
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,"Job Service notification",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifications from Job Service");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
