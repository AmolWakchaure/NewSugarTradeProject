
package com.prosolstech.sugartrade.fcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.BuySellDashBoardActivity;
import com.prosolstech.sugartrade.activity.MyRequestedActivity;
import com.prosolstech.sugartrade.activity.NotificationActivity;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.model.ModelNotification;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.Constant;


public class FcmMessageHandler extends IntentService {

    String ns = Context.NOTIFICATION_SERVICE;
    private Handler handler;

    public FcmMessageHandler() {
        super("FcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        Log.e("ABCDEF_titleid", ": " + extras.getString("id"));
        Log.e("ABCDEF_titleflag", ": " + extras.getString("flag"));


        ModelNotification object = new ModelNotification();

        object.setTitle(extras.getString("title"));
        object.setBody(extras.getString("body"));
        DataBaseHelper db = new DataBaseHelper(this);
        DataBaseHelper.DBNotification.insert(object);
        showNotification(object, this, extras);
        FcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    private void showNotification(final ModelNotification object, final Context context, Bundle extras) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        Intent notificationIntent = null;
        if (extras.getString("flag").equalsIgnoreCase("0")) {
            notificationIntent = new Intent(context, BuySellDashBoardActivity.class);
        } else if (extras.getString("flag").equalsIgnoreCase("1")) {
            Log.e("ABCDEF_titlek", ": " + extras.getString("id"));
            ACU.MySP.saveSP(context, ACU.MySP.NOTIFY_ID, extras.getString("id"));
            notificationIntent = new Intent(context, MyRequestedActivity.class);
//            notificationIntent = new Intent(context, MyRequestedActivity.class).putExtra(Constant.INTENT_KEY_MESSAGE_ONE, extras.getString("id"));
        } else if (extras.getString("flag").equalsIgnoreCase("2")) {
            ACU.MySP.saveSP(context, ACU.MySP.NOTIFY_MY_BOOKINGS, "2");
            notificationIntent = new Intent(context, BuySellDashBoardActivity.class);
        } else if (extras.getString("flag").equalsIgnoreCase("3")) {
//            ACU.MySP.saveSP(context, ACU.MySP.NOTIFY_ID, extras.getString("id"));
            ACU.MySP.saveSP(context, ACU.MySP.NOTIFY_MY_BOOKINGS, "3");
            notificationIntent = new Intent(context, BuySellDashBoardActivity.class);
        }


        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Bitmap bitmapMila = BitmapFactory.decodeResource(getResources(), R.drawable.new_registration);
        int color = ContextCompat.getColor(context, R.color.colorAccent);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(object.getTitle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setColor(color)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(object.getTitle()))
                .setContentTitle(object.getTitle())
                .setContentText(object.getTitle())
                .setSmallIcon(R.drawable.new_registration)
                .setContentIntent(contentIntent)
                .setLargeIcon(bitmapMila);


        Notification notification = mNotifyBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        mNotificationManager.cancel(1);
        if (object.getTitle() != null) {
            mNotificationManager.notify(1, notification);
        }
    }


}