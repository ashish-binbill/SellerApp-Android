package com.binbill.seller.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.binbill.seller.Model.SkuItem;
import com.binbill.seller.Model.SkuMeasurement;
import com.binbill.seller.R;
import com.binbill.seller.SharedPref;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class ManageFruitVegAdapter extends
        RecyclerView.Adapter<ManageFruitVegAdapter.ViewHolder> {

    private List<SkuItem> stList;
    private Activity act;

    public ManageFruitVegAdapter(List<SkuItem> items, Activity act) {
        this.stList = items;
        this.act = act;
    }

    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_fruits_vegetables, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        final int pos = position;
        ArrayList<SkuMeasurement> details = stList.get(pos).getSkuMeasurements();
        viewHolder.tv_item.setText(stList.get(pos).getTitle());
        viewHolder.et_mrp.setText(act.getString(R.string.rupee_sign)+" "+details.get(1).getMrp());
        viewHolder.et_quantity.setText(details.get(1).getMeasurementValue()+" "+ details
        .get(1).getMeasurementAcronym());
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

      /*  picasso.load(Constants.BASE_URL_IMAGE + stList.get(pos).getImageUrl())
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

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tv_item = (TextView) itemLayoutView.findViewById(R.id.tv_item);
            et_quantity = (EditText) itemLayoutView.findViewById(R.id.et_quantity);
            et_mrp = (EditText) itemLayoutView.findViewById(R.id.et_mrp);
            iv_item = (ImageView) itemLayoutView.findViewById(R.id.iv_item);
            chkSelected = (CheckBox) itemLayoutView
                    .findViewById(R.id.chkSelected);

        }


    }

    // method to access in activity after updating selection
    public List<SkuItem> getItemList() {
        return stList;
    }
}
