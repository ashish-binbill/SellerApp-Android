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
 * Created by shruti.vig on 8/22/18.
 */

public class StateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final ItemSelectedInterface listener;
    private ArrayList<StateCityModel> mFilteredList;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mStateCityList;
                } else {

                    ArrayList<StateCityModel> filteredList = new ArrayList<>();

                    for (StateCityModel listItem : mStateCityList) {
                        if (listItem.getStateName().toLowerCase().contains(charString.toLowerCase())) {
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
                mFilteredList = (ArrayList<StateCityModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

    public static class StateHolder extends RecyclerView.ViewHolder {
        protected View mRootCard;
        protected TextView mTitle;

        public StateHolder(View view) {
            super(view);
            mRootCard = view;
            mTitle = (TextView) view.findViewById(R.id.tv_main_category);
        }
    }

    private ArrayList<StateCityModel> mStateCityList;

    public StateAdapter(ArrayList<StateCityModel> list, ItemSelectedInterface itemSelectedInterface) {
        this.mStateCityList = list;
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
        return new StateHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final StateHolder stateHolder = (StateHolder) holder;

        final StateCityModel stateCityModel = mFilteredList.get(position);

        stateHolder.mTitle.setText(stateCityModel.getStateName());

        stateHolder.mRootCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onItemSelected(stateCityModel);
            }
        });
    }
}
