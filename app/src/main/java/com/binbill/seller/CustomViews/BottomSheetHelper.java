package com.binbill.seller.CustomViews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.R;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 8/30/18.
 */

public class BottomSheetHelper {

    public interface BottomSheetClickInterface {
        void onRowClicked(int position);
    }

    public static void showBottomSheet(Context context, ArrayList<String> items, ArrayList<Drawable> icons, final BottomSheetClickInterface listener) {
        final View parentView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_icon_text, null, false);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(parentView);

        TextView header = (TextView) parentView.findViewById(R.id.header);
        header.setText(R.string.upload_from);

        LinearLayout containerLayout = (LinearLayout) parentView.findViewById(R.id.container);

        for (int i = 0; i < items.size(); i++) {

            String label = items.get(i);
            Drawable drawable = icons.get(i);

            LayoutInflater inflate = LayoutInflater.from(context);
            View inflatedLayout = inflate.inflate(R.layout.dynamic_text_view, null, false);
            TextView nameView = (TextView) inflatedLayout.findViewById(R.id.text);
            ImageView imageView = (ImageView) inflatedLayout.findViewById(R.id.icon);
            imageView.setImageDrawable(drawable);
            nameView.setText(label);

            final int position = i;
            nameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (listener != null)
                        listener.onRowClicked(position);
                    BottomSheetBehavior.from((View) parentView.getParent()).setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            });
            containerLayout.addView(inflatedLayout);
        }

        bottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        bottomSheetDialog.show();
    }
}
