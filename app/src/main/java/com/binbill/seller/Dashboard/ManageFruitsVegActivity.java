package com.binbill.seller.Dashboard;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.binbill.seller.Adapter.MainCategoryAdapter;
import com.binbill.seller.Adapter.ManageFruitVegAdapter;
import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.CustomViews.SquareAppButton;
import com.binbill.seller.Model.SkuItem;
import com.binbill.seller.Model.SkuMeasurement;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

@EActivity(R.layout.activity_manage_fruits_veg)
public class ManageFruitsVegActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById(R.id.btn_no_data)
    SquareAppButton btnNoData;

    @ViewById(R.id.sl_pull_to_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @ViewById
    RecyclerView rv_fruits_veg_list;

    @ViewById
    LinearLayout shimmer_view_container, no_data_layout;

    @ViewById
    TextView tv_no_data;

    @ViewById(R.id.btn_add_data)
    SquareAppButton btnSave;

    UserRegistrationDetails userRegistrationDetails;
    String title;
    String toolbar_text1 = "";
    ManageFruitVegAdapter mAdapter;
    public static boolean isOthers;

    @AfterViews
    public void initiateViews() {
        setUpToolbar();
        getIntentData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                rv_fruits_veg_list.setVisibility(View.GONE);
                shimmer_view_container.setVisibility(View.VISIBLE);
                no_data_layout.setVisibility(View.GONE);
                makeAPICall();

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAPICall();
            }
        });

        rv_fruits_veg_list.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                ManageFruitVegAdapter.hasFocused = true;
            }
        });

    }


    private void updateAPICall() {
        HashMap<String, String> hmap = new HashMap<>();
        HashMap<String, String> map = new HashMap<>();
        JSONArray fruit_veg = new JSONArray();
        String checkKey = "";

        /*for(int k = 0 ; k < ManageFruitVegAdapter.itemChange_ids.size(); k++){
            map = ManageFruitVegAdapter.itemChange_ids.get(k);
            for ( String key : map.keySet() ) {
                System.out.println( key );
                checkKey = key;
            }
            String value = map.get(checkKey);
            System.out.println( value );
            Log.v("TAGGGGGG", checkKey+"->"+ value);
        }

        for(int k1 = 0 ; k1 < ManageFruitVegAdapter.valueChange_ids.size(); k1++){
            map = ManageFruitVegAdapter.valueChange_ids.get(k1);
            for ( String key : map.keySet() ) {
                System.out.println( key );
                checkKey = key;
            }
            String value = map.get(checkKey);
            System.out.println( value );
            Log.v("TAGGGGGG", checkKey+"->"+ value);
        }*/
        ArrayList<SkuItem> skuList = AppSession.getInstance(ManageFruitsVegActivity.this).getSkuItemList();
        int k = 0;
        int k1 = 0;
        for (int p = 0; p < ManageFruitVegAdapter.posEdit.size(); p++) {
            int positionCheck = ManageFruitVegAdapter.posEdit.get(p);
            ArrayList<SkuMeasurement> detailsArray = skuList.get(positionCheck).getSkuMeasurements();
            String value = "";
            String MrpValue = "";
            String valueCheck = "";

            for (; k < ManageFruitVegAdapter.itemChange_ids.size(); k++) {
                hmap = ManageFruitVegAdapter.itemChange_ids.get(k);
                for (String key : hmap.keySet()) {
                    System.out.println(key);
                    checkKey = key;
                }
                valueCheck = hmap.get(checkKey);
                System.out.println(valueCheck);
                ManageFruitVegAdapter.itemChange_ids.remove(k);
                Log.v("TAGGGGGG", checkKey + "->" + valueCheck);
                break;
            }

            for (; k1 < ManageFruitVegAdapter.valueChange_ids.size(); k1++) {
                hmap = ManageFruitVegAdapter.valueChange_ids.get(k1);
                for (String key : hmap.keySet()) {
                    System.out.println(key);
                    checkKey = key;
                }
                MrpValue = hmap.get(checkKey);
                System.out.println(MrpValue);
                ManageFruitVegAdapter.valueChange_ids.remove(k1);
                Log.v("TAGGGGGG", MrpValue);
                break;
            }
            for (int l = 0; l < detailsArray.size(); l++) {
                JSONObject jsonObject = new JSONObject();
                try {


                    if (valueCheck.equalsIgnoreCase(detailsArray.get(l).getId())) {
                        try {
                            jsonObject.put("sku_id",
                                    Integer.parseInt(detailsArray.get(l).getSku_id()));
                            jsonObject.put("sku_measurement_id",
                                    Integer.parseInt(detailsArray.get(l).getId()));
                            jsonObject.put("seller_mrp", Integer.parseInt(MrpValue));
                            fruit_veg.put(jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            jsonObject.put("sku_id",
                                    Integer.parseInt(detailsArray.get(l).getSku_id()));
                            jsonObject.put("sku_measurement_id",
                                    Integer.parseInt(detailsArray.get(l).getId()));
                            if (isOthers) {
                                if (detailsArray.get(l).getMeasurementValue().equalsIgnoreCase("100")) {
                                    jsonObject.put("seller_mrp", (Integer.parseInt(MrpValue) * 2));
                                } else if (detailsArray.get(l).getMeasurementValue().equalsIgnoreCase("250")) {
                                    jsonObject.put("seller_mrp", (Integer.parseInt(MrpValue) * 5));
                                } else if (detailsArray.get(l).getMeasurementValue().equalsIgnoreCase("500")) {
                                    jsonObject.put("seller_mrp", (Integer.parseInt(MrpValue) * 10));
                                } else if (detailsArray.get(l).getMeasurementValue().equalsIgnoreCase("1")) {
                                    jsonObject.put("seller_mrp", (Integer.parseInt(MrpValue) * 20));
                                } else {
                                    jsonObject.put("seller_mrp", (Integer.parseInt(MrpValue)));
                                }
                            } else {
                                if (detailsArray.get(l).getMeasurementValue().equalsIgnoreCase("250")) {
                                    jsonObject.put("seller_mrp", (Integer.parseInt(MrpValue) / 2));
                                } else if (detailsArray.get(l).getMeasurementValue().equalsIgnoreCase("1")) {
                                    jsonObject.put("seller_mrp", (Integer.parseInt(MrpValue) * 2));
                                } else if (detailsArray.get(l).getMeasurementValue().equalsIgnoreCase("2")) {
                                    jsonObject.put("seller_mrp", (Integer.parseInt(MrpValue) * 4));
                                } else if (detailsArray.get(l).getMeasurementValue().equalsIgnoreCase("3")) {
                                    jsonObject.put("seller_mrp", (Integer.parseInt(MrpValue) * 6));
                                } else if (detailsArray.get(l).getMeasurementValue().equalsIgnoreCase("5")) {
                                    jsonObject.put("seller_mrp", (Integer.parseInt(MrpValue) * 10));
                                } else {
                                    jsonObject.put("seller_mrp", (Integer.parseInt(MrpValue)));
                                }
                            }

                            fruit_veg.put(jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {


                }
            }

        }

        try {
            String priceFormatted = new JSONTokener(fruit_veg.toString()).nextValue().toString();
            //  object.put("delivery_rules", priceFormatted);
              /*  String[] dsf = new String[list.size()];
                list.toArray(dsf);*/
            map.put("fruit_veg", priceFormatted);
        } catch (Exception e) {
            e.printStackTrace();
        }


        new RetrofitHelper(this).updateFruitsVeg(userRegistrationDetails.getId(),
                map, new RetrofitHelper.RetrofitCallback() {
                    @Override
                    public void onResponse(String response) {
                        handleResponse(response);
                    }

                    @Override
                    public void onErrorResponse() {
                        handleError();
                    }
                });

    }

    private void handleError() {

        showSnackBar(getString(R.string.something_went_wrong));
    }

    private void handleResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("status")) {
                ManageFruitVegAdapter.itemChange_ids.clear();
                ManageFruitVegAdapter.valueChange_ids.clear();
                ManageFruitVegAdapter.posEdit.clear();
                int size = ManageFruitVegAdapter.itemChange_ids.size();
                int size1 = ManageFruitVegAdapter.valueChange_ids.size();
                Toast.makeText(this, toolbar_text1 + " "
                        + "MRP saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else
                handleError();
        } catch (JSONException e) {
            handleError();
        }
    }

    private void setUpToolbar() {
        toolbar_text1 = getIntent().getStringExtra("Title");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbarText.setText(toolbar_text1);

        if (toolbar_text1.equalsIgnoreCase("Other")) {
            isOthers = true;
        } else {
            isOthers = false;
        }
    }

    private void getIntentData() {
        title = getIntent().getStringExtra("Id");
        userRegistrationDetails = AppSession.getInstance(this).getUserRegistrationDetails();
        btnNoData.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
        btnSave.setText("Save");
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeAPICall();
    }

    private void makeAPICall() {
        rv_fruits_veg_list.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.VISIBLE);
        no_data_layout.setVisibility(View.GONE);
        int categoryId = Integer.parseInt(title);
        new RetrofitHelper(this).fetchFruitsVeg(userRegistrationDetails.getFruitsCategoryId(),
                categoryId, new RetrofitHelper.RetrofitCallback() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("status")) {

                                if (jsonObject.optJSONObject("result") != null) {
                                    ManageFruitVegAdapter.itemChange_ids.clear();
                                    ManageFruitVegAdapter.valueChange_ids.clear();
                                    ManageFruitVegAdapter.posEdit.clear();
                                    ManageFruitVegAdapter.hasFocused = true;
                                    JSONObject jObject = jsonObject.getJSONObject("result");
                                    JSONArray skuArray = jObject.getJSONArray("sku_items");
                                    Type classType = new TypeToken<ArrayList<SkuItem>>() {
                                    }.getType();

                                    ArrayList<SkuItem> skuList = new Gson().fromJson(skuArray.toString(), classType);
                                    AppSession.getInstance(ManageFruitsVegActivity.this).setSkuItemList(skuList);
                                    handleResponse();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse() {
                        shimmer_view_container.setVisibility(View.GONE);
                        rv_fruits_veg_list.setVisibility(View.GONE);
                        no_data_layout.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void handleResponse() {
        swipeRefreshLayout.setRefreshing(false);
        ArrayList<SkuItem> skuList = AppSession.getInstance(ManageFruitsVegActivity.this).getSkuItemList();
        if (skuList != null && skuList.size() > 0) {
            setUpData(skuList);
        } else {
            rv_fruits_veg_list.setVisibility(View.GONE);
            shimmer_view_container.setVisibility(View.GONE);
            no_data_layout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setUpData(ArrayList<SkuItem> list) {
        rv_fruits_veg_list.setHasFixedSize(true);
        rv_fruits_veg_list.setLayoutManager(new LinearLayoutManager(this));
        rv_fruits_veg_list.setVisibility(View.VISIBLE);
        shimmer_view_container.setVisibility(View.GONE);
        no_data_layout.setVisibility(View.GONE);
        ManageFruitVegAdapter.hasFocused = true;
        mAdapter = new ManageFruitVegAdapter(list, this);
        rv_fruits_veg_list.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // ManageFruitVegAdapter.hasFocused = false;
        ManageFruitVegAdapter.itemChange_ids.clear();
        ManageFruitVegAdapter.valueChange_ids.clear();
        ManageFruitVegAdapter.posEdit.clear();
    }
}
