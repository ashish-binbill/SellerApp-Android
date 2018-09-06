package com.binbill.seller.Offers;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.binbill.seller.R;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 9/1/18.
 */

public class DefaultBannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface BannerInteractionListener {
        void onBannerSelected(int position);
    }

    public static class BannerHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected ImageView mImage;

        public BannerHolder(View view) {
            super(view);
            mRootCard = view;
            mImage = (ImageView) view.findViewById(R.id.iv_banner);
        }
    }

    private ArrayList<Drawable> mList;
    private final BannerInteractionListener mListener;

    public DefaultBannerAdapter(BannerInteractionListener listener, ArrayList<Drawable> list) {
        this.mList = list;
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_default_banner, parent, false);
        return new BannerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final BannerHolder bannerHolder = (BannerHolder) holder;

        final Drawable drawable = mList.get(position);
        bannerHolder.mImage.setImageDrawable(drawable);

        bannerHolder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.onBannerSelected(position);
            }
        });
    }
}
