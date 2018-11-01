package com.binbill.seller.Wallet;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.Constants;
import com.binbill.seller.R;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
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
        protected TextView mTitle, mUserName, mTransactionId, mDate, mAmount, mStatus;
        protected ImageView userImage;

        public WalletHolder(View view) {
            super(view);
            mRootCard = view;
            mUserName = (TextView) view.findViewById(R.id.tv_user_name);
            userImage = (ImageView) view.findViewById(R.id.iv_user_image);
            mTransactionId = (TextView) view.findViewById(R.id.tv_transaction_id);
            mDate = (TextView) view.findViewById(R.id.tv_date);
            mTitle = (TextView) view.findViewById(R.id.tv_title);
            mAmount = (TextView) view.findViewById(R.id.tv_amount);
            mStatus = (TextView) view.findViewById(R.id.tv_user_status);
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

        final WalletHolder walletHolder = (WalletHolder) holder;
        final WalletTransaction model = mList.get(position);

        /**
         * Credit
         */
        if (model.getTransactionType().equalsIgnoreCase("1")) {
            walletHolder.mDate.setText(Utility.getFormattedDate(10, model.getDate(), 0));
            walletHolder.mTransactionId.setText(" " + model.getId());
            walletHolder.mAmount.setText("+ " + model.getAmount());
            walletHolder.mAmount.setTextColor(ContextCompat.getColor(walletHolder.mAmount.getContext(), R.color.status_green));
            if (model.getCashbackSource().equalsIgnoreCase("1")) {
                /**
                 * Against home delivery
                 */
                walletHolder.mTitle.setText(R.string.received_from_home_delivery);
                walletHolder.mUserName.setVisibility(View.GONE);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                );

                int margins = Utility.convertDPtoPx(walletHolder.userImage.getContext(), 5);
                params.setMargins(margins, margins, margins, margins);
                walletHolder.userImage.setLayoutParams(params);

                walletHolder.userImage.setImageDrawable(ContextCompat.getDrawable(walletHolder.userImage.getContext(), R.drawable.ic_binbill));

            } else if (model.getCashbackSource().equalsIgnoreCase("2")) {
                /**
                 * By user
                 */
                walletHolder.mTitle.setText(R.string.received_from_customer);
                walletHolder.mUserName.setText(walletHolder.mUserName.getContext().getString(R.string.name_string, model.getUserName()));
                walletHolder.mUserName.setVisibility(View.VISIBLE);

                final String authToken = SharedPref.getString(walletHolder.userImage.getContext(), SharedPref.AUTH_TOKEN);
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
                walletHolder.userImage.setLayoutParams(params);

                Picasso picasso = new Picasso.Builder(walletHolder.userImage.getContext())
                        .downloader(new OkHttp3Downloader(okHttpClient))
                        .build();
                picasso.load(Constants.BASE_URL + "customer/" + model.getUserId() + "/images")
                        .config(Bitmap.Config.RGB_565)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(walletHolder.userImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) walletHolder.userImage.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(walletHolder.userImage.getContext().getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                walletHolder.userImage.setImageDrawable(imageDrawable);
                            }

                            @Override
                            public void onError(Exception e) {
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.MATCH_PARENT
                                );

                                int margins = Utility.convertDPtoPx(walletHolder.userImage.getContext(), 5);
                                params.setMargins(margins, margins, margins, margins);
                                walletHolder.userImage.setLayoutParams(params);

                                walletHolder.userImage.setImageDrawable(ContextCompat.getDrawable(walletHolder.userImage.getContext(), R.drawable.ic_user));
                            }
                        });
            } else if (model.getCashbackSource().equalsIgnoreCase("3")) {
                /**
                 * Against referral
                 */
                walletHolder.mTitle.setText(R.string.received_from_referral);
                walletHolder.mUserName.setVisibility(View.GONE);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                );

                int margins = Utility.convertDPtoPx(walletHolder.userImage.getContext(), 5);
                params.setMargins(margins, margins, margins, margins);
                walletHolder.userImage.setLayoutParams(params);

                walletHolder.userImage.setImageDrawable(ContextCompat.getDrawable(walletHolder.userImage.getContext(), R.drawable.ic_binbill));

            }else if(model.getCashbackSource().equalsIgnoreCase("4")){
                walletHolder.mTitle.setText(R.string.received_for_order);
                walletHolder.mUserName.setText(walletHolder.mUserName.getContext().getString(R.string.name_string, model.getUserName()));
                walletHolder.mUserName.setVisibility(View.VISIBLE);

                final String authToken = SharedPref.getString(walletHolder.userImage.getContext(), SharedPref.AUTH_TOKEN);
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
                walletHolder.userImage.setLayoutParams(params);

                Picasso picasso = new Picasso.Builder(walletHolder.userImage.getContext())
                        .downloader(new OkHttp3Downloader(okHttpClient))
                        .build();
                picasso.load(Constants.BASE_URL + "customer/" + model.getUserId() + "/images")
                        .config(Bitmap.Config.RGB_565)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(walletHolder.userImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) walletHolder.userImage.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(walletHolder.userImage.getContext().getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                walletHolder.userImage.setImageDrawable(imageDrawable);
                            }

                            @Override
                            public void onError(Exception e) {
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.MATCH_PARENT
                                );

                                int margins = Utility.convertDPtoPx(walletHolder.userImage.getContext(), 5);
                                params.setMargins(margins, margins, margins, margins);
                                walletHolder.userImage.setLayoutParams(params);

                                walletHolder.userImage.setImageDrawable(ContextCompat.getDrawable(walletHolder.userImage.getContext(), R.drawable.ic_user));
                            }
                        });
            }
            walletHolder.mStatus.setVisibility(View.GONE);
        } else {

            /**
             * Debit
             */
            walletHolder.mDate.setText(Utility.getFormattedDate(10, model.getDate(), 0));
            walletHolder.mTransactionId.setText(" " + model.getId());
            walletHolder.mAmount.setText("- " + model.getAmount());
            walletHolder.mAmount.setTextColor(ContextCompat.getColor(walletHolder.mAmount.getContext(), R.color.text_44));

            walletHolder.mTitle.setText(R.string.redeemed_at_paytm);
            walletHolder.mUserName.setVisibility(View.GONE);
            if (model.getStatusType().equalsIgnoreCase("13")) {
                /**
                 * pending
                 */
                walletHolder.mStatus.setText(walletHolder.mStatus.getContext().getString(R.string.pending));
                walletHolder.mStatus.setBackground(ContextCompat.getDrawable(walletHolder.mStatus.getContext(), R.drawable.tag_pending));

            } else if (model.getStatusType().equalsIgnoreCase("14")) {
                /**
                 * success
                 */
                walletHolder.mStatus.setText(walletHolder.mStatus.getContext().getString(R.string.success));
                walletHolder.mStatus.setBackground(ContextCompat.getDrawable(walletHolder.mStatus.getContext(), R.drawable.verification_tag));
            }
            walletHolder.mStatus.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );

            int margins = Utility.convertDPtoPx(walletHolder.userImage.getContext(), 5);
            params.setMargins(margins, margins, margins, margins);
            walletHolder.userImage.setLayoutParams(params);

            walletHolder.userImage.setImageDrawable(ContextCompat.getDrawable(walletHolder.userImage.getContext(), R.drawable.ic_paytm));
        }
    }
}
