package com.binbill.seller.Offers;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Model.UserModel;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by shruti.vig on 8/30/18.
 */

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private boolean loadMore = false;
    private ArrayList<UserModel> mList, mFilteredList;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mList;
                } else {

                    ArrayList<UserModel> filteredList = new ArrayList<>();

                    for (UserModel listItem : mList) {
                        if (!Utility.isEmpty(listItem.getUserName()) && listItem.getUserName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(listItem);
                        }

                        if (!Utility.isEmpty(listItem.getUserMobile()) && listItem.getUserMobile().contains(charString)) {
                            filteredList.add(listItem);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<UserModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface CardInteractionListener {
        void onCardInteraction();

        void onCardClicked(int position);

        void onAddPoints(int position);

        void onAddCredits(int position);

        void onCustomerAdded(int position);
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mUserName, mUserTransactions, mUserAddress;
        protected ImageView userImage;
        protected CardView mCard;
        protected RelativeLayout mCardOverlay;
        protected AppButton mAddCustomer;

        public UserHolder(View view) {
            super(view);
            mRootCard = view;
            mCard = (CardView) view.findViewById(R.id.cv_root);
            mUserName = (TextView) view.findViewById(R.id.tv_user_name);
            mUserTransactions = (TextView) view.findViewById(R.id.tv_user_transactions);
            mUserAddress = (TextView) view.findViewById(R.id.tv_address);
            userImage = (ImageView) view.findViewById(R.id.iv_user_image);
            mCardOverlay = (RelativeLayout) view.findViewById(R.id.card_selected);
            mAddCustomer = (AppButton) view.findViewById(R.id.add_customer);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mShimmerFrameLayout;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            mShimmerFrameLayout = (LinearLayout) itemView.findViewById(R.id.shimmer_view_container);
        }
    }


    public static class MyCustomerHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected CardView mCard;
        protected TextView mUserName, mDate, mUserTransactions, mUserCredit, mUserAddress, mUserPoints, mAddCredits, mAddPoints, mDistance, mStatus;
        protected ImageView userImage;

        public MyCustomerHolder(View view) {
            super(view);
            mRootCard = view;
            mCard = (CardView) view.findViewById(R.id.cv_root);
            mUserName = (TextView) view.findViewById(R.id.tv_user_name);
            userImage = (ImageView) view.findViewById(R.id.iv_user_image);
            mUserCredit = (TextView) view.findViewById(R.id.tv_user_credit);
            mUserPoints = (TextView) view.findViewById(R.id.tv_user_points);
            mUserTransactions = (TextView) view.findViewById(R.id.tv_user_txn);
            mUserAddress = (TextView) view.findViewById(R.id.tv_address);
            mAddCredits = (TextView) view.findViewById(R.id.tv_add_credit);
            mAddPoints = (TextView) view.findViewById(R.id.tv_add_points);
            mDistance = (TextView) view.findViewById(R.id.tv_distance);
            mStatus = (TextView) view.findViewById(R.id.tv_user_status);
            mDate = (TextView) view.findViewById(R.id.tv_date);

        }
    }

    private CardInteractionListener mListener;
    private final boolean linkUser;
    private int type;

    public UserAdapter(int featureType, ArrayList<UserModel> list, CardInteractionListener context, boolean isLinkUserFlow) {
        this.type = featureType;
        this.mList = list;
        this.mListener = context;
        this.linkUser = isLinkUserFlow;
        this.mFilteredList = list;
    }

    @Override
    public int getItemCount() {
        if (mFilteredList == null)
            return 0;
        return mFilteredList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (type == Constants.OFFER || type == Constants.ADD_CUSTOMER) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.row_user_model, parent, false);
            return new UserHolder(itemView);
        } else if (type == Constants.MY_CUSTOMER) {
            if (viewType == Constants.TYPE_DATA) {
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.row_user_model_my_customer, parent, false);
                return new MyCustomerHolder(itemView);
            } else if (viewType == Constants.TYPE_LOADING) {
                itemView = LayoutInflater.
                        from(parent.getContext()).
                        inflate(R.layout.layout_shimmer_holder, parent, false);
                return new UserAdapter.LoadingViewHolder(itemView);
            } else
                return null;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (type == Constants.OFFER || type == Constants.ADD_CUSTOMER) {
            onBindOfferHolder(holder, position);
        } else if (type == Constants.MY_CUSTOMER) {
            onBindMyCustomerHolder(holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        final UserModel modelList = mList.get(position);
        if (isLoadMore() && position == mList.size() - 1)
            return Constants.TYPE_LOADING;
        else
            return Constants.TYPE_DATA;
    }

    public void setLoadMore(boolean load) {
        this.loadMore = load;
    }

    public boolean isLoadMore() {
        return loadMore;
    }

    private void onBindMyCustomerHolder(RecyclerView.ViewHolder holder, final int position) {

        if (getItemViewType(position) == Constants.TYPE_ORDER) {
            final MyCustomerHolder userHolder = (MyCustomerHolder) holder;

            final UserModel model = mFilteredList.get(position);

            if (!Utility.isEmpty(model.getDateTimeOfUserCreation()))
                userHolder.mDate.setText(Utility.getFormattedDate(9, model.getDateTimeOfUserCreation(), 0));

            if (!Utility.isEmpty(model.getUserName()))
                userHolder.mUserName.setText(model.getUserName());
            else if (!Utility.isEmpty(model.getUserEmail()))
                userHolder.mUserName.setText(model.getUserEmail());
            else
                userHolder.mUserName.setText(Utility.getMaskedNumber(model.getUserMobile()));

            userHolder.mUserTransactions.setText(model.getTransactionCount());
            userHolder.mUserCredit.setText(userHolder.mUserCredit.getContext().getString(R.string.rupee_sign) + " " + model.getUserCredit());
            userHolder.mUserPoints.setText(model.getUserLoyalty());
            if (!Utility.isEmpty(model.getAddress())) {
                userHolder.mUserAddress.setText(model.getAddress());
                userHolder.mUserAddress.setVisibility(View.VISIBLE);
            } else
                userHolder.mUserAddress.setVisibility(View.GONE);

            if (!Utility.isEmpty(model.getUserDistance())) {
                userHolder.mDistance.setText(model.getUserDistance() + " " + model.getDistanceMetric());
                userHolder.mDistance.setVisibility(View.VISIBLE);

            } else
                userHolder.mDistance.setVisibility(View.GONE);

            if (model.getUserStatusType().equalsIgnoreCase(Constants.ACTIVE)) {
                userHolder.mStatus.setVisibility(View.VISIBLE);
            } else
                userHolder.mStatus.setVisibility(View.GONE);

            userHolder.mCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null)
                        mListener.onCardClicked(position);
                }
            });

            if (model.getUserImage() != null && !Utility.isEmpty(model.getUserImage())) {

                final String authToken = SharedPref.getString(userHolder.mCard.getContext(), SharedPref.AUTH_TOKEN);
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

                Picasso picasso = new Picasso.Builder(userHolder.mCard.getContext())
                        .downloader(new OkHttp3Downloader(okHttpClient))
                        .build();
                picasso.load(Constants.BASE_URL + "customer/" + model.getUserId() + "/images")
                        .config(Bitmap.Config.RGB_565)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(userHolder.userImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) userHolder.userImage.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(userHolder.userImage.getContext().getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
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
            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                );

                int margins = Utility.convertDPtoPx(userHolder.userImage.getContext(), 15);
                params.setMargins(margins, margins, margins, margins);
                userHolder.userImage.setLayoutParams(params);

                userHolder.userImage.setImageDrawable(ContextCompat.getDrawable(userHolder.userImage.getContext(), R.drawable.ic_user));
            }

            userHolder.mAddCredits.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null)
                        mListener.onAddCredits(position);
                }
            });

            userHolder.mAddPoints.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null)
                        mListener.onAddPoints(position);
                }
            });
        } else if (getItemViewType(position) == Constants.TYPE_LOADING) {
            UserAdapter.LoadingViewHolder loadingViewHolder = (UserAdapter.LoadingViewHolder) holder;
            loadingViewHolder.mShimmerFrameLayout.setVisibility(View.VISIBLE);
        }
    }

    public ArrayList<UserModel> getFilteredList() {
        return mFilteredList;
    }

    private void onBindOfferHolder(RecyclerView.ViewHolder holder, final int position) {

        final UserHolder userHolder = (UserHolder) holder;

        final UserModel model = mFilteredList.get(position);

        if (!Utility.isEmpty(model.getUserName()))
            userHolder.mUserName.setText(model.getUserName());
        else if (!Utility.isEmpty(model.getUserEmail()))
            userHolder.mUserName.setText(model.getUserEmail());
        else
            userHolder.mUserName.setText(Utility.getMaskedNumber(model.getUserMobile()));


        int transactionCount = 0;
        if (model.getTransactionCount() != null && !Utility.isEmpty(model.getTransactionCount()))
            transactionCount = Integer.parseInt(model.getTransactionCount());

        userHolder.mUserTransactions.setText(userHolder.userImage.getContext().getResources().getQuantityString(R.plurals.transaction, transactionCount, transactionCount));
        if (!Utility.isEmpty(model.getAddress())) {
            userHolder.mUserAddress.setText(model.getAddress());
            userHolder.mUserAddress.setVisibility(View.VISIBLE);
        } else
            userHolder.mUserAddress.setVisibility(View.GONE);

        if (model.getUserImage() != null && !Utility.isEmpty(model.getUserImage())) {

            final String authToken = SharedPref.getString(userHolder.mCard.getContext(), SharedPref.AUTH_TOKEN);
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

            Picasso picasso = new Picasso.Builder(userHolder.mCard.getContext())
                    .downloader(new OkHttp3Downloader(okHttpClient))
                    .build();
            picasso.load(Constants.BASE_URL + "customer/" + model.getUserId() + "/images")
                    .config(Bitmap.Config.RGB_565)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(userHolder.userImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) userHolder.userImage.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(userHolder.userImage.getContext().getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
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
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );

            int margins = Utility.convertDPtoPx(userHolder.userImage.getContext(), 15);
            params.setMargins(margins, margins, margins, margins);
            userHolder.userImage.setLayoutParams(params);

            userHolder.userImage.setImageDrawable(ContextCompat.getDrawable(userHolder.userImage.getContext(), R.drawable.ic_user));
        }

        if (linkUser) {
            if (model.isSelected()) {
                userHolder.mCardOverlay.setVisibility(View.VISIBLE);
            } else {
                userHolder.mCardOverlay.setVisibility(View.GONE);
            }

            userHolder.mCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (model.isSelected()) {
                        model.setSelected(false);
                        userHolder.mCardOverlay.setVisibility(View.GONE);
                    } else {
                        model.setSelected(true);
                        userHolder.mCardOverlay.setVisibility(View.VISIBLE);
                    }

                    if (mListener != null)
                        mListener.onCardInteraction();
                }
            });
        }

        if (model.isLinked()) {
            userHolder.mAddCustomer.setVisibility(View.GONE);
        } else
            userHolder.mAddCustomer.setVisibility(View.VISIBLE);

        userHolder.mAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new RetrofitHelper(userHolder.mAddCustomer.getContext()).setCreditLimitForUser(model.getUserId(), false, "0", new RetrofitHelper.RetrofitCallback() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.optBoolean("status")) {
                                userHolder.mAddCustomer.setVisibility(View.GONE);
                                if (mListener != null)
                                    mListener.onCustomerAdded(position);
                            }

                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void onErrorResponse() {

                    }

                });

            }
        });
    }
}

