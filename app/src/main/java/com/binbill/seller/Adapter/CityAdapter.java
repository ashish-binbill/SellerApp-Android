package com.binbill.seller.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.binbill.seller.Interface.ItemSelectedInterface;
import com.binbill.seller.Model.StateCityModel;
import com.binbill.seller.R;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 8/23/18.
 */

public class CityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable{

    private final ItemSelectedInterface listener;
    private ArrayList<StateCityModel.CityModel> mFilteredList;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mCityList;
                } else {

                    ArrayList<StateCityModel.CityModel> filteredList = new ArrayList<>();

                    for (StateCityModel.CityModel listItem : mCityList) {
                        if (listItem.getCityName().toLowerCase().contains(charString.toLowerCase())) {
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
                mFilteredList = (ArrayList<StateCityModel.CityModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class CityHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mTitle;

        public CityHolder(View view) {
            super(view);
            mRootCard = view;
            mTitle = (TextView) view.findViewById(R.id.tv_main_category);
        }
    }

    private ArrayList<StateCityModel.CityModel> mCityList;

    public CityAdapter(ArrayList<StateCityModel.CityModel> list, ItemSelectedInterface itemSelectedInterface) {
        this.mCityList = list;
        this.listener = itemSelectedInterface;
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
                inflate(R.layout.row_selection_list, parent, false);
        return new CityHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final CityHolder cityHolder = (CityHolder) holder;

        final StateCityModel.CityModel cityModel = mFilteredList.get(position);

        cityHolder.mTitle.setText(cityModel.getCityName());

        cityHolder.mRootCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onItemSelected(cityModel);
            }
        });
    }
}
