package com.binbill.seller.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.binbill.seller.Model.ASCBrandCategoryModel;
import com.binbill.seller.R;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 8/18/18.
 */

public class ASCServicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public static class ASCServicesHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mTitle;
        protected CheckBox mCheck;

        public ASCServicesHolder(View view) {
            super(view);
            mRootCard = view;
            mTitle = (TextView) view.findViewById(R.id.tv_title_name);
            mCheck = (CheckBox) view.findViewById(R.id.cb_select);
        }
    }

    private ArrayList<ASCBrandCategoryModel> mList;
    private ArrayList<ASCBrandCategoryModel> mFilteredList;

    public ASCServicesAdapter(ArrayList<ASCBrandCategoryModel> list) {
        this.mList = list;
        this.mFilteredList = list;
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.row_check_box_list, parent, false);
        return new ASCServicesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ASCServicesHolder ascServicesHolder = (ASCServicesHolder) holder;

        final ASCBrandCategoryModel model = mFilteredList.get(position);

        ascServicesHolder.mTitle.setText(model.getTitle());

        if (model.isChecked())
            ascServicesHolder.mCheck.setChecked(true);
        else
            ascServicesHolder.mCheck.setChecked(false);

        ascServicesHolder.mRootCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ascServicesHolder.mCheck.setChecked(!ascServicesHolder.mCheck.isChecked());
                if (ascServicesHolder.mCheck.isChecked()) {
                    model.setChecked(true);
                    ascServicesHolder.mCheck.setChecked(true);
                } else {
                    model.setChecked(false);
                    ascServicesHolder.mCheck.setChecked(false);
                }
            }
        });

    }

    public ArrayList<ASCBrandCategoryModel> getUpdatedList() {
        return this.mFilteredList;
    }

    public void setUpdatedList(ArrayList<ASCBrandCategoryModel> list){
        this.mFilteredList = list;
        notifyDataSetChanged();
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

                    ArrayList<ASCBrandCategoryModel> filteredList = new ArrayList<>();

                    for (ASCBrandCategoryModel listItem : mList) {
                        if (listItem.getTitle().toLowerCase().contains(charString.toLowerCase())) {
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
                mFilteredList = (ArrayList<ASCBrandCategoryModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }
}
