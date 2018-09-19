package com.binbill.seller.Wallet;

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
import com.binbill.seller.Verification.VerificationAdapter;
import com.binbill.seller.Verification.VerificationModel;
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

public class WalletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class WalletHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mTitle, mUserName, mTransactionId, mDate;
        protected ImageView userImage;

        public WalletHolder(View view) {
            super(view);
            mRootCard = view;
            mUserName = (TextView) view.findViewById(R.id.tv_name);
            userImage = (ImageView) view.findViewById(R.id.iv_user_image);
            mTransactionId = (TextView) view.findViewById(R.id.tv_txn_id);
            mDate = (TextView) view.findViewById(R.id.tv_redeemed_points);
        }
    }

    private ArrayList<WalletTransaction> mList;

    public WalletAdapter(ArrayList<WalletTransaction> list) {
        this.mList = list;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_wallet_txn_item, parent, false);
        return new WalletHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final WalletHolder verificationHolder = (WalletHolder) holder;
        final WalletTransaction model = mList.get(position);

        verificationHolder.mUserName.setText(model.getUserName());
        verificationHolder.mTransactionId.setText(" " + model.getJobId());


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
    }
}
