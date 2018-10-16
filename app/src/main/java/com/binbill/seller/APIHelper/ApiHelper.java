package com.binbill.seller.APIHelper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.binbill.seller.AppSession;
import com.binbill.seller.AssistedService.AssistedUserModel;
import com.binbill.seller.Model.DashboardModel;
import com.binbill.seller.Model.UserModel;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.Order.Order;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SplashActivity;
import com.binbill.seller.UpgradeHelper;
import com.binbill.seller.Utility;
import com.binbill.seller.Verification.RejectReasonModel;
import com.binbill.seller.Verification.VerificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by shruti.vig on 8/28/18.
 */

public class ApiHelper {

    public static void makeDashboardDataCall(final Context context, RetrofitHelper.RetrofitCallback retrofitCallback) {

        String identifier = AppSession.getInstance(context).getSellerId();

        new RetrofitHelper(context).fetchDashboardData(identifier, retrofitCallback);
    }

    public static void makeDashboardDataCall(final Context context) {

        String identifier = AppSession.getInstance(context).getSellerId();

        new RetrofitHelper(context).fetchDashboardData(identifier, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        Type classType = new TypeToken<DashboardModel>() {
                        }.getType();

                        DashboardModel dashboardModel = new Gson().fromJson(jsonObject.toString(), classType);
                        AppSession.getInstance(context).setDashboardData(dashboardModel);

                        if(dashboardModel.getForceUpdate() != null){
                            if (dashboardModel.getForceUpdate().equalsIgnoreCase("TRUE"))
                                UpgradeHelper.invokeUpdateDialog((Activity) context, true);
                            else if (dashboardModel.getForceUpdate().equalsIgnoreCase("FALSE"))
                                UpgradeHelper.invokeUpdateDialog((Activity) context, false);
                        }
                        UpgradeHelper.invokeUpdateDialog((Activity)context, false);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onErrorResponse() {

            }
        });
    }

    public static void fetchAllCustomer(final Context context, RetrofitHelper.RetrofitCallback retrofitCallback) {
        new RetrofitHelper(context).fetchUsersForSeller(retrofitCallback);
    }

    public static void fetchJobsForVerification(final Context context) {

        new RetrofitHelper(context).fetchJobsForVerification(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.optJSONArray("result") != null) {
                            JSONArray userArray = jsonObject.getJSONArray("result");
                            Type classType = new TypeToken<ArrayList<VerificationModel>>() {
                            }.getType();

                            ArrayList<VerificationModel> verificationList = new Gson().fromJson(userArray.toString(), classType);
                            AppSession.getInstance(context).setVerificationJobList(verificationList);
                        }

                        if (jsonObject.optJSONArray("reasons") != null) {
                            JSONArray userArray = jsonObject.getJSONArray("reasons");
                            Type classType = new TypeToken<ArrayList<RejectReasonModel>>() {
                            }.getType();

                            ArrayList<RejectReasonModel> rejectReasonList = new Gson().fromJson(userArray.toString(), classType);
                            AppSession.getInstance(context).setRejectReasonList(rejectReasonList);
                        }
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onErrorResponse() {

            }
        });
    }

    public static void fetchJobsForVerification(final Context context, RetrofitHelper.RetrofitCallback retrofitCallback) {
        new RetrofitHelper(context).fetchJobsForVerification(retrofitCallback);
    }

    public static void fetchAllCustomer(final Context context) {

        new RetrofitHelper(context).fetchUsersForSeller(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.optJSONArray("result") != null) {
                            JSONArray userArray = jsonObject.getJSONArray("result");
                            Type classType = new TypeToken<ArrayList<UserModel>>() {
                            }.getType();

                            ArrayList<UserModel> userList = new Gson().fromJson(userArray.toString(), classType);
                            AppSession.getInstance(context).setMyCustomerList(userList);
                        }
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onErrorResponse() {

            }
        });
    }

    public static void fetchAllAssistedServices(final Context context, RetrofitHelper.RetrofitCallback retrofitCallback) {
        new RetrofitHelper(context).fetchAssistedServices(retrofitCallback);
    }

    public static void fetchAllAssistedServices(final Context context) {

        new RetrofitHelper(context).fetchAssistedServices(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.optJSONArray("result") != null) {
                            JSONArray userArray = jsonObject.getJSONArray("result");
                            Type classType = new TypeToken<ArrayList<AssistedUserModel>>() {
                            }.getType();

                            ArrayList<AssistedUserModel> userList = new Gson().fromJson(userArray.toString(), classType);
                            AppSession.getInstance(context).setAssistedServiceList(userList);
                        }
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onErrorResponse() {

                Log.d("SHRUTI", "Error");
            }
        });
    }


    public static void fetchOrders(final Context context) {

        new RetrofitHelper(context).fetchOrders(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.optJSONArray("result") != null) {
                            JSONArray userArray = jsonObject.getJSONArray("result");
                            Type classType = new TypeToken<ArrayList<Order>>() {
                            }.getType();

                            ArrayList<Order> orderList = new Gson().fromJson(userArray.toString(), classType);
                            AppSession.getInstance(context).setOrderList(orderList);
                        }
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onErrorResponse() {

            }
        });
    }

    public static void fetchOrders(final Context context, RetrofitHelper.RetrofitCallback retrofitCallback) {
        new RetrofitHelper(context).fetchOrders(retrofitCallback);
    }

    public static void getUserSelectedCategories(final Context context) {
        new RetrofitHelper(context).fetchSellerCategories(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        if (jsonObject.optJSONArray("result") != null) {
                            JSONArray categoryArray = jsonObject.getJSONArray("result");
                            parseAndSaveUserCategories(context, categoryArray);
                        }
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onErrorResponse() {

            }
        });
    }

    public static void parseAndSaveUserCategories(Context context, JSONArray categoryArray) {

        HashMap<String, ArrayList<String>> map = new HashMap<>();
        HashMap<String, ArrayList<String>> brandMap = new HashMap<>();

        try {
            for (int i = 0; i < categoryArray.length(); i++) {
                JSONObject categoryObject = categoryArray.getJSONObject(i);
                String categoryId = categoryObject.getString("sub_category_id");
                ArrayList<String> list = new ArrayList<>();

                JSONArray categoryBrands = categoryObject.getJSONArray("category_brands");

                for (int j = 0; j < categoryBrands.length(); j++) {
                    JSONObject jsonObject = (JSONObject) categoryBrands.get(j);
                    String catId = jsonObject.getString("category_4_id");
                    list.add(catId);

                    JSONArray brandArray = jsonObject.optJSONArray("brand_ids");
                    if (brandArray != null && brandArray.length() > 0)
                        brandMap.put(catId, Utility.convert(brandArray));
                }

                map.put(categoryId, list);
            }

            UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(context).getUserRegistrationDetails();
            userRegistrationDetails.setFmcgCategoriesSelected(map);
            userRegistrationDetails.setFmcgBrandsSelected(brandMap);

            AppSession.getInstance(context).setUserRegistrationDetails(userRegistrationDetails);
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    public static void parseAndSaveUserBrands(Context context, JSONArray brandArray) {

        HashMap<String, ArrayList<String>> map = new HashMap<>();

        try {
            for (int i = 0; i < brandArray.length(); i++) {
                JSONObject categoryObject = brandArray.getJSONObject(i);
                String categoryId = categoryObject.getString("category_4_id");
                ArrayList<String> list = map.get(categoryId);
                list = new ArrayList<>(Arrays.asList(categoryObject.getString("brand_ids").split(",")));

                map.put(categoryId, list);
            }

            UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(context).getUserRegistrationDetails();
            userRegistrationDetails.setFmcgBrandsSelected(map);
            AppSession.getInstance(context).setUserRegistrationDetails(userRegistrationDetails);
        } catch (JSONException e) {

        }

    }
}
