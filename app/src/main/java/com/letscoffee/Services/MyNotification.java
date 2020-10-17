package com.letscoffee.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.letscoffee.Activities.HomeActivity;
import com.letscoffee.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MyNotification {
    private static Context mContext;
    private static PendingIntent dismissIntent;
    private static Uri alarmSound;
    private String TITLE;
    private String MESSAGE;
    private String IMAGE;
    private PendingIntent pendingIntent;
    private NotificationManager notifManager;
    private int Marker= R.drawable.logo;

    public static MyNotification get(Context context) {
        mContext = context;
        Intent buttonIntent = new Intent(mContext, NotificationReceiver.class);
        dismissIntent = PendingIntent.getBroadcast(mContext, 0, buttonIntent, 0);
        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        return new MyNotification();
    }

    public MyNotification setTitle(String title) {
        this.TITLE = title;
        return this;
    }

    public MyNotification setMessage(String message) {
        this.MESSAGE = message;
        return this;
    }

    public MyNotification setImage(String image) {
        this.IMAGE = image;
        return this;
    }public MyNotification setMarker(int marker) {
        this.Marker = marker;
        return this;
    }

    public MyNotification setPendingIntent(String key) {
        Intent intent;
        intent = new Intent(mContext, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        return this;
    }public MyNotification setPendingIntent(String key, String id) {
        Intent intent;
        if (key.equals("CHAT")){
            intent = new Intent(mContext, HomeActivity.class).putExtra("id",id);
        }else {
            intent = new Intent(mContext, HomeActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        return this;
    }

    public void headsUpNotification() {
        int NOTIFICATION_ID = 1;
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(TITLE)
                        .setContentText(MESSAGE)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent buttonIntent = new Intent(mContext, NotificationReceiver.class);
        buttonIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent dismissIntent = PendingIntent.getBroadcast(mContext, 0, buttonIntent, 0);
        builder.addAction(android.R.drawable.ic_menu_view, "VIEW", pendingIntent);
        builder.addAction(android.R.drawable.ic_delete, "DISMISS", dismissIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(0, builder.build());

    }

    public void notificationActions() {
        int NOTIFICATION_ID = 1;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setSmallIcon(R.drawable.logo);
        builder.setColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo));
        builder.setContentTitle(TITLE);
        builder.setContentText(MESSAGE);
        builder.setAutoCancel(true);

        Intent buttonIntent = new Intent(mContext, NotificationReceiver.class);
        buttonIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent dismissIntent = PendingIntent.getBroadcast(mContext, 0, buttonIntent, 0);
        builder.setContentIntent(pendingIntent);
        builder.addAction(android.R.drawable.ic_menu_view, "VIEW", pendingIntent);
        builder.addAction(android.R.drawable.ic_delete, "DISMISS", dismissIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(0, builder.build());
    }

    private void clearNotification(int notificationId, Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }

    public void Notify() {
        Notification notification = new NotificationCompat.Builder(mContext, "channel01")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(TITLE)
                .setContentText(MESSAGE)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(android.R.drawable.ic_menu_view, "VIEW", pendingIntent)
                .addAction(android.R.drawable.ic_delete, "DISMISS", dismissIntent)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(0, notification);


    }

    public void BigNotification() {
        if (IMAGE.length() > 0) {
            Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    Picasso.get().load(IMAGE).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Notification notification = new NotificationCompat.Builder(mContext, "channel01")
                                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                                    .setContentTitle(TITLE)
                                    .setContentText(MESSAGE)
                                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .addAction(android.R.drawable.ic_menu_view, "VIEW", pendingIntent)
                                    .addAction(android.R.drawable.ic_delete, "DISMISS", dismissIntent)
                                    .build();
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
                            notificationManager.notify(0, notification);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Notify();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                }
            });
        } else {
            Notify();
        }

    }

    public void createNotification() {
        final int NOTIFY_ID = 1002;
        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.
        NotificationCompat.Builder builder;

        if (notifManager == null) {
            notifManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setShowBadge(true);
                mChannel.setLightColor(Color.GREEN);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(mContext, id);


            builder.setContentTitle(TITLE)  // required
                    .setSmallIcon(Marker) // required
                    .setContentText(MESSAGE)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(MESSAGE)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {

            builder = new NotificationCompat.Builder(mContext);
            builder.setContentTitle(TITLE)                           // required
                    .setSmallIcon(Marker) // required
                    .setContentText(MESSAGE)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(MESSAGE)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(alarmSound);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            if (alarmSound != null) {
                // Changing Default mode of notification
                builder.setDefaults(Notification.DEFAULT_VIBRATE);
                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                // Creating Channel
                NotificationChannel notificationChannel = new NotificationChannel("CH_ID", "Testing_Audio", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setSound(alarmSound, audioAttributes);
                notifManager.createNotificationChannel(notificationChannel);
            }
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }
}
