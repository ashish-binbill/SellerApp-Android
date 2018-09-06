package com.binbill.seller.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binbill.seller.Interface.ItemSelectedInterface;
import com.binbill.seller.Model.MainCategory;
import com.binbill.seller.R;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 8/15/18.
 */

public class MainCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ItemSelectedInterface listener;

    public static class MainCategoryHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mMainCategory;

        public MainCategoryHolder(View view) {
            super(view);
            mRootCard = view;
            mMainCategory = (TextView) view.findViewById(R.id.tv_main_category);
        }
    }

    private ArrayList<MainCategory> mMainCategoryList;

    public MainCategoryAdapter(ArrayList<MainCategory> list, ItemSelectedInterface itemSelectedInterface) {
        this.mMainCategoryList = list;
        this.listener = itemSelectedInterface;
    }


    @Override
    public int getItemCount() {
        return mMainCategoryList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_selection_list, parent, false);
        return new MainCategoryAdapter.MainCategoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final MainCategoryAdapter.MainCategoryHolder mainCategoryHolder = (MainCategoryAdapter.MainCategoryHolder) holder;

        final MainCategory mainCategory = mMainCategoryList.get(position);

        mainCategoryHolder.mMainCategory.setText(mainCategory.getName());

        mainCategoryHolder.mRootCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onItemSelected(mainCategory);
            }
        });
    }
}
