package com.binbill.seller.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.binbill.seller.Constants;
import com.binbill.seller.Dashboard.ManageFruitsVegActivity;
import com.binbill.seller.Dashboard.UpdateValues;
import com.binbill.seller.Model.SkuItem;
import com.binbill.seller.Model.SkuMeasurement;
import com.binbill.seller.Order.OrderAdapter;
import com.binbill.seller.R;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Util;

public class ManageFruitVegAdapter extends
        RecyclerView.Adapter<ManageFruitVegAdapter.ViewHolder> {

    private List<SkuItem> stList;
    private Activity act;
    public String[] mDataset;
    private String idRedundant="";
    public static ArrayList<HashMap<String, String>> itemChange_ids = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> valueChange_ids = new ArrayList<>();
    public static ArrayList<Integer> posEdit = new ArrayList<>();
    public static boolean hasFocused = true;
    ViewHolder viewHolder ;

    public ManageFruitVegAdapter(List<SkuItem> items, Activity act) {
        this.stList = items;
        this.act = act;
        mDataset = new String[stList.size()];
        itemChange_ids.clear();
        valueChange_ids.clear();
        posEdit.clear();
        for (int i = 0; i < stList.size(); i++) {
            try {
                ArrayList<SkuMeasurement> detailsArray = stList.get(i).getSkuMeasurements();
               // ArrayList<SkuMeasurement> detailsArray = new ArrayList<>();
                if (ManageFruitsVegActivity.isOthers) {
                    mDataset[i] = detailsArray.get(0).getMrp();
                } else {
                    mDataset[i] = detailsArray.get(0).getMrp();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_fruits_vegetables, null);

        // create ViewHolder
        viewHolder = new ViewHolder(itemLayoutView, new MyCustomEditTextListener());

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        final int pos = position;

        ArrayList<SkuMeasurement> details = stList.get(pos).getSkuMeasurements();
        //ArrayList<SkuMeasurement> details = new ArrayList<>();
        viewHolder.tv_item.setText(details.get(0).getTitle());

        if(ManageFruitsVegActivity.isOthers){
            viewHolder.et_quantity.setText(details.get(0).getMeasurementValue() + " " + details
                    .get(0).getMeasurementAcronym());
        }else{
            viewHolder.et_quantity.setText(details.get(0).getMeasurementValue() + " " + details
                    .get(0).getMeasurementAcronym());
        }


        viewHolder.myCustomEditTextListener.updatePosition(viewHolder.getAdapterPosition());
        viewHolder.et_mrp.setText(mDataset[pos]);

        final String authToken = SharedPref.getString(act, SharedPref.AUTH_TOKEN);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        return response.request().newBuilder()
                                .header("Authorization", authToken)
                                .build();
                    }
                }).build();

        Picasso picasso = new Picasso.Builder(act)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
        picasso.load(Constants.BASE_URL_IMAGE + "/skus/" + details.get(0).getSku_id()
                    +"/measurements/"+details.get(0).getId()+ "/images")
                .config(Bitmap.Config.RGB_565)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(viewHolder.iv_item, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        viewHolder.iv_item.setImageDrawable
                                (ContextCompat.getDrawable(act, R.drawable.default_profile));

                    }
                });

        viewHolder.et_mrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.et_mrp.requestFocus();
                hasFocused = false;
            }
        });

        viewHolder.et_mrp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
              hasFocused = false;
                return false;
            }
        });

        viewHolder.et_mrp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    hasFocused = false;
                    ArrayList<SkuMeasurement> details = stList.get(pos).getSkuMeasurements();
                   // ArrayList<SkuMeasurement> details = new ArrayList<>();
                    if (ManageFruitsVegActivity.isOthers) {
                        if (!idRedundant.equalsIgnoreCase(details.get(0).getId())) {
                            posEdit.add(pos);
                        }
                        idRedundant = details.get(0).getId();
                    }
                    else {
                        if (!idRedundant.equalsIgnoreCase(details.get(0).getId())) {
                            posEdit.add(pos);
                        }
                        idRedundant = details.get(0).getId();
                    }

                    //===========================================================================//


                } else {

                }
            }
        });
       /* viewHolder.et_mrp.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<SkuMeasurement> details = stList.get(pos).getSkuMeasurements();
                HashMap<String, String> hmapValue = new HashMap<>();
                HashMap<String, String> hmap = new HashMap<>();
            }
        });*/

       /* viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                CategoryModel contact = (CategoryModel) cb.getTag();

                contact.setSelected(cb.isChecked());
                stList.get(pos).setSelected(cb.isChecked());


            }
        });*/
    }

    @Override
    public int getItemCount() {
        return stList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_item;
        public ImageView iv_item;
        public EditText et_quantity;
        public EditText et_mrp;

        public CheckBox chkSelected;

        public MyCustomEditTextListener myCustomEditTextListener;

        public ViewHolder(View itemLayoutView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemLayoutView);
            tv_item = (TextView) itemLayoutView.findViewById(R.id.tv_item);
            et_quantity = (EditText) itemLayoutView.findViewById(R.id.et_quantity);
            et_mrp = (EditText) itemLayoutView.findViewById(R.id.et_mrp);
            iv_item = (ImageView) itemLayoutView.findViewById(R.id.iv_item);
            this.et_quantity = (EditText) itemLayoutView.findViewById(R.id.et_quantity);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.et_mrp.addTextChangedListener(myCustomEditTextListener);
            chkSelected = (CheckBox) itemLayoutView
                    .findViewById(R.id.chkSelected);

        }
    }


    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder
    private class MyCustomEditTextListener implements TextWatcher {
        private int positions;

        public void updatePosition(int position) {
            this.positions = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            mDataset[positions] = charSequence.toString();
            if (charSequence.toString().length() == 1 && !charSequence.toString().startsWith("₹ ")) {
                mDataset[positions] = charSequence.toString();
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!hasFocused) {
                hasFocused = false;
                ArrayList<SkuMeasurement> details = stList.get(positions).getSkuMeasurements();
              //  ArrayList<SkuMeasurement> details = new ArrayList<>();
                HashMap<String, String> hmapValue = new HashMap<>();
                HashMap<String, String> hmap = new HashMap<>();

                if (ManageFruitsVegActivity.isOthers) {
                    hmap.put(details.get(0).getId_main(), details.get(0).getId());
                    hmapValue.put(details.get(0).getId_main(), mDataset[positions]);
                    itemChange_ids.add(hmap);
                    valueChange_ids.add(hmapValue);
                    UpdateValues value = new UpdateValues(hmap,hmapValue);
                    value.removeDuplicateElements();
                    value.arrangeCorrectData(hmap,hmapValue);
                }else {
                    hmap.put(details.get(0).getId_main(), details.get(0).getId());
                    hmapValue.put(details.get(0).getId_main(), mDataset[positions]);
                    itemChange_ids.add(hmap);
                    valueChange_ids.add(hmapValue);
                    UpdateValues value = new UpdateValues(hmap,hmapValue);
                    value.removeDuplicateElements();
                    value.arrangeCorrectData(hmap,hmapValue);
                }

             /*   hasFocused = false;
                boolean isremovedPos = false;
                ArrayList<SkuMeasurement> details = stList.get(positions).getSkuMeasurements();
                HashMap<String, String> hmapValue = new HashMap<>();
                HashMap<String, String> hmap = new HashMap<>();

                //===========================================================================//

                try {
                    if (ManageFruitsVegActivity.isOthers) {
                        if (!idRedundant.equalsIgnoreCase(details.get(0).getId())) {
                            hmap.put(stList.get(positions).getId(), details.get(0).getId());
                            hmapValue.put(stList.get(positions).getId(), mDataset[positions]);
                            itemChange_ids.add(hmap);
                            valueChange_ids.add(hmapValue);
                            // positionsEdit.add(positions);
                        } else {
                            hmapValue.put(stList.get(positions).getId(), mDataset[positions]);
                            valueChange_ids.remove(positions);
                            valueChange_ids.add(hmapValue);
                        }
                        idRedundant = details.get(0).getId();
                    } else {
                        if (!idRedundant.equalsIgnoreCase(details.get(1).getId())) {
                            hmap.put(stList.get(positions).getId(), details.get(1).getId());
                            hmapValue.put(stList.get(positions).getId(), mDataset[positions]);
                            itemChange_ids.add(hmap);
                            valueChange_ids.add(hmapValue);
                            //  positionsEdit.add(positions);
                        } else {
                            hmapValue.put(stList.get(positions).getId(), mDataset[positions]);
                            hmap.put(stList.get(positions).getId(), details.get(1).getId());
                            int size = valueChange_ids.size();
                            int size1 = itemChange_ids.size();
                            try {
                                if (valueChange_ids.size() != 0) {
                                    isremovedPos = true;
                                    valueChange_ids.remove(positions);
                                }
                                if (itemChange_ids.size() != 0) {
                                    isremovedPos = true;
                                    itemChange_ids.remove(positions);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                if(size>1) {
                                    valueChange_ids.remove(size - 1);
                                }
                                if(size1>1){
                                    itemChange_ids.remove(size1-1);
                                }
                                if(isremovedPos){
                                    valueChange_ids.remove(size - 1);
                                    itemChange_ids.remove(size1-1);
                                    isremovedPos = false;
                                }

                            }
                            itemChange_ids.add(hmap);
                            valueChange_ids.add(hmapValue);
                        }
                        idRedundant = details.get(1).getId();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    hmapValue.put(stList.get(positions).getId(), mDataset[positions]);
                    if (ManageFruitsVegActivity.isOthers) {
                        if (!idRedundant.equalsIgnoreCase(details.get(0).getId())) {
                            itemChange_ids.add(hmap);
                            valueChange_ids.add(hmapValue);
                            //  positionsEdit.add(positions);
                        }else{
                            itemChange_ids.add(hmap);
                            valueChange_ids.add(hmapValue);
                        }
                        idRedundant = details.get(0).getId();
                    }else {
                        if (!idRedundant.equalsIgnoreCase(details.get(1).getId())) {
                            itemChange_ids.add(hmap);
                            valueChange_ids.add(hmapValue);
                        }else{
                            //itemChange_ids.add(hmap);
                           // valueChange_ids.add(hmapValue);
                        }
                        idRedundant = details.get(1).getId();
                    }
                }

            }*/
            }
        }
    }

    // method to access in activity after updating selection
    public List<SkuItem> getItemList() {
        return stList;
    }
}
