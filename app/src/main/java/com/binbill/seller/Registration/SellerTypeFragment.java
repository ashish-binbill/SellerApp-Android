package com.binbill.seller.Registration;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.binbill.seller.AppSession;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;

public class SellerTypeFragment extends Fragment {

    private static final int SELLER_TYPE = 0;
    private static final int SELLER_POS_AVAILABILITY = 1;

    private int flowType = SELLER_TYPE;
    private SellerTypeInterface mListener;

    public interface SellerTypeInterface {
        void onNext(int stage);
    }

    public SellerTypeFragment() {
    }

    public static SellerTypeFragment newInstance(int flow) {
        SellerTypeFragment fragment = new SellerTypeFragment();
        Bundle args = new Bundle();
        args.putInt("FLOW", flow);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getArguments() != null) {
            flowType = getArguments().getInt("FLOW");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup sellerTypes = view.findViewById(R.id.radio_group_seller);
        LinearLayout posAvailability = view.findViewById(R.id.ll_pos);

        AppButton btnNext = (AppButton) view.findViewById(R.id.btn_next);

        switch (flowType) {
            case SELLER_TYPE:
                sellerTypes.setVisibility(View.VISIBLE);
                posAvailability.setVisibility(View.GONE);

                final RadioGroup radioGroupSeller = (RadioGroup) view.findViewById(R.id.radio_group_seller);

                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int radioButtonID = radioGroupSeller.getCheckedRadioButtonId();

                        UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(getActivity()).getUserRegistrationDetails();

                        switch (radioButtonID) {
                            case R.id.rb_fmcg:
                                userRegistrationDetails.setFmcg(true);
                                userRegistrationDetails.setAssisted(false);
                                break;
                            case R.id.rb_fmcg_assisted:
                                userRegistrationDetails.setFmcg(true);
                                userRegistrationDetails.setAssisted(true);
                                break;
                            case R.id.rb_assisted:
                                userRegistrationDetails.setFmcg(false);
                                userRegistrationDetails.setAssisted(true);
                                break;
                        }

                        AppSession.getInstance(getActivity()).setUserRegistrationDetails(userRegistrationDetails);

                        mListener.onNext(flowType);
                    }
                });

                break;

            case SELLER_POS_AVAILABILITY:
                sellerTypes.setVisibility(View.GONE);
                posAvailability.setVisibility(View.VISIBLE);

                final RadioGroup radioGroupPos = (RadioGroup) view.findViewById(R.id.radio_group_pos);
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int radioButtonID = radioGroupPos.getCheckedRadioButtonId();

                        UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(getActivity()).getUserRegistrationDetails();

                        switch (radioButtonID) {
                            case R.id.rb_pos_yes:
                                userRegistrationDetails.setHasPos(true);
                                break;
                            case R.id.rb_pos_no:
                                userRegistrationDetails.setHasPos(false);
                                break;
                        }

                        AppSession.getInstance(getActivity()).setUserRegistrationDetails(userRegistrationDetails);

                        mListener.onNext(flowType);
                    }
                });

                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SellerTypeInterface)
            mListener = (SellerTypeInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
