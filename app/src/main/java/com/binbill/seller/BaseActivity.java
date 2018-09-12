package com.binbill.seller;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;

import org.aviran.cookiebar2.CookieBar;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showSnackBar(String message) {
//        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
//        snackbar.show();

        CookieBar.build(BaseActivity.this)
                .setTitle("")
                .setLayoutGravity(Gravity.BOTTOM)
                .setMessage(message)
                .show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CookieBar.dismiss(BaseActivity.this);
            }
        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BinBillSeller.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BinBillSeller.activityPaused();
    }
}



