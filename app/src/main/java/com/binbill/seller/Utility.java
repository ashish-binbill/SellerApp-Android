package com.binbill.seller;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by shruti.vig on 8/14/18.
 */

public class Utility {

    public static ArrayList<String> convert(JSONArray jArr) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            for (int i = 0, l = jArr.length(); i < l; i++) {
                list.add(jArr.getString(i));
            }
        } catch (JSONException e) {
        }

        return list;
    }

    public static void enableButton(Context context, Button button, boolean enable) {
        if (enable) {
            button.setAlpha(1f);
            button.setTextColor(ContextCompat.getColor(context, R.color.color_white));
            button.setEnabled(true);
        } else {
            button.setAlpha(0.4f);
            button.setTextColor(ContextCompat.getColor(context, R.color.color_white_40));
            button.setEnabled(false);
        }
    }

    public static long isValidMobileNumber(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.length() < 10 || TextUtils.isEmpty(mobileNumber)) {
            return -1;
        }

        mobileNumber = mobileNumber.replaceAll("-", "");
        mobileNumber = mobileNumber.replaceAll("\\(", "");
        mobileNumber = mobileNumber.replaceAll("\\)", "");
        mobileNumber = mobileNumber.replaceAll("\\s+", "");
        String startOne = "^0091.*"; //Welcome(.*)
        String startTwo = "^\\+91.*";
        String startThree = "^91.*";
        String startFour = "^0.*";
        String startFive = "^[6-9][0-9]{9}$";
        //"^(?:0091|\\+91||91|0)[7-9][0-9-]{9}$";

        if (mobileNumber.matches(startFive) && mobileNumber.length() == 10) {
            return Long.parseLong(mobileNumber);
        } else if (mobileNumber.matches(startOne)) {
            mobileNumber = mobileNumber.substring(4);
        } else if (mobileNumber.matches(startTwo)) {
            mobileNumber = mobileNumber.substring(3);
        } else if (mobileNumber.matches(startThree)) {
            mobileNumber = mobileNumber.substring(2);
        } else if (mobileNumber.matches(startFour)) {
            mobileNumber = mobileNumber.substring(1);
        }
        if (mobileNumber.matches(startFive)) {
            return Long.parseLong(mobileNumber);
        } else {
            return -1;
        }
    }

    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public static Uri proceedToTakePicture(Activity context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        context.startActivityForResult(intent, Constants.ACTIVITY_RESULT_CAMERA);

        return file;
    }

    public static String getDateDifference(String start, String end) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        format.setTimeZone(TimeZone.getTimeZone("IST"));

        try {
            Date startDate = format.parse(start);
            Date endDate = format.parse(end);

            long different = endDate.getTime() - startDate.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            System.out.printf(
                    "%d days, %d hours, %d minutes, %d seconds%n",
                    elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

            StringBuilder timeElapsed = new StringBuilder();
            if (elapsedDays > 0)
                timeElapsed.append(elapsedDays + " Days ");

            if (elapsedHours > 0)
                timeElapsed.append(elapsedHours + " Hours ");

            if (elapsedMinutes > 0)
                timeElapsed.append(elapsedMinutes + " Min");

            return timeElapsed.toString();
        } catch (ParseException e) {

        }
        return "";
    }

    public static String getFormattedString(double d) {
        if (d == (long) d) {
            return showDoubleString(d);
        } else
            return String.format("%s", d);
    }

    public static String getMaskedNumber(String mobile) {
        if (!TextUtils.isEmpty(mobile)) {
            if (mobile.length() == 10) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(mobile.subSequence(0, 2));

                for (int i = 0; i < mobile.length() - 4; i++)
                    stringBuilder.append("*");
                stringBuilder.append(mobile.subSequence((mobile.length() - 2), mobile.length()));
                return stringBuilder.toString();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String getFormattedDate(int type, String dateTime, int dateFormat) {

        String formattedDate = null;
        SimpleDateFormat format = null;
        if (dateFormat == 0) {
            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        } else if (dateFormat == 1) {
            format = new SimpleDateFormat("yyyy-MM-dd");
        } else if (dateFormat == 2) {
            format = new SimpleDateFormat("dd MMM");
        } else if (dateFormat == 3) {
            format = new SimpleDateFormat("dd MMM yy");
        }

        format.setTimeZone(TimeZone.getTimeZone("IST"));

        if (dateTime == null || dateTime.isEmpty())
            return "";

        try {
            Date date = format.parse(dateTime);
            switch (type) {
                /**
                 * Add one day after each date format
                 */
                case 1:
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    c.add(Calendar.DATE, 1);
                    CharSequence date1 = android.text.format.DateFormat.format("dd MMM", c.getTime());
                    if (date1 != null)
                        formattedDate = date1.toString();
                    break;
                case 2:
                    CharSequence time = android.text.format.DateFormat.format("kk:mm a", date);
                    if (time != null)
                        formattedDate = time.toString();
                    break;
                case 3:
                    CharSequence day = android.text.format.DateFormat.format("EEE", date);
                    if (day != null)
                        formattedDate = day.toString();
                    break;
                case 4:
                    CharSequence dayAndMonth = android.text.format.DateFormat.format("dd MMM", date);
                    if (dayAndMonth != null)
                        formattedDate = dayAndMonth.toString();
                    break;
                case 5:
                    CharSequence monthyear = android.text.format.DateFormat.format("MMM yyyy", date);
                    if (monthyear != null)
                        formattedDate = monthyear.toString();
                    break;
                case 6:
                    CharSequence year = android.text.format.DateFormat.format("yyyy", date);
                    if (year != null)
                        formattedDate = year.toString();
                    break;
                case 7:
                    CharSequence fulldate = android.text.format.DateFormat.format("dd MMM yyyy", date);
                    if (fulldate != null)
                        formattedDate = fulldate.toString();
                    break;
                /** time in mills**/
                case 8:
                    long timeInMilliseconds = date.getTime();
                    formattedDate = Long.toString(timeInMilliseconds);
                    break;
                /** Doc date format
                 *  10 Aug, 2017  |  09:10 pm
                 **/
                case 9:
                    CharSequence custom = android.text.format.DateFormat.format("dd MMM, yyyy", date);
                    CharSequence customTime = android.text.format.DateFormat.format("hh:mm a", date);
                    if (custom != null)
                        formattedDate = custom.toString() + " | " + customTime;
                    break;
                case 10:
                    CharSequence bDate = android.text.format.DateFormat.format("dd MMM, yyyy", date);
                    if (bDate != null)
                        formattedDate = bDate.toString();
                    break;
                case 11:
                    CharSequence fulldate1 = android.text.format.DateFormat.format("dd MMM yy", date);
                    if (fulldate1 != null)
                        formattedDate = fulldate1.toString();
                    break;
                case 12:
                    CharSequence fulldate2 = android.text.format.DateFormat.format("yyyy-MM-dd", date);
                    if (fulldate2 != null)
                        formattedDate = fulldate2.toString();
                    break;
                case 13:
                    CharSequence monthyear2 = android.text.format.DateFormat.format("MMM, yyyy", date);
                    if (monthyear2 != null)
                        formattedDate = monthyear2.toString();
                    break;
                case 14:
                    CharSequence monthyear3 = android.text.format.DateFormat.format("dd MMM, yyyy", date);
                    if (monthyear3 != null)
                        formattedDate = monthyear3.toString();
                    break;
                case 15:
                    CharSequence dateOnly = android.text.format.DateFormat.format("dd", date);
                    if (dateOnly != null)
                        formattedDate = dateOnly.toString();
                    break;
                case 16:
                    CharSequence monthYear = android.text.format.DateFormat.format("MMM, yyyy", date);
                    if (monthYear != null)
                        formattedDate = monthYear.toString();
                    break;
                case 17:
                    CharSequence custom1 = android.text.format.DateFormat.format("dd MMM, yyyy", date);
                    CharSequence customTime1 = android.text.format.DateFormat.format("hh:mm a", date);
                    if (custom1 != null)
                        formattedDate = custom1.toString() + " " + customTime1;
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    public static void proceedToPickImageFromGallery(Activity context) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        context.startActivityForResult(intent, Constants.ACTIVITY_RESULT_GALLERY);
    }

    public static String getPath(final Context context, final Uri uri) {
        //check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }

            //DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            else
                return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "BinBillSeller");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }


    /**
     * Function to hide the android keyboard manually
     *
     * @param view is the view where focus is invalidated
     */
    public static void hideKeyboard(Context context, View view) {

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static int convertDPtoPx(Context context, int dp) {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics());
        return px;
    }

    public static String fetchMobile(Context context) {

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {

            String acname;
            AccountManager am = AccountManager.get(context);
            Account[] accounts = am.getAccounts();
            String mobile_no = null;
            for (Account ac : accounts) {
                acname = ac.name;

                if (acname.startsWith("91")) {
                    mobile_no = acname;
                }
            }
            if (mobile_no != null && !mobile_no.isEmpty()) {
                return mobile_no.substring(2);
            }
        }

        return "";
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isEmpty(String string) {
        return (string == null || string.trim().length() == 0);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Method to check if a GSTIN is valid. Checks the GSTIN format and the
     * check digit is valid for the passed input GSTIN
     *
     * @param gstin
     * @return boolean - valid or not
     * @throws Exception
     */
    public static boolean validGSTIN(String gstin) throws Exception {
        boolean isValidFormat = false;
        if (checkPattern(gstin, Constants.GSTINFORMAT_REGEX)) {
            isValidFormat = verifyCheckDigit(gstin);
        }
        return isValidFormat;

    }

    /**
     * Method for checkDigit verification.
     *
     * @param gstinWCheckDigit
     * @return
     * @throws Exception
     */
    private static boolean verifyCheckDigit(String gstinWCheckDigit) throws Exception {
        Boolean isCDValid = false;
        String newGstninWCheckDigit = getGSTINWithCheckDigit(
                gstinWCheckDigit.substring(0, gstinWCheckDigit.length() - 1));

        if (gstinWCheckDigit.trim().equals(newGstninWCheckDigit)) {
            isCDValid = true;
        }
        return isCDValid;
    }

    /**
     * Method to check if an input string matches the regex pattern passed
     *
     * @param inputval
     * @param regxpatrn
     * @return boolean
     */
    public static boolean checkPattern(String inputval, String regxpatrn) {
        boolean result = false;
        if ((inputval.trim()).matches(regxpatrn)) {
            result = true;
        }
        return result;
    }

    /**
     * Method to get the check digit for the gstin (without checkdigit)
     *
     * @param gstinWOCheckDigit
     * @return : GSTIN with check digit
     * @throws Exception
     */
    public static String getGSTINWithCheckDigit(String gstinWOCheckDigit) throws Exception {
        int factor = 2;
        int sum = 0;
        int checkCodePoint = 0;
        char[] cpChars;
        char[] inputChars;

        try {
            if (gstinWOCheckDigit == null) {
                throw new Exception("GSTIN supplied for checkdigit calculation is null");
            }
            cpChars = Constants.GSTN_CODEPOINT_CHARS.toCharArray();
            inputChars = gstinWOCheckDigit.trim().toUpperCase().toCharArray();

            int mod = cpChars.length;
            for (int i = inputChars.length - 1; i >= 0; i--) {
                int codePoint = -1;
                for (int j = 0; j < cpChars.length; j++) {
                    if (cpChars[j] == inputChars[i]) {
                        codePoint = j;
                    }
                }
                int digit = factor * codePoint;
                factor = (factor == 2) ? 1 : 2;
                digit = (digit / mod) + (digit % mod);
                sum += digit;
            }
            checkCodePoint = (mod - (sum % mod)) % mod;
            return gstinWOCheckDigit + cpChars[checkCodePoint];
        } finally {
            inputChars = null;
            cpChars = null;
        }
    }

    public static String showDoubleString(double value) {
        DecimalFormat formatter = new DecimalFormat("###.##");
        String formatted = formatter.format(value);
        return formatted;
    }

    public static String showDoubleString(String value) {

        double input = Double.parseDouble(value);

        DecimalFormat formatter = new DecimalFormat("###.##");
        String formatted = formatter.format(input);
        return formatted;
    }


    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static boolean isValueNonZero(String priceString) {

        try {
            double price = Double.parseDouble(priceString);
            if (price > 0)
                return true;
        } catch (Exception e) {

        }

        return false;
    }
}
