package com.binbill.seller.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.binbill.seller.R;

/**
 * Created by shruti.vig on 8/14/18.
 */

public class PrefixEditText extends AppCompatEditText {

    private String mPrefix; // can be hardcoded for demo purposes
    private Rect mPrefixRect = new Rect(); // actual prefix size

    public PrefixEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        final TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.PrefixEditText);
        String preFixVal = attrArray.getString(R.styleable.PrefixEditText_prefix_string);
        if (preFixVal != null) {
            mPrefix = preFixVal;
        } else {
            mPrefix = "+91";
        }
        attrArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getPaint().getTextBounds(mPrefix, 0, mPrefix.length(), mPrefixRect);
        mPrefixRect.right += getPaint().measureText("   "); // add some offset

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(mPrefix, super.getCompoundPaddingLeft(), getBaseline(), getPaint());
    }

    @Override
    public int getCompoundPaddingLeft() {
        return super.getCompoundPaddingLeft() + mPrefixRect.width();
    }
}

