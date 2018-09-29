package com.binbill.seller.Dashboard;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.binbill.seller.Model.CreditLoyaltyDashboardModel;
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

public class CreditPointsDashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int mType;

    public static class CreditPointsHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mValue, mName, mAddress, mTitle;
        protected ImageView userImage;

        public CreditPointsHolder(View view) {
            super(view);
            mRootCard = view;
            mValue = (TextView) view.findViewById(R.id.tv_points);
            mAddress = (TextView) view.findViewById(R.id.tv_address);
            mName = (TextView) view.findViewById(R.id.tv_name);
            userImage = (ImageView) view.findViewById(R.id.iv_user_image);
            mTitle = (TextView) view.findViewById(R.id.tv_title);
        }
    }

    private ArrayList<CreditLoyaltyDashboardModel> mList;

    public CreditPointsDashboardAdapter(int type, ArrayList<CreditLoyaltyDashboardModel> list) {
        this.mList = list;
        this.mType = type;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_credit_loyalty_dashboard_item, parent, false);
        return new CreditPointsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final CreditPointsDashboardAdapter.CreditPointsHolder creditLoyaltyHolder = (CreditPointsHolder) holder;
        final CreditLoyaltyDashboardModel model = mList.get(position);

        if (mType == 1)
            creditLoyaltyHolder.mTitle.setText("Credit Pending : ");
        else if (mType == 2)
            creditLoyaltyHolder.mTitle.setText("Loyalty Discounts : ");
        else if (mType == 3)
            creditLoyaltyHolder.mTitle.setText("Transaction Value : ");
        else
            creditLoyaltyHolder.mTitle.setText("Cashback Given : ");


        if (!Utility.isEmpty(model.getName()))
            creditLoyaltyHolder.mName.setText(model.getName());
        else if (!Utility.isEmpty(model.getEmail()))
            creditLoyaltyHolder.mName.setText(model.getEmail());
        else
            creditLoyaltyHolder.mName.setText(model.getMobile());

        if (!Utility.isEmpty(model.getAddress())) {
            creditLoyaltyHolder.mAddress.setText(model.getAddress());
            creditLoyaltyHolder.mAddress.setVisibility(View.VISIBLE);
        } else
            creditLoyaltyHolder.mAddress.setVisibility(View.GONE);

        String rupee = creditLoyaltyHolder.mValue.getContext().getString(R.string.rupee_sign);
        if (mType == 1)
            creditLoyaltyHolder.mValue.setText(" " + rupee + " " + model.getTotalCredit());
        else if (mType == 2)
            creditLoyaltyHolder.mValue.setText(" " + model.getTotalPoints());
        else if (mType == 3)
            creditLoyaltyHolder.mValue.setText(" " + rupee + " " + model.getTotalTransactions());
        else
            creditLoyaltyHolder.mValue.setText(" " + rupee + " " + model.getTotalCashback());

        if (model.getImage() != null && !Utility.isEmpty(model.getImage())) {

            final String authToken = SharedPref.getString(creditLoyaltyHolder.mAddress.getContext(), SharedPref.AUTH_TOKEN);
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
            creditLoyaltyHolder.userImage.setLayoutParams(params);

            Picasso picasso = new Picasso.Builder(creditLoyaltyHolder.mAddress.getContext())
                    .downloader(new OkHttp3Downloader(okHttpClient))
                    .build();
            picasso.load(Constants.BASE_URL + "customer/" + model.getUserId() + "/images")
                    .config(Bitmap.Config.RGB_565)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(creditLoyaltyHolder.userImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) creditLoyaltyHolder.userImage.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(creditLoyaltyHolder.userImage.getContext().getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            creditLoyaltyHolder.userImage.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }
    }
}
