package com.binbill.seller.AssistedService;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.Constants;
import com.binbill.seller.R;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.nex3z.flowlayout.FlowLayout;
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

/**
 * Created by shruti.vig on 9/2/18.
 */

public class AssistedServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public interface CardInteractionListener {
        void onDeleteAssistedService(int position);

        void addAdditionalService(int position);

        void onEditServiceCard(int position);

        void onShowReviews(int position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mList;
                } else {

                    ArrayList<AssistedUserModel> filteredList = new ArrayList<>();

                    for (AssistedUserModel listItem : mList) {
                        if (!Utility.isEmpty(listItem.getName()) && listItem.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(listItem);
                        }

                        if (!Utility.isEmpty(listItem.getMobile()) && listItem.getMobile().contains(charString)) {
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
                mFilteredList = (ArrayList<AssistedUserModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class AssistedServiceHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mUserName, mReviews;
        protected ImageView userImage, mCall, mEdit, mDelete, mShare;
        protected AppCompatRatingBar mRating;
        protected FlowLayout mTagLayout;
        protected TextView mAddService;

        public AssistedServiceHolder(View view) {
            super(view);
            mRootCard = view;
            mTagLayout = (FlowLayout) view.findViewById(R.id.fl_tag_layout);
            mUserName = (TextView) view.findViewById(R.id.tv_name);
            mReviews = (TextView) view.findViewById(R.id.tv_reviews);
            userImage = (ImageView) view.findViewById(R.id.iv_user_image);
            mRating = (AppCompatRatingBar) view.findViewById(R.id.rb_rating);
            mCall = (ImageView) view.findViewById(R.id.iv_call);
            mEdit = (ImageView) view.findViewById(R.id.iv_edit);
            mDelete = (ImageView) view.findViewById(R.id.iv_delete);
            mShare = (ImageView) view.findViewById(R.id.iv_share);
            mAddService = (TextView) view.findViewById(R.id.iv_add_additional_service);

        }
    }

    private ArrayList<AssistedUserModel> mList, mFilteredList;
    private AssistedServiceAdapter.CardInteractionListener mListener;

    public AssistedServiceAdapter(ArrayList<AssistedUserModel> list, AssistedServiceAdapter.CardInteractionListener context) {
        this.mList = list;
        this.mListener = context;
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
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_user_assisted, parent, false);
        return new AssistedServiceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final AssistedServiceHolder userHolder = (AssistedServiceHolder) holder;
        final AssistedUserModel model = mFilteredList.get(position);

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

        if (userHolder.mTagLayout.getChildCount() > 0)
            userHolder.mTagLayout.removeAllViews();
        ArrayList<AssistedUserModel.ServiceType> serviceTypes = model.getServiceTypes();
        if (serviceTypes != null) {
            for (AssistedUserModel.ServiceType serviceType : serviceTypes) {
                LayoutInflater inflater = LayoutInflater.from(userHolder.mUserName.getContext());
                TextView inflatedLayout = (TextView) inflater.inflate(R.layout.item_tag_view, null, false);
                if (serviceType.getPrice() != null) {
                    AssistedUserModel.Price basePrice = serviceType.getPrice().get(0);
                    if (serviceType.getPrice().size() > 1) {
                        AssistedUserModel.Price overTime = serviceType.getPrice().get(1);
                        inflatedLayout.setText(serviceType.getServiceType() + " ( Rs " + basePrice.getValue() + ", " + overTime.getValue() + " )");
                    } else {
                        inflatedLayout.setText(serviceType.getServiceType() + " ( Rs " + basePrice.getValue() + " )");
                    }
                }
                userHolder.mTagLayout.addView(inflatedLayout);
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
            picasso.load(Constants.BASE_URL + "assisted/" + model.getId() + "/profile")
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

        userHolder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onEditServiceCard(position);

            }
        });

        userHolder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListener != null)
                    mListener.onDeleteAssistedService(position);
            }
        });

        userHolder.mAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.addAdditionalService(position);
            }
        });
    }
}

