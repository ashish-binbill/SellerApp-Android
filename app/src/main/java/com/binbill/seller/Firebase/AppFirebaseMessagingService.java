package com.binbill.seller.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.notification.MobiComPushReceiver;
import com.binbill.seller.Constants;
import com.binbill.seller.R;
import com.binbill.seller.SplashActivity;
import com.binbill.seller.Utility;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static android.app.Notification.DEFAULT_SOUND;
import static android.support.v4.app.NotificationCompat.DEFAULT_VIBRATE;

/**
 * Created by shruti.vig on 8/20/18.
 */

public class AppFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "AppFirebase";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (MobiComPushReceiver.isMobiComPushNotification(remoteMessage.getData())) {
                Log.i(TAG, "Applozic notification processing...");
                MobiComPushReceiver.processMessageAsync(this, remoteMessage.getData());
                return;
            }

            String title = "BinBill Partner";
            String description = "";
            String notificationType = "0";
            String orderId = "";
            String notificationId = "";
            String orderStatus = "";
            Map<String, String> map = remoteMessage.getData();
            if (map.containsKey("title"))
                title = map.get("title");
            if (map.containsKey("description"))
                description = map.get("description");
            if (map.containsKey("big_text"))
                description = map.get("big_text");

            if (map.containsKey("notification_type"))
                notificationType = map.get("notification_type");

            if (map.containsKey("order_id"))
                orderId = map.get("order_id");

            if (map.containsKey("notification_id"))
                notificationId = map.get("notification_id");

            if (map.containsKey("status_type"))
                orderStatus = map.get("status_type");

            handleNow(title, description, notificationType, orderId, notificationId, orderStatus);
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        sendRegistrationToServer(token);

        Applozic.getInstance(this).setDeviceRegistrationId(token);
        if (MobiComUserPreference.getInstance(this).isRegistered()) {
            try {
                RegistrationResponse registrationResponse = new RegisterUserClientService(this).updatePushNotificationId(token);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleNow(String title, String message, String notificationType, String orderId, String notificationId, String orderStatus) {
        Log.d(TAG, "Short lived task is done.");
        sendNotification(title, message, notificationType, orderId, notificationId, orderStatus);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private RemoteViews createCustomView(Context context, String title, String messageBody, String notificationType, String orderId) {
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_layout);
        if (contentView != null) {
            contentView.setTextViewText(R.id.title, Html.fromHtml(title));
            contentView.setTextViewText(R.id.description, Html.fromHtml(messageBody));
            contentView.setOnClickPendingIntent(R.id.root_layout, getNotificationClickIntent(notificationType, orderId));
        }
        return contentView;
    }

    private PendingIntent getNotificationClickIntent(String notificationType, String orderId) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.NOTIFICATION_DEEPLINK, notificationType);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        if (!Utility.isEmpty(orderId))
            intent.putExtra(Constants.ORDER_ID, orderId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        return pendingIntent;
    }

    private void sendNotification(String title, String messageBody, String notificationType, String orderId, String notificationId, String orderStatus) {

        PowerManager.WakeLock screenOn = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "example");
        screenOn.acquire();

        String channelId = getString(R.string.default_notification_channel_id);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setLargeIcon(icon)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(messageBody))
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notify_order))
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                        .setCustomBigContentView(createCustomView(AppFirebaseMessagingService.this, title, messageBody, notificationType, orderId));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getString(R.string.default_notification_channel_id),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        int notifyId = 0;
        if (!Utility.isEmpty(notificationId)) {
            try {
                notifyId = Integer.parseInt(notificationId);
            } catch (Exception e) {

            }
        }

        Notification notification = notificationBuilder.build();

        if (!Utility.isEmpty(notificationType)) {
            if ((notificationType.equalsIgnoreCase("1"))) {

                if (!Utility.isEmpty(orderStatus))
                    try {
                        int status = Integer.parseInt(orderStatus);
                        if (status == Constants.STATUS_NEW_ORDER || status == Constants.STATUS_APPROVED) {
                            notification.flags = Notification.FLAG_INSISTENT | Notification.VISIBILITY_PUBLIC | Notification.FLAG_AUTO_CANCEL;
                        }
                    } catch (Exception e) {


                    }

                if (notificationType.equalsIgnoreCase("6")) {
                    notification.flags = Notification.FLAG_INSISTENT | Notification.VISIBILITY_PUBLIC | Notification.FLAG_AUTO_CANCEL;
                }
            }
        }

        notificationManager.notify(notifyId /* ID of notification */, notification);
    }
}