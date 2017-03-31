package petproject.ridesharing;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

//import com.firebase.jobdispatcher.Constraint;
//import com.firebase.jobdispatcher.FirebaseJobDispatcher;
//import com.firebase.jobdispatcher.GooglePlayDriver;
//import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import petproject.ridesharing.models.ChatMessage;
import petproject.ridesharing.models.User;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            handleNow(remoteMessage);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]



    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow(RemoteMessage remoteMessage) {
        Log.d(TAG, "Short lived task is done.");

        Map<String, String> data = remoteMessage.getData();

        try {
            final String userID = remoteMessage.getData().get("from_user");
            final String msg = remoteMessage.getData().get("message");
            Log.d(TAG, userID + ": " + msg);
            Handler handler = new Handler(Looper.getMainLooper());

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Run your task here
                    Toast.makeText(getApplicationContext(), "From user " + userID + ":\n" + msg, Toast.LENGTH_LONG).show();
                }
            }, 1000 );

//            JSONObject datObj = new JSONObject(data);

//            String chatRoomId = datObj.getString("chat_room_id");

//            JSONObject mObj = datObj.getJSONObject("message");
            ChatMessage message = new ChatMessage();
//            message.setMessage(mObj.getString("message"));
            message.setMessage(msg);
//            message.setId(mObj.getString("message_id"));
//            message.setCreatedAt(mObj.getString("created_at"));

//            JSONObject uObj = datObj.getJSONObject("user");

            // skip the message if the message belongs to same user as
            // the user would be having the same message when he was sending
            // but it might differs in your scenario
//            if (uObj.getString("user_id").equals(MyApplication.getInstance().getPrefManager().getUser().getId())) {
//                Log.e(TAG, "Skipping the push message as it belongs to same user");
//                return;
//            }

            User user = new User();
//            user.setId(uObj.getString("user_id"));
            user.setId(userID);
//            user.setPhone(uObj.getString("phone"));
//            user.setName(uObj.getString("name"));
            message.setUser(user);

            Intent newMessage = new Intent("NewMessage");
            newMessage.putExtra("message", message);
            newMessage.putExtra("fromUser", userID);
            LocalBroadcastManager.getInstance(this).sendBroadcast(newMessage);
        }
        catch (Exception e) {
            System.out.print(e);
            e.printStackTrace();
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_menu_send)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}