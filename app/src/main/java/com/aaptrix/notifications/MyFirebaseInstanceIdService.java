package com.aaptrix.notifications;

/**
 * Created by Administrator on 2/9/2018.
 */

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.messaging.FirebaseMessagingService;

import com.aaptrix.R;


public class MyFirebaseInstanceIdService extends FirebaseMessagingService
{

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(getString(R.string.FCM_TOKEN),token);
        editor.apply();
    }
}
