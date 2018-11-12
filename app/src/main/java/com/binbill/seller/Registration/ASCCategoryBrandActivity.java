package com.binbill.seller.Registration;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.Adapter.ASCServicesAdapter;
import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Model.ASCBrandCategoryModel;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.activity_asc_category_brand)
public class ASCCategoryBrandActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    @ViewById
    AppButton btn_submit;

    @ViewById
    LinearLayout btn_submit_progress;

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    RecyclerView recycler_view;

    @ViewById
    CheckBox cb_select_all;

    @ViewById
    TextView header;

    @ViewById
    SearchView search_view;

    ArrayList<ASCBrandCategoryModel> mSelectionMap;

    UserRegistrationDetails userRegistrationDetails;
    @InstanceState
    int mType;
    ASCServicesAdapter mAdapter = null;

    @AfterViews
    public void initialiseViews() {

        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
        mType = getIntent().getIntExtra(Constants.ASC_SELECTION_TYPE, Constants.CATEGORY);

        setUpToolbar();
        setUpListeners();
        setUpViews();
    }

    private void setUpViews() {
        recycler_view.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(llm);

        switch (mType) {
            case Constants.CATEGORY:
                mSelectionMap = getDummyCategoryList(true);
                mAdapter = new ASCServicesAdapter(mSelectionMap);
                header.setText(getString(R.string.select_categories_asc));
                search_view.setVisibility(View.GONE);
                break;
            case Constants.BRAND:

                mSelectionMap = getDummyCategoryList(false);
                mAdapter = new ASCServicesAdapter(mSelectionMap);
                header.setText(getString(R.string.select_categories_brand));
                search_view.setVisibility(View.VISIBLE);
                break;
        }
        recycler_view.setAdapter(mAdapter);

        search_view.setIconifiedByDefault(false);
        search_view.setOnQueryTextListener(this);
        search_view.setSubmitButtonEnabled(false);
    }

    @Click(R.id.btn_submit)
    public void onSubmitClicked(View viewRegister) {
        Utility.hideKeyboard(this, btn_submit);
        if (isValid()) {
            btn_submit.setVisibility(View.GONE);
            btn_submit_progress.setVisibility(View.VISIBLE);
            printMap();

            makeUploadDataToServerCall();

        }
    }

    private boolean isValid() {

        boolean enable = false;

        for (ASCBrandCategoryModel model : mSelectionMap) {
            if (model.isChecked())
                enable = true;
        }

        return enable;
    }

    private void makeUploadDataToServerCall() {

        int registrationIndex = getIntent().getIntExtra(Constants.REGISTRATION_INDEX, -1);
        Intent intent = RegistrationResolver.getNextIntent(this, registrationIndex);
        if (intent != null)
            startActivity(intent);

        btn_submit.setVisibility(View.VISIBLE);
        btn_submit_progress.setVisibility(View.GONE);
    }

    private ArrayList<ASCBrandCategoryModel> getDummyCategoryList(boolean isCategory) {
        ArrayList<ASCBrandCategoryModel> list = new ArrayList<>();
        if (isCategory) {
            list.add(new ASCBrandCategoryModel("Mobile"));
            list.add(new ASCBrandCategoryModel("TV"));
            list.add(new ASCBrandCategoryModel("Washing Machine"));
            list.add(new ASCBrandCategoryModel("Laptop"));
            list.add(new ASCBrandCategoryModel("Chair"));
            list.add(new ASCBrandCategoryModel("Fridge"));
            list.add(new ASCBrandCategoryModel("Table"));
            list.add(new ASCBrandCategoryModel("Fan"));
            list.add(new ASCBrandCategoryModel("Bed"));
        } else {

            list.add(new ASCBrandCategoryModel("Maruti"));
            list.add(new ASCBrandCategoryModel("Hyundai"));
            list.add(new ASCBrandCategoryModel("TATA"));
            list.add(new ASCBrandCategoryModel("Audi"));
            list.add(new ASCBrandCategoryModel("Jaguar"));
            list.add(new ASCBrandCategoryModel("Mercedes"));
            list.add(new ASCBrandCategoryModel("Honda"));
            list.add(new ASCBrandCategoryModel("Porsche"));
            list.add(new ASCBrandCategoryModel("Bajaj"));

            list.add(new ASCBrandCategoryModel("LG"));
            list.add(new ASCBrandCategoryModel("Samsung"));
            list.add(new ASCBrandCategoryModel("Sony"));
            list.add(new ASCBrandCategoryModel("Philips"));
            list.add(new ASCBrandCategoryModel("TLC"));
        }

        return list;
    }

    private void printMap() {
        for (ASCBrandCategoryModel model : mSelectionMap) {
            Log.d("SHRUTI", model.getTitle() + " = " + model.isChecked());
        }
    }

    private void setUpListeners() {

        cb_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                ArrayList<ASCBrandCategoryModel> categoryList;
                if (checked) {
                    categoryList = mAdapter.getUpdatedList();
                    for (ASCBrandCategoryModel model : categoryList)
                        model.setChecked(true);
                } else {
                    categoryList = mAdapter.getUpdatedList();
                    for (ASCBrandCategoryModel model : categoryList)
                        model.setChecked(false);
                }

                mAdapter.setUpdatedList(categoryList);
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        switch (mType) {
            case Constants.CATEGORY:
                toolbarText.setText(R.string.select_services);
                break;
            case Constants.BRAND:
                toolbarText.setText(R.string.select_brand);
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (!Utility.isEmpty(newText))
            header.setVisibility(View.GONE);
        else
            header.setVisibility(View.VISIBLE);
        mAdapter.getFilter().filter(newText);
        return true;
    }
}
