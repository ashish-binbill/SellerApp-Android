package com.binbill.seller.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.Model.FMCGChildModel;
import com.binbill.seller.Model.FMCGHeaderModel;
import com.binbill.seller.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by shruti.vig on 8/17/18.
 */

public class FMCGExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<FMCGHeaderModel> expandableListTitle;
    private LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> expandableListDetail;

    public FMCGExpandableAdapter(Context context, List<FMCGHeaderModel> expandableListTitle,
                                 LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final FMCGChildModel expandedListText = (FMCGChildModel) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.layout_fmcg_child_list, null);
        }

        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.child_name);
        CheckBox expandableChildCheckBox = (CheckBox) convertView.findViewById(R.id.check_box);

        if (expandedListText.isUserSelected())
            expandableChildCheckBox.setChecked(true);
        else
            expandableChildCheckBox.setChecked(false);

        expandedListTextView.setText(expandedListText.getName());

        if (isLastChild) {
            convertView.setBackground(context.getResources().getDrawable(R.drawable.expandable_bottom_child_bg));
        } else {
            convertView.setBackground(context.getResources().getDrawable(R.drawable.expandable_middle_child_bg));
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {

        ArrayList<FMCGChildModel> child = (ArrayList<FMCGChildModel>) this.expandableListDetail.get(this.expandableListTitle.get(listPosition));
        if (child == null)
            return 0;
        else
            return child.size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(final int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final FMCGHeaderModel listTitle = (FMCGHeaderModel) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.layout_fmcg_header_list, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.header_name);
        listTitleTextView.setText(listTitle.getName());

        final LinearLayout headerExpanded = (LinearLayout) convertView.findViewById(R.id.header_expanded_layout);
        View divider = (View) convertView.findViewById(R.id.divider);

        if (isExpanded) {
            headerExpanded.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            convertView.setBackground(context.getResources().getDrawable(R.drawable.expandable_header_bg));
            listTitleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_collapse), null);
        } else {
            headerExpanded.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            convertView.setBackground(context.getResources().getDrawable(R.drawable.expandable_header_bg_collapsed));
            listTitleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_expand), null);
        }

        headerExpanded.setBackground(context.getResources().getDrawable(R.drawable.expandable_middle_child_bg));

        final CheckBox selectAll = (CheckBox) headerExpanded.findViewById(R.id.cb_select_all);
        if (listTitle.isShowSelectAll())
            selectAll.setChecked(true);
        else
            selectAll.setChecked(false);

        headerExpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectAll.isChecked()) {
                    setAllChildSelected(listPosition, false);
                    listTitle.setShowSelectAll(false);
                } else {
                    setAllChildSelected(listPosition, true);
                    listTitle.setShowSelectAll(true);
                }
            }
        });

        return convertView;
    }

    private void setAllChildSelected(int listPosition, boolean enable) {

        for (FMCGChildModel fmcgChildModel : expandableListDetail.get(
                expandableListTitle.get(listPosition)))
            fmcgChildModel.setUserSelected(enable);

        notifyDataSetChanged();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    public LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> getUpdatedModelMap() {
        return this.expandableListDetail;
    }
}
