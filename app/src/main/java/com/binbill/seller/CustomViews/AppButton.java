package com.binbill.seller.CustomViews;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.binbill.seller.R;

/**
 * Created by shruti.vig on 8/10/18.
 */

public class AppButton extends AppCompatButton {

    public AppButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public AppButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppButton(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackground(ContextCompat.getDrawable(context, R.drawable.button_ripple_bg));
            setTransformationMethod(null);
            setElevation(0);
        } else {
            setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button));
        }
        setTextColor(ContextCompat.getColor(context, R.color.color_white));
    }
}