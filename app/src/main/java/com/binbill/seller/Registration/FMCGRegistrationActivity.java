package com.binbill.seller.Registration;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.Adapter.FMCGExpandableAdapter;
import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_fmcg_registration)
public class FMCGRegistrationActivity extends BaseActivity {

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

    @ViewById
    RelativeLayout just_sec_layout;

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
            case Constants.FMCG_BRANDS:
                makeFMCGBrandCall();
                title.setText(getString(R.string.select_brand_you_select));
                break;
//            case Constants.SERVICE_CATEGORY:
//                expandableListDetail = getServicesCategoryData();
//                title.setText(getString(R.string.select_services_categories));
//                break;
//            case Constants.SERVICE_BRAND:
//                expandableListDetail = getServicesBrandData(userRegistrationDetails);
//                title.setText(getString(R.string.select_brand_you_select));
//                break;
        }

        if (expandableListDetail != null) {
            expandableListTitle = new ArrayList<FMCGHeaderModel>(expandableListDetail.keySet());

            fmcgExpandableAdapter = new FMCGExpandableAdapter(this, expandableListTitle, expandableListDetail);
            expandable_list.setAdapter(fmcgExpandableAdapter);
        }

    }

    private void makeFMCGBrandCall() {

        just_sec_layout.setVisibility(View.VISIBLE);

        if (userRegistrationDetails != null) {
            HashMap<String, ArrayList<String>> fmcgCategoriesSelected = userRegistrationDetails.getFmcgCategoriesSelected();
            String commaSeparatedSubCategories = "";
            Iterator it = fmcgCategoriesSelected.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (Utility.isEmpty(commaSeparatedSubCategories))
                    commaSeparatedSubCategories = TextUtils.join(", ", (ArrayList<String>) pair.getValue());
                else
                    commaSeparatedSubCategories = commaSeparatedSubCategories + ", " + TextUtils.join(", ", (ArrayList<String>) pair.getValue());
            }
//            String commaSeparatedCategories = TextUtils.join(",", fmcgCategoriesSelected.keySet());

            new RetrofitHelper(this).fetchBrandsFromCategories(commaSeparatedSubCategories, new RetrofitHelper.RetrofitCallback() {
                @Override
                public void onResponse(String response) {

                    LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> map = new LinkedHashMap<>();
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        JSONArray resultArray = responseObject.getJSONArray("result");

                        HashMap<String, ArrayList<String>> fmcgSelectedMap = userRegistrationDetails.getFmcgBrandsSelected();

                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject object = resultArray.getJSONObject(i);
                            FMCGHeaderModel model = new FMCGHeaderModel(object.getString("category_name"), object.getString("category_id"), false);
                            model.setRefId(object.getString("ref_id"));

                            ArrayList<FMCGChildModel> childList = new ArrayList<>();

                            ArrayList<String> selectedBrands = fmcgSelectedMap.get(object.getString("category_id"));
                            JSONArray brandNames = object.optJSONArray("brands");
                            if (brandNames != null && brandNames.length() > 0) {
                                for (int j = 0; j < brandNames.length(); j++) {
                                    JSONObject brandObject = brandNames.getJSONObject(j);
                                    boolean selected = false;

                                    if (selectedBrands != null && selectedBrands.size() > 0)
                                        for (String selectedBrandId : selectedBrands) {
                                            if (selectedBrandId.equalsIgnoreCase(brandObject.getString("id")))
                                                selected = true;
                                        }
                                    FMCGChildModel childModel = new FMCGChildModel(brandObject.getString("brandName"), brandObject.getString("id"), selected);
                                    childList.add(childModel);
                                }
                                map.put(model, (List) childList);
                            }
                        }

                        expandableListDetail = map;

                        if (expandableListDetail != null) {
                            expandableListTitle = new ArrayList<FMCGHeaderModel>(expandableListDetail.keySet());

                            fmcgExpandableAdapter = new FMCGExpandableAdapter(FMCGRegistrationActivity.this, expandableListTitle, expandableListDetail);
                            expandable_list.setAdapter(fmcgExpandableAdapter);
                        }

                        just_sec_layout.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        just_sec_layout.setVisibility(View.GONE);
                        showSnackBar(getString(R.string.something_went_wrong));
                        finish();
                    }

                }

                @Override
                public void onErrorResponse() {
                    just_sec_layout.setVisibility(View.GONE);
                    showSnackBar(getString(R.string.something_went_wrong));
                    finish();
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

                expandable_list.setSelectedGroup(groupPosition);

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
            uploadData();
        }
    }

    private void uploadData() {
        switch (mType) {
            case Constants.FMCG:
                saveDataInUserRegistrationModel();
                makeUploadDataToServerCall();
                break;
            case Constants.FMCG_BRANDS:
                saveBrandInUserRegistrationModel();
                makeUploadDataToServerCallBrands();
                break;
        }
    }

    private void saveBrandInUserRegistrationModel() {
        HashMap<FMCGHeaderModel, ArrayList<String>> checkedMap = new HashMap<>();
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
                checkedMap.put((FMCGHeaderModel) pair.getKey(), checkedItems);
            }
        }
        userRegistrationDetails.setNonASCBrandsSelected(checkedMap);
    }

    private void makeUploadDataToServerCallBrands() {
        JSONArray mapArray = new JSONArray();
        try {
            if (userRegistrationDetails != null) {
                HashMap<FMCGHeaderModel, ArrayList<String>> fmcgBrandsSelected = userRegistrationDetails.getNonASCBrandsSelected();
                Iterator it = fmcgBrandsSelected.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    ArrayList<FMCGChildModel> childList = (ArrayList<FMCGChildModel>) pair.getValue();
                    JSONObject childObject = new JSONObject();
                    childObject.put("provider_type_id", 1);
                    childObject.put("sub_category_id", ((FMCGHeaderModel) pair.getKey()).getRefId());
                    childObject.put("category_4_id", ((FMCGHeaderModel) pair.getKey()).getId());
                    childObject.put("brand_ids", new JSONArray(childList));
                    mapArray.put(childObject);
                }
            }
        } catch (JSONException e) {

        }

        new RetrofitHelper(this).saveBrandsForSeller(mapArray, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                btn_submit.setVisibility(View.VISIBLE);
                btn_submit_progress.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {

                        JSONArray array = jsonObject.getJSONArray("seller_provider_types");
                        ApiHelper.parseAndSaveUserBrands(FMCGRegistrationActivity.this, array);

                        showSnackBar(getString(R.string.details_saved_successfully));

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        }, 1000);
                    } else {
                        showSnackBar(getString(R.string.something_went_wrong));
                    }

                } catch (JSONException e) {
                    showSnackBar(getString(R.string.something_went_wrong));
                }

            }

            @Override
            public void onErrorResponse() {
                btn_submit.setVisibility(View.VISIBLE);
                btn_submit_progress.setVisibility(View.GONE);

                showSnackBar(getString(R.string.something_went_wrong));
            }
        });
    }

    private void makeUploadDataToServerCall() {
        JSONArray mapArray = new JSONArray();
        try {
            if (userRegistrationDetails != null) {
                HashMap<String, ArrayList<String>> fmcgCategoriesSelected = userRegistrationDetails.getFmcgCategoriesSelected();
                Iterator it = fmcgCategoriesSelected.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    ArrayList<FMCGChildModel> childList = (ArrayList<FMCGChildModel>) pair.getValue();
                    JSONObject childObject = new JSONObject();
                    childObject.put("provider_type_id", 1);
                    childObject.put("sub_category_id", pair.getKey());
                    childObject.put("category_4_id", new JSONArray(childList));
                    mapArray.put(childObject);
                }
            }
        } catch (JSONException e) {

        }

        new RetrofitHelper(this).saveCategoriesForSeller(mapArray, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                btn_submit.setVisibility(View.VISIBLE);
                btn_submit_progress.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {

                        JSONArray array = jsonObject.getJSONArray("seller_provider_types");
                        ApiHelper.parseAndSaveUserCategories(FMCGRegistrationActivity.this, array);

                        showSnackBar(getString(R.string.details_saved_successfully));

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        }, 1000);
                    } else {
                        showSnackBar(getString(R.string.something_went_wrong));
                    }

                } catch (JSONException e) {
                    showSnackBar(getString(R.string.something_went_wrong));
                }

            }

            @Override
            public void onErrorResponse() {
                btn_submit.setVisibility(View.VISIBLE);
                btn_submit_progress.setVisibility(View.GONE);

                showSnackBar(getString(R.string.something_went_wrong));
            }
        });
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

        userRegistrationDetails.setFmcgCategoriesSelected(checkedMap);
//            case Constants.SERVICE_CATEGORY:
//                userRegistrationDetails.setNonASCCategoriesSelected(checkedMap);
//                break;
//            case Constants.SERVICE_BRAND:
//                userRegistrationDetails.setNonASCBrandsSelected(checkedMap);
//                break;

    }

    private boolean isValid() {
        expandableListDetail = fmcgExpandableAdapter.getUpdatedModelMap();

        /**
         * Always true
         */
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
        return true;
    }

    public LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> getFmcgData() {

        HashMap<String, ArrayList<String>> selectedList = AppSession.getInstance(this).getUserRegistrationDetails().getFmcgCategoriesSelected();

        LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> expandableListDetail = new LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>>();
        ArrayList<FMCGHeaderModel> categories = AppSession.getInstance(this).getCategories();
        for (FMCGHeaderModel model : categories) {
            if (model.getSubCategories() != null && model.getSubCategories().size() > 0) {

                ArrayList<FMCGChildModel> updatedSubCategories = new ArrayList<>();

                if (selectedList != null && selectedList.containsKey(model.getId())) {
                    ArrayList<String> selectedSubCat = selectedList.get(model.getId());
                    for (FMCGChildModel subCat : model.getSubCategories()) {
                        if (selectedSubCat.contains(subCat.getId()))
                            subCat.setUserSelected(true);

                        updatedSubCategories.add(subCat);
                    }
                } else
                    updatedSubCategories = model.getSubCategories();

                boolean isSelectAll = true;
                for (FMCGChildModel subCat : updatedSubCategories) {
                    if (!subCat.isUserSelected())
                        isSelectAll = false;
                }

                model.setShowSelectAll(isSelectAll);
                expandableListDetail.put(model, updatedSubCategories);
            }
        }
        return expandableListDetail;
    }

    public LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> getServicesCategoryData() {
        LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> expandableListDetail = new LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>>();
        ArrayList<FMCGHeaderModel> categories = AppSession.getInstance(this).getCategories();
        for (FMCGHeaderModel model : categories) {
            if (model.getSubCategories() != null && model.getSubCategories().size() > 0)
                expandableListDetail.put(model, model.getSubCategories());
        }
        return expandableListDetail;
    }

    public static LinkedHashMap<FMCGHeaderModel, List<FMCGChildModel>> getServicesBrandData(UserRegistrationDetails userRegistrationDetails) {
        return null;
    }
}
