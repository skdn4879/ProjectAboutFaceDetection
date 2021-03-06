package com.techtown.mygraduationprojectwithjava;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FcmService extends FirebaseMessagingService {
    public FcmService() {
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d("token", token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("Load Message", remoteMessage.toString());
        Log.d("Received Message", "From : " + remoteMessage.getFrom() + " " +
                "Title : " + remoteMessage.getData().get("title") + " " +
                "Contents : " + remoteMessage.getData().get("contents"));

        if(!remoteMessage.getData().isEmpty()){
            sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("contents"));
        }
        //sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("contents"));
        //sendToActivity(remoteMessage.getFrom(), remoteMessage.getData().get("title"), remoteMessage.getData().get("contents"));
    }

    public void sendToActivity(String from, String title, String contents){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("title", title);
        intent.putExtra("contents", contents);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        getApplicationContext().startActivity(intent);
    }

    private void sendNotification(String title, String body){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //???????????? ?????? ?????? ??????

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT); //?????????

        String channelId = "graduationChannel"; //?????? ?????????
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_thumb_up_selected)
                .setContentTitle(title) //??????
                .setContentText(body) //??????
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //??????????????? ????????????
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Channel_Graduation", "Channel_Graduation",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build()); //???????????? 0?????? ?????? ????????? ???????????? ??? ??? ?????? ????????? ???????????? ??????
    }
}