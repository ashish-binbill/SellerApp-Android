package com.binbill.seller.FAQ;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_faq)
public class FaqActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    SwipeRefreshLayout sl_pull_to_refresh;

    @ViewById
    LinearLayout shimmer_view_container, no_data_layout;

    @ViewById
    TextView tv_no_data;

    @ViewById
    RecyclerView rv_credits_view;

    @ViewById
    AppButton btn_no_data;

    @AfterViews
    public void initiateViews() {

        setUpToolbar();
        setUpListeners();
        btn_no_data.setVisibility(View.GONE);
        tv_no_data.setText(getString(R.string.something_went_wrong));

    }

    private void setUpListeners() {

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        toolbarText.setText(getString(R.string.faqs));
    }

}
