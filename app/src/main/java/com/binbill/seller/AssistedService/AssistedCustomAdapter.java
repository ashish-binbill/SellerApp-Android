package com.binbill.seller.AssistedService;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.binbill.seller.R;

import java.util.List;

/**
 * Created by shruti.vig on 9/3/18.
 */

public class AssistedCustomAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<String> items;
    private final int mResource;

    public AssistedCustomAdapter(@NonNull Context context, @LayoutRes int resource,
                                 @NonNull List list) {
        super(context, resource, 0, list);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = list;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = mInflater.inflate(mResource, parent, false);

        TextView title = (TextView) view.findViewById(R.id.text);
        title.setGravity(Gravity.START);

        title.setText(items.get(position));
        return view;
    }
}