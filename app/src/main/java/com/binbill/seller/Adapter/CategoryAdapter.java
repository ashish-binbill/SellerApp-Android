package com.binbill.seller.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.Constants;
import com.binbill.seller.Dashboard.ProfileActivity;
import com.binbill.seller.Model.CategoryModel;
import com.binbill.seller.R;
import com.binbill.seller.SharedPref;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class CategoryAdapter extends
        RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryModel> stList;
    private Activity act;

    public CategoryAdapter(List<CategoryModel> items, Activity act) {
        this.stList = items;
        this.act = act;
    }

    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_category_main, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        final int pos = position;

        viewHolder.tv_item.setText(stList.get(position).getName());

        viewHolder.chkSelected.setChecked(stList.get(position).isSelected());

        viewHolder.chkSelected.setTag(stList.get(position));

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

        picasso.load(Constants.BASE_URL_IMAGE + stList.get(pos).getImageUrl())
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


        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                CategoryModel contact = (CategoryModel) cb.getTag();

                contact.setSelected(cb.isChecked());
                stList.get(pos).setSelected(cb.isChecked());


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
        public ImageView iv_item;

        public CheckBox chkSelected;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tv_item = (TextView) itemLayoutView.findViewById(R.id.tv_item);

            iv_item = (ImageView) itemLayoutView.findViewById(R.id.iv_item);
            chkSelected = (CheckBox) itemLayoutView
                    .findViewById(R.id.chkSelected);

        }


    }

    // method to access in activity after updating selection
    public List<CategoryModel> getItemList() {
        return stList;
    }

}
