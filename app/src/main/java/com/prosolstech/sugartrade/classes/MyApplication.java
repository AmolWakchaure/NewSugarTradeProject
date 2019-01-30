package com.prosolstech.sugartrade.classes;

import android.app.Application;
import android.content.Context;

import java.io.File;

public class MyApplication extends Application {

    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }




}
