package com.binbill.seller.Offers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.R;

/**
 * Created by shruti.vig on 8/29/18.
 */

public class AddOfferSelectionDialog extends DialogFragment {
    private OfferPopUpClickListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.dialog_offer_type_selection, null);

        alertDialog.setContentView(view);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        alertDialog.getWindow().setBackgroundDrawable(inset);

        TextView cancelButton = (TextView) view.findViewById(R.id.header);
        LinearLayout createImageOffer = (LinearLayout) view.findViewById(R.id.ll_create_image);
        LinearLayout createVideoOffer = (LinearLayout) view.findViewById(R.id.ll_create_video);
        LinearLayout createCouponOffer = (LinearLayout) view.findViewById(R.id.ll_create_coupon);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        createImageOffer.setOnClickListener(offerTypeListener);
        createVideoOffer.setOnClickListener(offerTypeListener);
        createCouponOffer.setOnClickListener(offerTypeListener);

        return alertDialog;
    }

    View.OnClickListener offerTypeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_create_image:
                    listener.onOptionSelected("IMAGE");
                    break;
                case R.id.ll_create_video:
                    listener.onOptionSelected("VIDEO");
                    break;
                case R.id.ll_create_coupon:
                    listener.onOptionSelected("COUPON");
                    break;

            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OfferPopUpClickListener) {
            listener = (OfferPopUpClickListener) context;
        } else {
            throw new IllegalStateException();
        }
    }

    public interface OfferPopUpClickListener {
        void onOptionSelected(String type);
    }
}
