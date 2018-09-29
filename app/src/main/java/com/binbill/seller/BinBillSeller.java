package com.binbill.seller;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;

import io.fabric.sdk.android.Fabric;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by shruti.vig on 8/20/18.
 */

public class BinBillSeller extends MultiDexApplication {

    private static Socket mSocket;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        FirebaseApp.initializeApp(this);
        Fabric.with(this, new Crashlytics());
    }

    public static Socket getSocket(Context context) {

        if (mSocket == null) {
            IO.Options opts = new IO.Options();
            try {
                String authToken = SharedPref.getString(context, SharedPref.AUTH_TOKEN);
                opts.query = "token=" + authToken;
                mSocket = IO.socket(Constants.BASE_URL, opts);
                mSocket.connect();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        return mSocket;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;

}
