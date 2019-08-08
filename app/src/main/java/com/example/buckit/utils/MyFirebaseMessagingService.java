package com.example.buckit.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.buckit.R;
import com.example.buckit.activities.HomeActivity;
import com.example.buckit.activities.ViewFriends;
import com.example.buckit.activities.ViewProfile;
import com.example.buckit.models.UserNotification;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingServic";
    private static final String KEY_BODY = "body";
    private static final String KEY_DATA = "data";
    private static final String KEY_NUMTYPE = "numType";
    private static final String KEY_FRIEND_NOTIFICATION = "friendNotification";
    private static final String KEY_EVENT_NOTIFICATION = "eventNotification";
    private static final String KEY_RELATEDUSER = "relatedUser";
    private static final String KEY_TOINVITE = "toInvite";

    public MyFirebaseMessagingService() {

    }

    @Override
    public void onDeletedMessages() {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getData().size() > 0){
            Log.d(TAG, remoteMessage.getData().toString());
            sendNotification(remoteMessage.getData().get(KEY_BODY), remoteMessage.getData().get(KEY_NUMTYPE));
            addToList(remoteMessage.getData().get(KEY_BODY), remoteMessage.getData().get(KEY_TOINVITE), remoteMessage.getData().get(KEY_NUMTYPE));
        }
    }

    private void addToList(String message, String relatedUser, String type){
        UserNotification newNotification = new UserNotification();
        newNotification.setMessage(message);
        newNotification.setToNotify(relatedUser);
        newNotification.setType(type);
        newNotification.setSeen(false);
        newNotification.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("Notifications", "Create a new notification success!");
                } else {
                    Log.d("Notifications", "Failed in creating a notification!");
                    e.printStackTrace();
                }
            }
        });


    }


    private void sendNotification(String messageBody, String numType) {
        Intent intent = new Intent(this, HomeActivity.class);
        if(numType.equals(KEY_FRIEND_NOTIFICATION)){
            intent = new Intent(this, ViewFriends.class);
        } else {
            intent = new Intent(this, ViewProfile.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Standard";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.bucket_icon_white)
                        .setContentTitle("Buck It")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}