package com.binbill.seller.APIHelper;

import android.content.Context;
import android.util.Log;

import com.binbill.seller.AppSession;
import com.binbill.seller.AssistedService.AssistedUserModel;
import com.binbill.seller.Model.DashboardModel;
import com.binbill.seller.Model.UserModel;
import com.binbill.seller.Order.Order;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Verification.RejectReasonModel;
import com.binbill.seller.Verification.VerificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

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

                            ArrayList<Order> verificationList = new Gson().fromJson(userArray.toString(), classType);
                            AppSession.getInstance(context).setOrderList(verificationList);
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
}
