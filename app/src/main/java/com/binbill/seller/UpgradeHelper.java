package com.binbill.seller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.binbill.seller.Login.OTPLoginActivity;

public class UpgradeHelper {

    private static final String DIALOG_VISIBLE = "DIALOG_VISIBLE";

    public static void invokeUpdateDialog(final Activity context, final boolean forceUpdate) {
        Activity currentActivity = ((BinBillSeller) context.getApplicationContext()).getCurrentActivity();

        if (!(currentActivity instanceof SplashActivity) && !(currentActivity instanceof OTPLoginActivity)) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invokeUpdateDialog(context, forceUpdate, true);
                }
            });
        }

    }

    public static void invokeUpdateDialog(final Activity context, boolean forceUpdate, boolean onUIthread) {

        boolean isAppDialogShown = false;
        if (!forceUpdate && SharedPref.getBoolean(context, Constants.UPDATE_POPUP_NOT_NOW_CLICKED))
            isAppDialogShown = true;

        if (!isAppDialogShown) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.FullScreenDialog);
            LayoutInflater inflater = context.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_force_update, null);

            Rect displayRectangle = new Rect();
            Window window = context.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

            dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));
            dialogView.setMinimumHeight((int) (displayRectangle.height() * 0.9f));

            builder.setView(dialogView);
            builder.setCancelable(false);

            final AlertDialog dialog = builder.create();
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            TextView notNow = (TextView) dialogView.findViewById(R.id.not_now);
            Button updateButton = (Button) dialogView.findViewById(R.id.update);
            if (forceUpdate) {
                notNow.setVisibility(View.GONE);
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToPlayStore(context);
                        dialog.cancel();
                    }
                });

            } else {
                notNow.setVisibility(View.VISIBLE);
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        goToPlayStore(context);
                    }
                });

                notNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPref.putBoolean(context, Constants.UPDATE_POPUP_NOT_NOW_CLICKED, true);
                        dialog.cancel();
                    }
                });
            }

            if (!context.isFinishing())
                dialog.show();
        }
    }

    public static void goToPlayStore(Context context) {
        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
