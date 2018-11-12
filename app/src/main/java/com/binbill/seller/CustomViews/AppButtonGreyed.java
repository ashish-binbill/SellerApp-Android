package com.binbill.seller.CustomViews;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.binbill.seller.R;

/**
 * Created by shruti.vig on 8/30/18.
 */

public class AppButtonGreyed extends AppCompatButton {

    public AppButtonGreyed(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public AppButtonGreyed(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppButtonGreyed(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackground(ContextCompat.getDrawable(context, R.drawable.button_ripple_bg_grey));
            setTransformationMethod(null);
            setElevation(0);
        } else {
            setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_grey));
        }
        setTextColor(ContextCompat.getColor(context, R.color.color_white));

        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.dimen_14dp));
    }
}
