package com.binbill.seller;

import android.app.Application;
import android.os.StrictMode;

import com.google.firebase.FirebaseApp;

/**
 * Created by shruti.vig on 8/20/18.
 */

public class BinBillSeller extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}
