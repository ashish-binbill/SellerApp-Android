package com.binbill.seller.Login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.binbill.seller.R;
import com.rd.PageIndicatorView;

public class LandingFragment extends Fragment {

    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private int page;

    public LandingFragment() {
    }

    public static LandingFragment newInstance(int pageNo) {
        LandingFragment fragment = new LandingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, pageNo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landing, container, false);

        TextView title = (TextView) view.findViewById(R.id.title);
        TextView subTitle = (TextView) view.findViewById(R.id.sub_title);
        ImageView image = (ImageView) view.findViewById(R.id.image);

        switch (page) {
            case 1:
                image.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.landing_1));

                title.setText(getString(R.string.onboarding_title_1));
                subTitle.setText(getString(R.string.onboarding_sub_title_1));
                break;

            case 2:
                image.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.landing_2));

                title.setText(getString(R.string.onboarding_title_2));
                subTitle.setText(getString(R.string.onboarding_sub_title_2));
                break;
            case 3:
                image.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.landing_3));

                title.setText(getString(R.string.onboarding_title_3));
                subTitle.setText(getString(R.string.onboarding_sub_title_3));
                break;
            case 4:
                image.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.landing_4));

                title.setText(getString(R.string.onboarding_title_4));
                subTitle.setText(getString(R.string.onboarding_sub_title_4));
                break;
        }

        return view;
    }
}
