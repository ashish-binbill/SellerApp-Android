package com.binbill.seller.Offers;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private final OfferManipulationListener mListener;
    private ArrayList<OfferItem> mList;
    private int offerType, offerSubType = -1;

    public OfferAdapter(OfferManipulationListener listener, ArrayList<OfferItem> list, int type) {
        this.mList = list;
        this.mListener = listener;
        this.offerType = type;
    }

    public OfferAdapter(OfferManipulationListener listener, ArrayList<OfferItem> list, int type, int subType) {
        this.mList = list;
        this.mListener = listener;
        this.offerType = type;
        this.offerSubType = subType;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (offerType == Constants.OFFER_TYPE_NEW_PRODUCT || offerType == Constants.OFFER_TYPE_DISCOUNTED || offerType == Constants.OFFER_TYPE_BOGO ||
                offerType == Constants.OFFER_TYPE_EXTRA) {
            itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.row_suggested_offer_item, parent, false);
            return new SuggestedOfferHolder(itemView);
        } else if (offerType == Constants.OFFER_TYPE_GENERAL) {
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
        if (offerType == Constants.OFFER_TYPE_GENERAL)
            onBindNormalOfferViewHolder(holder, position);
        else if (offerType == Constants.TYPE_BARCODE_OFFER)
            onBindBarCodeOfferViewHolder(holder, position);
        else if (offerType == Constants.OFFER_TYPE_NEW_PRODUCT || offerType == Constants.OFFER_TYPE_DISCOUNTED || offerType == Constants.OFFER_TYPE_BOGO ||
                offerType == Constants.OFFER_TYPE_EXTRA)
            onBindSuggestedOfferViewHolder(holder, position);
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

        String url = "/suggested/offers/" + model.getOfferId() + "/images";
        if (offerSubType > -1 && offerSubType == Constants.OFFER_TYPE_GENERAL) {
            url = Constants.BASE_URL + "offer/" + model.getOfferId() + "/images/0";
            offerHolder.mAddToOffer.setVisibility(View.INVISIBLE);
            offerHolder.mNeedThisItem.setVisibility(View.INVISIBLE);
        }

        Picasso picasso = new Picasso.Builder(offerHolder.mDescription.getContext())
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
        picasso.load(url)
                .config(Bitmap.Config.RGB_565)
                .fit()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .placeholder(ContextCompat.getDrawable(offerHolder.mDescription.getContext(), R.drawable.ic_default_offer_image))
                .into(offerHolder.mImage);

//        offerHolder.addUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mListener != null)
//                    mListener.onOfferManupulation(position, Constants.ADD_USER_FOR_OFFER);
//            }
//        });
//        offerHolder.editOffer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mListener != null)
//                    mListener.onOfferManupulation(position, Constants.EDIT_OFFER);
//            }
//        });

        if (model.isOfferAdded()) {
            offerHolder.mNeedThisItem.setVisibility(View.INVISIBLE);
            offerHolder.mAddToOffer.setVisibility(View.INVISIBLE);
            offerHolder.mOfferAlreadyAdded.setVisibility(View.VISIBLE);
        } else {
            offerHolder.mNeedThisItem.setVisibility(View.VISIBLE);
            offerHolder.mAddToOffer.setVisibility(View.VISIBLE);
            offerHolder.mOfferAlreadyAdded.setVisibility(View.GONE);
        }

        if (offerSubType > -1 && offerSubType == Constants.OFFER_TYPE_GENERAL) {
            offerHolder.mNeedThisItem.setVisibility(View.INVISIBLE);
            offerHolder.mAddToOffer.setVisibility(View.INVISIBLE);
            offerHolder.mOfferAlreadyAdded.setVisibility(View.GONE);
        }

        offerHolder.deleteOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onOfferManupulation(position, Constants.DELETE_OFFER);
            }
        });

        offerHolder.mAddToOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onOfferManupulation(position, Constants.ADD_OFFER_TO_SELLER);
            }
        });

        offerHolder.mNeedThisItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onOfferManupulation(position, Constants.NEED_THIS_ITEM);
            }
        });
    }

    public void onBindBarCodeOfferViewHolder(RecyclerView.ViewHolder holder, final int position) {


        final OfferBarCodeHolder offerHolder = (OfferBarCodeHolder) holder;

        final OfferItem.OfferSku model = mList.get(position).getSku();
        final OfferItem offerItem = mList.get(position);

        offerHolder.mTitle.setText(model.getSkuTitle());
        offerHolder.mMrp.setText("MRP " + model.getMrp());
        if (!Utility.isEmpty(offerItem.getMeasurementValue()) && !Utility.isEmpty(offerItem.getAcronym())) {
            offerHolder.mQuantity.setText("(" + offerItem.getMeasurementValue() + " " + offerItem.getAcronym() + ")");
            offerHolder.mQuantity.setVisibility(View.VISIBLE);
        } else
            offerHolder.mQuantity.setVisibility(View.GONE);

        try {
            double mrp = Double.parseDouble(model.getMrp());
            double discount = Double.parseDouble(offerItem.getOfferDiscount());

            if (discount > 0) {
                double offeredPrice = mrp - (mrp * discount / 100);

                offerHolder.mOfferedMrp.setText("PRICE " + Utility.showDoubleString(offeredPrice));
                offerHolder.mOfferedMrp.setVisibility(View.VISIBLE);

                offerHolder.mMrp.setPaintFlags(offerHolder.mMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                offerHolder.mOfferedMrp.setVisibility(View.GONE);
                offerHolder.mDiscount.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            offerHolder.mOfferedMrp.setVisibility(View.GONE);
        }
        offerHolder.mDiscount.setText(offerItem.getOfferDiscount() + "% Discount");
        offerHolder.mExpiry.setText("Expires on: " + Utility.getFormattedDate(14, offerItem.getOfferEndDate(), 1));


        if (offerSubType == Constants.OFFER_TYPE_BOGO || offerSubType == Constants.OFFER_TYPE_EXTRA) {
            offerHolder.editOffer.setVisibility(View.GONE);
            offerHolder.mExpiry.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ContextCompat.getDrawable(offerHolder.mExpiry.getContext(), R.drawable.ic_edit_orange), null);

            offerHolder.mEnterExpiry.setVisibility(View.GONE);
            offerHolder.mExpiry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    offerHolder.mExpiry.setText("Expires on: ");
                    offerHolder.mEnterExpiry.setVisibility(View.VISIBLE);
                    offerHolder.mExpiry.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            });

            offerHolder.mEnterExpiry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null)
                        mListener.onOfferManupulation(position, Constants.ADD_EXPIRY_IN_OFFER);
                }
            });
        }

        String imageUrl = Constants.BASE_URL + "skus/" + offerItem.getSkuId() + "/measurements/" + offerItem.getSkuMeasurementId() + "/images";
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

    public void onBindSuggestedOfferViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final SuggestedOfferHolder offerHolder = (SuggestedOfferHolder) holder;

        final OfferItem offerItem = mList.get(position);

        String rupee = offerHolder.mTitle.getContext().getString(R.string.rupee_sign);
        offerHolder.mTitle.setText(offerItem.getSkuTitle());
        offerHolder.mMRP.setText("MRP " + rupee + Utility.showDoubleString(offerItem.getMrp()));

        if (!Utility.isEmpty(offerItem.getMeasurementValue()) && !Utility.isEmpty(offerItem.getAcronym())) {
            offerHolder.mQuantity.setText("(" + offerItem.getMeasurementValue() + " " + offerItem.getAcronym() + ")");
            offerHolder.mQuantity.setVisibility(View.VISIBLE);
        } else
            offerHolder.mQuantity.setVisibility(View.GONE);

        if (offerType == Constants.OFFER_TYPE_DISCOUNTED) {

            try {
                double mrp = Double.parseDouble(offerItem.getMrp());
                double percentage = Double.parseDouble(offerItem.getOfferValue());

                double discountedPrice = mrp - (mrp * percentage / 100);

                offerHolder.mDiscount.setText("(" + Utility.showDoubleString(percentage) + "% Off)");
                offerHolder.mDiscount.setVisibility(View.VISIBLE);

                offerHolder.mDiscountedPrice.setText("PRICE " + rupee + Utility.showDoubleString(discountedPrice));
                offerHolder.mDiscountedPrice.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                offerHolder.mDiscount.setVisibility(View.GONE);
            }
            offerHolder.mStockLastTitle.setVisibility(View.VISIBLE);
            offerHolder.mDiscountLayout.setVisibility(View.VISIBLE);
        } else {
            offerHolder.mDiscountedPrice.setVisibility(View.GONE);
            offerHolder.mDiscount.setVisibility(View.GONE);
            offerHolder.mStockLastTitle.setVisibility(View.GONE);

            offerHolder.mDiscountLayout.setVisibility(View.GONE);
        }

        if (offerType == Constants.OFFER_TYPE_NEW_PRODUCT) {
            offerHolder.mExpiry.setVisibility(View.GONE);
        } else
            offerHolder.mExpiry.setVisibility(View.VISIBLE);

        offerHolder.mExpiry.setText("Expires on: " + Utility.getFormattedDate(14, offerItem.getOfferEndDate(), 1));

        String imageUrl = Constants.BASE_URL + "skus/" + offerItem.getSkuId() + "/measurements/" + offerItem.getSkuMeasurementId() + "/images";
        Picasso.get()
                .load(imageUrl)
                .config(Bitmap.Config.RGB_565)
                .placeholder(ContextCompat.getDrawable(offerHolder.mImage.getContext(), R.drawable.ic_placeholder_sku))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(offerHolder.mImage);

        offerHolder.deleteOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onOfferManupulation(position, Constants.DELETE_OFFER);
            }
        });

        if (offerItem.isOfferAdded()) {
            offerHolder.mBtnLayout.setVisibility(View.GONE);
            offerHolder.mOfferAlreadyAdded.setVisibility(View.VISIBLE);
        } else {
            offerHolder.mBtnLayout.setVisibility(View.VISIBLE);
            offerHolder.mOfferAlreadyAdded.setVisibility(View.GONE);
        }

        offerHolder.mAddToOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onOfferManupulation(position, Constants.ADD_OFFER_TO_SELLER);
            }
        });

        offerHolder.mNeedThisItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onOfferManupulation(position, Constants.NEED_THIS_ITEM);
            }
        });
    }

    public interface OfferManipulationListener {
        void onOfferManupulation(int position, String type);
    }

    public static class SuggestedOfferHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mTitle, mDetails, mQuantity, mOfferAlreadyAdded, mExpiry, mDiscountedPrice, mMRP, mDiscount, mStockLastTitle;
        protected ImageView deleteOffer;
        protected Button mAddToOffer, mNeedThisItem;
        protected ImageView mImage;
        protected LinearLayout mDiscountLayout, mBtnLayout;

        public SuggestedOfferHolder(View view) {
            super(view);
            mRootCard = view;
            mTitle = (TextView) view.findViewById(R.id.tv_offer_name);
            mDetails = (TextView) view.findViewById(R.id.tv_view_details);
            mExpiry = (TextView) view.findViewById(R.id.tv_offer_expiry);
            mImage = (ImageView) view.findViewById(R.id.iv_offer);
            deleteOffer = (ImageView) view.findViewById(R.id.iv_offer_delete);
            mDiscountedPrice = (TextView) view.findViewById(R.id.tv_offer_discounted_price);
            mDiscount = (TextView) view.findViewById(R.id.tv_offer_discount);
            mMRP = (TextView) view.findViewById(R.id.tv_offer_mrp);
            mStockLastTitle = (TextView) view.findViewById(R.id.tv_stock_last);
            mDiscountLayout = (LinearLayout) view.findViewById(R.id.ll_discount);
            mOfferAlreadyAdded = (TextView) view.findViewById(R.id.tv_offer_already_added);
            mQuantity = (TextView) view.findViewById(R.id.tv_quantity);

            mAddToOffer = (Button) view.findViewById(R.id.btn_add_to_offer);
            mNeedThisItem = (Button) view.findViewById(R.id.btn_need_this_item);
            mBtnLayout = (LinearLayout) view.findViewById(R.id.ll_btn_layout);
        }
    }

    public static class OfferHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mTitle, mDescription, mExpiry, mOfferAlreadyAdded;
        protected ImageView deleteOffer;
        protected PhotoView mImage;
        protected Button mAddToOffer, mNeedThisItem;
        protected LinearLayout mBtnLayout;

        public OfferHolder(View view) {
            super(view);
            mRootCard = view;
            mTitle = (TextView) view.findViewById(R.id.tv_offer_name);
            mDescription = (TextView) view.findViewById(R.id.tv_offer_description);
            mExpiry = (TextView) view.findViewById(R.id.tv_offer_expiry);
            mImage = (PhotoView) view.findViewById(R.id.iv_offer);
            deleteOffer = (ImageView) view.findViewById(R.id.iv_offer_delete);
            mOfferAlreadyAdded = (TextView) view.findViewById(R.id.tv_offer_already_added);

            mAddToOffer = (Button) view.findViewById(R.id.btn_add_to_offer);
            mNeedThisItem = (Button) view.findViewById(R.id.btn_need_this_item);
            mBtnLayout = (LinearLayout) view.findViewById(R.id.ll_btn_layout);
        }
    }

    public static class OfferBarCodeHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mTitle, mMrp, mQuantity, mDiscount, mExpiry, mOfferedMrp;
        protected ImageView editOffer, deleteOffer;
        protected EditText mEnterExpiry;
        protected ImageView mImage;

        public OfferBarCodeHolder(View view) {
            super(view);
            mRootCard = view;
            mTitle = (TextView) view.findViewById(R.id.tv_offer_name);
            mMrp = (TextView) view.findViewById(R.id.tv_offer_mrp);
            mOfferedMrp = (TextView) view.findViewById(R.id.tv_offer_price);
            mDiscount = (TextView) view.findViewById(R.id.tv_discount);
            mExpiry = (TextView) view.findViewById(R.id.tv_offer_expiry);
            mImage = (ImageView) view.findViewById(R.id.iv_offer);
            editOffer = (ImageView) view.findViewById(R.id.iv_offer_edit);
            deleteOffer = (ImageView) view.findViewById(R.id.iv_offer_delete);
            mEnterExpiry = (EditText) view.findViewById(R.id.et_expiry_date);
            mQuantity = (TextView) view.findViewById(R.id.tv_quantity);
        }
    }
}
