package com.binbill.seller.Offers;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.binbill.seller.Constants;
import com.binbill.seller.R;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.github.chrisbanes.photoview.PhotoView;
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
 * Created by shruti.vig on 8/29/18.
 */

public class OfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OfferManipulationListener {
        void onOfferManupulation(int position, String type);
    }

    public static class OfferHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mTitle, mDescription, mExpiry;
        protected ImageView addUser, editOffer, deleteOffer;
        protected PhotoView mImage;

        public OfferHolder(View view) {
            super(view);
            mRootCard = view;
            mTitle = (TextView) view.findViewById(R.id.tv_offer_name);
            mDescription = (TextView) view.findViewById(R.id.tv_offer_description);
            mExpiry = (TextView) view.findViewById(R.id.tv_offer_expiry);
            mImage = (PhotoView) view.findViewById(R.id.iv_offer);
            addUser = (ImageView) view.findViewById(R.id.iv_offer_add_user);
            editOffer = (ImageView) view.findViewById(R.id.iv_offer_edit);
            deleteOffer = (ImageView) view.findViewById(R.id.iv_offer_delete);
        }
    }

    public static class OfferBarCodeHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mTitle, mMrp, mDiscount, mExpiry;
        protected ImageView editOffer, deleteOffer;
        protected ImageView mImage;

        public OfferBarCodeHolder(View view) {
            super(view);
            mRootCard = view;
            mTitle = (TextView) view.findViewById(R.id.tv_offer_name);
            mMrp = (TextView) view.findViewById(R.id.tv_offer_mrp);
            mDiscount = (TextView) view.findViewById(R.id.tv_discount);
            mExpiry = (TextView) view.findViewById(R.id.tv_offer_expiry);
            mImage = (ImageView) view.findViewById(R.id.iv_offer);
            editOffer = (ImageView) view.findViewById(R.id.iv_offer_edit);
            deleteOffer = (ImageView) view.findViewById(R.id.iv_offer_delete);
        }
    }

    private ArrayList<OfferItem> mList;
    private final OfferManipulationListener mListener;
    private int offerType;

    public OfferAdapter(OfferManipulationListener listener, ArrayList<OfferItem> list, int type) {
        this.mList = list;
        this.mListener = listener;
        this.offerType = type;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (offerType == Constants.TYPE_NORMAL_OFFER) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.row_offer_item, parent, false);
            return new OfferHolder(itemView);
        } else if (offerType == Constants.TYPE_BARCODE_OFFER) {
            itemView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.row_barcode_item, parent, false);
            return new OfferBarCodeHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (offerType == Constants.TYPE_NORMAL_OFFER)
            onBindNormalOfferViewHolder(holder, position);
        else if (offerType == Constants.TYPE_BARCODE_OFFER)
            onBindBarCodeOfferViewHolder(holder, position);
    }

    public void onBindNormalOfferViewHolder(RecyclerView.ViewHolder holder, final int position) {


        final OfferHolder offerHolder = (OfferHolder) holder;

        final OfferItem model = mList.get(position);

        offerHolder.mTitle.setText(model.getOfferTitle());
        offerHolder.mDescription.setText(model.getOfferDescription());
        offerHolder.mExpiry.setText("Expires on: " + Utility.getFormattedDate(14, model.getOfferEndDate(), 0));

        final String authToken = SharedPref.getString(offerHolder.mDescription.getContext(), SharedPref.AUTH_TOKEN);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        return response.request().newBuilder()
                                .header("Authorization", authToken)
                                .build();
                    }
                }).build();

        Picasso picasso = new Picasso.Builder(offerHolder.mDescription.getContext())
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
        picasso.load(Constants.BASE_URL + "offer/" + model.getOfferId() + "/images/0")
                .config(Bitmap.Config.RGB_565)
                .fit()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(ContextCompat.getDrawable(offerHolder.mDescription.getContext(), R.drawable.ic_default_offer_image))
                .into(offerHolder.mImage);

        offerHolder.addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onOfferManupulation(position, Constants.ADD_USER_FOR_OFFER);
            }
        });
        offerHolder.editOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onOfferManupulation(position, Constants.EDIT_OFFER);
            }
        });
        offerHolder.deleteOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onOfferManupulation(position, Constants.DELETE_OFFER);
            }
        });
    }

    public void onBindBarCodeOfferViewHolder(RecyclerView.ViewHolder holder, final int position) {


        final OfferBarCodeHolder offerHolder = (OfferBarCodeHolder) holder;

        final OfferItem.OfferSku model = mList.get(position).getSku();
        final OfferItem offerItem = mList.get(position);

        offerHolder.mTitle.setText(model.getSkuTitle());
        offerHolder.mMrp.setText("MRP " + model.getMrp());
        offerHolder.mDiscount.setText(model.getOfferDiscount() + "% Discount");
        offerHolder.mExpiry.setText("Expires on: " + Utility.getFormattedDate(14, offerItem.getOfferEndDate(), 0));


        String imageUrl = Constants.BASE_URL + "skus/" + model.getSkuId() + "/measurements/" + model.getSkuMeasurementId() + "/images";
        Picasso.get()
                .load(imageUrl)
                .config(Bitmap.Config.RGB_565)
                .placeholder(ContextCompat.getDrawable(offerHolder.mImage.getContext(), R.drawable.ic_placeholder_sku))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(offerHolder.mImage);

        offerHolder.editOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onOfferManupulation(position, Constants.EDIT_OFFER);
            }
        });
        offerHolder.deleteOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onOfferManupulation(position, Constants.DELETE_OFFER);
            }
        });
    }
}
