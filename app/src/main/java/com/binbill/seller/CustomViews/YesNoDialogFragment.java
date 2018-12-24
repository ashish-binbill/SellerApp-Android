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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.R;
import com.binbill.seller.Utility;

/**
 * Created by shruti.vig on 8/30/18.
 */

public class YesNoDialogFragment extends DialogFragment {
    private YesNoClickInterface listener;
    private boolean isOrderPrice;

    public static YesNoDialogFragment newInstance(String title, String header) {
        YesNoDialogFragment f = new YesNoDialogFragment();
        Bundle args = new Bundle();
        args.putString("TITLE", title);
        args.putString("HEADER", header);
        args.putBoolean("IS_ALERT", false);
        f.setArguments(args);

        return f;
    }

    public static YesNoDialogFragment newInstance(boolean isAlert, String yesButton, String noButton, String message) {
        YesNoDialogFragment f = new YesNoDialogFragment();
        Bundle args = new Bundle();
        args.putString("YES_BUTTON", yesButton);
        args.putString("NO_BUTTON", noButton);
        args.putString("HEADER", "");
        args.putString("TITLE", message);
        args.putBoolean("IS_ALERT", isAlert);
        f.setArguments(args);

        return f;
    }

    public static YesNoDialogFragment newInstance(boolean isAlert, String yesButton, String noButton, String message, boolean isApproval) {
        YesNoDialogFragment f = new YesNoDialogFragment();
        Bundle args = new Bundle();
        args.putString("YES_BUTTON", yesButton);
        args.putString("NO_BUTTON", noButton);
        args.putString("HEADER", "");
        args.putString("TITLE", message);
        args.putBoolean("IS_ALERT", isAlert);
        args.putBoolean("IS_ORDER_PRICE_DIALOG", true);
        args.putBoolean("IS_APPROVAL", isApproval);
        f.setArguments(args);

        return f;
    }

    public static YesNoDialogFragment newInstance(boolean isAlert, String yesButton, String message) {
        YesNoDialogFragment f = new YesNoDialogFragment();
        Bundle args = new Bundle();
        args.putString("YES_BUTTON", yesButton);
        args.putString("NO_BUTTON", "");
        args.putString("HEADER", "");
        args.putString("TITLE", message);
        args.putBoolean("IS_ALERT", isAlert);
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
        final AppButtonGreyed noButton = (AppButtonGreyed) view.findViewById(R.id.btn_no);
        LinearLayout headerLayout = (LinearLayout) view.findViewById(R.id.ll_header);


        if (getArguments() != null) {
            String headerText = getArguments().getString("HEADER");
            String titleText = getArguments().getString("TITLE");

            cancelButton.setText(headerText);
            title.setText(titleText);

            boolean isAlert = getArguments().getBoolean("IS_ALERT");
            if (isAlert) {
                String yesButtonText = getArguments().getString("YES_BUTTON");
                String noButtonText = getArguments().getString("NO_BUTTON");

                yesButton.setText(yesButtonText);

                if (!Utility.isEmpty(noButtonText))
                    noButton.setText(noButtonText);
                else
                    noButton.setVisibility(View.GONE);

                cancelButton.setVisibility(View.GONE);
                headerLayout.setVisibility(View.GONE);

                isOrderPrice = getArguments().getBoolean("IS_ORDER_PRICE_DIALOG");
                if (isOrderPrice) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.image);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
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

                if (isOrderPrice) {
                    if (listener != null)
                        listener.onProceedOrder(getArguments().getBoolean("IS_APPROVAL"), true);
                } else {
                    if (listener != null) {
                        if (noButton.getVisibility() == View.VISIBLE)
                            listener.onOptionSelected(true);
                        else
                            listener.onOptionSelected(false);
                    }
                }
                dismiss();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOrderPrice) {
                    if (listener != null)
                        listener.onProceedOrder(getArguments().getBoolean("IS_APPROVAL"), false);
                } else {
                    if (listener != null)
                        listener.onOptionSelected(false);
                }

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

        void onProceedOrder(boolean isApproval, boolean isProceed);
    }
}
