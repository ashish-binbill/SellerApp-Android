package com.binbill.seller.Order;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.AssistedService.AssistedUserModel;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.BinBillSeller;
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
import java.util.List;

import io.socket.emitter.Emitter;
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
    TextView tv_address, tv_date, tv_name, tv_order_status, header_shopping_list, header_quantity, tv_start_time, tv_end_time, tv_time_elapsed, tv_total_amount;
    @ViewById
    ImageView iv_user_image;

    @ViewById
    RecyclerView rv_shopping_list;

    @ViewById
    LinearLayout shimmer_view_container, ll_user_action, ll_bill_layout;
    private Order orderDetails;

    @ViewById
    RelativeLayout just_sec_layout;

    /**
     * Service agent layout
     */
    @ViewById
    CardView cv_root_delivery;

    @ViewById
    AppButton btn_accept;

    @ViewById
    AppButtonGreyed btn_decline;

    @ViewById
    FrameLayout frame_decline;

    @ViewById
    NestedScrollView nested_scroll_view;

    @ViewById
    ProgressBar btn_accept_progress, btn_decline_progress;

    String orderId;
    private OrderShoppingListAdapter mAdapter;
    private AlertDialog mSKUDialog;
    private boolean[] mUpdateStateArray;

    @AfterViews
    public void setUpView() {
        setUpToolbar();

        if (getIntent() != null && getIntent().hasExtra(Constants.ORDER_ID)) {
            orderId = getIntent().getStringExtra(Constants.ORDER_ID);
        } else
            finish();

        setUpListener();
        connectSocket();
    }

    private void connectSocket() {
        BinBillSeller.getSocket(this).connect();
        BinBillSeller.getSocket(this).on("order-placed", SOCKET_EVENT_ORDER_PLACED);
        BinBillSeller.getSocket(this).on("order-status-change", SOCKET_EVENT_ORDER_STATUS_CHANGED);
        BinBillSeller.getSocket(this).on("assisted-status-change", SOCKET_ASSISTED_ORDER_STATUS_CHANGED);
    }

    private Emitter.Listener SOCKET_EVENT_ORDER_PLACED = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SHRUTI", "order-placed" + args.toString());

            Type classType = new TypeToken<Order>() {
            }.getType();
            final Order mOrderDetails = new Gson().fromJson(args[0].toString(), classType);
            if (orderId.equalsIgnoreCase(mOrderDetails.getOrderId())) {

                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        orderDetails = mOrderDetails;
                        handleResponse();
                    }
                });
            }
        }
    };

    private Emitter.Listener SOCKET_EVENT_ORDER_STATUS_CHANGED = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SHRUTI", "order-status-change" + args.toString());
            Type classType = new TypeToken<Order>() {
            }.getType();

            final Order mOrderDetails = new Gson().fromJson(args[0].toString(), classType);
            if (orderId.equalsIgnoreCase(mOrderDetails.getOrderId())) {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        orderDetails = mOrderDetails;
                        handleResponse();
                    }
                });
            }

        }
    };

    private Emitter.Listener SOCKET_ASSISTED_ORDER_STATUS_CHANGED = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SHRUTI", "order-status-change" + args.toString());
            Type classType = new TypeToken<Order>() {
            }.getType();

            final Order mOrderDetails = new Gson().fromJson(args[0].toString(), classType);
            if (orderId.equalsIgnoreCase(mOrderDetails.getOrderId())) {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        orderDetails = mOrderDetails;
                        handleResponse();
                    }
                });
            }

        }
    };

    private void setUpListener() {
        btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_decline.setVisibility(View.GONE);
                btn_decline_progress.setVisibility(View.VISIBLE);

                makeDeclineOrderCall();
            }
        });

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textOnButton = btn_accept.getText().toString();
                if (orderDetails.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_FMCG)) {
                    ArrayList<OrderItem> updatedList = mAdapter.getUpdatedOrderList();
                    for (OrderItem orderItem : updatedList) {
                        orderItem.setItemAvailability(orderItem.isUpdateItemAvailable());
                    }
                    if (textOnButton.equalsIgnoreCase(getString(R.string.send_for_approval))) {
                        /**
                         * Modification call
                         */
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<OrderItem>>() {
                        }.getType();
                        String json = gson.toJson(updatedList, type);
                        btn_accept.setVisibility(View.GONE);
                        btn_accept_progress.setVisibility(View.VISIBLE);
                        new RetrofitHelper(OrderDetailsActivity.this).sendOrderModificationCall(orderDetails.getOrderId(), orderDetails.getUserId(), json, new RetrofitHelper.RetrofitCallback() {
                            @Override
                            public void onResponse(String response) {
                                btn_accept.setVisibility(View.VISIBLE);
                                btn_accept_progress.setVisibility(View.GONE);
                                handleApiResponse(response);
                            }

                            @Override
                            public void onErrorResponse() {
                                btn_accept.setVisibility(View.GONE);
                                btn_accept_progress.setVisibility(View.VISIBLE);
                                shimmer_view_container.setVisibility(View.GONE);

                                showSnackBar(getString(R.string.something_went_wrong));
                                finish();
                            }
                        });

                    } else if (textOnButton.equalsIgnoreCase(getString(R.string.accept))) {
                        /**
                         * Approval call
                         */

                        Gson gson = new Gson();
                        Type type = new TypeToken<List<OrderItem>>() {
                        }.getType();
                        String json = gson.toJson(updatedList, type);


                        new RetrofitHelper(OrderDetailsActivity.this).sendOrderApprovalCall(orderDetails.getOrderId(), orderDetails.getUserId(), json, new RetrofitHelper.RetrofitCallback() {
                            @Override
                            public void onResponse(String response) {
                                btn_accept.setVisibility(View.VISIBLE);
                                btn_accept_progress.setVisibility(View.GONE);
                                handleApiResponse(response);
                            }

                            @Override
                            public void onErrorResponse() {
                                btn_accept.setVisibility(View.GONE);
                                btn_accept_progress.setVisibility(View.VISIBLE);
                                shimmer_view_container.setVisibility(View.GONE);

                                showSnackBar(getString(R.string.something_went_wrong));
                                finish();
                            }
                        });

                    } else if (textOnButton.equalsIgnoreCase(getString(R.string.out_for_delivery))) {
                        /**
                         * Out for delivery call
                         */

                        Intent intent = new Intent(OrderDetailsActivity.this, SelectDeliveryAgentActivity_.class);
                        startActivityForResult(intent, Constants.INTENT_CALL_SELECT_DELIVERY_AGENT);

                    }

                } else if (orderDetails.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE)) {

                    if (textOnButton.equalsIgnoreCase(getString(R.string.accept))) {
                        Intent intent = new Intent(OrderDetailsActivity.this, SelectDeliveryAgentActivity_.class);

                        ArrayList<OrderItem> orderItems = orderDetails.getOrderItems();
                        if (orderItems.size() > 0) {
                            intent.putExtra(Constants.ORDER_TYPE_SERVICE, orderItems.get(0).getServiceTypeId());
                        }
                        startActivityForResult(intent, Constants.INTENT_CALL_SELECT_DELIVERY_AGENT);
                    } else {
                        new RetrofitHelper(OrderDetailsActivity.this).sendOrderOutForDeliveryCall(orderDetails.getOrderId(), orderDetails.getUserId(), null, null, new RetrofitHelper.RetrofitCallback() {
                            @Override
                            public void onResponse(String response) {
                                btn_accept.setVisibility(View.VISIBLE);
                                btn_accept_progress.setVisibility(View.GONE);
                                handleApiResponse(response);
                            }

                            @Override
                            public void onErrorResponse() {
                                btn_accept.setVisibility(View.GONE);
                                btn_accept_progress.setVisibility(View.VISIBLE);
                                shimmer_view_container.setVisibility(View.GONE);

                                showSnackBar(getString(R.string.something_went_wrong));
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.INTENT_CALL_SELECT_DELIVERY_AGENT) {
            if (resultCode == RESULT_OK) {

                Intent intent = data;
                String deliveryId = "";
                if (intent != null && intent.hasExtra(Constants.DELIVERY_AGENT_ID)) {
                    deliveryId = intent.getStringExtra(Constants.DELIVERY_AGENT_ID);
                }
                if (orderDetails.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE)) {

                    new RetrofitHelper(OrderDetailsActivity.this).sendOrderModifyAssisted(orderDetails.getOrderId(), orderDetails.getUserId(), deliveryId, new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {
                            btn_accept.setVisibility(View.VISIBLE);
                            btn_accept_progress.setVisibility(View.GONE);
                            handleApiResponse(response);
                        }

                        @Override
                        public void onErrorResponse() {
                            btn_accept.setVisibility(View.GONE);
                            btn_accept_progress.setVisibility(View.VISIBLE);
                            shimmer_view_container.setVisibility(View.GONE);

                            showSnackBar(getString(R.string.something_went_wrong));
                        }
                    });
                } else {
                    ArrayList<OrderItem> updatedList = mAdapter.getUpdatedOrderList();
                    for (OrderItem orderItem : updatedList) {
                        if (orderItem.getUpdatedSKUMeasurement() != null)
                            orderItem.setOrderSKU(orderItem.getUpdatedSKUMeasurement());
                        orderItem.setItemAvailability(orderItem.isUpdateItemAvailable());
                    }


                    Gson gson = new Gson();
                    Type type = new TypeToken<List<OrderItem>>() {
                    }.getType();
                    String json = gson.toJson(updatedList, type);

                    new RetrofitHelper(OrderDetailsActivity.this).sendOrderOutForDeliveryCall(orderDetails.getOrderId(), orderDetails.getUserId(), json, deliveryId, new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {
                            btn_accept.setVisibility(View.VISIBLE);
                            btn_accept_progress.setVisibility(View.GONE);
                            handleApiResponse(response);
                        }

                        @Override
                        public void onErrorResponse() {
                            btn_accept.setVisibility(View.GONE);
                            btn_accept_progress.setVisibility(View.VISIBLE);
                            shimmer_view_container.setVisibility(View.GONE);

                            showSnackBar(getString(R.string.something_went_wrong));
                        }
                    });
                }
            }
        }
    }

    private void makeDeclineOrderCall() {
        new RetrofitHelper(OrderDetailsActivity.this).sendOrderDeclineCall(orderDetails.getOrderId(), orderDetails.getUserId(), new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                btn_decline.setVisibility(View.VISIBLE);
                btn_decline_progress.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        invokeSuccessDialog();
                    } else {

                        showSnackBar(getString(R.string.something_went_wrong));
                    }
                } catch (JSONException e) {
                    showSnackBar(getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onErrorResponse() {
                btn_decline.setVisibility(View.VISIBLE);
                btn_decline_progress.setVisibility(View.GONE);
                showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    public void invokeSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_yes_no, null);

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView headerTitle = (TextView) dialogView.findViewById(R.id.header);
        TextView titleText = (TextView) dialogView.findViewById(R.id.title);
        AppButton yesButton = (AppButton) dialogView.findViewById(R.id.btn_yes);
        AppButtonGreyed noButton = (AppButtonGreyed) dialogView.findViewById(R.id.btn_no);
        noButton.setVisibility(View.GONE);

        titleText.setText(getString(R.string.string_order_rejected));
        headerTitle.setText(getString(R.string.order_rejected));
        yesButton.setText(getString(R.string.ok));

        headerTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onBackPressed();
            }
        });

        dialog.show();
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
                handleApiResponse(response);
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

    private void handleApiResponse(String response) {
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

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }
        } catch (JSONException e) {
            rv_shopping_list.setVisibility(View.GONE);
            shimmer_view_container.setVisibility(View.GONE);

            showSnackBar(getString(R.string.something_went_wrong));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        }
    }

    private void handleResponse() {
        if (orderDetails != null && orderDetails.getOrderItems() != null) {
            initialiseUpdatedDataArray(orderDetails.getOrderItems().size());
            setUpData();
        } else {
            rv_shopping_list.setVisibility(View.GONE);
            shimmer_view_container.setVisibility(View.GONE);

            showSnackBar(getString(R.string.something_went_wrong));
            finish();
        }
    }

    private void initialiseUpdatedDataArray(int size) {
        mUpdateStateArray = new boolean[size];
    }

    private void setUpData() {

        if (orderDetails.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE)) {
            header_shopping_list.setText(getString(R.string.service_requested));
            header_quantity.setVisibility(View.GONE);
        }

        Utility.hideKeyboard(this, rv_shopping_list);
        setUpUserLayout();

        rv_shopping_list.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_shopping_list.setLayoutManager(llm);

        mAdapter = new OrderShoppingListAdapter(orderDetails.getOrderType(), orderDetails.getOrderItems(), this, mUpdateStateArray, orderDetails.getOrderStatus(), orderDetails.isModified());
        rv_shopping_list.setAdapter(mAdapter);

        nested_scroll_view.fullScroll(View.FOCUS_UP);
        nested_scroll_view.smoothScrollTo(0, 0);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                rv_shopping_list.scrollTo(0, 0);
            }
        });

        if (orderDetails.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE) &&
                (orderDetails.getOrderStatus() == Constants.STATUS_JOB_ENDED) || orderDetails.getOrderStatus() == Constants.STATUS_COMPLETE) {
            if (orderDetails.getOrderItems() != null && orderDetails.getOrderItems().size() > 0) {
                final OrderItem orderItem = orderDetails.getOrderItems().get(0);
                if (!Utility.isEmpty(orderItem.getStartDate())) {
                    tv_start_time.setText(Utility.getFormattedDate(17, orderItem.getStartDate(), 0));
                    tv_end_time.setText(Utility.getFormattedDate(17, orderItem.getEndDate(), 0));
                    tv_time_elapsed.setText(Utility.getDateDifference(orderItem.getStartDate(), orderItem.getEndDate()));
                    tv_total_amount.setText(getString(R.string.rupee_sign) + " " + orderItem.getTotalAmount());
                    ll_bill_layout.setVisibility(View.VISIBLE);
                }

                if (orderItem.getServiceUser() != null) {

                    new RetrofitHelper(this).fetchDeliveryBoysForSeller(new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("status")) {
                                    if (jsonObject.optJSONArray("result") != null) {
                                        JSONArray userArray = jsonObject.getJSONArray("result");
                                        Type classType = new TypeToken<ArrayList<DeliveryModel>>() {
                                        }.getType();

                                        ArrayList<DeliveryModel> deliveryList = new Gson().fromJson(userArray.toString(), classType);
                                        if (deliveryList != null && deliveryList.size() > 0) {
                                            for (DeliveryModel deliveryModel : deliveryList) {
                                                if (deliveryModel.getDeliveryBoyId().equalsIgnoreCase(orderItem.getServiceUser().getId())) {
                                                    DeliveryAgentAdapter.DeliveryAgentHolder deliveryAgentHolder = new DeliveryAgentAdapter.DeliveryAgentHolder(cv_root_delivery);
                                                    updateAgentLayout(deliveryAgentHolder, deliveryModel, orderItem.getServiceTypeId());
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {

                            }
                        }

                        @Override
                        public void onErrorResponse() {

                        }
                    }, orderItem.getServiceTypeId());
                }
            }
        }

    }

    private void updateAgentLayout(final DeliveryAgentAdapter.DeliveryAgentHolder userHolder, DeliveryModel model, String serviceId) {
        userHolder.mUserName.setText(model.getName());

        float rating = 0;
        if (!Utility.isEmpty(model.getRating()))
            rating = Float.parseFloat(model.getRating());
        userHolder.mRating.setRating(rating);
        userHolder.ratingText.setText(userHolder.mReviews.getContext().getString(R.string.rating_value, String.valueOf(rating)));

        ArrayList<AssistedUserModel.Review> userReviews = model.getReviews();
        if (userReviews != null)
            userHolder.mReviews.setText(userHolder.mReviews.getContext().getString(R.string.reviews, String.valueOf(userReviews.size())));
        else
            userHolder.mReviews.setText(userHolder.mReviews.getContext().getString(R.string.reviews, "0"));

        userHolder.statusText.setVisibility(View.GONE);
        userHolder.mActionLayout.setVisibility(View.GONE);
        userHolder.statusColor.setVisibility(View.GONE);

        userHolder.mBasePrice.setVisibility(View.GONE);
        userHolder.mAdditionalPrice.setVisibility(View.GONE);

        ArrayList<AssistedUserModel.ServiceType> serviceTypes = model.getServiceType();
        for (AssistedUserModel.ServiceType serviceType : serviceTypes) {
            if (serviceType.getServiceTypeId().equalsIgnoreCase(serviceId)) {
                ArrayList<AssistedUserModel.Price> priceList = serviceType.getPrice();
                for (AssistedUserModel.Price price : priceList) {
                    if (price.getPriceType().equalsIgnoreCase("1")) {
                        userHolder.mBasePrice.setText(userHolder.mBasePrice.getContext().getString(R.string.base_price_value, price.getValue()));
                        userHolder.mBasePrice.setVisibility(View.VISIBLE);
                    }

                    if (price.getPriceType().equalsIgnoreCase("2")) {
                        userHolder.mAdditionalPrice.setText(userHolder.mBasePrice.getContext().getString(R.string.additional_price_value, price.getValue()));
                        userHolder.mAdditionalPrice.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        if (model.getProfileImage() != null) {

            final String authToken = SharedPref.getString(userHolder.userImage.getContext(), SharedPref.AUTH_TOKEN);
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
            userHolder.userImage.setLayoutParams(params);

            userHolder.userImage.setScaleType(ImageView.ScaleType.FIT_XY);

            Picasso picasso = new Picasso.Builder(userHolder.userImage.getContext())
                    .downloader(new OkHttp3Downloader(okHttpClient))
                    .build();
            picasso.load(Constants.BASE_URL + "assisted/" + model.getDeliveryBoyId() + "/profile")
                    .config(Bitmap.Config.RGB_565)
                    .into(userHolder.userImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) userHolder.userImage.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(userHolder.userImage.getContext().getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            userHolder.userImage.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError(Exception e) {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT
                            );

                            int margins = Utility.convertDPtoPx(userHolder.userImage.getContext(), 15);
                            params.setMargins(margins, margins, margins, margins);
                            userHolder.userImage.setLayoutParams(params);

                            userHolder.userImage.setImageDrawable(ContextCompat.getDrawable(userHolder.userImage.getContext(), R.drawable.ic_user));
                        }
                    });
        }

        userHolder.mSelectedCardLayout.setVisibility(View.GONE);
        cv_root_delivery.setVisibility(View.VISIBLE);
    }

    public void changeButtonStateToApproval(int state) {

        switch (state) {
            case 0:
                if (orderDetails.getOrderStatus() == Constants.STATUS_NEW_ORDER) {
                    if (orderDetails.isModified())
                        ll_user_action.setVisibility(View.GONE);
                    else
                        ll_user_action.setVisibility(View.VISIBLE);
                } else
                    ll_user_action.setVisibility(View.GONE);
                frame_decline.setVisibility(View.GONE);
                btn_accept.setText(getString(R.string.send_for_approval));
                break;
            case 1:
                if (orderDetails.getOrderStatus() == Constants.STATUS_NEW_ORDER) {
                    ll_user_action.setVisibility(View.VISIBLE);
                } else
                    ll_user_action.setVisibility(View.GONE);
                frame_decline.setVisibility(View.VISIBLE);
                btn_accept.setText(getString(R.string.accept));
                break;
            case 2:
                if (orderDetails.getOrderStatus() == Constants.STATUS_OUT_FOR_DELIVERY)
                    ll_user_action.setVisibility(View.GONE);
                else
                    ll_user_action.setVisibility(View.VISIBLE);
                frame_decline.setVisibility(View.GONE);

                if (orderDetails.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE))
                    btn_accept.setText(getString(R.string.out_for_service));
                else
                    btn_accept.setText(getString(R.string.out_for_delivery));
                break;
            default:
                ll_user_action.setVisibility(View.GONE);
                break;
        }
    }

    private void setUpUserLayout() {

        if (orderDetails != null) {

            switch (orderDetails.getOrderStatus()) {
                case Constants.STATUS_NEW_ORDER:
                    tv_order_status.setTextColor(ContextCompat.getColor(this, R.color.status_light_blue));
                    if (orderDetails.isModified()) {
                        tv_order_status.setText(getString(R.string.waiting_for_approval));
                        changeButtonStateToApproval(3);
                    } else {
                        tv_order_status.setText(getString(R.string.new_order));
                        changeButtonStateToApproval(1);
                    }
                    break;
                case Constants.STATUS_COMPLETE:
                    tv_order_status.setTextColor(ContextCompat.getColor(this, R.color.status_green));
                    tv_order_status.setText(getString(R.string.order_complete));
                    changeButtonStateToApproval(3);
                    break;
                case Constants.STATUS_APPROVED:
                    tv_order_status.setTextColor(ContextCompat.getColor(this, R.color.status_yellow));
                    if (orderDetails.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE))
                        tv_order_status.setText(getString(R.string.provider_accepted));
                    else
                        tv_order_status.setText(getString(R.string.in_progress));
                    changeButtonStateToApproval(2);
                    break;
                case Constants.STATUS_CANCEL:
                    tv_order_status.setTextColor(ContextCompat.getColor(this, R.color.status_red));
                    tv_order_status.setText(getString(R.string.order_cancelled));
                    changeButtonStateToApproval(3);
                    break;
                case Constants.STATUS_REJECTED:
                    tv_order_status.setTextColor(ContextCompat.getColor(this, R.color.status_orange));
                    tv_order_status.setText(getString(R.string.order_rejected));
                    changeButtonStateToApproval(3);
                    break;
                case Constants.STATUS_OUT_FOR_DELIVERY:
                    tv_order_status.setTextColor(ContextCompat.getColor(this, R.color.status_blue));

                    if (orderDetails.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE))
                        tv_order_status.setText(getString(R.string.provider_assigned));
                    else
                        tv_order_status.setText(getString(R.string.out_for_delivery));
                    changeButtonStateToApproval(3);
                    break;
                case Constants.STATUS_JOB_STARTED:
                    tv_order_status.setTextColor(ContextCompat.getColor(this, R.color.status_blue));
                    tv_order_status.setText(getString(R.string.service_started));
                    changeButtonStateToApproval(3);
                    break;
                case Constants.STATUS_JOB_ENDED:
                    tv_order_status.setTextColor(ContextCompat.getColor(this, R.color.status_blue));
                    tv_order_status.setText(getString(R.string.service_completed));
                    changeButtonStateToApproval(3);
                    break;
            }

            if (orderDetails.getUser() != null) {
                final UserModel userModel = orderDetails.getUser();

                if (!Utility.isEmpty(userModel.getUserName()))
                    tv_name.setText(userModel.getUserName());
                else if (!Utility.isEmpty(userModel.getUserEmail()))
                    tv_name.setText(userModel.getUserEmail());
                else
                    tv_name.setText(userModel.getUserMobile());

                tv_address.setText(orderDetails.getAddress());

                if (userModel.getUserId() != null) {

                    final String authToken = SharedPref.getString(this, SharedPref.AUTH_TOKEN);
                    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
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

                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Picasso picasso = new Picasso.Builder(OrderDetailsActivity.this)
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
                    });
                }
                tv_date.setText(Utility.getFormattedDate(9, orderDetails.getOrderCreationDate(), 0));
            }
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
            new RetrofitHelper(this).fetchMeasurementsByID(orderItem.getItemId(), new RetrofitHelper.RetrofitCallback() {
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

    @Override
    public void onItemInteraction(boolean enable) {
        if (orderDetails.getOrderStatus() == Constants.STATUS_NEW_ORDER && !orderDetails.isModified()) {
            if (enable)
                changeButtonStateToApproval(0);
            else
                changeButtonStateToApproval(1);
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
