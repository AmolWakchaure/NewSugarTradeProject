package com.prosolstech.sugartrade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.activity.LoginActivity;
import com.prosolstech.sugartrade.activity.BuySellDashBoardActivity;

public class SplashActivity extends Activity {
    Context context;
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = SplashActivity.this;
        splashTime();

    }

    public void splashTime() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ACU.MySP.getSPBoolean(context, ACU.MySP.LOGIN_STATUS, false)) {
                    finish();
                    Intent initial_intent = new Intent(context, BuySellDashBoardActivity.class);
                    ACU.MySP.saveSP(context, ACU.MySP.NOTIFY_MY_BOOKINGS, "");
                    startActivity(initial_intent);
                } else {
                    finish();
                    Intent initial_intent = new Intent(context, LoginActivity.class);
                    startActivity(initial_intent);
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
