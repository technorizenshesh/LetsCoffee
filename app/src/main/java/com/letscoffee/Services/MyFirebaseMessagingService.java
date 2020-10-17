package com.letscoffee.Services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.letscoffee.R;

import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingServ";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                JSONObject object = new JSONObject(remoteMessage.getData().get("message"));
                String key = object.getString("key");
                String message = object.getString("message");
                if(key.equals("Notification from Admin")) {
                }else if(key.equals("chat message")) {
                    MyNotification.get(getApplicationContext())
                            .setTitle("You have a new message From "+object.getString("user_name"))
                            .setMessage(message)
                            .setMarker(R.drawable.logo)
                            .setPendingIntent("CHAT",object.getString("userid"))
                            .createNotification();
                    return;
                }
                MyNotification.get(getApplicationContext())
                        .setTitle(key)
                        .setMessage(message)
                        .setPendingIntent("HOME")
                        .createNotification();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            MyNotification.get(getApplicationContext())
                    .setTitle(remoteMessage.getNotification().getTitle())
                    .setMessage(remoteMessage.getNotification().getBody())
                    .setPendingIntent("")
                    .createNotification();
        }}
}
