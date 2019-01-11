package com.binbill.seller.Registration;

import android.content.Context;
import android.content.Intent;

import com.binbill.seller.AppSession;
import com.binbill.seller.AssistedService.AssistedUserModel;
import com.binbill.seller.Constants;
import com.binbill.seller.Dashboard.DashboardActivity_;
import com.binbill.seller.Model.BusinessDetailsModel;
import com.binbill.seller.Model.FMCGHeaderModel;
import com.binbill.seller.Model.FruitsVeg;
import com.binbill.seller.Model.MainCategory;
import com.binbill.seller.Model.StateCityModel;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by shruti.vig on 8/15/18.
 */

public class RegistrationResolver {


    public static Intent getNextIntent(Context context, int currentIndex) {
        return getNext(context, ++currentIndex);
    }

    public static int getResolvedIndexForNextScreen(String state) {
        switch (state) {
            case "fresh_seller":
                return -1;
            case "basic_details":
                return 0;
            case "business_details":
                return 2;
            case "dashboard":
                return 5;
        }

        return -1;
    }

    public static void parseAndSaveData(Context context, String jsonObject) {

        try {
            JSONObject data = new JSONObject(jsonObject);
            JSONObject payload = data.getJSONObject("data");

            UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(context).getUserRegistrationDetails();
            String sellerId = data.optString("seller_id");
            if (!Utility.isEmpty(sellerId)) {
                userRegistrationDetails.setId(sellerId);
                SharedPref.putString(context, SharedPref.SELLER_ID, sellerId);
                AppSession.getInstance(context).setSellerId(sellerId);
            }

            String mainCategoryId = data.optString("main_category_id");
            if (!Utility.isEmpty(mainCategoryId)) {
                MainCategory mainCategory = new MainCategory();
                mainCategory.setId(mainCategoryId);
                userRegistrationDetails.setMainCategory(mainCategory);
            }

            AppSession.getInstance(context).setUserRegistrationDetails(userRegistrationDetails);

            /**
             * Categories
             */
            JSONArray mainCategoryArray = payload.getJSONArray("categories");
            Type classType = new TypeToken<ArrayList<MainCategory>>() {
            }.getType();

            ArrayList<MainCategory> mainCategoryList = new Gson().fromJson(mainCategoryArray.toString(), classType);
            AppSession.getInstance(context).setMainCategoryList(mainCategoryList);

            /**
             * Business Types
             */
            JSONArray businessArray = payload.getJSONArray("business_types");
            classType = new TypeToken<ArrayList<BusinessDetailsModel>>() {
            }.getType();

            ArrayList<BusinessDetailsModel> businessList = new Gson().fromJson(businessArray.toString(), classType);
            AppSession.getInstance(context).setBusinessDetails(businessList);

            /**
             * Payment Modes
             * Reusing model MainCategory for this since contract is same
             */
            JSONArray paymentModesArray = payload.getJSONArray("payment_modes");
            classType = new TypeToken<ArrayList<MainCategory>>() {
            }.getType();

            ArrayList<MainCategory> paymentModes = new Gson().fromJson(paymentModesArray.toString(), classType);
            AppSession.getInstance(context).setPaymentModes(paymentModes);

            /**
             * States
             */
            JSONArray stateArray = payload.getJSONArray("states");
            classType = new TypeToken<ArrayList<StateCityModel>>() {
            }.getType();

            ArrayList<StateCityModel> states = new Gson().fromJson(stateArray.toString(), classType);
            AppSession.getInstance(context).setStateList(states);


            /**
             * Categories
             */
            JSONArray categoriesData = data.optJSONArray("categories");
            if (categoriesData != null) {
                classType = new TypeToken<ArrayList<FMCGHeaderModel>>() {
                }.getType();

                ArrayList<FMCGHeaderModel> categories = new Gson().fromJson(categoriesData.toString(), classType);
                AppSession.getInstance(context).setCategories(categories);
            }



            /**
             * Assisted service types
             */
            JSONArray servicesData = payload.optJSONArray("assisted_service_types");
            if (servicesData != null) {
                classType = new TypeToken<ArrayList<AssistedUserModel.ServiceType>>() {
                }.getType();

                ArrayList<AssistedUserModel.ServiceType> serviceTypes = new Gson().fromJson(servicesData.toString(), classType);
                AppSession.getInstance(context).setmAssistedServiceTypes(serviceTypes);
            }

        } catch (JSONException e) {

        }
    }


    private static Intent getNext(Context context, int indexToOpen) {

        Intent intent = null;
        switch (indexToOpen) {

            case -1:
                /**
                 * Invalid case.
                 * Shouldn;t come
                 */
                break;

            /**
             * Registration flow
             */
            case 0:
                intent = new Intent(context, RegisterLocationActivity_.class);
                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);
                /*intent = new Intent(context, UploadShopImageActivity_.class);
                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);*/
                /*intent = new Intent(context, BasicDetails2Activity_.class);
                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);*/
               /* intent = new Intent(context, MainCategoryActivity_.class);
                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);*/
               /* intent = new Intent(context, RegisterLocationActivity_.class);
                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);*/
                /*intent = new Intent(context, RegisterActivity_.class);
                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);*/
                break;

            /**
             * BasicDetails 1
             */
            case 1:

                intent = new Intent(context, RegisterActivity_.class);
                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);
                /*intent = new Intent(context, BasicDetails1Activity_.class);
                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);*/

                break;

            /**
             * BasicDetails 2
             */
            case 2:
                intent = new Intent(context, BasicDetails2Activity_.class);
                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);
                break;

            case 3:
                intent = new Intent(context, UploadShopImageActivity_.class);
                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);
                break;
            /**
             * Business Details
             */
            case 4:
                intent = new Intent(context, BusinessDetailsRegisterActivity_.class);
                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);
                break;

            case 5:
                intent = new Intent(context, BusinessDetailsActivity_.class);
                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);
                break;
            /**
             * Dashboard
             */
            case 6:
               // intent = new Intent(context, RegisterActivity_.class);
                intent = new Intent(context, DashboardActivity_.class);
                //intent = new Intent(context, UploadShopImageActivity_.class);
                break;
            /****************************************************************************************************
             ****************************************************************************************************
             Dropped category and brand selection from onboarding
             ****************************************************************************************************
             ****************************************************************************************************/
            /**
             * Select Services
             */
            case 7: {
                UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(context).getUserRegistrationDetails();
                MainCategory mainCategory = userRegistrationDetails.getMainCategory();

                if (mainCategory != null) {
                    /**
                     * AUTOMOBILE/ ELECTRONINCS / ELECTRICAL
                     */
                    /*if (mainCategory.getId().equalsIgnoreCase("11") || mainCategory.getId().equalsIgnoreCase("12") ||
                            mainCategory.getId().equalsIgnoreCase("138") || mainCategory.getId().equalsIgnoreCase("139")) {

                        intent = new Intent(context, AutoEEServicesActivity_.class);
                        intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);

                    } else */if (mainCategory.getId().equalsIgnoreCase("28")) {
                        /**
                         * KIRANA STORE
                         */
                        intent = new Intent(context, FMCGRegistrationActivity_.class);
                        intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);
                        intent.putExtra(Constants.BUSINESS_TYPE, Constants.FMCG);
                    } else {
                        intent = new Intent(context, DashboardActivity_.class);
                    }
                }
                break;
            }
            case 8: {

                UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(context).getUserRegistrationDetails();
                MainCategory mainCategory = userRegistrationDetails.getMainCategory();

                if (mainCategory != null) {
                    /**
                     * AUTOMOBILE/ ELECTRONINCS / ELECTRICAL
                     */
                    if (mainCategory.getId().equalsIgnoreCase("11") || mainCategory.getId().equalsIgnoreCase("12") ||
                            mainCategory.getId().equalsIgnoreCase("138") || mainCategory.getId().equalsIgnoreCase("139")) {

                        ArrayList<String> servicesList = userRegistrationDetails.getAutoEEServices();

                        if (servicesList != null && servicesList.size() > 0) {

                            if (servicesList.contains(Constants.ASC)) {

                                /**
                                 * ASC, STEP 1
                                 * Select Category
                                 */
                                intent = new Intent(context, ASCCategoryBrandActivity_.class);
                                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);
                                intent.putExtra(Constants.ASC_SELECTION_TYPE, Constants.CATEGORY);

                            } else {
                                /**
                                 * Insurance / accessories / electronics / repair / amc
                                 * STEP 1, Select Category
                                 */
                                intent = new Intent(context, FMCGRegistrationActivity_.class);
                                intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);
                                intent.putExtra(Constants.BUSINESS_TYPE, Constants.SERVICE_CATEGORY);
                            }
                        }

                    } else if (mainCategory.getId().equalsIgnoreCase("28")) {
                        /**
                         * KIRANA STORE
                         */
                        //Open Dashboard.. We are done
                        intent = new Intent(context, FMCGRegistrationActivity_.class);
                        intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);
                        intent.putExtra(Constants.BUSINESS_TYPE, Constants.FMCG_BRANDS);
                    } else {
                        intent = new Intent(context, DashboardActivity_.class);
                    }
                }
                break;
            }
            case 9: {
                UserRegistrationDetails userRegistrationDetails = AppSession.getInstance(context).getUserRegistrationDetails();
                ArrayList<String> servicesList = userRegistrationDetails.getAutoEEServices();

                if (servicesList != null && servicesList.size() > 0) {

                    if (servicesList.contains("ASC")) {

                        /**
                         * ASC, STEP 2
                         * Select Brand
                         */
                        intent = new Intent(context, ASCCategoryBrandActivity_.class);
                        intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);
                        intent.putExtra(Constants.ASC_SELECTION_TYPE, Constants.BRAND);

                    } else {
                        /**
                         * Services, STEP 2
                         * Select Brand
                         */
                        intent = new Intent(context, FMCGRegistrationActivity_.class);
                        intent.putExtra(Constants.REGISTRATION_INDEX, indexToOpen);
                        intent.putExtra(Constants.BUSINESS_TYPE, Constants.SERVICE_BRAND);

                    }
                }
                break;
            }
        }

        return intent;

    }
}
