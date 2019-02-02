package com.prosolstech.sugartrade;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.prosolstech.sugartrade.activity.BuySellDashBoardActivity;
import com.prosolstech.sugartrade.database.DataBaseConstants;
import com.prosolstech.sugartrade.database.DataBaseHelper;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    private final static String TAG = "BroadcastService";
    private CountDownTimer count;
    private Activity activity;


    @Override
    public void onCreate() {
        super.onCreate();


        Log.i(TAG, "Starting timer...");
        }

    @Override
    public void onDestroy() {

//        cdt.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        final Timer timer =new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Log.i(TAG, "onStartCommand");








            }
        },60000,60000);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
