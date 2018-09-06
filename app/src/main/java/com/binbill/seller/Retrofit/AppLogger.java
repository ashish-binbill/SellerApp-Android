package com.binbill.seller.Retrofit;

import android.util.Log;

import com.binbill.seller.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by shruti.vig on 8/10/18.
 */

public class AppLogger {

    private static final String NETWORK_TAG = "NETWORK";

    private static String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static void logRequest(okhttp3.Request request) {
        if (BuildConfig.DEBUG) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("==============================================================================\n");
                sb.append("--> METHOD: " + AppLogger.getMethod(request.method()) + "\n");
                sb.append("--> URL: " + request.url() + "\n");
                sb.append("--> HEADERS: " + AppLogger.getHeaders(request.headers()) + "\n");

                if (request.body() != null && request.body().toString().length() > 2) {
                    String data = bodyToString(request);
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        sb.append("--> BODY:\n");
                        String dataString = jsonObject.toString(4);
                        dataString = dataString.replace("\\/", "/");
                        sb.append(dataString);
                        sb.append("\n");
                    } catch (JSONException e) {
                        try {
                            JSONArray jsonArray = new JSONArray(data);
                            sb.append("--> BODY:\n");
                            String dataString = jsonArray.toString(4);
                            dataString = dataString.replace("\\/", "/");
                            sb.append(dataString);
                            sb.append("\n");
                        } catch (JSONException e2) {
                            sb.append("--> BODY:\n");
                            String[] arr = data.split("&");
                            for (int i = 0; i < arr.length; i++) {
                                sb.append(arr[i]);
                                sb.append("\n");
                            }
                        }
                    }
                } else {
                    sb.append("--> BODY: <empty>\n");
                }
                sb.append("==============================================================================\n");
                logNetwork(sb.toString());
            } catch (Exception e) {
                logNetwork("logRequest error: " + e);
            }
        }
    }

    public static String getMethod(String method) {
        return method;
    }

    public static String getHeaders(Headers map) {
        return map.toString();
    }

    public static void logResponse(String url, int httpCode, String data) {
        if (BuildConfig.DEBUG) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("==============================================================================\n");
                sb.append("<-- URL: " + url + "\n");
                sb.append("<-- STATUS CODE: " + httpCode + "\n");

                try {
                    JSONObject jsonObject = new JSONObject(data);
                    sb.append("<-- DATA:\n");
                    sb.append("==============================================================================\n");
                    String dataString = jsonObject.toString(4);
                    dataString = dataString.replace("\\/", "/");
                    sb.append(dataString);
                    sb.append("\n");
                    sb.append("==============================================================================\n");
                } catch (JSONException e) {
                    try {
                        JSONArray jsonArray = new JSONArray(data);
                        sb.append("<-- DATA:\n");
                        sb.append("==============================================================================\n");
                        sb.append(jsonArray.toString());
                        sb.append("==============================================================================\n");
                        String dataString = jsonArray.toString(4);
                        dataString = dataString.replace("\\/", "/");
                        sb.append(dataString);
                        sb.append("\n");
                        sb.append("==============================================================================\n");
                    } catch (JSONException e2) {
                        sb.append("<-- DATA: " + data + "\n");
                    }
                }
                sb.append("==============================================================================\n");
                logNetwork(sb.toString());
            } catch (Exception e) {
                logNetwork("logResponse error: " + e);
            }
        }
    }

    private static void logNetwork(String msg) {
        Log.d(NETWORK_TAG, msg);
    }
}
