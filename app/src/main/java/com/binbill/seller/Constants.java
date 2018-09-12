package com.binbill.seller;

import android.os.Environment;

import java.util.regex.Pattern;

/**
 * Created by shruti.vig on 8/14/18.
 */

public class Constants {

    public static final String BASE_URL = "https://seller-test.binbill.com/";
    public static final String GET_OTP_FOR_USER_LOGIN = "sellers/getotp";
    public static final String VERIFY_OTP_FOR_USER_LOGIN = "sellers/validate";
    public static final String UPDATE_PAN_OR_GSTIN = "sellers/init";
    public static final String GET_LATEST_USER_STATE = "sellers/reference";
    public static final String UPDATE_BASIC_DETAILS = "/sellers/{id}/basic";
    public static final String GET_CITY_BY_STATE = "states/{id}/cities";
    public static final String GET_LOCALITY_BY_CITY_STATE = "states/{id}/cities/{city_id}/localities";
    public static final String GET_CATEGORIES = "sellers/categories";
    public static final String GET_BRANDS = "sellers/brands";
    public static final String GET_DASHBOARD_DATA = "sellers/{id}/dashboard";
    public static final String GET_OFFERS = "sellers/{id}/offers";
    public static final String ADD_OFFER = "sellers/{id}/offers";
    public static final String DELETE_OFFER_BY_ID = "sellers/{seller_id}/offers/{id}";
    public static final String FETCH_USERS_FOR_SELLER = "sellers/{seller_id}/users";
    public static final String PUBLISH_OFFERS_TO_USERS = "sellers/{seller_id}/offers/{id}/publish";
    public static final String FETCH_ASSISTED_SERVICE = "sellers/{seller_id}/assisted";
    public static final String ASSISTED_SERVICE_BY_ID = "sellers/{seller_id}/assisted/{id}";
    public static final String LINK_USER_WITH_SELLER = "sellers/{seller_id}/users/{id}";
    public static final String ADD_ASSISTED_SERVICE = "sellers/{seller_id}/assisted/{id}/types";
    public static final String DELETE_ASSISTED_SERVICE_TAG = "/sellers/{seller_id}/assisted/{user_id}/types/{service_type_id}";
    public static final String FETCH_SELLER_CREDITS = "sellers/{seller_id}/users/{customer_id}/credits";
    public static final String ADD_SETTLE_CREDIT = "sellers/{seller_id}/credits";
    public static final String FETCH_SELLER_POINTS = "sellers/{seller_id}/users/{customer_id}/points";
    public static final String ADD_SETTLE_POINTS = "sellers/{seller_id}/points";
    public static final String FETCH_JOBS_FOR_VERIFICATION = "sellers/{seller_id}/cashbacks";
    public static final String APPROVE_JOB = "sellers/{seller_id}/cashbacks/{id}/approve";
    public static final String REJECT_JOB = "sellers/{seller_id}/cashbacks/{id}/reject";
    public static final String LINK_CREDIT_WITH_JOB = "seller/{seller_id}/users/{customer_id}/credits/{credit_id}/jobs/{job_id}";
    public static final String LINK_POINTS_WITH_JOB = "seller/{seller_id}/users/{customer_id}/points/{point_id}/jobs/{job_id}";
    public static final String FETCH_USER_TRANSACTIONS = "sellers/{seller_id}/users/{customer_id}/transactions";
    public static final String FETCH_SELLER_DETAILS = "sellers/{seller_id}/details";
    public static final String FETCH_ORDERS = "sellers/{seller_id}/orders/active";
    public static final String FETCH_COMPLETED_ORDERS = "sellers/{seller_id}/orders";
    public static final String FETCH_ORDERS_BY_ID = "sellers/{seller_id}/orders/{id}";
    public static final String FETCH_SKU_BY_ID = "skus/{sku_id}/measurements";
    public static final String SEND_ORDER_FOR_APPROVAL = "sellers/{seller_id}/orders/{order_id}/modify";
    public static final String SEND_ORDER_ACCEPTANCE = "sellers/{seller_id}/orders/{order_id}/approve";
    public static final String SEND_ORDER_REJECTED = "sellers/{seller_id}/orders/{order_id}/reject";
    public static final String SEND_ORDER_OUT_FOR_DELIVERY = "sellers/{seller_id}/orders/{order_id}/outfordelivery";
    public static final String GET_DELIVERY_BOYS = "sellers/{seller_id}/delivery";
    public static final String SAVE_CATEGORIES_FOR_SELLER = "sellers/{seller_id}/providers";
    public static final String GET_SELLER_CATEGORIES = "sellers/{seller_id}/categories";

    public static final int PERMISSION_READ_SMS = 100;
    public static final int PERMISSION_CAMERA = 101;
    public static final int INTENT_CALL_SELECT_DELIVERY_AGENT = 1;

    public static final Pattern PAN_PATTERN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
    public static final Pattern ZIP_CODE_PATTERN = Pattern.compile("[1-9][0-9]{5}");
    public static final String GSTINFORMAT_REGEX = "[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}";
    public static final String GSTN_CODEPOINT_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String REGISTRATION_INDEX = "REGISTRATION_INDEX";
    public static final int MAIN_CATEGORY = 1;
    public static final int DELIVERY_DISTANCE = 2;
    public static final int STATES = 3;
    public static final int CITIES = 4;
    public static final int LOCALITY = 5;


    public static final String BUSINESS_TYPE = "BUSINESS_TYPE";
    public static final String FMCG = "FMCG";
    public static final String ASC_SELECTION_TYPE = "ASC_SELECTION_TYPE";
    public static final int CATEGORY = 1;
    public static final int BRAND = 2;
    public static final String SERVICE_CATEGORY = "SERVICE_CATEGORY";
    public static final String SERVICE_BRAND = "SERVICE_BRAND";
    public static final String FMCG_BRANDS = "FMCG_BRANDS";

    public static final String ELECTRONICS = "Electronics";
    public static final String ACCESSORIES = "Accessories";
    public static final String INSURANCE = "Insurance";
    public static final String AMC = "AMC";
    public static final String REPAIR = "Repair";
    public static final String ASC = "ASC";

    public static final int GET_USER_STATE = 1;
    public static final int OTP_LOGIN = 2;

    public static final String IMAGE_PATH = Environment
            .getExternalStorageDirectory().getPath() + "/BinBillSeller";

    public static final int ACTIVITY_RESULT_CAMERA = 100;
    public static final int ACTIVITY_RESULT_GALLERY = 101;
    public static final int SELECT_DEFAULT_IMAGE = 102;

    public static final String UPLOAD_TYPE_SELLER_SHOP = "1";
    public static final String UPLOAD_TYPE_BUSINESS_TYPE = "2";
    public static final String UPLOAD_TYPE_ASSISTED_SERVICE = "3";
    public static final String UPLOAD_TYPE_SELLER_OFFER = "4";
    public static final String UPLOAD_TYPE_ASSISTED_SERVICE_PROFILE = "5";
    public static final String FILE_URI = "FILE_URI";
    public static final String TYPE = "TYPE";
    public static final String ADD_USER_FOR_OFFER = "ADD_USER_FOR_OFFER";
    public static final String EDIT_OFFER = "EDIT_OFFER";
    public static final String DELETE_OFFER = "DELETE_OFFER";
    public static final String OFFER_ITEM = "OFFER_ITEM";
    public static final String FLOW_TYPE = "FLOW_TYPE";
    public static final String SHOW_LINKED_USERS = "SHOW_LINKED_USERS";
    public static final String CREATE_OFFER = "CREATE_OFFER";
    public static final int OFFER = 1;
    public static final int MY_CUSTOMER = 2;
    public static final int ADD_CUSTOMER = 3;
    public static final String DEFAULT_BANNER = "DEFAULT_BANNER";
    public static final String EDIT_ASSISTED_SERVICES = "EDIT_ASSISTED_SERVICES";
    public static final String DEBIT = "2";
    public static final String CREDIT = "1";
    public static final String USER_MODEL = "USER_MODEL";
    public static final String IMAGE_TYPE = "IMAGE_TYPE";
    public static final int TYPE_URI = 1;
    public static final int TYPE_URL = 2;

    public static final int STATUS_NEW_ORDER = 4;
    public static final int STATUS_COMPLETE = 5;
    public static final int STATUS_APPROVED = 16;
    public static final int STATUS_CANCEL = 17;
    public static final int STATUS_REJECTED = 18;
    public static final int STATUS_OUT_FOR_DELIVERY = 19;
    public static final String ORDER_ID = "ORDER_ID";
    public static final String DELIVERY_AGENT_ID = "DELIVERY_AGENT_ID";
    public static final String ORDER_TYPE_FMCG = "1";
    public static final String ORDER_TYPE_SERVICE = "2";
}
