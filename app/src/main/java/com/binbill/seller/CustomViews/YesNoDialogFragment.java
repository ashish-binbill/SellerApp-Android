package com.binbill.seller.CustomViews;

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
import android.widget.TextView;

import com.binbill.seller.Offers.AddOfferSelectionDialog;
import com.binbill.seller.R;

/**
 * Created by shruti.vig on 8/30/18.
 */

public class YesNoDialogFragment extends DialogFragment {
    private YesNoClickInterface listener;

    public static YesNoDialogFragment newInstance(String title, String header) {
        YesNoDialogFragment f = new YesNoDialogFragment();
        Bundle args = new Bundle();
        args.putString("TITLE", title);
        args.putString("HEADER", header);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.dialog_yes_no, null);

        alertDialog.setContentView(view);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        alertDialog.getWindow().setBackgroundDrawable(inset);

        TextView cancelButton = (TextView) view.findViewById(R.id.header);
        TextView title = (TextView) view.findViewById(R.id.title);
        AppButton yesButton = (AppButton) view.findViewById(R.id.btn_yes);
        AppButtonGreyed noButton = (AppButtonGreyed) view.findViewById(R.id.btn_no);


        if (getArguments() != null) {
            String headerText = getArguments().getString("HEADER");
            String titleText = getArguments().getString("TITLE");

            cancelButton.setText(headerText);
            title.setText(titleText);
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onOptionSelected(true);
                dismiss();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onOptionSelected(false);

                dismiss();
            }
        });

        return alertDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof YesNoClickInterface) {
            listener = (YesNoClickInterface) context;
        } else {
            throw new IllegalStateException();
        }
    }

    public interface YesNoClickInterface {
        void onOptionSelected(boolean isProceed);
    }
}
