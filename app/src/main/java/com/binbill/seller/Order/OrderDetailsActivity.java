package com.binbill.seller.Order;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.AppButtonGreyed;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by shruti.vig on 9/6/18.
 */

@EActivity(R.layout.activity_order_details)
public class OrderDetailsActivity extends BaseActivity implements OrderShoppingListAdapter.OrderItemSelectedInterface, OrderSKUAdapter.OrderSKUInterface {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    TextView tv_address, tv_date, tv_name;
    @ViewById
    ImageView iv_user_image;

    @ViewById
    RecyclerView rv_shopping_list;

    @ViewById
    LinearLayout shimmer_view_container;
    private Order orderDetails;

    @ViewById
    RelativeLayout just_sec_layout;

    @ViewById
    AppButton btn_accept;

    @ViewById
    AppButtonGreyed btn_decline;

    String orderId;
    private OrderShoppingListAdapter mAdapter;
    private AlertDialog mSKUDialog;

    @AfterViews
    public void setUpView() {
        setUpToolbar();

        if (getIntent() != null && getIntent().hasExtra(Constants.ORDER_ID)) {
            orderId = getIntent().getStringExtra(Constants.ORDER_ID);
        } else
            finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (orderId != null)
            makeFetchOrderDetailsApiCall(orderId);
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

                            rv_shopping_list.setVisibility(View.VISIBLE);
                            shimmer_view_container.setVisibility(View.GONE);
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
//            initialiseUpdatedDataArray();
        } else {
            rv_shopping_list.setVisibility(View.GONE);
            shimmer_view_container.setVisibility(View.GONE);

            showSnackBar(getString(R.string.something_went_wrong));
            finish();
        }
    }

    private void setUpData() {
        setUpUserLayout();

        rv_shopping_list.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_shopping_list.setLayoutManager(llm);
        mAdapter = new OrderShoppingListAdapter(orderDetails.getOrderItems(), this);
        rv_shopping_list.setAdapter(mAdapter);

    }

    private void setUpUserLayout() {

        if (orderDetails != null) {
            if (orderDetails.getUser() != null) {
                UserModel userModel = orderDetails.getUser();

                if (!Utility.isEmpty(userModel.getUserName()))
                    tv_name.setText(userModel.getUserName());
                else if (!Utility.isEmpty(userModel.getUserEmail()))
                    tv_name.setText(userModel.getUserEmail());
                else
                    tv_name.setText(userModel.getUserMobile());

                tv_address.setText(orderDetails.getUserAddress());

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
                    iv_user_image.setLayoutParams(params);

                    iv_user_image.setScaleType(ImageView.ScaleType.FIT_XY);

                    Picasso picasso = new Picasso.Builder(this)
                            .downloader(new OkHttp3Downloader(okHttpClient))
                            .build();
                    picasso.load(Constants.BASE_URL + "customer/" + userModel.getUserId() + "/images")
                            .config(Bitmap.Config.RGB_565)
                            .into(iv_user_image, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Bitmap imageBitmap = ((BitmapDrawable) iv_user_image.getDrawable()).getBitmap();
                                    RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                    imageDrawable.setCircular(true);
                                    iv_user_image.setImageDrawable(imageDrawable);
                                }

                                @Override
                                public void onError(Exception e) {
                                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.MATCH_PARENT,
                                            RelativeLayout.LayoutParams.MATCH_PARENT
                                    );

                                    int margins = Utility.convertDPtoPx(OrderDetailsActivity.this, 15);
                                    params.setMargins(margins, margins, margins, margins);
                                    iv_user_image.setLayoutParams(params);

                                    iv_user_image.setImageDrawable(ContextCompat.getDrawable(OrderDetailsActivity.this, R.drawable.ic_user));
                                }
                            });
                }
            }
            tv_date.setText(Utility.getFormattedDate(9, orderDetails.getOrderCreationDate(), 0));
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.order_details));
    }


    @Override
    public void onOrderItemQuantitySelected(int pos) {
        if (orderDetails != null && orderDetails.getOrderItems() != null &&
                orderDetails.getOrderItems().size() > 0) {
            final OrderItem orderItem = orderDetails.getOrderItems().get(pos);

            just_sec_layout.setVisibility(View.VISIBLE);
            new RetrofitHelper(this).fetchMeasurementsByID(orderItem.getOrderSKU().getSkuId(), new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {
                            just_sec_layout.setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("status")) {
                                    if (jsonObject.optJSONArray("result") != null) {
                                        JSONArray orderJson = jsonObject.getJSONArray("result");
                                        Type classType = new TypeToken<ArrayList<OrderItem.OrderSKU>>() {
                                        }.getType();

                                        ArrayList<OrderItem.OrderSKU> skuOptions = new Gson().fromJson(orderJson.toString(), classType);
                                        invokeSkuPopUp(skuOptions, orderItem.getItemId());


                                    }
                                } else {
                                    showSnackBar(getString(R.string.something_went_wrong));

                                }
                            } catch (JSONException e) {

                                showSnackBar(getString(R.string.something_went_wrong));
                            }

                        }

                        @Override
                        public void onErrorResponse() {
                            just_sec_layout.setVisibility(View.GONE);
                            showSnackBar(getString(R.string.something_went_wrong));
                        }
                    }
            );


        }
    }

    private void invokeSkuPopUp(ArrayList<OrderItem.OrderSKU> skuList, String itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_show_available_sku, null);

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        builder.setView(dialogView);
        builder.setCancelable(false);

        mSKUDialog = builder.create();
        mSKUDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mSKUDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.rv_list);
        OrderSKUAdapter mAdapter = null;
        if (skuList != null && skuList.size() > 0) {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            mAdapter = new OrderSKUAdapter(skuList, this, itemId);
            recyclerView.setAdapter(mAdapter);
        } else {
            mSKUDialog.dismiss();
        }

        TextView headerTitle = (TextView) dialogView.findViewById(R.id.header);
        headerTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSKUDialog.dismiss();
            }
        });

        mSKUDialog.show();
    }

    @Override
    public void onSKUSelected(OrderItem.OrderSKU sku, String itemId) {

        ArrayList<OrderItem> itemList = orderDetails.getOrderItems();
        for (OrderItem item : itemList) {
            if (item.getItemId().equalsIgnoreCase(itemId))
                item.setUpdatedSKUMeasurement(sku);
        }

        if (mAdapter != null) {
            mAdapter.refreshEvents();
        }

        if (mSKUDialog != null)
            mSKUDialog.dismiss();
    }
}
