package com.binbill.seller;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.google.firebase.FirebaseApp;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by shruti.vig on 8/20/18.
 */

public class BinBillSeller extends Application {

    private static Socket mSocket;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        getSocket(getApplicationContext()).connect();
    }

    public static Socket getSocket(Context context) {

        if (mSocket == null) {
            IO.Options opts = new IO.Options();
            try {
                String authToken = SharedPref.getString(context, SharedPref.AUTH_TOKEN);
                opts.query = "token=" + authToken;
                mSocket = IO.socket(Constants.BASE_URL, opts);

            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        return mSocket;
    }

}
