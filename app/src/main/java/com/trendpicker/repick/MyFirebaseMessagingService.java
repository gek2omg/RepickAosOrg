package com.trendpicker.repick;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.trendpicker.repick.Props.fcm_data_click_action;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData() + " size: " + remoteMessage.getData().size());
            Map<String, String> map= remoteMessage.getData();

            sendNotification(map.get("title"), map.get("message"), map.get(fcm_data_click_action));
        }

        if (remoteMessage.getNotification() != null){
            RemoteMessage.Notification noti = remoteMessage.getNotification();

            Log.d(TAG, "notification Message body : " + noti.getBody());
            sendNotification(noti.getTitle(), noti.getBody(), noti.getClickAction());

        }

    }


    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, String action) {
        Log.d("MessageService", "sendNotification called..................");

        Intent intent= null;
        intent = new Intent(this, MainActivity.class);

        if ((action != null && !action.isEmpty()) ){
            intent.putExtra(fcm_data_click_action, action);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE); // FLAG_ONE_SHOT

        String channelId = "001";
        // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fcm);
        AudioAttributes soundAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build();

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setVibrate(new long[0])
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSound(soundUri)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder.setFullScreenIntent(pendingIntent,true);

        // Since android Oreo notification channel is needed.
        Log.d("123",""+ Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            if (notificationManager.getNotificationChannel(channelId) == null) {
                String channelName_marketing = "REPICK 알림";
                NotificationChannel channel_marketing = new NotificationChannel(channelId, channelName_marketing, NotificationManager.IMPORTANCE_HIGH);
//
//                //--- setup start for push sound -------------------------//
                channel_marketing.setSound(soundUri, soundAttributes);
//                //--- setup end   for push sound -------------------------//
//
                notificationManager.createNotificationChannel(channel_marketing);
            }

        }
        Notification notification = notificationBuilder.build();

        notificationManager.notify(0 /* ID of notification */, notification);

//        playSound();
    }

//    private void playSound() {
//        try {
////            AudioAttributes soundAttributes = new AudioAttributes.Builder()
////                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
////                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
////                    .build();
//
//            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.sample_6s);
//            MediaPlayer mMediaPlayer = MediaPlayer.create(getApplicationContext(), soundUri);
//
//            if( mMediaPlayer == null ) {
//                throw new Exception( "Can't create player" );
//            }
//
//            // STREAM_VOICE_CALL, STREAM_SYSTEM, STREAM_RING, STREAM_MUSIC, STREAM_ALARM
//            // STREAM_NOTIFICATION, STREAM_DTMF
//            //mMediaPlayer.setAudioStreamType( AudioManager.STREAM_ALARM );
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            // mMediaPlayer.setAudioAttributes(soundAttributes);
//            mMediaPlayer.start();
//
//        } catch( Exception e ) {
//            Toast.makeText( this, e.getMessage(), Toast.LENGTH_SHORT ).show();
//            Log.e(TAG, e.getMessage() );
//            e.printStackTrace();
//        }
//    }
}