package com.binbill.seller;

import android.content.Context;

import com.binbill.seller.AssistedService.AssistedUserModel;
import com.binbill.seller.Dashboard.ProfileModel;
import com.binbill.seller.Model.BusinessDetailsModel;
import com.binbill.seller.Model.DashboardModel;
import com.binbill.seller.Model.FMCGHeaderModel;
import com.binbill.seller.Model.FruitsVeg;
import com.binbill.seller.Model.MainCategory;
import com.binbill.seller.Model.SkuItem;
import com.binbill.seller.Model.StateCityModel;
import com.binbill.seller.Model.UserModel;
import com.binbill.seller.Model.UserRegistrationDetails;
import com.binbill.seller.Order.Order;
import com.binbill.seller.Verification.RejectReasonModel;
import com.binbill.seller.Verification.VerificationModel;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by shruti.vig on 8/18/18.
 */

public class AppSession {

    private static AppSession INSTANCE;
    private final AppSessionData mAppSessionData = new AppSessionData();
    private final Context mContext;

    public AppSession(Context context) {
        this.mContext = context;
        setUserRegistrationDetails(new UserRegistrationDetails());
    }

    public static synchronized AppSession getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AppSession(context);
        }
        return INSTANCE;
    }

    public static synchronized void setInstanceToNull() {
        INSTANCE = null;
    }

    public void setDeliveryTimeList(JSONArray list) {
        mAppSessionData.setDeliveryTimeList(list);
    }

    public JSONArray getDeliveryTimeList() {
        return mAppSessionData.getDeliveryTimeList();
    }

    private class AppSessionData {
        private UserRegistrationDetails userRegistrationDetails;
        private String mobile;
        private ArrayList<MainCategory> mainCategoryList;
        private ArrayList<FruitsVeg> fruitsVegList;
        private ArrayList<SkuItem> skuItemList;
        private ArrayList<MainCategory> paymentModes;
        private ArrayList<StateCityModel> stateList;
        private ArrayList<BusinessDetailsModel> businessDetails;
        private ArrayList<FMCGHeaderModel> categories;
        private DashboardModel dashboardData;
        private String sellerId;
        private ArrayList<UserModel> myCustomerList;
        private ArrayList<AssistedUserModel> assistedServiceList;
        private ArrayList<AssistedUserModel.ServiceType> mAssistedServiceTypes;
        private ArrayList<VerificationModel> jobVerificationList;
        private ArrayList<RejectReasonModel> rejectReasonList;
        private ArrayList<Order> orderList;
        private ProfileModel profile;
        private String notificationType;
        private String notificationOrderId;
        private JSONArray deliveryTimeList;

        private void setSellerId(String id) {
            this.sellerId = id;
        }

        private String getSellerId() {
            return sellerId;
        }

        private ArrayList<BusinessDetailsModel> getBusinessDetails() {
            return businessDetails;
        }

        private void setBusinessDetails(ArrayList<BusinessDetailsModel> businessDetails) {
            this.businessDetails = businessDetails;
        }

        private UserRegistrationDetails getUserRegistrationDetails() {
            return userRegistrationDetails;
        }

        private void setUserRegistrationDetails(UserRegistrationDetails userRegistrationDetails) {
            this.userRegistrationDetails = userRegistrationDetails;
        }

        private String getMobile() {
            return this.mobile;
        }

        private void setMobile(String mobile) {
            this.mobile = mobile;
        }

        private void setMainCategoryList(ArrayList<MainCategory> list) {
            this.mainCategoryList = list;
        }

        private ArrayList<MainCategory> getMainCategoryList() {
            return this.mainCategoryList;
        }

        private void setPaymentModes(ArrayList<MainCategory> modes) {
            this.paymentModes = modes;
        }

        private ArrayList<MainCategory> getPaymentModes() {
            return paymentModes;
        }

        private ArrayList<StateCityModel> getStateList() {
            return this.stateList;
        }

        private void setStateList(ArrayList<StateCityModel> stateList) {
            this.stateList = stateList;
        }

        private void setCategories(ArrayList<FMCGHeaderModel> cat) {
            this.categories = cat;
        }

        private ArrayList<FMCGHeaderModel> getCategories() {
            return categories;
        }

        private void setDashboardData(DashboardModel dashboard) {
            this.dashboardData = dashboard;
        }

        private DashboardModel getDashboardData() {
            return dashboardData;
        }

        private void setMyCustomerList(ArrayList<UserModel> list) {
            this.myCustomerList = list;
        }

        private void setFruitsVegList(ArrayList<FruitsVeg> list) {
            this.fruitsVegList = list;
        }

        private ArrayList<FruitsVeg> getFruitsVegList() {
            return fruitsVegList;
        }

        private void setSkuItemList(ArrayList<SkuItem> list) {
            this.skuItemList = list;
        }

        private ArrayList<SkuItem> getSkuItemList() {
            return skuItemList;
        }


        private ArrayList<UserModel> getMyCustomerList() {
            return myCustomerList;
        }

        private ArrayList<AssistedUserModel> getAssistedServiceList() {
            return assistedServiceList;
        }

        private void setAssistedServiceList(ArrayList<AssistedUserModel> list) {
            this.assistedServiceList = list;
        }

        private ArrayList<AssistedUserModel.ServiceType> getAssistedServiceTypes() {
            return this.mAssistedServiceTypes;
        }

        private JSONArray getDeliveryTimeList() {
            return deliveryTimeList;
        }

        private void setmAssistedServiceTypes(ArrayList<AssistedUserModel.ServiceType> mAssistedServiceTypes) {
            this.mAssistedServiceTypes = mAssistedServiceTypes;
        }

        private ArrayList<VerificationModel> getVerificationJobList() {
            return jobVerificationList;
        }

        private void setVerificationJobList(ArrayList<VerificationModel> list) {
            this.jobVerificationList = list;
        }

        private ArrayList<RejectReasonModel> getRejectReasonList() {
            return rejectReasonList;
        }

        private void setRejectReasonList(ArrayList<RejectReasonModel> list) {
            this.rejectReasonList = list;
        }

        private ArrayList<Order> getOrderList() {
            return orderList;
        }

        private void setOrderList(ArrayList<Order> list) {
            this.orderList = list;
        }


        private ProfileModel getSellerProfile() {
            return profile;
        }

        private void setSellerProfile(ProfileModel list) {
            this.profile = list;
        }

        private void setNotificationIntent(String stringExtra) {
            this.notificationType = stringExtra;
        }

        private String getNotificationIntent() {
            return this.notificationType;
        }

        private void setNotificationOrderId(String notificationOrderId) {
            this.notificationOrderId = notificationOrderId;
        }

        private String getNotificationOrderId() {
            return notificationOrderId;
        }

        private void setDeliveryTimeList(JSONArray list) {
            this.deliveryTimeList = list;
        }
    }

    public void setNotificationIntent(String stringExtra) {
        mAppSessionData.setNotificationIntent(stringExtra);
    }

    public String getNotificationIntent() {
        return mAppSessionData.getNotificationIntent();
    }

    public void setNotificationOrderId(String id) {
        mAppSessionData.setNotificationOrderId(id);
    }

    public String getNotificationOrderId() {
        return mAppSessionData.getNotificationOrderId();
    }

    public ProfileModel getSellerProfile() {
        return mAppSessionData.getSellerProfile();
    }

    public void setSellerProfile(ProfileModel model) {
        mAppSessionData.setSellerProfile(model);
    }

    public ArrayList<Order> getOrderList() {
        return mAppSessionData.getOrderList();
    }

    public void setOrderList(ArrayList<Order> list) {
        mAppSessionData.setOrderList(list);
    }

    public ArrayList<RejectReasonModel> getRejectReasonList() {
        return mAppSessionData.getRejectReasonList();
    }

    public void setRejectReasonList(ArrayList<RejectReasonModel> list) {
        mAppSessionData.setRejectReasonList(list);
    }

    public ArrayList<VerificationModel> getVerificationJobList() {
        return mAppSessionData.getVerificationJobList();
    }

    public void setVerificationJobList(ArrayList<VerificationModel> list) {
        mAppSessionData.setVerificationJobList(list);
    }

    public ArrayList<AssistedUserModel.ServiceType> getAssistedServiceTypes() {
        return mAppSessionData.getAssistedServiceTypes();
    }

    public void setmAssistedServiceTypes(ArrayList<AssistedUserModel.ServiceType> mAssistedServiceTypes) {
        mAppSessionData.setmAssistedServiceTypes(mAssistedServiceTypes);
    }

    public void setCategories(ArrayList<FMCGHeaderModel> catList) {
        mAppSessionData.setCategories(catList);
    }

    public void setSellerId(String id) {
        mAppSessionData.setSellerId(id);
    }

    public String getSellerId() {

        if (mAppSessionData.getSellerId() == null)
            setSellerId(SharedPref.getString(mContext, SharedPref.SELLER_ID));

        return mAppSessionData.getSellerId();
    }


    public void setDashboardData(DashboardModel dashboardModel) {
        mAppSessionData.setDashboardData(dashboardModel);
    }

    public DashboardModel getDashboardData() {
        return mAppSessionData.getDashboardData();
    }

    public void setMyCustomerList(ArrayList<UserModel> userModel) {
        mAppSessionData.setMyCustomerList(userModel);
    }

    public void setFruitsVegList(ArrayList<FruitsVeg> userModel) {
        mAppSessionData.setFruitsVegList(userModel);
    }

    public void setSkuItemList(ArrayList<SkuItem> userModel) {
        mAppSessionData.setSkuItemList(userModel);
    }
    public ArrayList<SkuItem> getSkuItemList() {
        return mAppSessionData.getSkuItemList();
    }

    public ArrayList<UserModel> getMyCustomerList() {
        return mAppSessionData.getMyCustomerList();
    }

    public ArrayList<FruitsVeg> getFruitsVegList() {
        return mAppSessionData.getFruitsVegList();
    }

    public void setAssistedServiceList(ArrayList<AssistedUserModel> userModel) {
        mAppSessionData.setAssistedServiceList(userModel);
    }

    public ArrayList<AssistedUserModel> getAssistedServiceList() {
        return mAppSessionData.getAssistedServiceList();
    }

    public ArrayList<FMCGHeaderModel> getCategories() {
        return mAppSessionData.getCategories();
    }

    public ArrayList<BusinessDetailsModel> getBusinessDetails() {
        return mAppSessionData.getBusinessDetails();
    }

    public void setBusinessDetails(ArrayList<BusinessDetailsModel> details) {
        mAppSessionData.setBusinessDetails(details);
    }

    public ArrayList<StateCityModel> getStateList() {
        return mAppSessionData.getStateList();
    }

    public void setStateList(ArrayList<StateCityModel> list) {
        mAppSessionData.setStateList(list);
    }

    public void setPaymentModes(ArrayList<MainCategory> paymentModes) {
        mAppSessionData.setPaymentModes(paymentModes);
    }

    public ArrayList<MainCategory> getPaymentModes() {
        return mAppSessionData.getPaymentModes();
    }

    public void setMainCategoryList(ArrayList<MainCategory> mainCategoryList) {
        mAppSessionData.setMainCategoryList(mainCategoryList);
    }

    public ArrayList<MainCategory> getMainCategoryList() {
        return mAppSessionData.getMainCategoryList();
    }

    public UserRegistrationDetails getUserRegistrationDetails() {
        return mAppSessionData.getUserRegistrationDetails();
    }

    public void setUserRegistrationDetails(UserRegistrationDetails userRegistrationDetails) {
        mAppSessionData.setUserRegistrationDetails(userRegistrationDetails);
    }

    public String getMobile() {
        if (mAppSessionData.getMobile() == null)
            setMobile(SharedPref.getString(mContext, SharedPref.MOBILE));

        return mAppSessionData.getMobile();
    }

    public void setMobile(String mobile) {
        mAppSessionData.setMobile(mobile);
    }
}
