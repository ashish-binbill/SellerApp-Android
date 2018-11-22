package com.binbill.seller.Order;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.binbill.seller.Adapter.DeliveryDistanceAdapter;
import com.binbill.seller.AssistedService.AssistedUserModel;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.BinBillSeller;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.AppButtonGreyed;
import com.binbill.seller.CustomViews.PrefixEditText;
import com.binbill.seller.CustomViews.ReviewAdapter;
import com.binbill.seller.CustomViews.ReviewsDialogFragment;
import com.binbill.seller.Interface.ItemSelectedInterface;
import com.binbill.seller.Model.UserModel;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
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
import java.util.Calendar;
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
    TextView tv_address, tv_date, tv_home_delivery, tv_name, tv_order_status, header_shopping_list, tv_start_time, tv_end_time, tv_time_elapsed, tv_total_amount;
    @ViewById
    ImageView iv_user_image, header_quantity;

    @ViewById
    TextView tv_start_time_header, tv_end_time_header, tv_time_elapsed_header, tv_delivery_header_fmcg, tv_delivery_header_service;
    @ViewById
    RecyclerView rv_shopping_list;

    @ViewById
    LinearLayout shimmer_view_container, ll_offer_layout, ll_user_action, ll_bill_layout, start_time, end_time, time_elapsed, ll_amount_entry, ll_call_customer;
    private Order orderDetails;

    @ViewById
    RelativeLayout just_sec_layout;

    @ViewById
    FrameLayout fl_icon_chat;

    @ViewById
    PrefixEditText et_total_amount, et_offered_amount;

    @ViewById
    TextView tv_delivery_review, tv_seller_review;

    @ViewById
    CardView cv_delivery_review, cv_seller_review;

    @ViewById
    View v_divider;

    @ViewById
    TextView tv_unread_message;

    /**
     * Service agent layout
     */
    @ViewById
    CardView cv_root_delivery, cv_root_fmcg_delivery;

    @ViewById
    RelativeLayout rl_delivery_review, rl_seller_review;

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
            if (orderId != null && orderId.equalsIgnoreCase(mOrderDetails.getOrderId())) {

                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        orderDetails = mOrderDetails;
                        handleResponse();
                    }
                });
            } else
                finish();
        }
    };

    private Emitter.Listener SOCKET_EVENT_ORDER_STATUS_CHANGED = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SHRUTI", "order-status-change" + args.toString());
            Type classType = new TypeToken<Order>() {
            }.getType();

            final Order mOrderDetails = new Gson().fromJson(args[0].toString(), classType);
            if (orderId != null && orderId.equalsIgnoreCase(mOrderDetails.getOrderId())) {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        orderDetails = mOrderDetails;
                        handleResponse();
                    }
                });
            } else
                finish();

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

        ll_call_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderDetails.getUser() != null && orderDetails.getUser().getUserMobile() != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + orderDetails.getUser().getUserMobile()));
                    startActivity(intent);
                }
            }
        });

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
                if (orderDetails != null && orderDetails.getOrderType() != null && orderDetails.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_FMCG)) {
                    ArrayList<OrderItem> updatedList = mAdapter.getUpdatedOrderList();
                    for (OrderItem orderItem : updatedList) {
                        orderItem.setItemAvailability(orderItem.isUpdateItemAvailable());

                        /**
                         * Remove suggestion if any of the 2 items are missing : measurement or title
                         */
                        if (orderItem.getSuggestion() != null) {
                            Suggestion suggestion = orderItem.getSuggestion();
                            if (Utility.isEmpty(suggestion.getItemName()) || Utility.isEmpty(suggestion.getMeasuremenValue())) {
                                suggestion = null;
                            }

                            orderItem.setSuggestion(suggestion);
                        }
                    }
                    if (textOnButton.equalsIgnoreCase(getString(R.string.send_for_approval))) {
                        /**
                         * Modification call
                         */
                        btn_accept_progress.setVisibility(View.VISIBLE);
                        btn_accept.setVisibility(View.GONE);

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
                        btn_accept_progress.setVisibility(View.VISIBLE);
                        btn_accept.setVisibility(View.GONE);

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
                        btn_accept_progress.setVisibility(View.VISIBLE);
                        btn_accept.setVisibility(View.GONE);
                        Intent intent = new Intent(OrderDetailsActivity.this, SelectDeliveryAgentActivity_.class);
                        startActivityForResult(intent, Constants.INTENT_CALL_SELECT_DELIVERY_AGENT);

                    } else if (textOnButton.equalsIgnoreCase(getString(R.string.order_ready))) {
                        /**
                         * Order Ready
                         */
                        btn_accept_progress.setVisibility(View.VISIBLE);
                        btn_accept.setVisibility(View.GONE);

                        ArrayList<OrderItem> updatedList2 = mAdapter.getUpdatedOrderList();
                        for (OrderItem orderItem : updatedList2) {
                            if (orderItem.getUpdatedSKUMeasurement() != null)
                                orderItem.setOrderSKU(orderItem.getUpdatedSKUMeasurement());
                            orderItem.setItemAvailability(orderItem.isUpdateItemAvailable());
                        }


                        Gson gson = new Gson();
                        Type type = new TypeToken<List<OrderItem>>() {
                        }.getType();
                        String json = gson.toJson(updatedList2, type);

                        String totalAmount = "";
                        if (!Utility.isEmpty(et_total_amount.getText().toString())) {
                            totalAmount = et_total_amount.getText().toString().trim();
                        }

                        new RetrofitHelper(OrderDetailsActivity.this).sendOrderOutForDeliveryCall(orderDetails.getOrderId(), orderDetails.getUserId(), json, "", totalAmount, new RetrofitHelper.RetrofitCallback() {
                            @Override
                            public void onResponse(String response) {
                                btn_accept.setVisibility(View.VISIBLE);
                                btn_accept_progress.setVisibility(View.GONE);
                                handleApiResponse(response);
                            }

                            @Override
                            public void onErrorResponse() {
                                btn_accept.setVisibility(View.VISIBLE);
                                btn_accept_progress.setVisibility(View.GONE);
                                shimmer_view_container.setVisibility(View.GONE);

                                showSnackBar(getString(R.string.something_went_wrong));
                            }
                        });
                    }

                } else if (orderDetails.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE)) {

                    if (textOnButton.equalsIgnoreCase(getString(R.string.assign))) {
                        btn_accept_progress.setVisibility(View.VISIBLE);
                        btn_accept.setVisibility(View.GONE);

                        Intent intent = new Intent(OrderDetailsActivity.this, SelectDeliveryAgentActivity_.class);

                        ArrayList<OrderItem> orderItems = orderDetails.getOrderItems();
                        if (orderItems.size() > 0) {
                            intent.putExtra(Constants.ORDER_TYPE_SERVICE, orderItems.get(0).getServiceTypeId());
                        }
                        startActivityForResult(intent, Constants.INTENT_CALL_SELECT_DELIVERY_AGENT);
                    } else {
                        btn_accept_progress.setVisibility(View.VISIBLE);
                        btn_accept.setVisibility(View.GONE);

                        new RetrofitHelper(OrderDetailsActivity.this).sendOrderOutForDeliveryCall(orderDetails.getOrderId(), orderDetails.getUserId(), null, null, null, new RetrofitHelper.RetrofitCallback() {
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
                            btn_accept.setVisibility(View.VISIBLE);
                            btn_accept_progress.setVisibility(View.GONE);
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

                    String totalAmount = "";
                    if (!Utility.isEmpty(et_total_amount.getText().toString())) {
                        totalAmount = et_total_amount.getText().toString().trim();
                    }

                    new RetrofitHelper(OrderDetailsActivity.this).sendOrderOutForDeliveryCall(orderDetails.getOrderId(), orderDetails.getUserId(), json, deliveryId, totalAmount, new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {
                            btn_accept.setVisibility(View.VISIBLE);
                            btn_accept_progress.setVisibility(View.GONE);
                            handleApiResponse(response);
                        }

                        @Override
                        public void onErrorResponse() {
                            btn_accept.setVisibility(View.VISIBLE);
                            btn_accept_progress.setVisibility(View.GONE);
                            shimmer_view_container.setVisibility(View.GONE);

                            showSnackBar(getString(R.string.something_went_wrong));
                        }
                    });
                }
            } else {
                btn_accept.setVisibility(View.VISIBLE);
                btn_accept_progress.setVisibility(View.GONE);
            }
        }
    }

    private void makeDeclineOrderCall() {

        if (orderDetails != null) {
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
        header_quantity.setVisibility(View.GONE);
        if (orderDetails.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE)) {
            header_shopping_list.setText(getString(R.string.service_requested));
            btn_accept.setText(getString(R.string.assign));
        } else {
            header_shopping_list.setText(getString(R.string.shopping_list, String.valueOf(orderDetails.getOrderItems().size())));
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

        if (orderDetails.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE)
                && orderDetails.getOrderItems() != null && orderDetails.getOrderItems().size() > 0) {

            final OrderItem orderItem = orderDetails.getOrderItems().get(0);

            if (orderDetails.getOrderStatus() == Constants.STATUS_JOB_ENDED || orderDetails.getOrderStatus() == Constants.STATUS_COMPLETE) {

                if (!Utility.isEmpty(orderItem.getStartDate())) {
                    tv_start_time.setText(Utility.getFormattedDate(17, orderItem.getStartDate(), 0));
                    tv_end_time.setText(Utility.getFormattedDate(17, orderItem.getEndDate(), 0));
                    tv_time_elapsed.setText(Utility.getDateDifference(orderItem.getStartDate(), orderItem.getEndDate()));

                    if (!Utility.isEmpty(orderItem.getTotalAmount()))
                        tv_total_amount.setText(getString(R.string.rupee_sign) + " " + orderItem.getTotalAmount());
                    else
                        tv_total_amount.setText(getString(R.string.rupee_sign) + " 0");
                    ll_bill_layout.setVisibility(View.VISIBLE);
                }
            }

            if (orderDetails.getOrderStatus() != Constants.STATUS_NEW_ORDER) {
                if (orderDetails.getDeliveryUser() != null) {
                    DeliveryAgentAdapter.DeliveryAgentHolder deliveryAgentHolder = new DeliveryAgentAdapter.DeliveryAgentHolder(cv_root_delivery);
                    updateAgentLayout(deliveryAgentHolder, orderDetails.getDeliveryUser(), orderItem.getServiceTypeId());
                }
            }

        } else {

            /**
             * PATCH: Save suggestionSku in suggestion java object
             */
            for (OrderItem item : orderDetails.getOrderItems()) {
                if (item.getSuggestion() != null && item.getSuggestion().getMeasurement() != null) {

                    SuggestionSku suggestionSku = item.getSuggestion().getSuggestionSku();
                    if (suggestionSku == null)
                        suggestionSku = new SuggestionSku();

                    ArrayList<OrderItem.OrderSKU> skuArray = new ArrayList<>();
                    skuArray.add(item.getSuggestion().getMeasurement());

                    suggestionSku.setMeasurement(skuArray);

                    Suggestion suggestion = item.getSuggestion();
                    suggestion.setSuggestionSku(suggestionSku);

                    item.setSuggestion(suggestion);
                }
            }

            if (orderDetails.getOrderStatus() != Constants.STATUS_COMPLETE)
                if (orderDetails.getDeliveryUser() != null) {
                    DeliveryAgentAdapter.DeliveryAgentHolder deliveryAgentHolder = new DeliveryAgentAdapter.DeliveryAgentHolder(cv_root_fmcg_delivery);
                    updateAgentLayout(deliveryAgentHolder, orderDetails.getDeliveryUser(), null);
                }

            tv_start_time.setVisibility(View.GONE);
            tv_end_time.setVisibility(View.GONE);
            tv_time_elapsed.setVisibility(View.GONE);
            tv_start_time_header.setVisibility(View.GONE);
            tv_end_time_header.setVisibility(View.GONE);
            tv_time_elapsed_header.setVisibility(View.GONE);

            start_time.setVisibility(View.GONE);
            end_time.setVisibility(View.GONE);
            time_elapsed.setVisibility(View.GONE);

            v_divider.setVisibility(View.GONE);

            if (!Utility.isEmpty(orderDetails.getTotalAmount())) {
                tv_total_amount.setText(getString(R.string.rupee_sign) + " " + orderDetails.getTotalAmount());
            } else {
                tv_total_amount.setText(getString(R.string.rupee_sign) + " 0");
            }

            /**
             * Set edit text
             */
            setAmountInTotalEditText();

            if (orderDetails.getOrderStatus() == Constants.STATUS_OUT_FOR_DELIVERY || orderDetails.getOrderStatus() == Constants.STATUS_COMPLETE ||
                    orderDetails.getOrderStatus() == Constants.STATUS_AUTO_CANCEL || orderDetails.getOrderStatus() == Constants.STATUS_AUTO_EXPIRED ||
                    orderDetails.getOrderStatus() == Constants.STATUS_REJECTED || orderDetails.getOrderStatus() == Constants.STATUS_CANCEL) {
                ll_bill_layout.setVisibility(View.VISIBLE);
                ll_amount_entry.setVisibility(View.GONE);
            } else {
                ll_amount_entry.setVisibility(View.VISIBLE);
                ll_bill_layout.setVisibility(View.GONE);
            }

            /**
             * Payment status
             */
            if (orderDetails.getOrderStatus() == Constants.STATUS_COMPLETE) {
                if (orderDetails.getPaymentModeId() > 0) {
                    switch (orderDetails.getPaymentModeId()) {
                        case Constants.PAYMENT_MODE_CASH:
                            header_quantity.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paid_offline_watermark));
                            header_quantity.setVisibility(View.VISIBLE);
                            break;
                        case Constants.PAYMENT_MODE_ONLINE:
                            header_quantity.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paid_online_watermark));
                            header_quantity.setVisibility(View.VISIBLE);
                            break;
                        case Constants.PAYMENT_MODE_CREDIT:
                            header_quantity.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paid_credit_watermark));
                            header_quantity.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }

            if (orderDetails.getDeliveryReview() != null) {
                ReviewAdapter.ReviewHolder reviewHolder = new ReviewAdapter.ReviewHolder(rl_delivery_review);
                updateDeliveryReview(reviewHolder, orderDetails.getDeliveryReview());
                tv_delivery_review.setVisibility(View.VISIBLE);
                cv_delivery_review.setVisibility(View.VISIBLE);
            } else {
                tv_delivery_review.setVisibility(View.GONE);
                cv_delivery_review.setVisibility(View.GONE);
            }

            if (orderDetails.getSellerReview() != null) {
                ReviewAdapter.ReviewHolder reviewHolder = new ReviewAdapter.ReviewHolder(rl_seller_review);
                updateDeliveryReview(reviewHolder, orderDetails.getSellerReview());
                tv_seller_review.setVisibility(View.VISIBLE);
                cv_seller_review.setVisibility(View.VISIBLE);
            } else {
                tv_seller_review.setVisibility(View.GONE);
                cv_seller_review.setVisibility(View.GONE);
            }
        }

    }

    private void setAmountInTotalEditText() {

        if (mAdapter != null) {
            ArrayList<OrderItem> orderItems = mAdapter.getUpdatedOrderList();

            double totalAmount = 0;
            double offerAmount = 0;
            for (OrderItem item : orderItems) {

                String price = item.getUpdatedPrice();
                boolean showOfferAmount = true;
                if (orderDetails.getOrderStatus() == Constants.STATUS_COMPLETE ||
                        orderDetails.getOrderStatus() == Constants.STATUS_OUT_FOR_DELIVERY) {
                    price = item.getSellingPrice();
                    showOfferAmount = false;
                }

                if (!Utility.isEmpty(price) && Utility.isValueNonZero(price)) {

                    int quantity = 1;
                    try {

                        if (!Utility.isEmpty(item.getQuantity()))
                            quantity = Integer.parseInt(item.getQuantity());

                        if (!Utility.isEmpty(item.getUpdatedQuantityCount()))
                            quantity = Integer.parseInt(item.getUpdatedQuantityCount());

                    } catch (Exception e) {
                        quantity = 1;
                    }

                    totalAmount = totalAmount + (quantity * Double.parseDouble(price));
                }

                if (showOfferAmount) {
                    if (!Utility.isEmpty(item.getTempOfferPrice()) && Utility.isValueNonZero(item.getTempOfferPrice())) {

                        int quantity = 1;
                        try {

                            if (!Utility.isEmpty(item.getQuantity()))
                                quantity = Integer.parseInt(item.getQuantity());

                            if (!Utility.isEmpty(item.getUpdatedQuantityCount()))
                                quantity = Integer.parseInt(item.getUpdatedQuantityCount());

                        } catch (Exception e) {
                            quantity = 1;
                        }

                        offerAmount = offerAmount + (quantity * Double.parseDouble(item.getTempOfferPrice()));
                    }
                }
            }

            if (orderDetails.getOrderStatus() != Constants.STATUS_CANCEL && orderDetails.getOrderStatus() != Constants.STATUS_COMPLETE &&
                    orderDetails.getOrderStatus() != Constants.STATUS_OUT_FOR_DELIVERY && orderDetails.getOrderStatus() != Constants.STATUS_REJECTED &&
                    orderDetails.getOrderStatus() != Constants.STATUS_AUTO_CANCEL && orderDetails.getOrderStatus() != Constants.STATUS_AUTO_EXPIRED) {
                if (Double.compare(totalAmount, offerAmount) != 0 && Utility.isValueNonZero(String.valueOf(offerAmount))) {
                    ll_offer_layout.setVisibility(View.VISIBLE);
                    et_offered_amount.setText(Utility.showDoubleString(offerAmount));
                }
            }

            Log.d("XYZ", "PRICE: " + Utility.showDoubleString(totalAmount));
            et_total_amount.setText(Utility.showDoubleString(totalAmount));
        }
    }

    private void updateDeliveryReview(final ReviewAdapter.ReviewHolder reviewHolder, AssistedUserModel.Review deliveryReview) {

        reviewHolder.mReview.setText(deliveryReview.getUserName());
        reviewHolder.mUser.setText(deliveryReview.getFeedback());

        if (!Utility.isEmpty(deliveryReview.getRating()))
            reviewHolder.mRating.setRating(Float.parseFloat(deliveryReview.getRating()));
        else
            reviewHolder.mRating.setRating(0);

        reviewHolder.mDivider.setVisibility(View.GONE);

        final String authToken = SharedPref.getString(reviewHolder.mUserImage.getContext(), SharedPref.AUTH_TOKEN);
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
        reviewHolder.mUserImage.setLayoutParams(params);

        Picasso picasso = new Picasso.Builder(reviewHolder.mUserImage.getContext())
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
        picasso.load(Constants.BASE_URL + "customer/" + deliveryReview.getUserID() + "/images")
                .config(Bitmap.Config.RGB_565)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(reviewHolder.mUserImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) reviewHolder.mUserImage.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(reviewHolder.mUserImage.getContext().getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                        reviewHolder.mUserImage.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError(Exception e) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT
                        );

                        int margins = Utility.convertDPtoPx(reviewHolder.mUserImage.getContext(), 15);
                        params.setMargins(margins, margins, margins, margins);
                        reviewHolder.mUserImage.setLayoutParams(params);

                        reviewHolder.mUserImage.setImageDrawable(ContextCompat.getDrawable(reviewHolder.mUserImage.getContext(), R.drawable.ic_user));
                    }
                });
    }

    private void updateAgentLayout(final DeliveryAgentAdapter.DeliveryAgentHolder userHolder, final DeliveryModel model, String serviceId) {
        userHolder.mUserName.setText(model.getName());

        Double rating = 0.0;
        if (!Utility.isEmpty(model.getRating()))
            rating = Double.parseDouble(model.getRating());

        userHolder.mRating.setRating(rating.floatValue());
        userHolder.ratingText.setText(userHolder.mReviews.getContext().getString(R.string.rating_value, String.format("%.2f", rating)));

        ArrayList<AssistedUserModel.Review> userReviews = model.getReviews();
        if (userReviews != null)
            userHolder.mReviews.setText(userHolder.mReviews.getContext().getString(R.string.reviews, String.valueOf(userReviews.size())));
        else
            userHolder.mReviews.setText(userHolder.mReviews.getContext().getString(R.string.reviews, "0"));

        userHolder.mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.getMobile() != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getMobile()));
                    userHolder.mCall.getContext().startActivity(intent);
                }
            }
        });

        userHolder.statusText.setVisibility(View.GONE);
        userHolder.mActionLayout.setVisibility(View.GONE);
        userHolder.statusColor.setVisibility(View.GONE);

        userHolder.mBasePrice.setVisibility(View.GONE);
        userHolder.mAdditionalPrice.setVisibility(View.GONE);

        if (!Utility.isEmpty(serviceId)) {
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
        } else {
            userHolder.mBasePrice.setVisibility(View.GONE);
            userHolder.mAdditionalPrice.setVisibility(View.GONE);
        }

        if (userReviews != null && userReviews.size() > 0) {
            userHolder.mReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onShowReviews(model);
                }
            });
        }


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
                .memoryPolicy(MemoryPolicy.NO_CACHE)
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

        userHolder.mSelectedCardLayout.setVisibility(View.GONE);

        /**
         * If serviceId is null ---> means FMCG order
         */
        if (!Utility.isEmpty(serviceId)) {
            cv_root_delivery.setVisibility(View.VISIBLE);
            tv_delivery_header_service.setVisibility(View.VISIBLE);
        } else {
            cv_root_fmcg_delivery.setVisibility(View.VISIBLE);
            tv_delivery_header_fmcg.setVisibility(View.VISIBLE);
        }
    }

    private void onShowReviews(DeliveryModel deliveryModel) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        ReviewsDialogFragment fragment = ReviewsDialogFragment.newInstance(getString(R.string.reviews_string), deliveryModel.getReviews());
        fragment.show(fm, "ReviewsDialogFragment");
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

                if (orderDetails.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE))
                    btn_accept.setText(getString(R.string.assign));
                else
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
                else {

                    if (orderDetails.isCollectAtStore())
                        btn_accept.setText(getString(R.string.order_ready));
                    else
                        btn_accept.setText(getString(R.string.out_for_delivery));
                }
                break;
            default:
                ll_user_action.setVisibility(View.GONE);
                ll_amount_entry.setVisibility(View.GONE);
                break;
        }
    }

    private void setUpUserLayout() {

        if (orderDetails != null) {
            tv_home_delivery.setVisibility(View.GONE);
            switch (orderDetails.getOrderStatus()) {
                case Constants.STATUS_NEW_ORDER:
                    tv_order_status.setTextColor(ContextCompat.getColor(this, R.color.status_light_blue));

                    if (orderDetails.isCollectAtStore()) {
                        tv_home_delivery.setText(getString(R.string.collect_at_store));
                        tv_home_delivery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_collect_at_store_order), null, null, null);
                    } else {
                        tv_home_delivery.setText(getString(R.string.home_delivery));
                        tv_home_delivery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_home_delivery_order), null, null, null);
                    }
                    tv_home_delivery.setVisibility(View.VISIBLE);

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
                    else {
                        tv_order_status.setText(getString(R.string.in_progress));
                    }
                    changeButtonStateToApproval(2);
                    break;
                case Constants.STATUS_CANCEL:
                    tv_order_status.setTextColor(ContextCompat.getColor(this, R.color.status_red));
                    tv_order_status.setText(getString(R.string.order_cancelled));
                    changeButtonStateToApproval(3);
                    break;
                case Constants.STATUS_AUTO_EXPIRED:
                case Constants.STATUS_AUTO_CANCEL:
                    tv_order_status.setTextColor(ContextCompat.getColor(this, R.color.status_lighter_blue));
                    tv_order_status.setText(getString(R.string.not_responded));
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
                    else {
                        if (orderDetails.isCollectAtStore())
                            tv_order_status.setText(getString(R.string.order_ready));
                        else
                            tv_order_status.setText(getString(R.string.out_for_delivery));
                    }
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


                Calendar calendar = Calendar.getInstance();

                Calendar last7days = Calendar.getInstance();
                last7days.add(Calendar.DAY_OF_YEAR, -10);

                List<Message> messageList = new MobiComConversationService(this).getMessages("user_" + userModel.getUserId(), last7days.getTimeInMillis(), calendar.getTimeInMillis());
                int contactUnreadCount = new MessageDatabaseService(this).getUnreadMessageCountForContact("user_" + userModel.getUserId());
                if (messageList != null && messageList.size() > 0) {
                    Log.d("SHRUTI", "Message count: " + messageList.size());
                    fl_icon_chat.setVisibility(View.VISIBLE);

                    if (contactUnreadCount > 0) {
                        tv_unread_message.setVisibility(View.VISIBLE);
                        tv_unread_message.setText(contactUnreadCount + "");
                    } else
                        tv_unread_message.setVisibility(View.GONE);
                } else
                    fl_icon_chat.setVisibility(View.GONE);

                fl_icon_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(OrderDetailsActivity.this, ConversationActivity.class);
                        intent.putExtra(ConversationUIService.USER_ID, "user_" + userModel.getUserId());
                        intent.putExtra(ConversationUIService.TAKE_ORDER, true); //Skip chat list for showing on back press
                        startActivity(intent);
                    }
                });


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
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
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
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
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

            String itemId = orderItem.getItemId();
            if (orderItem.getSuggestion() != null && orderItem.getSuggestion().getSuggestionStatus() == Constants.SUGGESTION_STATUS_EXISTING)
                itemId = orderItem.getSuggestion().getItemId();

            new RetrofitHelper(this).fetchMeasurementsByID(itemId, new RetrofitHelper.RetrofitCallback() {
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
                                        invokeSkuPopUp(skuOptions, orderItem.getUid());
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

    @Override
    public void onItemAmountChanged() {
        setAmountInTotalEditText();
    }

    @Override
    public void onOrderItemQuantityDenominationSelected(int pos, String quantity, String setQuantity) {
        if (orderDetails != null && orderDetails.getOrderItems() != null &&
                orderDetails.getOrderItems().size() > 0) {
            final OrderItem orderItem = orderDetails.getOrderItems().get(pos);

            invokeQuantityPopUp(orderItem.getUid(), quantity, setQuantity);
        }
    }

    @Override
    public void onSuggestionClicked(int pos) {
        if (orderDetails != null && orderDetails.getOrderItems() != null &&
                orderDetails.getOrderItems().size() > 0) {
            final OrderItem orderItem = orderDetails.getOrderItems().get(pos);

            String measurementId = orderItem.getOrderSKU().getSkuId();

            just_sec_layout.setVisibility(View.VISIBLE);
            new RetrofitHelper(this).fetchSuggestionsByID(orderItem.getItemId(), measurementId, new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {
                            just_sec_layout.setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("status")) {
                                    if (jsonObject.optJSONArray("result") != null) {
                                        JSONArray orderJson = jsonObject.getJSONArray("result");
                                        Type classType = new TypeToken<ArrayList<SuggestionSku>>() {
                                        }.getType();

                                        ArrayList<SuggestionSku> skuOptions = new Gson().fromJson(orderJson.toString(), classType);
                                        invokeSuggestionPopUp(skuOptions, orderItem.getUid());

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

    private void invokeSuggestionPopUp(final ArrayList<SuggestionSku> skuOptions, final String uid) {
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

        final TextView addNew = (TextView) dialogView.findViewById(R.id.tv_add_new);
        final LinearLayout addNewLayout = (LinearLayout) dialogView.findViewById(R.id.ll_add_new_item);

        addNew.setVisibility(View.VISIBLE);
        addNewLayout.setVisibility(View.GONE);

        final EditText newItem = (EditText) addNewLayout.findViewById(R.id.et_add_item);
        final TextView newItemError = (TextView) addNewLayout.findViewById(R.id.tv_error_add_item);
        AppButton add = (AppButton) addNewLayout.findViewById(R.id.btn_yes);
        AppButtonGreyed cancel = (AppButtonGreyed) addNewLayout.findViewById(R.id.btn_no);

        final RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.rv_list);

        final TextView noData = (TextView) dialogView.findViewById(R.id.tv_no_data);
        noData.setVisibility(View.GONE);
        OrderSKUAdapter mAdapter = null;
        if (skuOptions != null && skuOptions.size() > 0) {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            mAdapter = new OrderSKUAdapter(skuOptions, this, uid, true);
            recyclerView.setAdapter(mAdapter);
        } else {
            noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNew.setVisibility(View.GONE);
                addNewLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                noData.setVisibility(View.GONE);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideKeyboard(OrderDetailsActivity.this, addNew);
                newItemError.setVisibility(View.GONE);
                String newItemText = newItem.getText().toString();
                if (!Utility.isEmpty(newItemText)) {
                    onSKUSelectedNewItem(newItemText, uid);
                } else {
                    newItemError.setVisibility(View.VISIBLE);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utility.hideKeyboard(OrderDetailsActivity.this, addNew);
                newItemError.setVisibility(View.GONE);
                addNewLayout.setVisibility(View.GONE);

                addNew.setVisibility(View.VISIBLE);

                if (skuOptions != null && skuOptions.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                } else
                    noData.setVisibility(View.GONE);
            }
        });

        TextView headerTitle = (TextView) dialogView.findViewById(R.id.header);
        headerTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.hideKeyboard(OrderDetailsActivity.this, addNew);
                mSKUDialog.dismiss();
            }
        });

        mSKUDialog.show();
    }

    private void invokeSkuPopUp(ArrayList<OrderItem.OrderSKU> skuList, String uid) {
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
            mAdapter = new OrderSKUAdapter(skuList, this, uid);
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

    private void invokeQuantityPopUp(final String uid, final String quantity, final String setQuantity) {
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

        ArrayList<String> mDisplayList = new ArrayList<>();


        int maxCount = 20;
        if (!Utility.isEmpty(quantity)) {
            maxCount = Integer.parseInt(quantity);
        }

        for (int i = 1; i <= maxCount; i++) {
            mDisplayList.add(i + " Nos.");
        }

        RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.rv_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        DeliveryDistanceAdapter adapter = new DeliveryDistanceAdapter(mDisplayList, new ItemSelectedInterface() {
            @Override
            public void onItemSelected(Object object) {

                ArrayList<OrderItem> itemList = orderDetails.getOrderItems();
                for (OrderItem item : itemList) {
                    if (item.getUid().equalsIgnoreCase(uid)) {

                        String quantitySelected = (String) object;
                        quantitySelected = quantitySelected.substring(0, quantitySelected.length() - 5);
                        if (setQuantity.equalsIgnoreCase(quantitySelected))
                            item.setUpdatedQuantityCount(null);
                        else
                            item.setUpdatedQuantityCount(quantitySelected);
                    }
                }

                if (mAdapter != null) {
                    mAdapter.refreshEvents(itemList);
                }

                if (mSKUDialog != null)
                    mSKUDialog.dismiss();

            }
        });
        recyclerView.setAdapter(adapter);

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
    public void onSKUSelected(OrderItem.OrderSKU sku, String uid) {

        ArrayList<OrderItem> itemList = orderDetails.getOrderItems();
        for (OrderItem item : itemList) {
            if (item.getUid().equalsIgnoreCase(uid)) {
                if (item.getSuggestion() != null && item.getSuggestion().getSuggestionStatus() == Constants.SUGGESTION_STATUS_EXISTING) {
                    Suggestion suggestion = item.getSuggestion();
                    suggestion.setMeasurementId(sku.getSkuId());
                    suggestion.setMeasuremenValue(sku.getSkuMeasurementValue() + " " + sku.getSkuMeasurementAcronym());
                    suggestion.setSuggestionPrice(sku.getSkuMrp());
                    item.setSuggestion(suggestion);
                } else {
                    item.setUpdatedSKUMeasurement(sku);
                    item.setUpdatedPrice(sku.getSkuMrp());
                }
            }
        }

        if (mAdapter != null) {
            mAdapter.refreshEvents(itemList);
        }

        if (mSKUDialog != null)
            mSKUDialog.dismiss();
    }

    @Override
    public void onSKUSelected(SuggestionSku sku, String uid) {
        ArrayList<OrderItem> itemList = orderDetails.getOrderItems();
        Suggestion suggestion;
        for (OrderItem item : itemList) {
            if (item.getUid().equalsIgnoreCase(uid)) {
                if (item.getSuggestion() != null)
                    suggestion = item.getSuggestion();
                else
                    suggestion = new Suggestion();

                suggestion.setItemName(sku.getTitle());
                suggestion.setItemId(sku.getId());
                suggestion.setSuggestionSku(sku);
                suggestion.setSuggestionStatus(Constants.SUGGESTION_STATUS_EXISTING);

                OrderItem.OrderSKU measurement = sku.getMeasurement().get(0);
                suggestion.setMeasurementId(measurement.getSkuId());
                suggestion.setMeasuremenValue(measurement.getSkuMeasurementValue() + " " + measurement.getSkuMeasurementAcronym());
                suggestion.setSuggestionPrice(measurement.getSkuMrp());
                item.setSuggestion(suggestion);
            }
        }

        if (mAdapter != null) {
            mAdapter.refreshEvents(itemList);
        }

        if (mSKUDialog != null)
            mSKUDialog.dismiss();
    }

    public void onSKUSelectedNewItem(String sku, String uid) {
        ArrayList<OrderItem> itemList = orderDetails.getOrderItems();
        Suggestion suggestion;
        for (OrderItem item : itemList) {
            if (item.getUid().equalsIgnoreCase(uid)) {
                if (item.getSuggestion() != null)
                    suggestion = item.getSuggestion();
                else
                    suggestion = new Suggestion();

                suggestion.setItemName(sku);
                suggestion.setSuggestionStatus(Constants.SUGGESTION_STATUS_NEW);
                suggestion.setItemId(null);
                suggestion.setMeasurementId(null);
                item.setSuggestion(suggestion);
            }
        }

        if (mAdapter != null) {
            mAdapter.refreshEvents(itemList);
        }

        if (mSKUDialog != null)
            mSKUDialog.dismiss();
    }
}
