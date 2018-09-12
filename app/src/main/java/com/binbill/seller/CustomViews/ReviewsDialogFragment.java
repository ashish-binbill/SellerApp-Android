package com.binbill.seller.CustomViews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.binbill.seller.AssistedService.AssistedUserModel;
import com.binbill.seller.R;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/9/18.
 */

public class ReviewsDialogFragment extends DialogFragment {

    public static ReviewsDialogFragment newInstance(String header, ArrayList<AssistedUserModel.Review> reviews) {
        ReviewsDialogFragment f = new ReviewsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("REVIEW_LIST", reviews);
        args.putString("HEADER", header);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.dialog_review_list, null);

        alertDialog.setContentView(view);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        alertDialog.getWindow().setBackgroundDrawable(inset);

        TextView cancelButton = (TextView) view.findViewById(R.id.header);

        if (getArguments() != null) {
            String headerText = getArguments().getString("HEADER");
            cancelButton.setText(headerText);
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ArrayList<AssistedUserModel.Review> list = (ArrayList<AssistedUserModel.Review>) getArguments().getSerializable("REVIEW_LIST");
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_reviews);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        ReviewAdapter mAdapter = new ReviewAdapter(list);
        recyclerView.setAdapter(mAdapter);

        return alertDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}

