package com.binbill.seller.Retrofit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.user.UserLogoutTask;
import com.binbill.seller.AppSession;
import com.binbill.seller.Constants;
import com.binbill.seller.Dashboard.DashboardActivity;
import com.binbill.seller.Login.LoginActivity_;
import com.binbill.seller.MultipartUtility;
import com.binbill.seller.R;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by shruti.vig on 8/10/18.
 */

public class RetrofitHelper {

    private Context mContext;

    public RetrofitHelper(Context context) {
        this.mContext = context;
    }

    private static Retrofit retrofit = null;

    public static Retrofit getClient(final Context context) {
        if (retrofit == null) {

            OkHttpClient.Builder client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Request newRequest = null;
                            String authToken = SharedPref.getString(context, SharedPref.AUTH_TOKEN);
                            if (Utility.isEmpty(authToken)) {
                                newRequest = request.newBuilder()
                                        .addHeader("Content-Type", "application/json")
                                        .build();
                            } else {
                                newRequest = request.newBuilder()
                                        .addHeader("Authorization", authToken)
                                        .addHeader("Content-Type", "application/json")
                                        .build();
                            }

                            AppLogger.logRequest(newRequest);
                            return chain.proceed(newRequest);
                        }
                    });

            /**
             * Unauthorised
             */
            Interceptor interceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    okhttp3.Response response = chain.proceed(request);

                    String bodyString = response.peekBody(Long.MAX_VALUE).string();
                    AppLogger.logResponse(request.url().toString(), response.code(), bodyString);
                    if (response.code() == 401) {
                        /**
                         * LOGOUT
                         */
                        SharedPref.clearSharedPreferences(context);
                        AppSession.setInstanceToNull();

                        UserLogoutTask.TaskListener userLogoutTaskListener = new UserLogoutTask.TaskListener() {
                            @Override
                            public void onSuccess(Context context) {
                                //Logout success
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                //Logout failure
                            }
                        };
                        UserLogoutTask userLogoutTask = new UserLogoutTask(userLogoutTaskListener, context);
                        userLogoutTask.execute((Void) null);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(context, LoginActivity_.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                            }
                        }, 2000);

                    }

                    return response;
                }
            };
            client.addInterceptor(interceptor);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(logging);

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public interface RetrofitCallback {
        void onResponse(String response);

        void onErrorResponse();
    }

    public interface RetrofitCallbackWithError {
        void onResponse(String response);

        void onErrorResponse(String error);
    }

    class LongOperation extends AsyncTask<Object, Void, String> {

        private final RetrofitCallback mCallback;

        public LongOperation(RetrofitCallback mContext) {
            mCallback = mContext;
        }

        @Override
        protected String doInBackground(Object... params) {
            MultipartUtility multipart = null;
            String response = "";
            try {
                multipart = new MultipartUtility((String) params[0], (String) params[1], (String) params[3], "-------------------------acebdf13572468");
                if (params[2] instanceof File)
                    multipart.addFilePart("filesName", (File) params[2]);
                else {
                    ArrayList<File> files = (ArrayList<File>) params[2];
                    for (int i = 0; i < files.size(); i++) {
                        multipart.addFilePart("filesName", files.get(i));
                    }
                }
                response = multipart.finish();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            StringBuilder sb = new StringBuilder();
            sb.append("==============================================================================\n");
            sb.append("RESPONSE\n");
            if (result != null)
                sb.append("Body: " + result.toString() + "\n");
            sb.append("==============================================================================\n");
            Log.d("Retrofit", sb.toString());

            try {
                JSONObject json = new JSONObject(result);
                if (json != null && json.has("status") && json.getBoolean("status")) {
                    mCallback.onResponse(json.toString());

                } else {
                    mCallback.onErrorResponse();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                mCallback.onErrorResponse();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void getOTPToLoginUser(final HashMap<String, String> map, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.getOTPToLoginUser(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void validateOTPToLoginUser(final HashMap<String, String> map, final RetrofitCallbackWithError retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.validateOTPToLoginUser(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else {
                    String errorString = getErrorJSONString(response);
                    retrofitCallback.onErrorResponse(errorString);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse(mContext.getString(R.string.something_went_wrong));
            }
        });
    }

    private String getErrorJSONString(Response<JsonObject> responseBody) {
        InputStream i = responseBody.errorBody().byteStream();
        BufferedReader r = new BufferedReader(new InputStreamReader(i));
        String finalline = "";
        String line = "";
        try {
            while ((line = r.readLine()) != null) {
                finalline = finalline + line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalline;
    }


    public void getUserState(final RetrofitCallback retrofitCallback) {
        getUserState(retrofitCallback, true);
    }

    public void getUserState(final RetrofitCallback retrofitCallback, boolean isFullDataRequired) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.getUserState(String.valueOf(isFullDataRequired));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchFaq(final RetrofitCallback retrofitCallback, boolean isFullDataRequired) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.getFaq();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void getSellerDetails(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        String sellerID = AppSession.getInstance(mContext).getSellerId();
        Call<JsonObject> call = apiService.getSellerDetails(sellerID);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void updatePanGstinInfo(final HashMap<String, String> map, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.updatePANorGSTINInfo(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void useExistingShop(final HashMap<String, String> map, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.updateExistingShop(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void createNewShop(final HashMap<String, String> map, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.createNewShop(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void updateBasicDetails(final String sellerId, final HashMap<String, String> map, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.updateBasicDetails(sellerId, map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void deleteOfferForSeller(String offerId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        String sellerID = AppSession.getInstance(mContext).getSellerId();
        Call<JsonObject> call = apiService.deleteOffer(sellerID, offerId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchCityByState(final String stateId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchCityByState(stateId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchLocalityByCityAndState(final String stateId, final String cityId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchLocality(stateId, cityId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchDashboardData(String identifier, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchDashboard(identifier);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void createAssistedService(String name, String mobile, String mServiceId, String fileUploadDetails, String profileImageDetails, final RetrofitCallback retrofitCallback) {

        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> assistedService = new HashMap<String, String>();
        assistedService.put("mobile_no", mobile);
        assistedService.put("name", name);
        assistedService.put("id", mServiceId);
        String jsonFormattedString = "";
        String profileImageString = "";
        try {
            if (!Utility.isEmpty(fileUploadDetails)) {
                jsonFormattedString = new JSONTokener(fileUploadDetails).nextValue().toString();
                assistedService.put("document_details", jsonFormattedString);
            }

            if (!Utility.isEmpty(profileImageDetails)) {
                profileImageString = new JSONTokener(profileImageDetails).nextValue().toString();
                assistedService.put("profile_image_detail", profileImageString);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = apiService.createAssistedService(AppSession.getInstance(mContext).getSellerId(), assistedService);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void createAssistedService(String name, String mobile, String serviceTypeId, String price, String overTimePrice, String fileUploadDetails, String profileImageDetails, final RetrofitCallback retrofitCallback) {

        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> assistedService = new HashMap<String, String>();
        assistedService.put("mobile_no", mobile);
        assistedService.put("name", name);
        String jsonFormattedString = "";
        String profileImageString = "";
        try {
            if (!Utility.isEmpty(fileUploadDetails)) {
                jsonFormattedString = new JSONTokener(fileUploadDetails).nextValue().toString();
                assistedService.put("document_details", jsonFormattedString);
            }

            if (!Utility.isEmpty(profileImageDetails)) {
                profileImageString = new JSONTokener(profileImageDetails).nextValue().toString();
                assistedService.put("profile_image_detail", profileImageString);
            }

            JSONArray serviceType = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("service_type_id", serviceTypeId);

            JSONArray priceObject = new JSONArray();

            JSONObject typeObject = new JSONObject();
            typeObject.put("price_type", "1");
            typeObject.put("value", price);
            priceObject.put(typeObject);

            if (!Utility.isEmpty(overTimePrice)) {
                JSONObject typeObject2 = new JSONObject();
                typeObject2.put("price_type", "2");
                typeObject2.put("value", overTimePrice);
                priceObject.put(typeObject2);
            }

            String priceFormatted = new JSONTokener(priceObject.toString()).nextValue().toString();
            object.put("price", priceFormatted);

            String serviceTypeObject = new JSONTokener(object.toString()).nextValue().toString();
            serviceType.put(serviceTypeObject);

            assistedService.put("service_type_detail", new JSONTokener(serviceType.toString()).nextValue().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = apiService.createAssistedService(AppSession.getInstance(mContext).getSellerId(), assistedService);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void createAssistedServiceDeliveryBoy(String name, String mobile, String profileImageDetails, final RetrofitCallback retrofitCallback) {

        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> assistedService = new HashMap<String, String>();
        assistedService.put("mobile_no", mobile);
        assistedService.put("name", name);
        String profileImageString = "";
        try {

            if (!Utility.isEmpty(profileImageDetails)) {
                profileImageString = new JSONTokener(profileImageDetails).nextValue().toString();
                assistedService.put("profile_image_detail", profileImageString);
            }

            JSONArray serviceType = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("service_type_id", Constants.UPLOAD_TYPE_ASSISTED_DELIVERY_BOY);
            String serviceTypeObject = new JSONTokener(object.toString()).nextValue().toString();
            serviceType.put(serviceTypeObject);

            assistedService.put("service_type_detail", new JSONTokener(serviceType.toString()).nextValue().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = apiService.createAssistedService(AppSession.getInstance(mContext).getSellerId(), assistedService);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }


    public void createAssistedServiceDeliveryBoy(String name, String mobile, String serviceTypeId, String profileImageDetails, final RetrofitCallback retrofitCallback) {

        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> assistedService = new HashMap<String, String>();
        assistedService.put("mobile_no", mobile);
        assistedService.put("name", name);
        assistedService.put("id", serviceTypeId);
        String profileImageString = "";
        try {

            if (!Utility.isEmpty(profileImageDetails)) {
                profileImageString = new JSONTokener(profileImageDetails).nextValue().toString();
                assistedService.put("profile_image_detail", profileImageString);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = apiService.createAssistedService(AppSession.getInstance(mContext).getSellerId(), assistedService);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void addServiceToAssistedService(String assistedServiceId, String linkId, String serviceTypeId, String price, String overTimePrice, final RetrofitCallback retrofitCallback) {

        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> assistedService = new HashMap<String, String>();
        try {

            if (!Utility.isEmpty(linkId))
                assistedService.put("id", linkId);
            assistedService.put("service_type_id", serviceTypeId);

            JSONArray priceObject = new JSONArray();

            JSONObject typeObject = new JSONObject();
            typeObject.put("price_type", "1");
            typeObject.put("value", price);
            priceObject.put(typeObject);

            if (!Utility.isEmpty(overTimePrice)) {
                JSONObject typeObject2 = new JSONObject();
                typeObject2.put("price_type", "2");
                typeObject2.put("value", overTimePrice);
                priceObject.put(typeObject2);
            }

            String priceFormatted = new JSONTokener(priceObject.toString()).nextValue().toString();
            assistedService.put("price", priceFormatted);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = apiService.addAssistedService(AppSession.getInstance(mContext).getSellerId(), assistedServiceId, assistedService);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchCategories(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchCategories();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchOrders(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchOrders(AppSession.getInstance(mContext).getSellerId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchSellerCategories(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchSellerCategories(AppSession.getInstance(mContext).getSellerId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchCompletedOrders(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchCompletedOrders(AppSession.getInstance(mContext).getSellerId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchOrderById(String orderId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchOrderById(AppSession.getInstance(mContext).getSellerId(), orderId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchMeasurementsByID(String skuId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchSKUById(skuId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void sendOrderModificationCall(String orderId, String userId, String list, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        String jsonFormattedString = "";
        try {
            jsonFormattedString = new JSONTokener(list).nextValue().toString();
            map.put("order_details", jsonFormattedString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put("user_id", userId);

        Call<JsonObject> call = apiService.sendOrderForApproval(AppSession.getInstance(mContext).getSellerId(), orderId, map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void sendOrderOutForDeliveryCall(String orderID, String userId, String list, String deliveryId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        if (list != null)
            map.put("order_details", list);
        map.put("user_id", userId);
        if (!Utility.isEmpty(deliveryId))
            map.put("delivery_user_id", deliveryId);

        Call<JsonObject> call = apiService.sendOrderForDelivery(AppSession.getInstance(mContext).getSellerId(), orderID, map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchDeliveryBoysForSeller(final RetrofitCallback retrofitCallback) {
        fetchDeliveryBoysForSeller(retrofitCallback, "");
    }

    public void fetchDeliveryBoysForSeller(final RetrofitCallback retrofitCallback, String query) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);


        if (!Utility.isEmpty(query)) {
            Call<JsonObject> call = apiService.getDeliveryBoys(AppSession.getInstance(mContext).getSellerId(), query);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject body = response.body();
                        retrofitCallback.onResponse(body.toString());
                    } else
                        retrofitCallback.onErrorResponse();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable throwable) {
                    retrofitCallback.onErrorResponse();
                }
            });
        } else {
            Call<JsonObject> call = apiService.getDeliveryBoys(AppSession.getInstance(mContext).getSellerId());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject body = response.body();
                        retrofitCallback.onResponse(body.toString());
                    } else
                        retrofitCallback.onErrorResponse();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable throwable) {
                    retrofitCallback.onErrorResponse();
                }
            });
        }
    }

    public void sendOrderApprovalCall(String orderID, String userId, String list, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("order_details", list);
        map.put("user_id", userId);

        Call<JsonObject> call = apiService.sendOrderAccepted(AppSession.getInstance(mContext).getSellerId(), orderID, map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void sendOrderModifyAssisted(String orderID, String userId, String deliveryUserId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("delivery_user_id", deliveryUserId);
        map.put("user_id", userId);

        Call<JsonObject> call = apiService.sendOrderModifyAssisted(AppSession.getInstance(mContext).getSellerId(), orderID, map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void sendOrderDeclineCall(String orderID, String userId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", userId);

        Call<JsonObject> call = apiService.sendOrderRejected(AppSession.getInstance(mContext).getSellerId(), orderID, map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchBrandsFromCategories(String categories, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchBrands(categories);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void addOfferFromSeller(String title, String description, String expiry, String fileUploadDetails, String offerId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Calendar calendar = Calendar.getInstance();
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        HashMap<String, String> offerObject = new HashMap<String, String>();
        offerObject.put("title", title);
        offerObject.put("description", description);
        offerObject.put("start_date", sdf.format(calendar.getTime()));

        if (offerId != null && !Utility.isEmpty(offerId))
            offerObject.put("id", offerId);

        if (!Utility.isEmpty(fileUploadDetails)) {
            String jsonFormattedString = "";
            try {
                jsonFormattedString = new JSONTokener(fileUploadDetails).nextValue().toString();
                offerObject.put("document_details", jsonFormattedString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        offerObject.put("end_date", expiry);

        Call<JsonObject> call = apiService.addOffer(AppSession.getInstance(mContext).getSellerId(), offerObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchOffersForSeller(String identifier, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchOffers(identifier);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchUsersForSeller(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchUsers(AppSession.getInstance(mContext).getSellerId(), String.valueOf(true));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchJobsForVerification(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchAllJobs(AppSession.getInstance(mContext).getSellerId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void approveJobForVerification(String jobId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.approveJobForVerification(AppSession.getInstance(mContext).getSellerId(), jobId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void rejectJobForVerification(String jobId, String rejectId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("reason_id", rejectId);

        Call<JsonObject> call = apiService.rejectJobForVerification(AppSession.getInstance(mContext).getSellerId(), jobId, map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void linkUserWithSeller(String userId, final RetrofitCallback retrofitCallback) {

        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.linkUser(AppSession.getInstance(mContext).getSellerId(), userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void inviteUser(String mobile, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("mobile_no", mobile);

        Call<JsonObject> call = apiService.inviteUser(AppSession.getInstance(mContext).getSellerId(), map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchUsersToInvite(String mobile, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchUsersToAdd(AppSession.getInstance(mContext).getSellerId(), mobile);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchOfferUsersForSeller(String offerId, boolean isLinked, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchUsers(AppSession.getInstance(mContext).getSellerId(), offerId, String.valueOf(isLinked));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void publishOfferToUsers(ArrayList<String> userId, String mOfferId, final RetrofitCallback retrofitCallback) {

        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("user_ids", userId.toString());

        Call<JsonObject> call = apiService.publishOfferToUsers(AppSession.getInstance(mContext).getSellerId(), mOfferId, map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void deleteAssistedService(String assistedServiceId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.deleteAssistedService(AppSession.getInstance(mContext).getSellerId(), assistedServiceId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void fetchAssistedServices(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchAssistedService(AppSession.getInstance(mContext).getSellerId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void deleteAssistedServiceTag(String userId, String serviceTypeId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.deleteAssistedServiceTag(AppSession.getInstance(mContext).getSellerId(), userId, serviceTypeId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void getUserCredits(String userId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchUserCredits(AppSession.getInstance(mContext).getSellerId(), userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void getUserCredits(String userId, String jobId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchUserCredits(AppSession.getInstance(mContext).getSellerId(), userId, jobId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void addSettleLoyaltyPoints(String amount, String remarks, String transactionType, String userId, final RetrofitCallback retrofitCallback) {

        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("amount", amount);
        map.put("description", remarks);
        map.put("transaction_type", transactionType);
        map.put("consumer_id", userId);

        Call<JsonObject> call = apiService.addSettleLoyaltyPoints(AppSession.getInstance(mContext).getSellerId(), map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void getUserLoyaltyPoints(String userId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchUserLoyaltyPoints(AppSession.getInstance(mContext).getSellerId(), userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void getUserLoyaltyPoints(String userId, String jobId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchUserLoyaltyPoints(AppSession.getInstance(mContext).getSellerId(), userId, jobId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void getUserTransactions(String userId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchUserTransactions(AppSession.getInstance(mContext).getSellerId(), userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void getSellerCredits(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.getAllSellerCredits(AppSession.getInstance(mContext).getSellerId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void getSellerPoints(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.getAllSellerPoints(AppSession.getInstance(mContext).getSellerId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void getSellerTransactions(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.getAllSellerTransactions(AppSession.getInstance(mContext).getSellerId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void getSellerCashbacks(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.getAllSellerCashbacks(AppSession.getInstance(mContext).getSellerId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void getWalletTransactions(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.fetchWalletTransactions(AppSession.getInstance(mContext).getSellerId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void redeemWalletAmount(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.redeemWalletAmount(AppSession.getInstance(mContext).getSellerId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void getLoyaltyPoints(final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.getLoyaltyPoints(AppSession.getInstance(mContext).getSellerId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void setLoyaltyPoints(String id, String points, String minPoints, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("minimum_points", minPoints);
        map.put("points_per_item", points);

        Call<JsonObject> call = apiService.setLoyaltyPoints(AppSession.getInstance(mContext).getSellerId(), map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void setSellerAvailability(String availability, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.setSellerAvailability(AppSession.getInstance(mContext).getSellerId(), availability);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void linkCreditWithJob(String userId, String creditId, String jobId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.linkCreditWithJob(AppSession.getInstance(mContext).getSellerId(), userId, creditId, jobId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void linkPointsWithJob(String userId, String creditId, String jobId, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        Call<JsonObject> call = apiService.linkPointsWithJob(AppSession.getInstance(mContext).getSellerId(), userId, creditId, jobId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void saveCategoriesForSeller(JSONArray mapArray, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("provider_type_detail", mapArray.toString());

        Call<JsonObject> call = apiService.saveSellerCategories(AppSession.getInstance(mContext).getSellerId(), map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void saveBrandsForSeller(JSONArray mapArray, final RetrofitCallback retrofitCallback) {
        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("provider_type_detail", mapArray.toString());

        Call<JsonObject> call = apiService.saveSellerBrands(AppSession.getInstance(mContext).getSellerId(), map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void addSettleCredit(String amount, String remarks, String transactionType, String userId, final RetrofitCallback retrofitCallback) {

        RetrofitApiInterface apiService =
                RetrofitHelper.getClient(mContext).create(RetrofitApiInterface.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("amount", amount);
        map.put("description", remarks);
        map.put("transaction_type", transactionType);
        map.put("consumer_id", userId);

        Call<JsonObject> call = apiService.addSettleCredits(AppSession.getInstance(mContext).getSellerId(), map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    retrofitCallback.onResponse(body.toString());
                } else
                    retrofitCallback.onErrorResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                retrofitCallback.onErrorResponse();
            }
        });
    }

    public void uploadFile(Context context, String sellerId, String uploadType, String businessTypeId, String imageTypeId,
                           Uri fileUri, RetrofitCallback callback) {
        ArrayList<File> filesToUpload = new ArrayList<>();
        final String charset = "UTF-8";
        /**
         * /sellers/{id}/upload/{type}?image_types=[array of image type ids]&business_type={business_type_id}
         */
        final String requestURL = Constants.BASE_URL + "sellers/" + sellerId + "/upload/" + uploadType + "?image_types=" + imageTypeId
                + "&business_type=" + businessTypeId;

        String[] proj = {MediaStore.Images.Media.DATA};

        String uriString = Utility.getPath(context, fileUri);

        try {
            if (uriString != null) {
                File myFile = new File(uriString);
                Log.d("SHRUTI", "fILE: " + uriString + " " + Integer.parseInt(String.valueOf(myFile.length() / 1024)));
                myFile = new Compressor(context)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .compressToFile(myFile);
                filesToUpload.add(myFile);

                final String authToken = SharedPref.getString(context, SharedPref.AUTH_TOKEN);
                new LongOperation(callback).execute(requestURL, charset, filesToUpload, authToken);
            } else {
                Toast.makeText(mContext, "Cannot upload file!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(mContext, "Cannot upload file!", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadFile(Context context, String sellerId, String uploadType, Uri fileUri, RetrofitCallback callback) {
        ArrayList<File> filesToUpload = new ArrayList<>();
        final String charset = "UTF-8";
        final String requestURL = Constants.BASE_URL + "sellers/" + sellerId + "/upload/" + uploadType;

        String[] proj = {MediaStore.Images.Media.DATA};

        String uriString = Utility.getPath(mContext, fileUri);

        try {
            if (uriString != null) {
                File myFile = new File(uriString);
                Log.d("SHRUTI", "fILE: " + uriString + " " + Integer.parseInt(String.valueOf(myFile.length() / 1024)));
                myFile = new Compressor(context)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .compressToFile(myFile, "BinBill_" + System.currentTimeMillis());
                filesToUpload.add(myFile);
            } else {
                Toast.makeText(mContext, "Cannot upload file!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(mContext, "Cannot upload file!", Toast.LENGTH_SHORT).show();
        }

        final String authToken = SharedPref.getString(context, SharedPref.AUTH_TOKEN);
        new LongOperation(callback).execute(requestURL, charset, filesToUpload, authToken);
    }

    public void updateOfferImage(Context context, String offerId, Uri fileUri, RetrofitCallback callback) {
        ArrayList<File> filesToUpload = new ArrayList<>();
        final String charset = "UTF-8";

        final String requestURL = Constants.BASE_URL + "offer/" + offerId + "/images/0";

        String[] proj = {MediaStore.Images.Media.DATA};

        String uriString = Utility.getPath(mContext, fileUri);

        try {
            if (uriString != null) {
                File myFile = new File(uriString);
                Log.d("SHRUTI", "fILE: " + uriString + " " + Integer.parseInt(String.valueOf(myFile.length() / 1024)));
                myFile = new Compressor(context)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .compressToFile(myFile, "BinBill_" + System.currentTimeMillis());
                filesToUpload.add(myFile);
            } else {
                Toast.makeText(mContext, "Cannot upload file!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(mContext, "Cannot upload file!", Toast.LENGTH_SHORT).show();
        }

        final String authToken = SharedPref.getString(context, SharedPref.AUTH_TOKEN);
        new LongOperation(callback).execute(requestURL, charset, filesToUpload, authToken);
    }

    public void updateAssistedProfileImage(Context context, String assistedId, Uri fileUri, RetrofitCallback callback) {
        ArrayList<File> filesToUpload = new ArrayList<>();
        final String charset = "UTF-8";

        final String requestURL = Constants.BASE_URL + "assisted/" + assistedId + "/profile/0";

        String[] proj = {MediaStore.Images.Media.DATA};

        String uriString = Utility.getPath(mContext, fileUri);

        try {
            if (uriString != null) {
                File myFile = new File(uriString);
                Log.d("SHRUTI", "fILE: " + uriString + " " + Integer.parseInt(String.valueOf(myFile.length() / 1024)));
                myFile = new Compressor(context)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .compressToFile(myFile, "BinBill_" + System.currentTimeMillis());
                filesToUpload.add(myFile);
            } else {
                Toast.makeText(mContext, "Cannot upload file!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(mContext, "Cannot upload file!", Toast.LENGTH_SHORT).show();
        }

        final String authToken = SharedPref.getString(context, SharedPref.AUTH_TOKEN);
        new LongOperation(callback).execute(requestURL, charset, filesToUpload, authToken);
    }


}
