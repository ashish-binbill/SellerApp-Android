package com.binbill.seller.Order;

import android.content.Context;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.Model.UserModel;
import com.binbill.seller.R;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by shruti.vig on 9/5/18.
 */

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean loadMore = false;

    public interface OrderSelectedInterface {
        void onOrderSelected(int pos);
    }

    public static class OrderHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mUserName, mAddress, mStatus, mItemCount, mDate, mUnreadCount;
        protected ImageView userImage, mStatusColor, mServiceType, mPaymentMode;
        protected RelativeLayout userImageLayout;
        protected FrameLayout mIconChat;

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
            mServiceType = (ImageView) view.findViewById(R.id.iv_service);
            userImageLayout = (RelativeLayout) view.findViewById(R.id.rl_user_image);
            mIconChat = (FrameLayout) view.findViewById(R.id.fl_icon_chat);
            mUnreadCount = (TextView) view.findViewById(R.id.tv_unread_message);
            mPaymentMode = (ImageView) view.findViewById(R.id.header_quantity);
        }
    }

    private ArrayList<Order> mList;
    private OrderSelectedInterface listener;

    public OrderAdapter(ArrayList<Order> list, OrderSelectedInterface listObject) {
        this.mList = list;
        this.listener = listObject;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mShimmerFrameLayout;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            mShimmerFrameLayout = (LinearLayout) itemView.findViewById(R.id.shimmer_view_container);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == Constants.TYPE_ORDER) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.row_order_item, parent, false);
            return new OrderHolder(itemView);
        } else if (viewType == Constants.TYPE_LOADING) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.layout_shimmer_holder, parent, false);
            return new LoadingViewHolder(itemView);
        } else
            return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (getItemViewType(position) == Constants.TYPE_ORDER) {
            final OrderHolder orderHolder = (OrderHolder) holder;
            final Order model = mList.get(position);

            UserModel userModel = model.getUser();

            Calendar calendar = Calendar.getInstance();

            Calendar last7days = Calendar.getInstance();
            last7days.add(Calendar.DAY_OF_YEAR, -10);

            List<Message> messageList =
                    new MobiComConversationService(orderHolder.mUserName.getContext()).getMessages("user_" + model.getUserId(), last7days.getTimeInMillis(), calendar.getTimeInMillis());
            int contactUnreadCount =
                    new MessageDatabaseService(orderHolder.mUserName.getContext()).getUnreadMessageCountForContact("user_" + model.getUserId());
            if (messageList != null && messageList.size() > 0) {
                orderHolder.mIconChat.setVisibility(View.VISIBLE);

                if (contactUnreadCount > 0) {
                    orderHolder.mUnreadCount.setVisibility(View.VISIBLE);
                    orderHolder.mUnreadCount.setText(contactUnreadCount + "");
                } else
                    orderHolder.mUnreadCount.setVisibility(View.GONE);

                orderHolder.mIconChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(orderHolder.mIconChat.getContext(), ConversationActivity.class);
                        intent.putExtra(ConversationUIService.USER_ID, "user_" + model.getUserId());
                        intent.putExtra(ConversationUIService.TAKE_ORDER, true); //Skip chat list for showing on back press
                        orderHolder.mIconChat.getContext().startActivity(intent);
                    }
                });
            } else
                orderHolder.mIconChat.setVisibility(View.GONE);


            if (userModel != null) {

                if (!Utility.isEmpty(userModel.getUserName()))
                    orderHolder.mUserName.setText(userModel.getUserName());
                else if (!Utility.isEmpty(userModel.getUserEmail()))
                    orderHolder.mUserName.setText(userModel.getUserEmail());
                else
                    orderHolder.mUserName.setText(Utility.getMaskedNumber(userModel.getUserMobile()));
            }

            orderHolder.mAddress.setText(model.getAddress());


            String itemCount = model.getItemCount();
            if (itemCount != null)
                orderHolder.mItemCount.setText(itemCount + " Items");
            else
                orderHolder.mItemCount.setText("0 Items");


            if (!Utility.isEmpty(model.getTotalAmount()) && !model.getTotalAmount().equalsIgnoreCase("0")) {
                String item = (String) orderHolder.mItemCount.getText();
                String rupee = orderHolder.mItemCount.getContext().getString(R.string.rupee_sign);
                orderHolder.mItemCount.setText(item + " | " + rupee + " " + model.getTotalAmount());
            }
            orderHolder.mDate.setText(Utility.getFormattedDate(9, model.getOrderCreationDate(), 0));

            orderHolder.mPaymentMode.setVisibility(View.GONE);
            /**
             * Status
             */
            switch (model.getOrderStatus()) {
                case Constants.STATUS_NEW_ORDER:
                    ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_light_blue));
                    if (model.isModified())
                        orderHolder.mStatus.setText(orderHolder.mStatusColor.getContext().getString(R.string.waiting_for_approval));
                    else
                        orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.new_order));
                    break;
                case Constants.STATUS_COMPLETE:
                    ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_green));
                    orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.order_complete));

                    /**
                     * Payment status
                     */
                    if (model.getOrderStatus() == Constants.STATUS_COMPLETE) {
                        if (model.getPaymentModeId() > 0) {
                            Context context = orderHolder.mPaymentMode.getContext();
                            switch (model.getPaymentModeId()) {
                                case Constants.PAYMENT_MODE_CASH:
                                    orderHolder.mPaymentMode.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.paid_offline_watermark));
                                    orderHolder.mPaymentMode.setVisibility(View.VISIBLE);
                                    orderHolder.mIconChat.setVisibility(View.GONE);
                                    break;
                                case Constants.PAYMENT_MODE_ONLINE:
                                    orderHolder.mPaymentMode.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.paid_online_watermark));
                                    orderHolder.mPaymentMode.setVisibility(View.VISIBLE);
                                    orderHolder.mIconChat.setVisibility(View.GONE);
                                    break;
                                case Constants.PAYMENT_MODE_CREDIT:
                                    orderHolder.mPaymentMode.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.paid_credit_watermark));
                                    orderHolder.mPaymentMode.setVisibility(View.VISIBLE);
                                    orderHolder.mIconChat.setVisibility(View.GONE);
                                    break;
                            }
                        }
                    }
                    break;
                case Constants.STATUS_APPROVED:
                    ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_yellow));
                    if (model.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE))
                        orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.provider_accepted));
                    else {
                        orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.in_progress));
                    }
                    break;
                case Constants.STATUS_CANCEL:
                    ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_red));
                    orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.order_cancelled));
                    break;
                case Constants.STATUS_AUTO_CANCEL:
                case Constants.STATUS_AUTO_EXPIRED:
                    ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_lighter_blue));
                    orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.not_responded));
                    break;
                case Constants.STATUS_REJECTED:
                    ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_orange));
                    orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.order_rejected));
                    break;
                case Constants.STATUS_OUT_FOR_DELIVERY:
                    ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_blue));
                    if (model.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_SERVICE))
                        orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.provider_assigned));
                    else {
                        if (model.isCollectAtStore())
                            orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.order_ready));
                        else
                            orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.out_for_delivery));
                    }
                    break;
                case Constants.STATUS_JOB_STARTED:
                    ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_blue));
                    orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.service_started));
                    break;
                case Constants.STATUS_JOB_ENDED:
                    ViewCompat.setBackgroundTintList(orderHolder.mStatusColor, ContextCompat.getColorStateList(orderHolder.mStatusColor.getContext(), R.color.status_blue));
                    orderHolder.mStatus.setText(orderHolder.mStatus.getContext().getString(R.string.service_completed));
                    break;
            }

            if (model.getOrderType().equalsIgnoreCase(Constants.ORDER_TYPE_FMCG)) {
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

                    Picasso.get().load(Constants.BASE_URL + "customer/" + model.getUserId() + "/images")
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
                } else {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT
                    );

                    int margins = Utility.convertDPtoPx(orderHolder.userImage.getContext(), 15);
                    params.setMargins(margins, margins, margins, margins);
                    orderHolder.userImage.setLayoutParams(params);

                    orderHolder.userImage.setImageDrawable(ContextCompat.getDrawable(orderHolder.userImage.getContext(), R.drawable.ic_user));
                }

//            orderHolder.mItemCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

                orderHolder.mServiceType.setVisibility(View.GONE);
                orderHolder.userImageLayout.setVisibility(View.VISIBLE);
            } else {
                orderHolder.userImageLayout.setVisibility(View.GONE);

                ArrayList<OrderItem> orderItems = model.getOrderItems();
                Picasso.get().load(Constants.BASE_URL + "assisted/" + orderItems.get(0).getServiceTypeId() + "/images")
                        .config(Bitmap.Config.RGB_565)
                        .into(orderHolder.mServiceType);
                orderHolder.mServiceType.setVisibility(View.VISIBLE);

//            orderHolder.mItemCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                orderHolder.mItemCount.setText(orderItems.get(0).getServiceName());
            }

            orderHolder.mRootCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onOrderSelected(position);
                }
            });
        } else if (getItemViewType(position) == Constants.TYPE_LOADING) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.mShimmerFrameLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        final Order orderList = mList.get(position);
        if (isLoadMore() && position == mList.size() - 1)
            return Constants.TYPE_LOADING;
        else
            return Constants.TYPE_ORDER;
    }


    public void setLoadMore(boolean load) {
        this.loadMore = load;
    }

    public boolean isLoadMore() {
        return loadMore;
    }
}


