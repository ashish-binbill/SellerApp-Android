package com.binbill.seller.Firebase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.Date;

public class FirebaseAnalytics {

    private static FirebaseAnalytics analytics;
    private com.google.firebase.analytics.FirebaseAnalytics firebaseAnalytics;

    private FirebaseAnalytics(Context context) {
        firebaseAnalytics = com.google.firebase.analytics.FirebaseAnalytics.getInstance(context);
    }

    public static FirebaseAnalytics with(Context context) {
        if (analytics == null) {
            analytics = new FirebaseAnalytics(context.getApplicationContext());
        }
        return analytics;
    }

    public static FirebaseAnalytics get() {
        if (analytics == null)
            throw new UnsupportedOperationException("Unable to get instance of Analytics - Did you missed to call Analytics.with(context) in you application class or before using this method.");
        return analytics;
    }

    public static FirebaseAnalytics get(Context context) {
        if (analytics == null) {
            analytics = new FirebaseAnalytics(context.getApplicationContext());
        }
        return analytics;
    }

    /**
     * Send event in Firebase analytics
     *
     * @param event Name of event
     * @param param Extra param for detailed event
     */
    public void event(@NonNull String event, Bundle param) {
        if (param == null) {
            param = new Bundle();
        }
        param.putString("TIME", new Date().toString());
        firebaseAnalytics.logEvent(event, param);
    }
}
