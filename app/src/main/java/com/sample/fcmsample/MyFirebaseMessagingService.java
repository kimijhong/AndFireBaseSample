package com.sample.fcmsample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";

    //상대방이 보낸메세지를 받았을때 처리
    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG,"onMessageReceived() 호출됨");


        //remoteMessage.



        String from = remoteMessage.getFrom();
        Map<String,String> data = remoteMessage.getData();

        //RemoteMessage.Notification notification  = remoteMessage.getNotification();
        //notification.getTitle();

        //notification.


        String content = data.get("contents");

        Log.e(TAG,"getNotification : " + remoteMessage.getNotification());
        Log.e(TAG,"getData : " + remoteMessage.getData());
        Log.e(TAG,"from : " + from +" , contnets : " + content + ", ");



        sendToAcitivty(getApplicationContext(),from,content);


    }

    public void sendToAcitivty(Context context, String from , String contents)
    {
           Intent intent = new Intent(context,MainActivity.class);
           intent.putExtra("from",from);
           intent.putExtra("content",contents);
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);

           context.startActivity(intent);
    }
}
