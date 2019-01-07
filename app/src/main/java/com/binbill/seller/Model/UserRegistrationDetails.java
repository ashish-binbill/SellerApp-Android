package com.binbill.seller.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shruti.vig on 8/18/18.
 */

public class UserRegistrationDetails implements Serializable {

    String mobile;
    String email;
    String pan;
    String gstin;
    String id;

    String shopName;
    String businessName;
    String businessAddress;
    String pincode;
    StateCityModel.CityModel city;
    StateCityModel state;
    StateCityModel.LocalityModel locality;
    MainCategory mainCategory;

    ArrayList<String> daysOpen;
    String shopOpen;
    String shopClose;
    boolean isHomeDelivery;
    boolean isOnlinePayment = true;
    String homeDeliveryDistance;
    ArrayList<String> paymentOptions;

    String typeOfBusiness;

    ArrayList<String> autoEEServices;
    HashMap<String, ArrayList<String>> nonASCCategoriesSelected;
    HashMap<FMCGHeaderModel, ArrayList<String>> nonASCBrandsSelected;
    ArrayList<String> ASCCategoriesSelected;
    ArrayList<String> ASCBrandsSelected;

    HashMap<String, ArrayList<String>> fmcgCategoriesSelected;
    private HashMap<String, ArrayList<String>> fmcgBrandsSelected;

    boolean fmcg;
    boolean assisted;
    boolean hasPos;

    String latitude;
    String longitude;
    String shop_address;
    String adhar;
    String shop_no;
    String shop_pin;
    ArrayList<Integer> mainCategoryIds;
    ArrayList<String> mainCatgoryNames;
    String storeSize;
    int staff_no;
    String sku;

    double isAmtLess100;
    double isAmt101_300;
    double isAmt301_499;
    double isAmt_500;
    boolean isHomeDeliveryFree;


    public boolean isHomeDeliveryFree() {
        return isHomeDeliveryFree;
    }

    public void setHomeDeliveryFree(boolean homeDeliveryFree) {
        isHomeDeliveryFree = homeDeliveryFree;
    }

    public boolean isFmcg() {
        return fmcg;
    }

    public boolean isAssisted() {
        return assisted;
    }

    public boolean isHasPos() {
        return hasPos;
    }

    public void setFmcg(boolean fmcg) {
        this.fmcg = fmcg;
    }

    public void setAssisted(boolean assisted) {
        this.assisted = assisted;
    }

    public void setHasPos(boolean hasPos) {
        this.hasPos = hasPos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getIsAmtLess100() {
        return isAmtLess100;
    }

    public void setIsAmtLess100(double isAmtLess100) {
        this.isAmtLess100 = isAmtLess100;
    }

    public double getIsAmt101_300() {
        return isAmt101_300;
    }

    public void setIsAmt101_300(double isAmt101_300) {
        this.isAmt101_300 = isAmt101_300;
    }

    public double getIsAmt301_499() {
        return isAmt301_499;
    }

    public void setIsAmt301_499(double isAmt301_499) {
        this.isAmt301_499 = isAmt301_499;
    }

    public double getIsAmt_500() {
        return isAmt_500;
    }

    public void setIsAmt_500(double isAmt_500) {
        this.isAmt_500 = isAmt_500;
    }

    public StateCityModel getState() {
        return state;
    }

    public void setState(StateCityModel state) {
        this.state = state;
    }

    public void setOnlinePayment(boolean onlinePayment) {
        isOnlinePayment = onlinePayment;
    }

    public boolean isOnlinePayment() {
        return isOnlinePayment;
    }

    public void setNonASCCategoriesSelected(HashMap<String, ArrayList<String>> nonASCCategoriesSelected) {
        this.nonASCCategoriesSelected = nonASCCategoriesSelected;
    }

    public void setNonASCBrandsSelected(HashMap<FMCGHeaderModel, ArrayList<String>> nonASCBrandsSelected) {
        this.nonASCBrandsSelected = nonASCBrandsSelected;
    }

    public void setASCCategoriesSelected(ArrayList<String> ASCCategoriesSelected) {
        this.ASCCategoriesSelected = ASCCategoriesSelected;
    }

    public void setASCBrandsSelected(ArrayList<String> ASCBrandsSelected) {
        this.ASCBrandsSelected = ASCBrandsSelected;
    }

    public void setFmcgCategoriesSelected(HashMap<String, ArrayList<String>> fmcgCategoriesSelected) {
        this.fmcgCategoriesSelected = fmcgCategoriesSelected;
    }

    public void setFmcgBrandsSelected(HashMap<String, ArrayList<String>> brands){
        this.fmcgBrandsSelected = brands;
    }

    public String getAdhar() {
        return adhar;
    }

    public void setAdhar(String adhar) {
        this.adhar = adhar;
    }

    public HashMap<String, ArrayList<String>> getFmcgBrandsSelected() {
        return fmcgBrandsSelected;
    }

    public HashMap<String, ArrayList<String>> getNonASCCategoriesSelected() {
        return nonASCCategoriesSelected;
    }

    public HashMap<FMCGHeaderModel, ArrayList<String>> getNonASCBrandsSelected() {
        return nonASCBrandsSelected;
    }

    public ArrayList<String> getASCCategoriesSelected() {
        return ASCCategoriesSelected;
    }

    public ArrayList<String> getASCBrandsSelected() {
        return ASCBrandsSelected;
    }

    public HashMap<String, ArrayList<String>> getFmcgCategoriesSelected() {
        return fmcgCategoriesSelected;
    }

    public void setAutoEEServices(ArrayList<String> autoEEServices) {
        this.autoEEServices = autoEEServices;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public void setCity(StateCityModel.CityModel city) {
        this.city = city;
    }

    public void setMainCategory(MainCategory mainCategory) {
        this.mainCategory = mainCategory;
    }

    public void setDaysOpen(ArrayList<String> daysOpen) {
        this.daysOpen = daysOpen;
    }

    public void setShopOpen(String shopOpen) {
        this.shopOpen = shopOpen;
    }

    public void setShopClose(String shopClose) {
        this.shopClose = shopClose;
    }

    public void setHomeDelivery(boolean homeDelivery) {
        isHomeDelivery = homeDelivery;
    }

    public void setHomeDeliveryDistance(String homeDeliveryDistance) {
        this.homeDeliveryDistance = homeDeliveryDistance;
    }

    public void setPaymentOptions(ArrayList<String> paymentOptions) {
        this.paymentOptions = paymentOptions;
    }

    public void setTypeOfBusiness(String typeOfBusiness) {
        this.typeOfBusiness = typeOfBusiness;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getPan() {
        return pan;
    }

    public String getGstin() {
        return gstin;
    }

    public String getShopName() {
        return shopName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public String getPincode() {
        return pincode;
    }

    public StateCityModel.CityModel getCity() {
        return city;
    }

    public StateCityModel.LocalityModel getLocality() {
        return locality;
    }

    public void setLocality(StateCityModel.LocalityModel locality) {
        this.locality = locality;
    }

    public MainCategory getMainCategory() {
        return mainCategory;
    }

    public ArrayList<String> getDaysOpen() {
        return daysOpen;
    }

    public String getShopOpen() {
        return shopOpen;
    }

    public String getShopClose() {
        return shopClose;
    }

    public boolean isHomeDelivery() {
        return isHomeDelivery;
    }

    public String getHomeDeliveryDistance() {
        return homeDeliveryDistance;
    }

    public ArrayList<String> getPaymentOptions() {
        return paymentOptions;
    }

    public String getTypeOfBusiness() {
        return typeOfBusiness;
    }

    public ArrayList<String> getAutoEEServices() {
        return autoEEServices;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getShop_address() {
        return shop_address;
    }

    public void setShop_address(String shop_address) {
        this.shop_address = shop_address;
    }

    public String getShop_no() {
        return shop_no;
    }

    public void setShop_no(String shop_no) {
        this.shop_no = shop_no;
    }

    public String getShop_pin() {
        return shop_pin;
    }

    public void setShop_pin(String shop_pin) {
        this.shop_pin = shop_pin;
    }

    public ArrayList<Integer> getMainCategoryIds() {
        return mainCategoryIds;
    }

    public void setMainCategoryIds(ArrayList<Integer> mainCategoryIds) {
        this.mainCategoryIds = mainCategoryIds;
    }

    public ArrayList<String> getMainCatgoryNames() {
        return mainCatgoryNames;
    }

    public void setMainCatgoryNames(ArrayList<String> mainCatgoryNames) {
        this.mainCatgoryNames = mainCatgoryNames;
    }

    public String getStoreSize() {
        return storeSize;
    }

    public void setStoreSize(String storeSize) {
        this.storeSize = storeSize;
    }

    public int getStaff_no() {
        return staff_no;
    }

    public void setStaff_no(int staff_no) {
        this.staff_no = staff_no;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
