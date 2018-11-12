package com.binbill.seller.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.binbill.seller.R;

/**
 * Created by shruti.vig on 8/10/18.
 */

public class AppButton extends AppCompatButton {

    public AppButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public AppButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AppButton(Context context) {
        super(context);
        init(context, null);
    }

    public void init(Context context, AttributeSet attrs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackground(ContextCompat.getDrawable(context, R.drawable.button_ripple_bg));
            setTransformationMethod(null);
            setElevation(0);
        } else {
            setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button));
        }
        setTextColor(ContextCompat.getColor(context, R.color.color_white));

        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.dimen_14dp));

//        if(attrs != null) {
//            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AppButton, 0, 0);
//            try {
//                float size = ta.getDimensionPixelSize(R.styleable.AppButton_sizeOfText, 20);
//                setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
//            } finally {
//                ta.recycle();
//            }
//        }
    }
}