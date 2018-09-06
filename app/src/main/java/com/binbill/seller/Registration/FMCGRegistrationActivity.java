package com.binbill.seller.Registration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.Adapter.FMCGExpandableAdapter;
import com.binbill.seller.AppSession;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Model.FMCGChildModel;
import com.binbill.seller.Model.FMCGHeaderModel;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_fmcg_registration)
public class FMCGRegistrationActivity extends AppCompatActivity {

    @ViewById
    AppButton btn_submit;

    @ViewById
    LinearLayout btn_submit_progress;

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    ExpandableListView expandable_list;

    @ViewById
    TextView title;

    UserRegistrationDetails userRegistrationDetails;

    ArrayList<FMCGHeaderModel> expandableListTitle;
    FMCGExpandableAdapter fmcgExpandableAdapter;
    LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> expandableListDetail;

    @InstanceState
    String mType;

    @AfterViews
    public void initialiseViews() {
        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
        mType = getIntent().getStringExtra(Constants.BUSINESS_TYPE);
        setUpToolbar();
        setUpListeners();

        setUpDataInView();
    }

    private void setUpDataInView() {

        switch (mType) {
            case Constants.FMCG:
                expandableListDetail = getFmcgData();
                title.setText(getString(R.string.select_services_you_provide));
                break;
            case Constants.SERVICE_CATEGORY:
                expandableListDetail = getServicesCategoryData(userRegistrationDetails);
                title.setText(getString(R.string.select_services_categories));
                break;
            case Constants.SERVICE_BRAND:
                expandableListDetail = getServicesBrandData(userRegistrationDetails);
                title.setText(getString(R.string.select_brand_you_select));
                break;
            case Constants.FMCG_BRANDS:
//                expandableListDetail = getFMCGBrandsData(userRegistrationDetails);

                makeFMCGBrandCall();
                title.setText(getString(R.string.select_brand_you_select));
                break;
        }

        if(expandableListDetail != null) {
            expandableListTitle = new ArrayList<FMCGHeaderModel>(expandableListDetail.keySet());

            fmcgExpandableAdapter = new FMCGExpandableAdapter(this, expandableListTitle, expandableListDetail);
            expandable_list.setAdapter(fmcgExpandableAdapter);
        }

    }

    private void makeFMCGBrandCall() {
        if (userRegistrationDetails != null) {
            HashMap<String, ArrayList<String>> fmcgCategoriesSelected = userRegistrationDetails.getFmcgCategoriesSelected();
            String commaSeparatedCategories = TextUtils.join(",", fmcgCategoriesSelected.keySet());

            new RetrofitHelper(this).fetchBrandsFromCategories(commaSeparatedCategories, new RetrofitHelper.RetrofitCallback() {
                @Override
                public void onResponse(String response) {
                    Log.d("SHRUTI", "data : " + response);

                }

                @Override
                public void onErrorResponse() {

                }
            });
        }
    }


    private void setUpListeners() {
        expandable_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                FMCGChildModel fmcgChildModel = expandableListDetail.get(
                        expandableListTitle.get(groupPosition)).get(
                        childPosition);

                if (fmcgChildModel.isUserSelected())
                    fmcgChildModel.setUserSelected(false);
                else
                    fmcgChildModel.setUserSelected(true);

                fmcgExpandableAdapter.notifyDataSetChanged();
                return false;
            }
        });

        final int[] prevExpandPosition = {-1};
        expandable_list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if (prevExpandPosition[0] >= 0 && prevExpandPosition[0] != groupPosition) {
                    expandable_list.collapseGroup(prevExpandPosition[0]);
                }
                prevExpandPosition[0] = groupPosition;

                fmcgExpandableAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        switch (mType) {
            case Constants.FMCG:
                toolbarText.setText(R.string.select_categories);
                break;
            case Constants.SERVICE_CATEGORY:
                toolbarText.setText(R.string.select_service_categories);
                break;
            case Constants.SERVICE_BRAND:
                toolbarText.setText(R.string.select_category_brand);
                break;
            case Constants.FMCG_BRANDS:
                toolbarText.setText(R.string.select_brands);
                break;

        }
    }

    @Click(R.id.btn_submit)
    public void onSubmitClicked(View viewRegister) {
        Utility.hideKeyboard(this, btn_submit);
        if (isValid()) {
            btn_submit.setVisibility(View.GONE);
            btn_submit_progress.setVisibility(View.VISIBLE);

            makeUploadDataToServerCall();

        }
    }

    private void makeUploadDataToServerCall() {

        saveDataInUserRegistrationModel();
        int registrationIndex = getIntent().getIntExtra(Constants.REGISTRATION_INDEX, -1);
        Intent intent = RegistrationResolver.getNextIntent(this, registrationIndex);
        if (intent != null)
            startActivity(intent);

        btn_submit.setVisibility(View.VISIBLE);
        btn_submit_progress.setVisibility(View.GONE);
    }

    private void saveDataInUserRegistrationModel() {
        HashMap<String, ArrayList<String>> checkedMap = new HashMap<>();
        expandableListDetail = fmcgExpandableAdapter.getUpdatedModelMap();

        Iterator it = expandableListDetail.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<FMCGChildModel> childList = (ArrayList<FMCGChildModel>) pair.getValue();
            ArrayList<String> checkedItems = new ArrayList<>();
            if (childList != null && childList.size() > 0)
                for (FMCGChildModel model : childList) {
                    if (model.isUserSelected()) {
                        checkedItems.add(model.getId());
                    }
                }

            if (checkedItems.size() > 0) {
                checkedMap.put(((FMCGHeaderModel) pair.getKey()).getId(), checkedItems);
            }
        }

        switch (mType) {
            case Constants.FMCG:
                userRegistrationDetails.setFmcgCategoriesSelected(checkedMap);
                break;
            case Constants.SERVICE_CATEGORY:
                userRegistrationDetails.setNonASCCategoriesSelected(checkedMap);
                break;
            case Constants.SERVICE_BRAND:
                userRegistrationDetails.setNonASCBrandsSelected(checkedMap);
                break;
            case Constants.FMCG_BRANDS:
                userRegistrationDetails.setNonASCBrandsSelected(checkedMap);
                break;

        }
    }

    private boolean isValid() {
        expandableListDetail = fmcgExpandableAdapter.getUpdatedModelMap();

        Iterator it = expandableListDetail.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<FMCGChildModel> childList = (ArrayList<FMCGChildModel>) pair.getValue();
            for (FMCGChildModel model : childList) {
                if (model.isUserSelected()) {
                    return true;
                }
            }
        }
        return false;
    }


    public LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> getFmcgData() {
        LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> expandableListDetail = new LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>>();
        ArrayList<FMCGHeaderModel> categories = AppSession.getInstance(this).getCategories();
        for (FMCGHeaderModel model : categories) {
            if (model.getSubCategories() != null && model.getSubCategories().size() > 0)
                expandableListDetail.put(model, model.getSubCategories());
        }
        return expandableListDetail;
    }

    public static LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> getServicesCategoryData(UserRegistrationDetails userRegistrationDetails) {
        LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> expandableListDetail = new LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>>();

        List<FMCGChildModel> data1 = new ArrayList<FMCGChildModel>();
        data1.add(new FMCGChildModel("Mobile", false));
        data1.add(new FMCGChildModel("TV", false));
        data1.add(new FMCGChildModel("Washing Machine", false));
        data1.add(new FMCGChildModel("Laptop", false));
        data1.add(new FMCGChildModel("Fridge", false));
        data1.add(new FMCGChildModel("Bed", false));
        data1.add(new FMCGChildModel("Car", false));
        data1.add(new FMCGChildModel("Fan", false));

        List<FMCGChildModel> data2 = new ArrayList<FMCGChildModel>();
        data2.add(new FMCGChildModel("Mobile", false));
        data2.add(new FMCGChildModel("TV", false));
        data2.add(new FMCGChildModel("Washing Machine", false));
        data2.add(new FMCGChildModel("Laptop", false));

        List<FMCGChildModel> data3 = new ArrayList<FMCGChildModel>();
        data3.add(new FMCGChildModel("Mobile", false));
        data3.add(new FMCGChildModel("TV", false));
        data3.add(new FMCGChildModel("Washing Machine", false));
        data3.add(new FMCGChildModel("Laptop", false));
        data3.add(new FMCGChildModel("Fan", false));

        if (userRegistrationDetails != null) {
            ArrayList<String> autoEEServices = userRegistrationDetails.getAutoEEServices();

            if (autoEEServices.contains(Constants.ELECTRONICS))
                expandableListDetail.put(new FMCGHeaderModel("Sell Electronics", false), data1);
            if (autoEEServices.contains(Constants.ACCESSORIES))
                expandableListDetail.put(new FMCGHeaderModel("Sell Accessories", false), data2);
            if (autoEEServices.contains(Constants.INSURANCE))
                expandableListDetail.put(new FMCGHeaderModel("Insurance", false), data3);
            if (autoEEServices.contains(Constants.AMC))
                expandableListDetail.put(new FMCGHeaderModel("AMC", false), data1);
            if (autoEEServices.contains(Constants.REPAIR))
                expandableListDetail.put(new FMCGHeaderModel("Repair", false), data2);
        }
        return expandableListDetail;
    }

    public static LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> getServicesBrandData(UserRegistrationDetails userRegistrationDetails) {
        LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> expandableListDetail = new LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>>();

        List<FMCGChildModel> data1 = new ArrayList<FMCGChildModel>();
        data1.add(new FMCGChildModel("MI", false));
        data1.add(new FMCGChildModel("Lenovo", false));
        data1.add(new FMCGChildModel("Samsung", false));
        data1.add(new FMCGChildModel("VIVO", false));
        data1.add(new FMCGChildModel("OnePlus", false));
        data1.add(new FMCGChildModel("IPhone", false));
        data1.add(new FMCGChildModel("OPPO", false));
        data1.add(new FMCGChildModel("HTC", false));

        List<FMCGChildModel> data2 = new ArrayList<FMCGChildModel>();
        data2.add(new FMCGChildModel("Samsung", false));
        data2.add(new FMCGChildModel("LG", false));
        data2.add(new FMCGChildModel("Sony", false));
        data2.add(new FMCGChildModel("TLC", false));

        List<FMCGChildModel> data3 = new ArrayList<FMCGChildModel>();
        data3.add(new FMCGChildModel("LG", false));
        data3.add(new FMCGChildModel("SAMSUNG", false));
        data3.add(new FMCGChildModel("Philips", false));
        data3.add(new FMCGChildModel("C-Company", false));
        data3.add(new FMCGChildModel("D-Company", false));

        if (userRegistrationDetails != null) {
            HashMap<String, ArrayList<String>> nonASCCategories = userRegistrationDetails.getNonASCCategoriesSelected();
            Iterator it = nonASCCategories.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String key = (String) pair.getKey();

                if (key.contains("Mobile"))
                    expandableListDetail.put(new FMCGHeaderModel("Mobile", false), data1);
                if (key.contains("TV"))
                    expandableListDetail.put(new FMCGHeaderModel("TV", false), data2);
                if (key.contains("Washing Machine"))
                    expandableListDetail.put(new FMCGHeaderModel("Washing Machine", false), data3);
                if (key.contains("Fridge"))
                    expandableListDetail.put(new FMCGHeaderModel("Fridge", false), data1);
                if (key.contains("Laptop"))
                    expandableListDetail.put(new FMCGHeaderModel("Laptop", false), data2);
                if (key.contains("Bed"))
                    expandableListDetail.put(new FMCGHeaderModel("Bed", false), data1);
                if (key.contains("Car"))
                    expandableListDetail.put(new FMCGHeaderModel("Car", false), data2);
                if (key.contains("Fan"))
                    expandableListDetail.put(new FMCGHeaderModel("Fan", false), data2);

            }
        }
        return expandableListDetail;
    }

    private LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> getFMCGBrandsData(UserRegistrationDetails userRegistrationDetails) {

        LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> expandableListDetail = new LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>>();

        return expandableListDetail;
    }
}
