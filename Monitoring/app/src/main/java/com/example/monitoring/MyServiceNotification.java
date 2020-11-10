package com.example.monitoring;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.example.monitoring.activity.MainActivity;
import com.example.monitoring.activity.MonitoringActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class MyServiceNotification extends FirebaseMessagingService {
    public void shownotifikasi(Context context, String judul, String isi, Intent intent){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultintent = new Intent(this, MonitoringActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,1,resultintent,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        AudioAttributes att = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        int idnotif = 0;
        String ChannelID = "com.example.monitoring.feeshchator";
        String ChannelName = "Feeshchator IoT";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel nChannel = new NotificationChannel(ChannelID, ChannelName, importance);
            nChannel.setShowBadge(true);
            nChannel.enableVibration(true);
            nChannel.setSound(sound, att);
            nChannel.setGroup("Feeshchator");
            nChannel.shouldVibrate();
            nChannel.setVibrationPattern(new long[]{0,1000,50,300,1000,200});
            nChannel.enableVibration(true);
            nChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(nChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ChannelID)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_logo1_round)
                .setContentTitle(judul)
                .setContentIntent(pi)
                .setContentText(isi)
                .setGroup("Feeshchator")
                .setSound(sound)
                .setVibrate(new long[]{0,1000,50,300,1000,200})
                .setWhen(Calendar.getInstance().getTimeInMillis())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setFullScreenIntent(pendingIntent,true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(isi))
                .setPriority(Notification.PRIORITY_MAX);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        notificationManager.notify(idnotif, builder.build());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String judul = remoteMessage.getData().get("judul");
        String isi = remoteMessage.getData().get("isi");

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        if  (remoteMessage.getNotification() != null)
            shownotifikasi(getApplicationContext(), title, body, new Intent());
        else if  (remoteMessage.getData().size() > 0)
            shownotifikasi(getApplicationContext(), judul, isi, new Intent());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("TOKEN", "onNewToken: "+s);
    }
}
