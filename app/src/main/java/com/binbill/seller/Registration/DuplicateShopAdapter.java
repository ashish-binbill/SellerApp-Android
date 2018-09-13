package com.binbill.seller.Registration;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/12/18.
 */

public class DuplicateShopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface AlreadyAddedShopListener {
        void onShopSelected(int pos);
    }

    public static class DuplicateShopHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mUserName, mAddress;
        protected ImageView mImage;
        protected RelativeLayout mCardSelected;

        public DuplicateShopHolder(View view) {
            super(view);
            mRootCard = view;
            mUserName = (TextView) view.findViewById(R.id.tv_name);
            mAddress = (TextView) view.findViewById(R.id.tv_address);
            mImage = (ImageView) view.findViewById(R.id.iv_user_image);
            mCardSelected = (RelativeLayout) view.findViewById(R.id.card_selected);
        }
    }

    private ArrayList<Seller> mList;
    private AlreadyAddedShopListener mListener;

    public DuplicateShopAdapter(ArrayList<Seller> list, AlreadyAddedShopListener object) {
        this.mList = list;
        this.mListener = object;
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_duplicate_shop, parent, false);
        return new DuplicateShopHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final DuplicateShopHolder userHolder = (DuplicateShopHolder) holder;
        final Seller model = mList.get(position);

        userHolder.mUserName.setText(model.getName());
        userHolder.mAddress.setText(model.getAddress());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(0, 0, 0, 0);
        userHolder.mImage.setLayoutParams(params);

        userHolder.mImage.setScaleType(ImageView.ScaleType.FIT_XY);

        Picasso.get().
                load(R.drawable.ic_shop)
                .config(Bitmap.Config.RGB_565)
                .into(userHolder.mImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) userHolder.mImage.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(userHolder.mImage.getContext().getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        userHolder.mImage.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        if (model.isSelected())
            userHolder.mCardSelected.setVisibility(View.VISIBLE);
        else
            userHolder.mCardSelected.setVisibility(View.GONE);

        userHolder.mRootCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAllCards();
                model.setSelected(true);
                userHolder.mCardSelected.setVisibility(View.VISIBLE);

                if (mListener != null)
                    mListener.onShopSelected(position);

                notifyDataSetChanged();
            }
        });
    }

    private void resetAllCards() {

        for (Seller seller : mList)
            seller.setSelected(false);
    }
}

