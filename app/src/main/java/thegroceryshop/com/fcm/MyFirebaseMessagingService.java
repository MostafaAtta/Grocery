package thegroceryshop.com.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import thegroceryshop.com.R;
import thegroceryshop.com.activity.SplashActivity;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by rohitg on 12/5/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private NotificationManager mNotificationManager;
    private Bitmap large_icon;


    @Override
    public void onNewToken(@NonNull String refreshedToken) {
        super.onNewToken(refreshedToken);

        // Get updated InstanceID token.
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        large_icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String message) {

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        Intent resultIntent = new Intent(this, SplashActivity.class);
        resultIntent = new Intent(this, SplashActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.putExtra("isFromNotification", true);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        int id = (int) (System.currentTimeMillis() * (int) (Math.random() * 100));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationHelper notificationHelper = new NotificationHelper(this);
            Notification.Builder notificationBuilder;
            notificationBuilder = notificationHelper.getNotification(getString(R.string.app_name), message, resultPendingIntent, large_icon);
            if (notificationBuilder != null) {
                notificationHelper.notify(id, notificationBuilder);
            }
        } else {
            Notification.Builder notification = new Notification.Builder(this)
                    .setContentText(message)
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    //.setLargeIcon(large_icon)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setTicker(message)
                    .setContentTitle(getString(R.string.app_name))
                    .setSound(sound)
                    .setVibrate(new long[]{100L, 100L, 200L, 500L});

            getNotificationManager().notify(id, notification.build());
        }

    }

    private NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }
}
