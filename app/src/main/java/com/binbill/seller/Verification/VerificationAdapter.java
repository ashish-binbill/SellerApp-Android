package com.binbill.seller.Verification;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.AppButtonGreyed;
import com.binbill.seller.R;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by shruti.vig on 9/4/18.
 */

public class VerificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface VerificationCardListener {
        void onLinkCredits(int pos);

        void onLinkPoints(int pos);

        void onViewBill(int pos);

        void onAccept(int pos);

        void onReject(int pos);
    }

    public static class VerificationHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mUserName, mJobStatus, mTransactionId, mItemCount, mHomeDelivery,
                mAmount, mBBCash, mLinkCredits, mLinkPoints, mViewBill, mTotalCredits, mTotalPoints, mRedeemedCredits, mRedeemedPoints;
        protected ImageView userImage;
        protected AppButton approve;
        protected AppButtonGreyed reject;
        protected LinearLayout approveProgress, totalCredits, redeemedCredits, totalPoints, redeemedPoints;

        public VerificationHolder(View view) {
            super(view);
            mRootCard = view;
            mUserName = (TextView) view.findViewById(R.id.tv_name);
            userImage = (ImageView) view.findViewById(R.id.iv_user_image);
            mJobStatus = (TextView) view.findViewById(R.id.tv_status);
            mTransactionId = (TextView) view.findViewById(R.id.tv_txn_id);
            mItemCount = (TextView) view.findViewById(R.id.tv_item_count);
            mHomeDelivery = (TextView) view.findViewById(R.id.tv_home_delivery);
            mAmount = (TextView) view.findViewById(R.id.tv_amount);
            mBBCash = (TextView) view.findViewById(R.id.tv_bb_cash);
            mLinkCredits = (TextView) view.findViewById(R.id.tv_link_credits);
            mLinkPoints = (TextView) view.findViewById(R.id.tv_link_points);
            mViewBill = (TextView) view.findViewById(R.id.tv_view_bill);
            approve = (AppButton) view.findViewById(R.id.btn_yes);
            reject = (AppButtonGreyed) view.findViewById(R.id.btn_no);
            approveProgress = (LinearLayout) view.findViewById(R.id.btn_yes_progress);
            totalCredits = (LinearLayout) view.findViewById(R.id.ll_total_credits);
            redeemedCredits = (LinearLayout) view.findViewById(R.id.ll_redeemed_credits);
            totalPoints = (LinearLayout) view.findViewById(R.id.ll_total_points);
            redeemedPoints = (LinearLayout) view.findViewById(R.id.ll_redeemed_points);
            mTotalCredits = (TextView) view.findViewById(R.id.tv_total_credits);
            mRedeemedCredits = (TextView) view.findViewById(R.id.tv_redeemed_credits);
            mTotalPoints = (TextView) view.findViewById(R.id.tv_total_points);
            mRedeemedPoints = (TextView) view.findViewById(R.id.tv_redeemed_points);
        }
    }

    private ArrayList<VerificationModel> mList;
    private VerificationCardListener mListener;

    public VerificationAdapter(ArrayList<VerificationModel> list, VerificationCardListener interfaceObject) {
        this.mList = list;
        this.mListener = interfaceObject;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_verification_item, parent, false);
        return new VerificationHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final VerificationHolder verificationHolder = (VerificationHolder) holder;
        final VerificationModel model = mList.get(position);

        verificationHolder.mUserName.setText(model.getUserName());
        verificationHolder.mTransactionId.setText(" " + model.getJobId());
        verificationHolder.mItemCount.setText(" " + model.getItemCount());
        String amountText = " " + verificationHolder.mAmount.getContext().getString(R.string.rupee_sign) + model.getAmount();
        verificationHolder.mAmount.setText(amountText);

        String homeDelivery = "No";
        if (model.isHomeDelivered())
            homeDelivery = "Yes";

        verificationHolder.mHomeDelivery.setText(" " + homeDelivery);
        verificationHolder.mBBCash.setText(" " + model.getCashback());

        SpannableString content = new SpannableString(verificationHolder.mViewBill.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        verificationHolder.mViewBill.setText(content);

        if (Utility.isEmpty(model.getTotalCredits()) || Utility.isEmpty(model.getRedeemedCredits())) {
            verificationHolder.mLinkCredits.setVisibility(View.VISIBLE);
            SpannableString linkCredits = new SpannableString(verificationHolder.mLinkCredits.getText().toString());
            linkCredits.setSpan(new UnderlineSpan(), 0, linkCredits.length(), 0);
            verificationHolder.mLinkCredits.setText(linkCredits);
        } else
            verificationHolder.mLinkCredits.setVisibility(View.GONE);

        if (Utility.isEmpty(model.getTotalLoyalty()) || Utility.isEmpty(model.getRedeemedLoyalty())) {
            SpannableString linkPoints = new SpannableString(verificationHolder.mLinkPoints.getText().toString());
            linkPoints.setSpan(new UnderlineSpan(), 0, linkPoints.length(), 0);
            verificationHolder.mLinkPoints.setText(linkPoints);
        } else
            verificationHolder.mLinkPoints.setVisibility(View.GONE);


        if (!Utility.isEmpty(model.getTotalCredits())) {
            verificationHolder.totalCredits.setVisibility(View.VISIBLE);
            verificationHolder.mTotalCredits.setText(" " + model.getTotalCredits());
        } else
            verificationHolder.totalCredits.setVisibility(View.GONE);

        if (!Utility.isEmpty(model.getRedeemedCredits())) {
            verificationHolder.redeemedCredits.setVisibility(View.VISIBLE);
            verificationHolder.mRedeemedCredits.setText(" " + model.getRedeemedCredits());
        } else
            verificationHolder.redeemedCredits.setVisibility(View.GONE);

        if (!Utility.isEmpty(model.getTotalLoyalty())) {
            verificationHolder.totalPoints.setVisibility(View.VISIBLE);
            verificationHolder.mTotalPoints.setText(" " + model.getTotalLoyalty());
        } else
            verificationHolder.mTotalPoints.setVisibility(View.GONE);

        if (!Utility.isEmpty(model.getRedeemedLoyalty())) {
            verificationHolder.redeemedPoints.setVisibility(View.VISIBLE);
            verificationHolder.mRedeemedPoints.setText(" " + model.getRedeemedLoyalty());
        } else
            verificationHolder.redeemedPoints.setVisibility(View.GONE);


        verificationHolder.mJobStatus.setText(model.getCashbackStatus());
        verificationHolder.mJobStatus.setVisibility(View.GONE);

        final String authToken = SharedPref.getString(verificationHolder.userImage.getContext(), SharedPref.AUTH_TOKEN);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        return response.request().newBuilder()
                                .header("Authorization", authToken)
                                .build();
                    }
                }).build();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(0, 0, 0, 0);
        verificationHolder.userImage.setLayoutParams(params);

        verificationHolder.userImage.setScaleType(ImageView.ScaleType.FIT_XY);

        Picasso picasso = new Picasso.Builder(verificationHolder.userImage.getContext())
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
        picasso.load(Constants.BASE_URL + "customer/" + model.getUserId() + "/images")
                .config(Bitmap.Config.RGB_565)
                .into(verificationHolder.userImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) verificationHolder.userImage.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(verificationHolder.userImage.getContext().getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        verificationHolder.userImage.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError(Exception e) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT
                        );

                        int margins = Utility.convertDPtoPx(verificationHolder.userImage.getContext(), 15);
                        params.setMargins(margins, margins, margins, margins);
                        verificationHolder.userImage.setLayoutParams(params);

                        verificationHolder.userImage.setImageDrawable(ContextCompat.getDrawable(verificationHolder.userImage.getContext(), R.drawable.ic_user));
                    }
                });

        verificationHolder.approveProgress.setVisibility(View.GONE);
        verificationHolder.approve.setVisibility(View.VISIBLE);

        verificationHolder.mLinkPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onLinkPoints(position);
            }
        });

        verificationHolder.mLinkCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onLinkCredits(position);
            }
        });

        verificationHolder.mViewBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onViewBill(position);
            }
        });

        verificationHolder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    verificationHolder.approve.setVisibility(View.GONE);
                    verificationHolder.approveProgress.setVisibility(View.VISIBLE);
                    mListener.onAccept(position);
                }
            }
        });

        verificationHolder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onReject(position);
            }
        });
    }
}


