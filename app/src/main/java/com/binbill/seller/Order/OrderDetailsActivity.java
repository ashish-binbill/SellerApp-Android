package com.binbill.seller.Order;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.Model.UserModel;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by shruti.vig on 9/6/18.
 */

@EActivity(R.layout.activity_order_details)
public class OrderDetailsActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    LinearLayout ll_user_layout;

    @ViewById
    RecyclerView rv_shopping_list;

    @ViewById
    LinearLayout shimmer_view_container;
    private Order orderDetails;

    @AfterViews
    public void setUpView() {
        setUpToolbar();

        if (getIntent() != null && getIntent().hasExtra(Constants.ORDER_ID))
            makeFetchOrderDetailsApiCall(getIntent().getStringExtra(Constants.ORDER_ID));
        else
            finish();
    }

    private void makeFetchOrderDetailsApiCall(String orderId) {

        shimmer_view_container.setVisibility(View.VISIBLE);
        rv_shopping_list.setVisibility(View.GONE);

        new RetrofitHelper(this).fetchOrderById(orderId, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.optJSONObject("result") != null) {
                            JSONObject orderJson = jsonObject.getJSONObject("result");
                            Type classType = new TypeToken<Order>() {
                            }.getType();

                            orderDetails = new Gson().fromJson(orderJson.toString(), classType);
                            handleResponse();
                        }
                    } else {
                        rv_shopping_list.setVisibility(View.GONE);
                        shimmer_view_container.setVisibility(View.GONE);
                        showSnackBar(getString(R.string.something_went_wrong));

                        finish();
                    }
                } catch (JSONException e) {
                    rv_shopping_list.setVisibility(View.GONE);
                    shimmer_view_container.setVisibility(View.GONE);

                    showSnackBar(getString(R.string.something_went_wrong));
                    finish();
                }
            }

            @Override
            public void onErrorResponse() {
                rv_shopping_list.setVisibility(View.GONE);
                shimmer_view_container.setVisibility(View.GONE);

                showSnackBar(getString(R.string.something_went_wrong));
                finish();
            }
        });
    }

    private void handleResponse() {
        if (orderDetails != null) {
            setUpData();
        } else {
            rv_shopping_list.setVisibility(View.GONE);
            shimmer_view_container.setVisibility(View.GONE);

            showSnackBar(getString(R.string.something_went_wrong));
            finish();
        }
    }

    private void setUpData() {
        setUpUserLayout();

    }

    private void setUpUserLayout() {

        if (orderDetails != null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.row_order_item, null);

            TextView mUserName = (TextView) view.findViewById(R.id.tv_name);
            final ImageView userImage = (ImageView) view.findViewById(R.id.iv_user_image);
            TextView mAddress = (TextView) view.findViewById(R.id.tv_address);
            TextView mItemCount = (TextView) view.findViewById(R.id.tv_item_count);
            TextView mDate = (TextView) view.findViewById(R.id.tv_date);
            ImageView mStatusColor = (ImageView) view.findViewById(R.id.iv_status_color);
            TextView mStatus = (TextView) view.findViewById(R.id.tv_status);

            mStatusColor.setVisibility(View.GONE);
            mStatus.setVisibility(View.GONE);
            if (orderDetails.getUser() != null) {
                UserModel userModel = orderDetails.getUser();

                if (!Utility.isEmpty(userModel.getUserName()))
                    mUserName.setText(userModel.getUserName());
                else if (!Utility.isEmpty(userModel.getUserEmail()))
                    mUserName.setText(userModel.getUserEmail());
                else
                    mUserName.setText(userModel.getUserMobile());

                //TODO
//            mAddress.setText(userModel.getad);

                if (userModel.getUserId() != null) {

                    final String authToken = SharedPref.getString(this, SharedPref.AUTH_TOKEN);
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
                    userImage.setLayoutParams(params);

                    userImage.setScaleType(ImageView.ScaleType.FIT_XY);

                    Picasso picasso = new Picasso.Builder(this)
                            .downloader(new OkHttp3Downloader(okHttpClient))
                            .build();
                    picasso.load(Constants.BASE_URL + "customer/" + userModel.getUserId() + "/images")
                            .config(Bitmap.Config.RGB_565)
                            .into(userImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Bitmap imageBitmap = ((BitmapDrawable) userImage.getDrawable()).getBitmap();
                                    RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                    imageDrawable.setCircular(true);
                                    userImage.setImageDrawable(imageDrawable);
                                }

                                @Override
                                public void onError(Exception e) {
                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.MATCH_PARENT,
                                            RelativeLayout.LayoutParams.MATCH_PARENT
                                    );

                                    int margins = Utility.convertDPtoPx(OrderDetailsActivity.this, 15);
                                    params.setMargins(margins, margins, margins, margins);
                                    userImage.setLayoutParams(params);

                                    userImage.setImageDrawable(ContextCompat.getDrawable(OrderDetailsActivity.this, R.drawable.ic_user));
                                }
                            });
                }
            }
            if (orderDetails.getOrderItems() != null)
                mItemCount.setText(orderDetails.getOrderItems().size() + "");
            else
                mItemCount.setText("0");
            mDate.setText(Utility.getFormattedDate(9, orderDetails.getOrderCreationDate(), 0));

            ll_user_layout.addView(view);
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.order_details));
    }


}
