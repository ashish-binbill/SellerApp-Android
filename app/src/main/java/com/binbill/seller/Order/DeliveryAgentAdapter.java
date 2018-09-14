package com.binbill.seller.Order;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.AssistedService.AssistedUserModel;
import com.binbill.seller.Constants;
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
 * Created by shruti.vig on 9/8/18.
 */

public class DeliveryAgentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface CardInteractionListener {

        void onShowReviews(int position);

        void onDeleteAgent(int position);

        void onEditAgent(int position);
    }

    public static class DeliveryAgentHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mUserName, mReviews, statusText;
        protected ImageView userImage, statusColor, editAgent, deleteAgent;
        protected AppCompatRatingBar mRating;
        protected RelativeLayout mImageLayout, mSelectedCardLayout;
        protected LinearLayout mInfoLayout, mActionLayout;
        protected ImageView mCall;

        public DeliveryAgentHolder(View view) {
            super(view);
            mRootCard = view;
            mUserName = (TextView) view.findViewById(R.id.tv_user_name);
            statusText = (TextView) view.findViewById(R.id.iv_assign_count);
            statusColor = (ImageView) view.findViewById(R.id.iv_assign_color);
            editAgent = (ImageView) view.findViewById(R.id.iv_edit);
            deleteAgent = (ImageView) view.findViewById(R.id.iv_delete);
            mReviews = (TextView) view.findViewById(R.id.tv_reviews);
            userImage = (ImageView) view.findViewById(R.id.iv_user_image);
            mRating = (AppCompatRatingBar) view.findViewById(R.id.rb_rating);
            mImageLayout = (RelativeLayout) view.findViewById(R.id.rl_image_layout);
            mInfoLayout = (LinearLayout) view.findViewById(R.id.ll_info_layout);
            mSelectedCardLayout = (RelativeLayout) view.findViewById(R.id.card_selected);
            mCall = (ImageView) view.findViewById(R.id.iv_call);
            mActionLayout = (LinearLayout) view.findViewById(R.id.user_actions);

        }
    }

    private ArrayList<DeliveryModel> mList;
    private boolean mShowSelection;
    private CardInteractionListener mListener;

    public DeliveryAgentAdapter(ArrayList<DeliveryModel> list, boolean showSelection, CardInteractionListener context) {
        this.mList = list;
        this.mListener = context;
        this.mShowSelection = showSelection;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_delivery_agent, parent, false);
        return new DeliveryAgentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final DeliveryAgentHolder userHolder = (DeliveryAgentHolder) holder;
        final DeliveryModel model = mList.get(position);

        userHolder.mUserName.setText(model.getName());

        float rating = 0;
        if (!Utility.isEmpty(model.getRating()))
            rating = Float.parseFloat(model.getRating());
        userHolder.mRating.setRating(rating);

        ArrayList<AssistedUserModel.Review> userReviews = model.getReviews();
        if (userReviews != null)
            userHolder.mReviews.setText(userHolder.mReviews.getContext().getString(R.string.reviews, String.valueOf(userReviews.size())));
        else
            userHolder.mReviews.setText(userHolder.mReviews.getContext().getString(R.string.reviews, "0"));

        if (!Utility.isEmpty(model.getOrderCount()) && Integer.valueOf(model.getOrderCount()) > 0) {
            ViewCompat.setBackgroundTintList(userHolder.statusColor, ContextCompat.getColorStateList(userHolder.statusColor.getContext(), R.color.status_red));
            userHolder.statusText.setText(userHolder.statusColor.getContext().getString(R.string.tasks_already_assigned, model.getOrderCount()));
            userHolder.statusText.setTextColor(ContextCompat.getColor(userHolder.statusColor.getContext(), R.color.status_red));
        } else {
            ViewCompat.setBackgroundTintList(userHolder.statusColor, ContextCompat.getColorStateList(userHolder.statusColor.getContext(), R.color.status_green));
            userHolder.statusText.setText(userHolder.statusColor.getContext().getString(R.string.no_task_assigned));
            userHolder.statusText.setTextColor(ContextCompat.getColor(userHolder.statusColor.getContext(), R.color.status_green));
        }

        if (mShowSelection)
            userHolder.mActionLayout.setVisibility(View.GONE);
        else
            userHolder.mActionLayout.setVisibility(View.VISIBLE);

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

        if (model.isSelected())
            userHolder.mSelectedCardLayout.setVisibility(View.VISIBLE);
        else
            userHolder.mSelectedCardLayout.setVisibility(View.GONE);

        if (mShowSelection) {

            userHolder.mImageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetSelection();
                    model.setSelected(true);
                    notifyDataSetChanged();
                }
            });

            userHolder.mInfoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetSelection();
                    model.setSelected(true);
                    notifyDataSetChanged();
                }
            });
        }

        if (userReviews != null && userReviews.size() > 0) {
            userHolder.mReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null)
                        mListener.onShowReviews(position);
                }
            });
        }

        if (userReviews != null && userReviews.size() > 0) {
            userHolder.mRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null)
                        mListener.onShowReviews(position);
                }
            });
        }

        userHolder.mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.getMobile() != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getMobile()));
                    userHolder.mCall.getContext().startActivity(intent);
                }
            }
        });

        userHolder.editAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onEditAgent(position);

            }
        });

        userHolder.deleteAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListener != null)
                    mListener.onDeleteAgent(position);
            }
        });

    }

    private void resetSelection() {
        for (DeliveryModel model : mList)
            model.setSelected(false);
    }

    public ArrayList<DeliveryModel> getUpdatedList() {
        return mList;
    }
}

