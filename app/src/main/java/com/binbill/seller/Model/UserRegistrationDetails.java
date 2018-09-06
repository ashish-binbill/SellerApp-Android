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
    String homeDeliveryDistance;
    ArrayList<String> paymentOptions;

    String typeOfBusiness;

    ArrayList<String> autoEEServices;
    HashMap<String, ArrayList<String>> nonASCCategoriesSelected;
    HashMap<String, ArrayList<String>> nonASCBrandsSelected;
    ArrayList<String> ASCCategoriesSelected;
    ArrayList<String> ASCBrandsSelected;

    HashMap<String, ArrayList<String>> fmcgCategoriesSelected;
    private HashMap<String, ArrayList<String>> fmcgBrandsSelected;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StateCityModel getState() {
        return state;
    }

    public void setState(StateCityModel state) {
        this.state = state;
    }

    public void setNonASCCategoriesSelected(HashMap<String, ArrayList<String>> nonASCCategoriesSelected) {
        this.nonASCCategoriesSelected = nonASCCategoriesSelected;
    }

    public void setNonASCBrandsSelected(HashMap<String, ArrayList<String>> nonASCBrandsSelected) {
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

    public HashMap<String, ArrayList<String>> getFmcgBrandsSelected() {
        return fmcgBrandsSelected;
    }

    public HashMap<String, ArrayList<String>> getNonASCCategoriesSelected() {
        return nonASCCategoriesSelected;
    }

    public HashMap<String, ArrayList<String>> getNonASCBrandsSelected() {
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
}
