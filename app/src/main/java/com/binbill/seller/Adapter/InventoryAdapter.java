package com.binbill.seller.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binbill.seller.Model.InventoryModel;
import com.binbill.seller.R;
import com.binbill.seller.Registration.InventoriesRegister;
import com.binbill.seller.Registration.RegisterActivity;

import java.util.List;

public class InventoryAdapter extends
        RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<InventoryModel> stList;

    Activity act;

    public InventoryAdapter(List<InventoryModel> items, Activity act) {
        this.stList = items;
        this.act = act;
    }

    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_invertory_register, null);

        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final int pos = position;

        viewHolder.tv_item.setText(stList.get(position).getName());
        viewHolder.tv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = stList.get(pos).getName();
                RegisterActivity.skuDetails = str;
                act.finish();
            }
        });

    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return stList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_item;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tv_item = (TextView) itemLayoutView.findViewById(R.id.tv_inventory);

        }
    }

    // method to access in activity after updating selection
    public List<InventoryModel> getItemList() {
        return stList;
    }

}
