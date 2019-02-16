package com.prosolstech.sugartrade.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;

public class ACU {

    public static class MySP {

        public static String MAIN_URL = new String("http://www.sugarcatalog.com/anakantuser/");   // this url on sugartrade server add on 30-10-18
        //public static String MAIN_URL = new String("http://192.168.0.100:8080/");   //
        //public static String MAIN_URL = new String("http://prosolstechin.ipage.com/");       // this url on ipage server commnet on 30-10-18

        public static String FCM_TOKEN = "fcm_token";
        public static String ID = "id";
        public static String ROLE = "role";
        public static String NAME = "name";
        public static String MOBILE_NO = "mobile_no";
        public static String EMAIL_ID = "email_id";
        public static String COMPANY_NAME = "company_name";
        public static String ADD_ONE = "add_one";
        public static String ADD_TWO = "add_two";
        public static String COUNTRY = "country";
        public static String STATE = "state";
        public static String DISTRICT = "district";
        public static String CITY = "city";
        public static String PINCODE = "pincode";
        public static String ACC_NAME = "acc_name";
        public static String BANK_NAME = "bank_name";
        public static String ACC_NO = "acc_no";
        public static String IFSC_CODE = "ifsc_code";
        public static String GSTIN = "gstin";
        public static String STATE_NAME = "state_name";
        public static String DISTRICT_NAME = "district_name";
        public static String LOGIN_STATUS = "login_status";
        public static String FLAG_ACCEPT = "flag_accept";
        public static String IS_FIRST_TIME_LAUNCH = "is_first_time_launch";
        public static String NOTIFY_ID = "notify_id";
        public static String NOTIFY_MY_BOOKINGS= "notify_id_my_booking";

        public static void saveSP(Context mContext, String key, String data)
        {
            final String PREFS_NAME = "SettingDetails";
            final SharedPreferences SpyAppData = mContext.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = SpyAppData.edit();
            editor.putString(key, data);
            editor.commit();
        }

        public static String getFromSP(Context mContext, String key, String dvalu)
        {
            final String PREFS_NAME = "SettingDetails";
            final SharedPreferences ToolsAppData = mContext.getSharedPreferences(PREFS_NAME, 0);
            final String preData = ToolsAppData.getString(key, dvalu).trim();
            return preData;

        }

        public static String getFromSPIs_firstTimeLaunch(Context mContext, String key, String dvalu) {
            final String PREFS_NAME = "SettingDetails";
            final SharedPreferences ToolsAppData = mContext.getSharedPreferences(
                    PREFS_NAME, 0);
            String preData = null;
            if (preData != null) {
                preData = ToolsAppData.getString(key, dvalu).trim();
            }
            return preData;

        }


        public static String getSPString(Context context, String tag, String defltValue) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getString(tag, defltValue);
        }

        public static Boolean setSPString(Context context, String tag, String value) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            try {
                sp.edit().putString(tag, value).apply();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        public static Boolean getSPBoolean(Context context, String tag, boolean defltValue) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            return sp.getBoolean(tag, defltValue);
        }

        public static Boolean setSPBoolean(Context context, String tag, boolean value) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            try {
                sp.edit().putBoolean(tag, value).apply();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

    }
}
