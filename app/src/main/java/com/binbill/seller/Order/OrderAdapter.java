package com.binbill.seller.Order;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.Constants;
import com.binbill.seller.Model.UserModel;
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
 * Created by shruti.vig on 9/5/18.
 */

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OrderSelectedInterface {
        void onOrderSelected(int pos);
    }

    public static class OrderHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mUserName, mAddress, mItemCount, mDate, mStatus;
        protected ImageView userImage, mStatusColor;

        public OrderHolder(View view) {
            super(view);
            mRootCard = view;
            mUserName = (TextView) view.findViewById(R.id.tv_name);
            userImage = (ImageView) view.findViewById(R.id.iv_user_image);
            mAddress = (TextView) view.findViewById(R.id.tv_address);
            mItemCount = (TextView) view.findViewById(R.id.tv_item_count);
            mDate = (TextView) view.findViewById(R.id.tv_date);
            mStatusColor = (ImageView) view.findViewById(R.id.iv_status_color);
            mStatus = (TextView) view.findViewById(R.id.tv_status);
        }
    }

    private ArrayList<Order> mList;

    public OrderAdapter(ArrayList<Order> list) {
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
                inflate(R.layout.row_order_item, parent, false);
        return new OrderHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final OrderHolder orderHolder = (OrderHolder) holder;
        final Order model = mList.get(position);

        UserModel userModel = model.getUser();

        if (userModel != null) {

            if (!Utility.isEmpty(userModel.getUserName()))
                orderHolder.mUserName.setText(userModel.getUserName());
            else if (!Utility.isEmpty(userModel.getUserEmail()))
                orderHolder.mUserName.setText(userModel.getUserEmail());
            else
                orderHolder.mUserName.setText(userModel.getUserMobile());
        }


        ArrayList<OrderItem> itemList = model.getOrderItems();
        if (itemList != null)
            orderHolder.mItemCount.setText(itemList.size() + "");
        else
            orderHolder.mItemCount.setText("0");
        orderHolder.mDate.setText(Utility.getFormattedDate(9, model.getOrderCreationDate(), 0));

        /**
         * Status
         */
        switch (model.getOrderStatus()) {
            case Constants.STATUS_NEW_ORDER:
                ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_light_blue));
                orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.new_order));
                break;
            case Constants.STATUS_COMPLETE:
                ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_green));
                orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.order_complete));
                break;
            case Constants.STATUS_APPROVED:
                ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_yellow));
                orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.in_progress));
                break;
            case Constants.STATUS_CANCEL:
                ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_red));
                orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.order_cancelled));
                break;
            case Constants.STATUS_REJECTED:
                ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_orange));
                orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.order_rejected));
                break;
            case Constants.STATUS_OUT_FOR_DELIVERY:
                ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_blue));
                orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.out_for_delivery));
                break;
        }

        if (model.getUserId() != null) {

            final String authToken = SharedPref.getString(orderHolder.userImage.getContext(), SharedPref.AUTH_TOKEN);
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
            orderHolder.userImage.setLayoutParams(params);

            orderHolder.userImage.setScaleType(ImageView.ScaleType.FIT_XY);

            Picasso picasso = new Picasso.Builder(orderHolder.userImage.getContext())
                    .downloader(new OkHttp3Downloader(okHttpClient))
                    .build();
            picasso.load(Constants.BASE_URL + "customer/" + model.getUserId() + "/images")
                    .config(Bitmap.Config.RGB_565)
                    .into(orderHolder.userImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) orderHolder.userImage.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(orderHolder.userImage.getContext().getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            orderHolder.userImage.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError(Exception e) {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT
                            );

                            int margins = Utility.convertDPtoPx(orderHolder.userImage.getContext(), 15);
                            params.setMargins(margins, margins, margins, margins);
                            orderHolder.userImage.setLayoutParams(params);

                            orderHolder.userImage.setImageDrawable(ContextCompat.getDrawable(orderHolder.userImage.getContext(), R.drawable.ic_user));
                        }
                    });
        }
    }
}


