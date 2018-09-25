package com.binbill.seller.CustomViews;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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
import com.binbill.seller.Verification.VerificationAdapter;
import com.binbill.seller.Verification.VerificationModel;
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
 * Created by shruti.vig on 9/9/18.
 */

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class ReviewHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mReview;
        protected AppCompatRatingBar mRating;
        protected  TextView mUser;

        public ReviewHolder(View view) {
            super(view);
            mRootCard = view;
            mReview = (TextView) view.findViewById(R.id.tv_review_text);
            mRating = (AppCompatRatingBar) view.findViewById(R.id.rb_rating);
            mUser = (TextView) view.findViewById(R.id.tv_review_user);
        }
    }

    private ArrayList<AssistedUserModel.Review> mList;

    public ReviewAdapter(ArrayList<AssistedUserModel.Review> list) {
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
                inflate(R.layout.row_review, parent, false);
        return new ReviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final ReviewHolder reviewHolder = (ReviewHolder) holder;
        final AssistedUserModel.Review model = mList.get(position);

        reviewHolder.mReview.setText(model.getFeedback());
        reviewHolder.mUser.setText("From " + model.getRating());
    }
}
