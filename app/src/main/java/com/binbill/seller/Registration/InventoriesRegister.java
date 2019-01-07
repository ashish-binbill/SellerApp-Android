package com.binbill.seller.Registration;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.binbill.seller.Adapter.InventoryAdapter;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Model.InventoryModel;
import com.binbill.seller.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_register_inventory)
public class InventoriesRegister extends BaseActivity {

    @ViewById
    RecyclerView recycler_view;

    InventoryAdapter mAdapter;

    private List<InventoryModel> itemList = new ArrayList<>();
    private String[] sku_arr ={" < Less than 1000", "1001 to 3000", "3001 to 5000", "5001 to 8000"
    ,"8001 to 10000", " > Greater than 10000"};

    @AfterViews
    public void initiateRecyclerView(){
        recycler_view.setHasFixedSize(true);
        // use a linear layout manager
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        recycler_view.addItemDecoration(new DividerItemDecoration(this, 0));

        for(int i = 0; i< sku_arr.length; i++){
            InventoryModel st = new InventoryModel(sku_arr[i]);
            itemList.add(st);
        }

        // create an Object for Adapter
        mAdapter = new InventoryAdapter(itemList, InventoriesRegister.this);
        // set the adapter object to the Recyclerview
        recycler_view.setAdapter(mAdapter);

        // create an Object for Adapter
        mAdapter = new InventoryAdapter(itemList, InventoriesRegister.this);

    }
}
