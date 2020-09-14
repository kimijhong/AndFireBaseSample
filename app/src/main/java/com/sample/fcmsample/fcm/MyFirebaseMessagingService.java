package com.sample.fcmsample.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sample.fcmsample.MainActivity;
import com.sample.fcmsample.R;


import java.util.Map;

@SuppressLint("LongLogTag")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";

    /**
     * 토큰 발급
     */

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }


    /**
     *
     *  알림 메시지 :
     *  데이터 메시지 :
     *  알림 + 데이터 메시지 :
     *
     *
     */

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();
        Log.e(TAG, "onMessageReceived() 호출됨 From: " + from);


        Log.d(TAG, "From: " + remoteMessage.getFrom());



        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


            Map<String,String> data = remoteMessage.getData();
            Log.e(TAG,data.toString());

            // String channelUrl = null;
            //try {
            if (remoteMessage.getData().containsKey("title")) {


                // JSONObject sendBird = new JSONObject(remoteMessage.getData().get("sendbird"));
                /*

                JSONObject channel = (JSONObject) sendBird.get("channel");
                channelUrl = (String) channel.get("channel_url");*/

               // Map<String,String> data = remoteMessage.getData();
                //String title = remoteMessage.getNotification().getTitle();
                // String message = remoteMessage.getNotification().getBody();

                String imageUrl = (String) data.get("image");
                String action = (String) data.get("action");

                // Log.i(TAG, "onMessageReceived: title : "+title);
                // Log.i(TAG, "onMessageReceived: message : "+message);

                Log.i(TAG, "onMessageReceived: imageUrl : "+imageUrl);
                Log.i(TAG, "onMessageReceived: action : "+action);


                // Also if you intend on generating your own notifications as a result of a received FCM
                // message, here is where that should be initiated. See sendNotification method below.
                sendNotification(getBaseContext(), remoteMessage.getData().get("contents"),"");
            }
            //} catch (JSONException e) {
            //    e.printStackTrace();
            // }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            //remoteMessage.getNotification().get
            //String getImageUrl = new Uri(remoteMessage.getNotification().getImageUrl());
        }



    }



    @SuppressLint("LongLogTag")
    public void notificationWithBigPicture(String title, String message) {

       // WakeUpScreen.acquire(context, 10000);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        final String CHANNEL_ID = "COSBALL_ID_1";
        if (Build.VERSION.SDK_INT >= 26) {  // Build.VERSION_CODES.O
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "COSBALL_C_NAME", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        Log.e(TAG, "iUniqueId : " + iUniqueId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, iUniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "COSBALL_ID_1")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(getString(R.string.app_name))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX/*항상 펼치기*/);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.setBigContentTitle(title);
        style.setSummaryText(message);
        builder.setStyle(style);

        notificationManager.notify(iUniqueId, builder.build());
    }

    public static void sendNotification(Context context, String messageBody,String param) {

       // WakeUpScreen.acquire(context, 10000);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

       // RemoteViews remoteViews =  new RemoteViews(context.getPackageName(), R.layout.layout_remote_view);
       // remoteViews.setImageViewResource(R.id.iv_img, R.mipmap.ic_launcher);

        final String CHANNEL_ID = "COSBALL_ID_1";
        if (Build.VERSION.SDK_INT >= 26) {  // Build.VERSION_CODES.O
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "COSBALL_C_NAME", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("param", param);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, iUniqueId /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.parseColor("#7469C4"))  // small icon background color
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        if (true) { // 메세지 숨길지 말지 옵션
            notificationBuilder.setContentText(messageBody);
        } else {
            notificationBuilder.setContentText("Somebody sent you a message.");
        }

        notificationManager.notify(iUniqueId /* ID of notification */, notificationBuilder.build());
    }

    @SuppressLint("LongLogTag")
    public void sendToAcitivty(Context context, String from , String contents)
    {
           Intent intent = new Intent(context,MainActivity.class);
           intent.putExtra("from",from);
           intent.putExtra("content",contents);
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);

           context.startActivity(intent);
    }
}
