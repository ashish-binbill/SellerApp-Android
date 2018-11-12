package com.binbill.seller.Retrofit;

import com.binbill.seller.Constants;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by shruti.vig on 8/10/18.
 */

public interface RetrofitApiInterface {

    @POST(Constants.GET_OTP_FOR_USER_LOGIN)
    Call<JsonObject> getOTPToLoginUser(@Body HashMap<String, String> map);

    @POST(Constants.VERIFY_OTP_FOR_USER_LOGIN)
    Call<JsonObject> validateOTPToLoginUser(@Body HashMap<String, String> map);

    @POST(Constants.UPDATE_PAN_OR_GSTIN)
    Call<JsonObject> updatePANorGSTINInfo(@Body HashMap<String, String> map);

    @PUT(Constants.LINK_SHOP)
    Call<JsonObject> updateExistingShop(@Body HashMap<String, String> map);

    @POST(Constants.LINK_SHOP)
    Call<JsonObject> createNewShop(@Body HashMap<String, String> map);

    @GET(Constants.GET_LATEST_USER_STATE)
    Call<JsonObject> getUserState(@Query(value = "data_required", encoded = true) String isDataRequired);

    @PUT(Constants.UPDATE_BASIC_DETAILS)
    Call<JsonObject> updateBasicDetails(@Path(value = "id", encoded = true) String identifier, @Body HashMap<String, String> map);

    @GET(Constants.GET_CITY_BY_STATE)
    Call<JsonObject> fetchCityByState(@Path(value = "id", encoded = true) String identifier);

    @GET(Constants.GET_LOCALITY_BY_CITY_STATE)
    Call<JsonObject> fetchLocality(@Path(value = "id", encoded = true) String stateId, @Path(value = "city_id", encoded = true) String cityId);

    @GET(Constants.GET_CATEGORIES)
    Call<JsonObject> fetchCategories();

    @GET(Constants.GET_BRANDS)
    Call<JsonObject> fetchBrands(@Query(value = "category_id", encoded = true) String categories);

    @GET(Constants.GET_DASHBOARD_DATA)
    Call<JsonObject> fetchDashboard(@Path(value = "id", encoded = true) String identifier);

    @GET(Constants.GET_OFFERS)
    Call<JsonObject> fetchOffers(@Path(value = "id", encoded = true) String identifier);

    @PUT(Constants.ADD_OFFER)
    Call<JsonObject> addOffer(@Path(value = "id", encoded = true) String identifier, @Body HashMap<String, String> body);

    @DELETE(Constants.DELETE_OFFER_BY_ID)
    Call<JsonObject> deleteOffer(@Path(value = "seller_id", encoded = true) String identifier, @Path(value = "id", encoded = true) String offerId);

    @GET(Constants.FETCH_USERS_FOR_SELLER)
    Call<JsonObject> fetchUsers(@Path(value = "seller_id", encoded = true) String identifier, @Query(value = "offer_id", encoded = true) String offerId, @Query(value = "is_linked_offers", encoded = true) String isLinked);

    @GET(Constants.FETCH_USERS_FOR_SELLER)
    Call<JsonObject> fetchCustomers(@Path(value = "seller_id", encoded = true) String identifier, @Query(value = "linked_only", encoded = true) String isLinked, @Query(value = "user_status_type", encoded = true) String statusType);

    @PUT(Constants.PUBLISH_OFFERS_TO_USERS)
    Call<JsonObject> publishOfferToUsers(@Path(value = "seller_id", encoded = true) String identifier, @Path(value = "id", encoded = true) String offerId, @Body HashMap<String, String> body);

    @GET(Constants.FETCH_USERS_FOR_SELLER)
    Call<JsonObject> fetchUsersToAdd(@Path(value = "seller_id", encoded = true) String identifier, @Query(value = "mobile_no", encoded = true) String mobile);

    @PUT(Constants.FETCH_USERS_FOR_SELLER)
    Call<JsonObject> inviteUser(@Path(value = "seller_id", encoded = true) String identifier, @Body HashMap<String, String> body);

    @PUT(Constants.LINK_USER_WITH_SELLER)
    Call<JsonObject> linkUser(@Path(value = "seller_id", encoded = true) String identifier, @Path(value = "id", encoded = true) String userId);

    @GET(Constants.FETCH_ASSISTED_SERVICE)
    Call<JsonObject> fetchAssistedService(@Path(value = "seller_id", encoded = true) String identifier);

    @POST(Constants.FETCH_ASSISTED_SERVICE)
    Call<JsonObject> createAssistedService(@Path(value = "seller_id", encoded = true) String identifier, @Body HashMap<String, String> body);

    @POST(Constants.ADD_ASSISTED_SERVICE)
    Call<JsonObject> addAssistedService(@Path(value = "seller_id", encoded = true) String identifier, @Path(value = "id", encoded = true) String assistedId, @Body HashMap<String, String> body);

    @DELETE(Constants.DELETE_ASSISTED_SERVICE_TAG)
    Call<JsonObject> deleteAssistedServiceTag(@Path(value = "seller_id", encoded = true) String selledId, @Path(value = "user_id", encoded = true) String userId, @Path(value = "service_type_id", encoded = true) String serviceTypeId);

    @DELETE(Constants.ASSISTED_SERVICE_BY_ID)
    Call<JsonObject> deleteAssistedService(@Path(value = "seller_id", encoded = true) String identifier, @Path(value = "id", encoded = true) String assistedServiceId);

    @GET(Constants.FETCH_SELLER_CREDITS)
    Call<JsonObject> fetchUserCredits(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "customer_id", encoded = true) String customerId);

    @GET(Constants.FETCH_SELLER_CREDITS)
    Call<JsonObject> fetchUserCredits(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "customer_id", encoded = true) String customerId, @Query(value = "job_id", encoded = true) String jobId);

    @PUT(Constants.ADD_SETTLE_CREDIT)
    Call<JsonObject> addSettleCredits(@Path(value = "seller_id", encoded = true) String sellerId, @Body HashMap<String, String> body);

    @GET(Constants.ADD_SETTLE_CREDIT)
    Call<JsonObject> getAllSellerCredits(@Path(value = "seller_id", encoded = true) String sellerId);

    @GET(Constants.ADD_SETTLE_POINTS)
    Call<JsonObject> getAllSellerPoints(@Path(value = "seller_id", encoded = true) String sellerId);

    @GET(Constants.FETCH_SELLER_TRANSACTIONS)
    Call<JsonObject> getAllSellerTransactions(@Path(value = "seller_id", encoded = true) String sellerId);

    @GET(Constants.FETCH_SELLER_CASHBACKS)
    Call<JsonObject> getAllSellerCashbacks(@Path(value = "seller_id", encoded = true) String sellerId);

    @GET(Constants.FETCH_SELLER_POINTS)
    Call<JsonObject> fetchUserLoyaltyPoints(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "customer_id", encoded = true) String customerId);

    @GET(Constants.FETCH_SELLER_POINTS)
    Call<JsonObject> fetchUserLoyaltyPoints(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "customer_id", encoded = true) String customerId, @Query(value = "job_id", encoded = true) String jobId);

    @PUT(Constants.ADD_SETTLE_POINTS)
    Call<JsonObject> addSettleLoyaltyPoints(@Path(value = "seller_id", encoded = true) String sellerId, @Body HashMap<String, String> body);

    @GET(Constants.FETCH_JOBS_FOR_VERIFICATION)
    Call<JsonObject> fetchAllJobs(@Path(value = "seller_id", encoded = true) String sellerId);

    @PUT(Constants.APPROVE_JOB)
    Call<JsonObject> approveJobForVerification(@Path(value = "seller_id", encoded = true) String identifier, @Path(value = "id", encoded = true) String jobId);

    @PUT(Constants.REJECT_JOB)
    Call<JsonObject> rejectJobForVerification(@Path(value = "seller_id", encoded = true) String identifier, @Path(value = "id", encoded = true) String jobId, @Body HashMap<String, String> body);

    @PUT(Constants.LINK_CREDIT_WITH_JOB)
    Call<JsonObject> linkCreditWithJob(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "customer_id", encoded = true) String customerId,
                                       @Path(value = "credit_id", encoded = true) String creditId, @Path(value = "job_id", encoded = true) String jobId);

    @PUT(Constants.LINK_POINTS_WITH_JOB)
    Call<JsonObject> linkPointsWithJob(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "customer_id", encoded = true) String customerId,
                                       @Path(value = "credit_id", encoded = true) String creditId, @Path(value = "job_id", encoded = true) String jobId);

    @GET(Constants.FETCH_USER_TRANSACTIONS)
    Call<JsonObject> fetchUserTransactions(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "customer_id", encoded = true) String customerId);

    @GET(Constants.FETCH_WALLET_TRANSACTIONS)
    Call<JsonObject> fetchWalletTransactions(@Path(value = "seller_id", encoded = true) String sellerId);

    @GET(Constants.FETCH_SELLER_DETAILS)
    Call<JsonObject> getSellerDetails(@Path(value = "seller_id", encoded = true) String sellerId);

    @PUT(Constants.SAVE_CATEGORIES_FOR_SELLER)
    Call<JsonObject> saveSellerCategories(@Path(value = "seller_id", encoded = true) String sellerId, @Body HashMap<String, String> body);

    @PUT(Constants.SAVE_BRANDS_FOR_SELLER)
    Call<JsonObject> saveSellerBrands(@Path(value = "seller_id", encoded = true) String sellerId, @Body HashMap<String, String> body);

    @GET(Constants.FETCH_ORDERS)
    Call<JsonObject> fetchOrders(@Path(value = "seller_id", encoded = true) String sellerId, @Query(value = "page_no", encoded = true) int page);

    @GET(Constants.FETCH_COMPLETED_ORDERS)
    Call<JsonObject> fetchCompletedOrders(@Path(value = "seller_id", encoded = true) String sellerId, @Query(value = "page_no", encoded = true) int page);

    @GET(Constants.FETCH_COMPLETED_ORDERS)
    Call<JsonObject> fetchNotRespondedOrders(@Path(value = "seller_id", encoded = true) String sellerId, @Query(value = "status_type", encoded = true) String status,  @Query(value = "page_no", encoded = true) int page);

    @GET(Constants.FETCH_ORDERS_BY_ID)
    Call<JsonObject> fetchOrderById(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "id", encoded = true) String jobId);

    @GET(Constants.FETCH_SKU_BY_ID)
    Call<JsonObject> fetchSKUById(@Path(value = "sku_id", encoded = true) String skuID);

    @GET(Constants.FETCH_SUGGESTION_BY_ID)
    Call<JsonObject> fetchSuggestionById(@Path(value = "sku_id", encoded = true) String skuID, @Query(value = "sku_measurement_id", encoded = true) String measurementId);

    @PUT(Constants.SEND_ORDER_FOR_APPROVAL)
    Call<JsonObject> sendOrderForApproval(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "order_id", encoded = true) String orderId, @Body HashMap<String, String> body);

    @PUT(Constants.SEND_ORDER_ACCEPTANCE)
    Call<JsonObject> sendOrderAccepted(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "order_id", encoded = true) String orderId, @Body HashMap<String, String> body);

    @PUT(Constants.SEND_ORDER_FOR_MODIFY_ASSISTED)
    Call<JsonObject> sendOrderModifyAssisted(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "order_id", encoded = true) String orderId, @Body HashMap<String, String> body);

    @PUT(Constants.SEND_ORDER_REJECTED)
    Call<JsonObject> sendOrderRejected(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "order_id", encoded = true) String orderId, @Body HashMap<String, String> body);

    @PUT(Constants.SEND_ORDER_OUT_FOR_DELIVERY)
    Call<JsonObject> sendOrderForDelivery(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "order_id", encoded = true) String orderId, @Body HashMap<String, String> body);

    @GET(Constants.GET_DELIVERY_BOYS)
    Call<JsonObject> getDeliveryBoys(@Path(value = "seller_id", encoded = true) String sellerId);

    @GET(Constants.FETCH_ASSISTED_SERVICE)
    Call<JsonObject> getDeliveryBoys(@Path(value = "seller_id", encoded = true) String sellerId, @Query(value = "service_type_id", encoded = true) String serviceTypeID);

    @GET(Constants.GET_SELLER_CATEGORIES)
    Call<JsonObject> fetchSellerCategories(@Path(value = "seller_id", encoded = true) String sellerId);

    @PUT(Constants.REDEEM_WALLET_AMOUNT)
    Call<JsonObject> redeemWalletAmount(@Path(value = "seller_id", encoded = true) String sellerId);

    @GET(Constants.GET_LOYALTY_POINTS)
    Call<JsonObject> getLoyaltyPoints(@Path(value = "seller_id", encoded = true) String sellerId);

    @PUT(Constants.GET_LOYALTY_POINTS)
    Call<JsonObject> setLoyaltyPoints(@Path(value = "seller_id", encoded = true) String sellerId, @Body HashMap<String, String> body);

    @PUT(Constants.SET_SELLER_AVAILABILITY)
    Call<JsonObject> setSellerAvailability(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "available", encoded = true) String availability);

    @GET(Constants.FETCH_FAQ)
    Call<JsonObject> getFaq();

    @POST(Constants.LOGOUT)
    Call<JsonObject> logoutUser(@Body HashMap<String, String> body);

    @PUT(Constants.ADD_CREDIT_LIMIT_FOR_USER)
    Call<JsonObject> addCreditLimitForUser(@Path(value = "seller_id", encoded = true) String sellerId, @Path(value = "customer_id", encoded = true) String customerId, @Body HashMap<String, String> body);
}
