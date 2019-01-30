package com.prosolstech.sugartrade.classes;

import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

public class T
{
    public static void t(String message)
    {
        Toast.makeText(MyApplication.context,message,Toast.LENGTH_LONG).show();
    }
    public static void e(String message)
    {
        Log.e("SUGAR_TRADE_LOG",message);
    }

    public static String returnTimeFormat(int seconds)
    {
        //long Seconds = seconds / 1000;
        long Minutes = seconds / 60;
        Log.e("Minutes", " : " + Minutes);
        long hours = Minutes / 60; //since both are ints, you get an int
        long minutes = Minutes % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }

    public static String formatTimeStamp(String inputTimeStamp)
    {

        String [] data = inputTimeStamp.split(" ");
        String [] dateData = data[0].split("-");

        String formatedTimeStamp = dateData[2]+"-"+dateData[1]+"-"+dateData[0]+" "+data[1];
        return formatedTimeStamp;
    }

}
