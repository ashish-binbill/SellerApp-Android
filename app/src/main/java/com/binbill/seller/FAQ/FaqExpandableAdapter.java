package com.binbill.seller.FAQ;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.binbill.seller.R;

import java.util.HashMap;
import java.util.List;

public class FaqExpandableAdapter extends BaseExpandableListAdapter {

    private static final int CHILD = 0;
    private Context mContext;
    private List<String> listDataHeader;
    private HashMap<String, String> listDataChild;

    public FaqExpandableAdapter(Context context, List<String> listDataHeader,
                                HashMap<String, String> listChildData) {
        this.mContext = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition));
    }

    public String getChildObject(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return CHILD;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        String headerTitle = getChildObject(groupPosition, childPosition);
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView headerText = null;
        convertView = inflater.inflate(R.layout.layout_row_faq_child, null);

        headerText = (TextView) convertView.findViewById(R.id.value);

        if (headerText != null) {
            headerText.setText(headerTitle);
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public String getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildTypeCount() {
        return 1;
    }

    @Override
    public int getGroupTypeCount() {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition);

        LayoutInflater infalInflater = (LayoutInflater) this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.layout_row_faq_header, null);

        TextView headerText = (TextView) convertView
                .findViewById(R.id.value);
        headerText.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

