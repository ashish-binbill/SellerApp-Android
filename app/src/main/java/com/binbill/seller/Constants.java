package com.binbill.seller;

import android.os.Environment;

import java.util.regex.Pattern;

/**
 * Created by shruti.vig on 8/14/18.
 */

public class Constants {

    //    public static final String BASE_URL = "https://seller-test.binbill.com/";
//    public static final String BASE_URL = "https://seller.binbill.com/";
/*
    smitha.ravindran@indusind.com
*/
   // public static final String BASE_URL = "https://seller-stage.binbill.com/";
    public static final String BASE_URL = "https://seller.binbill.com/";
    public static final String BASE_URL_IMAGE = "https://seller.binbill.com";
    public static final String APP_VERSION = "10703";
    public static final String GET_OTP_FOR_USER_LOGIN = "sellers/getotp";
    public static final String VERIFY_OTP_FOR_USER_LOGIN = "sellers/validate";
    public static final String UPDATE_PAN_OR_GSTIN = "sellers/init";
    public static final String LINK_SHOP = "sellers/link";
    public static final String GET_LATEST_USER_STATE = "sellers/reference";

    public static final String UPDATE_BASIC_DETAILS = "/sellers/{id}/basic";
    public static final String UPDATE_SELLER_DELIVERY_RULES = "/sellers/{id}/delivery/rules";
    public static final String UPDATE_FRUITS_VEG = "/sellers/{id}/fruitveg";
   // public static final String  MANAGE_FRUITS_VEG= "/sellers/{seller_id}/sku/list";
    public static final String  MANAGE_FRUITS_VEG= "/sellers/{seller_id}/fruitveg/list";
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
    public static final String LINK_POINTS_WITH_JOB = "seller/{seller_id}/users/{customer_id}/points/{credit_id}/jobs/{job_id}";
    public static final String FETCH_USER_TRANSACTIONS = "sellers/{seller_id}/users/{customer_id}/transactions";
    public static final String FETCH_SELLER_DETAILS = "sellers/{seller_id}/details";
    public static final String FETCH_ORDERS = "sellers/{seller_id}/orders/active";
    public static final String FETCH_COMPLETED_ORDERS = "sellers/{seller_id}/orders";
    public static final String FETCH_ORDERS_BY_ID = "sellers/{seller_id}/orders/{id}";
    public static final String FETCH_SKU_BY_ID = "skus/{sku_id}/measurements";
    public static final String SEND_ORDER_FOR_APPROVAL = "sellers/{seller_id}/orders/{order_id}/modify";
    public static final String SEND_ORDER_FOR_MODIFY_ASSISTED = "sellers/{seller_id}/assisted/{order_id}/modify";
    public static final String SEND_ORDER_ACCEPTANCE = "sellers/{seller_id}/orders/{order_id}/approve";
    public static final String SEND_ORDER_REJECTED = "sellers/{seller_id}/orders/{order_id}/reject";
    public static final String SEND_ORDER_OUT_FOR_DELIVERY = "sellers/{seller_id}/orders/{order_id}/outfordelivery";
    public static final String GET_DELIVERY_BOYS = "sellers/{seller_id}/delivery";
    public static final String SAVE_CATEGORIES_FOR_SELLER = "sellers/{seller_id}/providers";
    public static final String GET_SELLER_CATEGORIES = "sellers/{seller_id}/categories";
    public static final String SAVE_BRANDS_FOR_SELLER = "sellers/{seller_id}/providers/brands";
    public static final String FETCH_WALLET_TRANSACTIONS = "sellers/{seller_id}/wallet";
    public static final String REDEEM_WALLET_AMOUNT = "sellers/{seller_id}/wallet/redeem";
    public static final String GET_LOYALTY_POINTS = "sellers/{seller_id}/loyalty/rules";
    public static final String FETCH_SELLER_TRANSACTIONS = "sellers/{seller_id}/users/transactions";
    public static final String FETCH_SELLER_CASHBACKS = "sellers/{seller_id}/users/cashbacks";
    public static final String SET_SELLER_AVAILABILITY = "sellers/{seller_id}/rush/{available}";
    public static final String FETCH_FAQ = "faqs";
    public static final String LOGOUT = "sellers/logout";
    public static final String FETCH_SUGGESTION_BY_ID = "sellers/skus/{sku_id}";
    public static final String ADD_CREDIT_LIMIT_FOR_USER = "sellers/{seller_id}/users/{customer_id}";
    public static final String SUBSCRIBE_FOR_NOTIFICATION = "sellers/subscribe";
    public static final String GET_SKU_BY_BARCODE = "sellers/sku/{barcode}/item";
    public static final String GET_SUGGESTED_OFFERS = "sellers/{seller_id}/brands/{offer_type}/offers";
    public static final String DELETE_SUGGESTED_OFFER_BY_ID = "sellers/{seller_id}/brands/{offer_type}/offers/{id}";
    public static final String LINK_OFFER_WITH_SELLER = "sellers/{seller_id}/brands/{offer_type}/offers/{id}";
    public static final String SELLER_NEED_THIS_ITEM = "sellers/{seller_id}/brands/{offer_type}/offers/{id}/request";

    public static final int PERMISSION_READ_SMS = 100;
    public static final int PERMISSION_CAMERA = 101;
    public static final int PERMISSION_CALL = 102;
    public static final int PERMISSION_READ_CONTACT =  103;
    public static final int INTENT_CALL_SELECT_DELIVERY_AGENT = 1;
    public static final int INTENT_SELECT_PHONE_BOOK_CONTACT = 20;

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
    public static final String UPLOAD_TYPE_ASSISTED_DELIVERY_BOY = "0";
    public static final String FILE_URI = "FILE_URI";
    public static final String TYPE = "TYPE";
    public static final int CREDIT_PENDING = 1;
    public static final int POINTS = 2;
    public static final int TRANSACTIONS = 3;
    public static final int CASHBACKS = 4;
    public static final String ADD_USER_FOR_OFFER = "ADD_USER_FOR_OFFER";
    public static final String EDIT_OFFER = "EDIT_OFFER";
    public static final String DELETE_OFFER = "DELETE_OFFER";
    public static final String ADD_EXPIRY_IN_OFFER = "ADD_EXPIRY_IN_OFFER";
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
    public static final int STATUS_AUTO_CANCEL = 2;
    public static final int STATUS_AUTO_EXPIRED = 15;
    public static final int STATUS_REJECTED = 18;
    public static final int STATUS_OUT_FOR_DELIVERY = 19;
    public static final int STATUS_JOB_STARTED = 20;
    public static final int STATUS_JOB_ENDED = 21;
    public static final String ORDER_ID = "ORDER_ID";
    public static final String DELIVERY_AGENT_ID = "DELIVERY_AGENT_ID";
    public static final String ORDER_TYPE_FMCG = "1";
    public static final String ORDER_TYPE_SERVICE = "2";
    public static final String MOBILE = "MOBILE";
    public static final String SELLER_LIST = "SELLER_LIST";
    public static final String DUPLICATE_ITEM = "DUPLICATE_ITEM";
    public static final String GSTIN = "GSTIN";
    public static final String PAN = "PAN";
    public static final String NONE = "NONE";
    public static final String ACTIVE = "1";
    public static final String EDIT_DELIVERY_BOY = "EDIT_DELIVERY_BOY";
    public static final String PROFILE_MODEL = "PROFILE_MODEL";
    public static final String EDIT_MODE = "EDIT_MODE";
    public static final int TYPE_URL_FILE = 3;
    public static final String UPDATE_POPUP_NOT_NOW_CLICKED = "UPDATE_POPUP_NOT_NOW_CLICKED";
    public static final String NOTIFICATION_DEEPLINK = "NOTIFICATION_DEEPLINK";
    public static final String BUSINESS_MODEL = "BUSINESS_MODEL";

    public static final int SUGGESTION_STATUS_NO_SUGGESTION = 0;
    public static final int SUGGESTION_STATUS_EXISTING = 1;
    public static final int SUGGESTION_STATUS_NEW = 2;
    public static final String AUTO_CREDIT = "AUTO_CREDIT";
    public static final String LOYALTY_POINTS = "LOYALTY_POINTS";
    public static final int PAYMENT_MODE_CASH = 1;
    public static final int PAYMENT_MODE_ONLINE = 4;
    public static final int PAYMENT_MODE_CREDIT = 5;
    public static final int TYPE_ORDER = 2;
    public static final int TYPE_DATA = 2;
    public static final int TYPE_LOADING = 1;
    public static final int ORDER_PAGE_SIZE = 10;
    public static final int CUSTOMER_PAGE_SIZE = 5;
    public static final String OFFER_TYPE = "OFFER_TYPE";

    public static final int OFFER_TYPE_NEW_PRODUCT= 0;
    public static final int OFFER_TYPE_DISCOUNTED = 1;
    public static final int OFFER_TYPE_BOGO = 2;
    public static final int OFFER_TYPE_EXTRA = 3;
    public static final int OFFER_TYPE_GENERAL= 4;
    public static final int TYPE_BARCODE_OFFER = 5;
    public static final String ADD_OFFER_TO_SELLER = "ADD_OFFER_TO_SELLER";
    public static final String NEED_THIS_ITEM = "NEED_THIS_ITEM";

    public static final String BRAND_ID = "BRAND_ID";
}
