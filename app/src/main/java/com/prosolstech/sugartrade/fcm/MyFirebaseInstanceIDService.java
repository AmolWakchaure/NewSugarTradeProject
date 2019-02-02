package com.prosolstech.sugartrade.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.prosolstech.sugartrade.util.ACU;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {

        //For registration of token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("TOKEN: ", refreshedToken);
        ACU.MySP.saveSP(getApplicationContext(), ACU.MySP.FCM_TOKEN, refreshedToken);
        //To displaying token on logcat

    }





}