package com.prosolstech.sugartrade.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.prosolstech.sugartrade.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VU {
    public final static String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public final static String YMD = "yyyy-MM-dd";
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+";
    public static Pattern pattern;
    static Matcher matcher;

    public static boolean isEmpty(EditText editText) {
        // TODO method to check edit text is fill or no
        // return true when edit text is empty
        if (editText.getText().toString().trim().equals("")) {
            return true;
        }
        return false;
    }
    public static boolean isEmpty(Button editText) {
        // TODO method to check edit text is fill or no
        // return true when edit text is empty
        if (editText.getText().toString().trim().equals("")) {
            return true;
        }
        return false;
    }


    public static boolean isEmailId(EditText editText) {
        // method to check edit text is fill or no
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(editText.getText().toString().trim());
        if (matcher.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isConnectingToInternet(Context appContext) {
        // Method to check internet connection
        ConnectivityManager conMgr = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            // Toast.makeText(appContext, "No internet connection.", Toast.LENGTH_SHORT).show();
            showCustomDialog(appContext);
            return false;
        }
    }

    public static void showCustomDialog(final Context appContext) {
        final Dialog dialog = new Dialog(appContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                appContext.startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    public static String getddmmyyDate(String dt) {
        String dd = "", mm = "", yy = "";
        int i = 0;
        try {
            for (String retval : dt.split("-")) {
                if (i == 0)
                    yy = retval;
                else if (i == 1)
                    mm = retval;
                else
                    dd = retval;
                i++;
            }
            return (dd + "-" + mm + "-" + yy).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String getddmmyyDatezz(String dt) {
        String dd = "", mm = "", yy = "";
        int i = 0;
        try {
            for (String retval : dt.split("-")) {
                if (i == 0)
                    yy = retval;
                else if (i == 1)
                    mm = retval;
                else
                    dd = retval;
                i++;
            }
            return (yy + "-" + mm + "-" + dd).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String formatDate(String dateInput)
    {

        String dateData = null;


            String [] data = dateInput.split("-");

            if(data[0].length() == 4)
            {
                dateData = getddmmyyDatezz(dateInput);
            }
            else
            {
                dateData = getddmmyyDate(dateInput);
            }




        // String sddf =  String.format("%1$tY %1$tb %1$td", date);
        //String sddf =  String.format("%1$td %1$tb %1$tY", date);

        return dateData;
    }

    public static String getCurrentDateTimeStamp(String format) {

        DateFormat dateFormatter = new SimpleDateFormat(format);
        dateFormatter.setLenient(false);
        Date today = new Date();
        String s = dateFormatter.format(today);
        return s;
    }


}
