package com.binbill.seller.Dashboard;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.api.account.user.UserLogoutTask;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.AppSession;
import com.binbill.seller.AssistedService.AddAssistedServiceActivity_;
import com.binbill.seller.AssistedService.AdditionalServiceDialogFragment;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.BuildConfig;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.YesNoDialogFragment;
import com.binbill.seller.Customer.AddCustomerActivity_;
import com.binbill.seller.Customer.CustomerPagerAdapter;
import com.binbill.seller.Customer.InvitedCustomerFragment;
import com.binbill.seller.DeliveryAgent.DeliveryAgentActivity_;
import com.binbill.seller.FAQ.FaqActivity_;
import com.binbill.seller.HomeDelivery.SetHomeDeliveryActivity_;
import com.binbill.seller.Login.LoginActivity_;
import com.binbill.seller.Loyalty.LoyaltyRulesActivity_;
import com.binbill.seller.Model.DashboardModel;
import com.binbill.seller.Model.FruitsVeg;
import com.binbill.seller.Model.MainCategory;
import com.binbill.seller.Model.UserModel;
import com.binbill.seller.Offers.UserAdapter;
import com.binbill.seller.Order.OrderDetailsActivity_;
import com.binbill.seller.R;
import com.binbill.seller.Registration.RegistrationResolver;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;
import com.binbill.seller.Utility;
import com.binbill.seller.Wallet.WalletActivity_;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

@EActivity(R.layout.activity_dashboard)
public class DashboardActivity extends BaseActivity implements YesNoDialogFragment.YesNoClickInterface, FragmentEventListener,
        AdditionalServiceDialogFragment.AdditionalServiceClickInterface, NavigationView.OnNavigationItemSelectedListener {

    private static final int FMCG_ASSISTED_USER_POS = 0;
    private static final int FMCG_ASSISTED_USER_NO_POS = 1;
    private static final int FMCG_ONLY_USER_HAS_POS = 2;
    private static final int FMCG_ONLY_USER_NO_POS = 3;
    private static final int ASSISTED_ONLY_USER = 4;

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

   /* @ViewById(R.id.tv_dynamicId)
    TextView tv_dynamicId;*/

  /*  @ViewById
    ImageView iv_notification, iv_search;*/

    @ViewById
    FrameLayout container;

    @ViewById
    DrawerLayout drawer_layout;

    @ViewById
    NavigationView nav_view;

    @ViewById
    TextView tv_shop_name, tv_shop_number;

    @ViewById
    ImageView iv_user_image;

    @ViewById
    RelativeLayout just_sec_layout;

    @ViewById
    BottomNavigationView bottom_navigation;
    private AssistedServiceFragment assistedServiceFragment;
    private CustomerFragment myCustomerFragment;
    public int sellerType;
    private SwitchCompat sellerAvailability;

    String whichScreen ="Home";


    Menu menuPrepare;

    @AfterViews
    public void setUpView() {
        setUpNavigationView();
        setUpListener();
        ApiHelper.getUserSelectedCategories(this);

      //  ImageView imageProfile = nav_view.findViewById(R.id.iv_user_image);
        iv_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity_.class));
            }
        });


    }

    private void checkNotificationDeeplink() {
/**
 * 2| My Customer
 3| Cash Back
 4| Loyalty Points
 5| Seller Verification
 6| Cash Back Verification
 7| User credit
 */
        String notificationType = AppSession.getInstance(this).getNotificationIntent();
        Log.d("SHRUTI", "Notification type " + notificationType);
        switch (notificationType) {
            case "1":
                /**
                 * Order
                 */
                changeViewPagerFragment(1);

                Intent intent = new Intent(this, OrderDetailsActivity_.class);
                intent.putExtra(Constants.ORDER_ID, AppSession.getInstance(this).getNotificationOrderId());
                startActivity(intent);

                break;

            case "2":
                /**
                 * My customer
                 */
                changeViewPagerFragment(3);

                break;
            case "3":
                startActivity(new Intent(this, WalletActivity_.class));
                break;
            case "4":
                /**
                 * Loyalty Rules
                 */
                startActivity(new Intent(this, LoyaltyRulesActivity_.class));
                break;
            case "5":
                startActivity(new Intent(this, ProfileActivity_.class));
                break;
            case "6":
                /**
                 * Verification Tab
                 */
                changeViewPagerFragment(2);
                break;
            case "7":
                intent = new Intent(DashboardActivity.this, DashboardListActivity_.class);
                intent.putExtra(Constants.TYPE, Constants.CREDIT_PENDING);
                startActivity(intent);
                break;
            case "8":
                /**
                 * Offline payment
                 */
                // do nothing
                break;
            case "9":
                /**
                 * assisted users
                 */
                changeViewPagerFragment(4);
                break;
            case "10":
                /**
                 * Manage categories
                 */
                Intent intent1 = RegistrationResolver.getNextIntent(DashboardActivity.this, 4);
                if (intent1 != null)
                    startActivity(intent1);
                break;

        }

        AppSession.getInstance(this).setNotificationIntent(null);
    }

    private void initialiseApplozic() {


        if (!MobiComUserPreference.getInstance(this).isLoggedIn()) {
            UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {

                @Override
                public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                    //After successful registration with Applozic server the callback will come here
                    ApplozicSetting.getInstance(context).enableRegisteredUsersContactCall();
                    ApplozicClient.getInstance(context).showAppIconInNotification(true);
                    ApplozicClient.getInstance(context).enableNotification();

                    if (MobiComUserPreference.getInstance(context).isRegistered()) {

                        PushNotificationTask pushNotificationTask = null;
                        PushNotificationTask.TaskListener listener = new PushNotificationTask.TaskListener() {
                            @Override
                            public void onSuccess(RegistrationResponse registrationResponse) {

                            }

                            @Override
                            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                            }
                        };

                        pushNotificationTask = new PushNotificationTask(Applozic.getInstance(context).getDeviceRegistrationId(), listener, DashboardActivity.this);
                        pushNotificationTask.execute((Void) null);
                    }
                }

                @Override
                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                    Toast.makeText(DashboardActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
                }
            };

            String sellerId = AppSession.getInstance(this).getSellerId();
            ProfileModel profileModel = AppSession.getInstance(DashboardActivity.this).getSellerProfile();
            User user = new User();
            user.setUserId("seller_" + sellerId);
            user.setDisplayName(profileModel.getName());
            user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());
            user.setPassword("");
            user.setImageLink("");
            new UserLoginTask(user, listener, DashboardActivity.this).execute((Void) null);
        }
    }

    private void makeSellerProfileApiCall() {
        new RetrofitHelper(this).getSellerDetails(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        JSONObject profileJson = jsonObject.getJSONObject("result");
                        Type classType = new TypeToken<ProfileModel>() {
                        }.getType();

                        ProfileModel profileModel = new Gson().fromJson(profileJson.toString(), classType);
                        AppSession.getInstance(DashboardActivity.this).setSellerProfile(profileModel);

                        setUpHomeDeliveryInHamburger();

                        JSONArray paymentModesArray = jsonObject.getJSONArray("payment_modes");
                        classType = new TypeToken<ArrayList<MainCategory>>() {
                        }.getType();

                        ArrayList<MainCategory> paymentModes = new Gson().fromJson(paymentModesArray.toString(), classType);
                        AppSession.getInstance(DashboardActivity.this).setPaymentModes(paymentModes);

                        setUpHamburger();
                        initialiseApplozic();
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onErrorResponse() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeSellerProfileApiCall();

        if (AppSession.getInstance(this).getNotificationIntent() != null) {
            checkNotificationDeeplink();
            AppSession.getInstance(this).setNotificationIntent(null);
        }
    }

    private void setUpHamburger() {

        ProfileModel model = AppSession.getInstance(this).getSellerProfile();
        final String authToken = SharedPref.getString(this, SharedPref.AUTH_TOKEN);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        return response.request().newBuilder()
                                .header("Authorization", authToken)
                                .build();
                    }
                }).build();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );

        int margin = Utility.convertDPtoPx(this, 10);
        params.setMargins(-margin, -margin, -margin, -margin);
        iv_user_image.setLayoutParams(params);

        String sellerID = AppSession.getInstance(this).getSellerId();
        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
        picasso.load(Constants.BASE_URL + "sellers/" + sellerID + "/upload/1/images/" + 0)
                .config(Bitmap.Config.RGB_565)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(iv_user_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) iv_user_image.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
//                        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()));
                        iv_user_image.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError(Exception e) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT
                        );

                        int margins = Utility.convertDPtoPx(DashboardActivity.this, 15);
                        params.setMargins(margins, margins, margins, margins);
                        iv_user_image.setLayoutParams(params);

                        iv_user_image.setImageDrawable(ContextCompat.getDrawable(DashboardActivity.this, R.drawable.ic_user));
                    }
                });

        tv_shop_name.setText(model.getName());
        tv_shop_number.setText(model.getContactNo());

        TextView wallet = nav_view.findViewById(R.id.wallet_amount);

        try {
            if (model.getCashBack() != null && model.getCashBack().equalsIgnoreCase("")) {
                wallet.setText(getString(R.string.rupee_sign) + " " + 0
                        /*getString(R.string.wallet_points, model.getCashBack())*/);
            } else {
                wallet.setText(getString(R.string.rupee_sign) + " " + model.getCashBack()
                        /*getString(R.string.wallet_points, model.getCashBack())*/);
            }
        } catch (Exception e) {
            wallet.setText(getString(R.string.rupee_sign) + " " + 0
                    /*getString(R.string.wallet_points, model.getCashBack())*/);
        }
        /*   wallet.setText(getString(R.string.rupee_sign) + " " + model.getCashBack()
         *//*getString(R.string.wallet_points, model.getCashBack())*//*);*/
    }

    private void setUpListener() {

        TextView manageDelivery = nav_view.findViewById(R.id.tv_free_home_delivery);
        manageDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(DashboardActivity.this, DeliveryAgentActivity_.class));
            }
        });

        TextView setHomeDelivery = nav_view.findViewById(R.id.tv_free_home_delivery);
        manageDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(DashboardActivity.this, SetHomeDeliveryActivity_.class));
            }
        });

        TextView faq = nav_view.findViewById(R.id.tv_faq);
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(DashboardActivity.this, FaqActivity_.class));
            }
        });

        TextView manageCategory = nav_view.findViewById(R.id.tv_manage_category);
        manageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);
                Intent intent = RegistrationResolver.getNextIntent(DashboardActivity.this, 6);
                if (intent != null)
                    startActivity(intent);
            }
        });

        TextView manageBrands = nav_view.findViewById(R.id.tv_manage_brands);
        manageBrands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);
                Intent intent = RegistrationResolver.getNextIntent(DashboardActivity.this, 7);
                if (intent != null)
                    startActivity(intent);
            }
        });

        TextView loyaltyRules = nav_view.findViewById(R.id.tv_layalty_rules);
        loyaltyRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(DashboardActivity.this, LoyaltyRulesActivity_.class));

            }
        });

        TextView autoCreditLoyalty = nav_view.findViewById(R.id.tv_auto_credit);
        autoCreditLoyalty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);

                Intent intent = new Intent(DashboardActivity.this, LoyaltyRulesActivity_.class);
                intent.putExtra(Constants.TYPE, Constants.AUTO_CREDIT);
                startActivity(intent);
            }
        });

        TextView callUs = (TextView) findViewById(R.id.tv_call_us);
        callUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPhonePermission();
            }
        });

        TextView emailUs = (TextView) findViewById(R.id.tv_email_us);
        emailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                //intent.setType("message/rfc822");
                String[] recipients = {"support@binbill.com"};
                String phone_no = AppSession.getInstance(DashboardActivity.this).getMobile();
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, "BinBill: Partner Feedback (" + phone_no + ")");
                //intent.putExtra(Intent.EXTRA_TEXT, "BinBill Team");
                intent.putExtra(Intent.EXTRA_BCC, new String[]{"sagar@binbill.com", "rohit@binbill.com", "amar@binbill.com"});
                startActivity(Intent.createChooser(intent, "Send email"));
            }
        });

        TextView shareApp = (TextView) findViewById(R.id.tv_share_app);
        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.share_message) + " https://play.google.com/store/apps/details?id=com.binbill.seller");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        TextView subscribe = (TextView) findViewById(R.id.tv_subscribe_notification);
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                makeSubscribeNetworkCall();
            }
        });


        sellerAvailability = (SwitchCompat) findViewById(R.id.tv_availability);

        DashboardModel dashboardModel = AppSession.getInstance(this).getDashboardData();

        TextView apVersion = (TextView) findViewById(R.id.tv_app_version);
        apVersion.setText(

                getString(R.string.app_version, BuildConfig.VERSION_NAME));

        TextView logout = nav_view.findViewById(R.id.tv_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);
                just_sec_layout.setVisibility(View.VISIBLE);
                SharedPref.clearSharedPreferences(DashboardActivity.this, true);
                SharedPref.removePref(DashboardActivity.this, Constants.UPDATE_POPUP_NOT_NOW_CLICKED);
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
                UserLogoutTask userLogoutTask = new UserLogoutTask(userLogoutTaskListener, DashboardActivity.this);
                userLogoutTask.execute((Void) null);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(DashboardActivity.this, LoginActivity_.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);

            }
        });

        final TextView wallet = nav_view.findViewById(R.id.wallet_amount);
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(DashboardActivity.this, WalletActivity_.class));
            }
        });

        TextView profile = nav_view.findViewById(R.id.tv_shop_name);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity_.class));
            }
        });


        TextView mobile = nav_view.findViewById(R.id.tv_shop_number);
        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity_.class));
            }
        });

        /*iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedItem = bottom_navigation.getSelectedItemId();
                switch (selectedItem) {
                    case R.id.action_home:
                        break;
                    case R.id.action_verification:
                        break;
                    case R.id.action_chat:
                        break;
                    case R.id.action_customers:
                        startActivity(new Intent(DashboardActivity.this, AddCustomerActivity_.class));
                        break;
                    case R.id.action_assisted:
                        startActivity(new Intent(DashboardActivity.this, AddAssistedServiceActivity_.class));
                        break;
                }
            }
        });*/

       /* iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedItem = bottom_navigation.getSelectedItemId();
                switch (selectedItem) {
                    case R.id.action_home:
                        break;
                    case R.id.action_verification:
                        break;
                    case R.id.action_chat:
                        break;
                    case R.id.action_customers:
                      *//*  iv_search.setVisibility(View.GONE);
                        et_global_search.setVisibility(View.VISIBLE);*//*
                        myCustomerFragment.showSearchView();
                        break;
                    case R.id.action_assisted:
                        assistedServiceFragment.showSearchView();
                        break;
                }
            }
        });*/
    }

    private void makeSubscribeNetworkCall() {
        new RetrofitHelper(this).subscribeForNotification(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        showSnackBar(getString(R.string.successfully_subsribed_for_notification));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse() {

            }
        });
    }

    private void setUpHomeDeliveryInHamburger() {
        final ProfileModel profileModel = AppSession.getInstance(DashboardActivity.this).getSellerProfile();

        if (profileModel != null && profileModel.getSellerDetails() != null && profileModel.getSellerDetails().getBasicDetails() != null
                && profileModel.getSellerDetails().getBasicDetails().getHomeDelivery() != null && profileModel.getSellerDetails().getBasicDetails().getHomeDelivery().equalsIgnoreCase("true")) {
            sellerAvailability.setChecked(true);
        } else {
            sellerAvailability.setChecked(false);
        }

        sellerAvailability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {

                String homeDelivery;
                if (enable)
                    homeDelivery = "true";
                else
                    homeDelivery = "false";

                HashMap<String, String> map = new HashMap<>();
                map.put("home_delivery", homeDelivery);

                new RetrofitHelper(DashboardActivity.this).updateBasicDetails(profileModel.getId(), map, new RetrofitHelper.RetrofitCallback() {
                    @Override
                    public void onResponse(String response) {
                        /**
                         * Do nothing
                         */
                    }

                    @Override
                    public void onErrorResponse() {
                        /**
                         * Do nothing
                         */
                    }
                });
            }
        });
    }

    private void invokeAvailabilityDialog(String isAvailable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_show_available_options, null);

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView headerTitle = (TextView) dialogView.findViewById(R.id.header);

        RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.radio_group_availability);
        RadioButton available = (RadioButton) dialogView.findViewById(R.id.available);
        RadioButton busy = (RadioButton) dialogView.findViewById(R.id.busy);

        if (isAvailable.equalsIgnoreCase(getString(R.string.available))) {
            available.setChecked(true);
            busy.setChecked(false);
        } else {
            available.setChecked(false);
            busy.setChecked(true);
        }

        headerTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checked) {
                int radioButtonID = radioGroup.getCheckedRadioButtonId();
                String isBusy = "false";
                if (radioButtonID == R.id.busy)
                    isBusy = "true";

                new RetrofitHelper(DashboardActivity.this).setSellerAvailability(isBusy, new RetrofitHelper.RetrofitCallback() {
                    @Override
                    public void onResponse(String response) {
                        /**
                         * Do nothing
                         */
                    }

                    @Override
                    public void onErrorResponse() {
                        /**
                         * Do nothing
                         */
                    }
                });


                if (isBusy.equalsIgnoreCase("false")) {
                    sellerAvailability.setText(getString(R.string.available));
                    sellerAvailability.setBackgroundColor(ContextCompat.getColor(DashboardActivity.this, R.color.status_green_light));
                } else {
                    sellerAvailability.setText(getString(R.string.busy));
                    sellerAvailability.setBackgroundColor(ContextCompat.getColor(DashboardActivity.this, R.color.status_red));
                }
                dialog.dismiss();

            }
        });


        dialog.show();
    }

    private void setUpNavigationView() {

        HomeFragment homeFragment = HomeFragment.newInstance("", "");
        replaceFragment(homeFragment, 0);
        setUpToolbar(getString(R.string.dashboard));
        //   iv_notification.setVisibility(View.INVISIBLE);

        bottom_navigation.setSelectedItemId(R.id.action_home);
        /**
         * To disable the default shifting of bottom navigation menu items
         */
//        Utility.disableShiftMode(bottom_navigation);

        DashboardModel dashboardModel = AppSession.getInstance(this).getDashboardData();

        if (dashboardModel == null) {
            just_sec_layout.setVisibility(View.VISIBLE);
            ApiHelper.makeDashboardDataCall(this);
        } else {

            BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottom_navigation.getChildAt(0);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
                final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
                final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.bottom_navigation_icon_size), displayMetrics);
                layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.bottom_navigation_icon_size), displayMetrics);
                iconView.setLayoutParams(layoutParams);

//            int padding20 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.dimen_15dp), displayMetrics);
//            iconView.setPadding(0, 0, 0, padding20);
            }

            sellerType = dashboardModel.getSellerType();
            AppSession.getInstance(this).setDashboardData(dashboardModel);
            Menu menu = bottom_navigation.getMenu();
            switch (sellerType) {
                case FMCG_ASSISTED_USER_POS:
                    break;
                case FMCG_ASSISTED_USER_NO_POS:
                    menu.removeItem(R.id.action_verification);
                    break;
                case FMCG_ONLY_USER_HAS_POS:
                    menu.removeItem(R.id.action_assisted);
                    break;
                case FMCG_ONLY_USER_NO_POS:
                    menu.removeItem(R.id.action_assisted);
                    menu.removeItem(R.id.action_verification);
                    break;
                case ASSISTED_ONLY_USER:
                    menu.removeItem(R.id.action_verification);
                    nav_view.findViewById(R.id.tv_manage_delivery_boy).setVisibility(View.GONE);
                    nav_view.findViewById(R.id.ll_sku_management).setVisibility(View.GONE);
                    break;
            }

            if (dashboardModel.isCategoryBrandDataManuallyAdded()) {
                nav_view.findViewById(R.id.ll_sku_management).setVisibility(View.GONE);
            }

            bottom_navigation.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_home:
                                    HomeFragment homeFragment = HomeFragment.newInstance("", "");
                                    replaceFragment(homeFragment, 0);
                                    setUpToolbar(getString(R.string.dashboard));
                                    whichScreen = "Home";
                                    if(CustomerFragment.userList.size()!=0){
                                        CustomerFragment.userList.clear();
                                    }


                                    //   iv_notification.setVisibility(View.INVISIBLE);
                                    //   iv_search.setVisibility(View.GONE);
                                    break;
                                case R.id.action_verification:
                                    VerificationFragment verificationFragment = VerificationFragment.newInstance();
                                    replaceFragment(verificationFragment, 1);
                                    setUpToolbar(getString(R.string.verification));
                                    if(CustomerFragment.userList.size()!=0){
                                        CustomerFragment.userList.clear();
                                    }
                                    whichScreen = "Home";
                                    //  iv_notification.setVisibility(View.INVISIBLE);
                                    //  iv_search.setVisibility(View.GONE);
                                    break;
                                case R.id.action_chat:
                                    OrderFragment orderFragment = OrderFragment.newInstance(0);
                                    replaceFragment(orderFragment, 2);
                                    setUpToolbar(getString(R.string.my_order));
                                    if(CustomerFragment.userList.size()!=0){
                                        CustomerFragment.userList.clear();
                                    }
                                    whichScreen = "Home";
                                    // iv_notification.setVisibility(View.INVISIBLE);
                                    //  iv_search.setVisibility(View.GONE);
                                    break;
                                case R.id.action_customers:
                                    myCustomerFragment = CustomerFragment.newInstance(0);
                                    replaceFragment(myCustomerFragment, 3);
                                    setUpToolbar(getString(R.string.my_customers));
                                    whichScreen = "Customer";
                                    // iv_notification.setVisibility(View.VISIBLE);
                                    //  iv_notification.setImageDrawable(ContextCompat.getDrawable(DashboardActivity.this, R.drawable.ic_add_offer));
                                    //  iv_search.setVisibility(View.VISIBLE);
                                    break;
                                case R.id.action_assisted:
                                    assistedServiceFragment = AssistedServiceFragment.newInstance("", "");
                                    replaceFragment(assistedServiceFragment, 4);
                                    setUpToolbar(getString(R.string.assisted_services));
                                    if(CustomerFragment.userList.size()!=0){
                                        CustomerFragment.userList.clear();
                                    }
                                    whichScreen = "Assisted";
                                    //  iv_notification.setVisibility(View.VISIBLE);
                                    //  iv_notification.setImageDrawable(ContextCompat.getDrawable(DashboardActivity.this, R.drawable.ic_add_offer));
                                    //  iv_search.setVisibility(View.VISIBLE);
                                    break;

                            }
                            return true;
                        }
                    });
        }
        View.OnClickListener btnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               /* Toast.makeText(v.getContext(),""+ v.getTag() +"-"+ v.getId(),
                        Toast.LENGTH_SHORT).show();*/
              Intent i = new Intent(DashboardActivity.this, ManageFruitsVegActivity_.class);
              i.putExtra("Id",  String.valueOf(v.getId()));
              i.putExtra("Title",  ""+v.getTag());
              startActivity(i);
            }

        };

        TextView tv= null;
        ArrayList<FruitsVeg> list = new ArrayList<>();
        list = AppSession.getInstance(this).getFruitsVegList();
        for(int i = 0; i < list.size() ; i++){

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_textview_dynamic, null);
            LinearLayout dynamicLayout =  nav_view.findViewById(R.id.dynamic_lay);
            dynamicLayout.setVisibility(View.VISIBLE);
            tv = (TextView) view.findViewById(R.id.tv_dynamicId);

            tv.setText(list.get(i).getName());
            tv.setTag(list.get(i).getName());
            tv.setId(list.get(i).getId());
            tv.setOnClickListener(btnClickListener);
            dynamicLayout.addView(view);

        }



    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            int id = bottom_navigation.getSelectedItemId();
            if (id != R.id.action_home) {
                bottom_navigation.setSelectedItemId(R.id.action_home);
            } else
                super.onBackPressed();
        }
    }

    private void setUpToolbar(String toolbarTitle) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_navigation));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(toolbarTitle);

        nav_view.setNavigationItemSelectedListener(this);
    }

    private void checkPhonePermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.CALL_PHONE},
                    Constants.PERMISSION_CALL);
        } else {
            Intent intent = new Intent("android.intent.action.CALL");
            Uri data = Uri.parse("tel:7600919189");
            intent.setData(data);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_CALL: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent("android.intent.action.CALL");
                    Uri data = Uri.parse("tel:7600919189");
                    intent.setData(data);
                    startActivity(intent);
                } else {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                        showSnackBar(getString(R.string.enable_permission_call));
                    }
                    Log.d("SHRUTI", "Permission denied.. cannot use");
                }
                return;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                drawer_layout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_add:
                int selectedItem = bottom_navigation.getSelectedItemId();
                switch (selectedItem) {
                    case R.id.action_home:
                        break;
                    case R.id.action_verification:
                        break;
                    case R.id.action_chat:
                        break;
                    case R.id.action_customers:
                        startActivity(new Intent(DashboardActivity.this, AddCustomerActivity_.class));
                        break;
                    case R.id.action_assisted:
                        startActivity(new Intent(DashboardActivity.this, AddAssistedServiceActivity_.class));
                        break;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void replaceFragment(Fragment fragment, int position) {
        if (fragment != null) {
            try {
                FragmentManager fm = this.getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container, fragment, "fragment_" + position);
                ft.commitAllowingStateLoss();
            } catch (IllegalStateException ex) {

            }
        }
    }

    @Override
    public void onOptionSelected(boolean isProceed) {

        if (bottom_navigation.getSelectedItemId() == R.id.action_assisted) {
            assistedServiceFragment.onOptionSelected(isProceed);
        }
    }

    @Override
    public void onProceedOrder(boolean isApproval, boolean isProceed) {

    }

    @Override
    public void onAddService(String assistedServiceId, String linkId, String
            serviceTypeId, String price, String overTimePrice) {
        if (bottom_navigation.getSelectedItemId() == R.id.action_assisted) {
            assistedServiceFragment.onAddService(assistedServiceId, linkId, serviceTypeId, price, overTimePrice);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void changeViewPagerFragment(int position) {
        changeViewPagerFragment(position, 0);
    }

    public void changeViewPagerFragment(int position, int page) {

        if (position == 1) {
            /**
             * Moving this up, since it resets the page of the pager to 0
             */
            bottom_navigation.setSelectedItemId(R.id.action_chat);

            OrderFragment orderFragment = OrderFragment.newInstance(page);
            replaceFragment(orderFragment, 2);
            setUpToolbar(getString(R.string.my_order));

            //  iv_notification.setVisibility(View.INVISIBLE);
            //  iv_search.setVisibility(View.GONE);

        } else if (position == 2) {
            VerificationFragment verificationFragment = VerificationFragment.newInstance();
            replaceFragment(verificationFragment, 1);
            setUpToolbar(getString(R.string.verification));

            //  iv_notification.setVisibility(View.INVISIBLE);
            // iv_search.setVisibility(View.GONE);

            bottom_navigation.setSelectedItemId(R.id.action_verification);
        } else if (position == 3) {
            myCustomerFragment = CustomerFragment.newInstance(0);
            replaceFragment(myCustomerFragment, 3);
            setUpToolbar(getString(R.string.my_customers));

            //  iv_notification.setVisibility(View.VISIBLE);
            //  iv_notification.setImageDrawable(ContextCompat.getDrawable(DashboardActivity.this, R.drawable.ic_add_offer));
            //  iv_search.setVisibility(View.VISIBLE);

            bottom_navigation.setSelectedItemId(R.id.action_customers);
        } else if (position == 4) {
            assistedServiceFragment = AssistedServiceFragment.newInstance("", "");
            replaceFragment(assistedServiceFragment, 4);
            setUpToolbar(getString(R.string.assisted_services));

            //   iv_notification.setVisibility(View.VISIBLE);
            //   iv_notification.setImageDrawable(ContextCompat.getDrawable(DashboardActivity.this, R.drawable.ic_add_offer));
            //   iv_search.setVisibility(View.VISIBLE);

            bottom_navigation.setSelectedItemId(R.id.action_assisted);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app_bar, menu);


        MenuItem mSearch = menu.findItem(R.id.action_search);
        MenuItem mNotification = menu.findItem(R.id.action_notification);
        MenuItem mAddOffer = menu.findItem(R.id.action_add);

        if(whichScreen.equalsIgnoreCase("Home")){
            mSearch.setVisible(false);
            mNotification.setVisible(false);
            mAddOffer.setVisible(false);
        }else if (whichScreen.equalsIgnoreCase("Customer")){
            mNotification.setVisible(false);
        }else if (whichScreen.equalsIgnoreCase("Assisted")){
            mNotification.setVisible(false);
        }

        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.equalsIgnoreCase("")){
                    MyCustomerFragment.frag.onRefreshPage();
                    InvitedCustomerFragment.frag.onRefreshPage();
                }else{
                    if (whichScreen.equalsIgnoreCase("Customer")) {
                        CustomerFragment.fragment.showSearchView();
                        int pos = CustomerPagerAdapter.positionPager;
                        if (pos == 0) {
                            // InvitedCustomerFragment.frag.isInvited = false;
                            MyCustomerFragment.frag.FireQuery(newText);
                        }else if(pos ==1){
                            //  InvitedCustomerFragment.frag.isInvited = true;
                            InvitedCustomerFragment.frag.FireQuery(newText);
                        }
                    }else if(whichScreen.equalsIgnoreCase("Assisted")){
                        AssistedServiceFragment.frag.FireQuery(newText);
                    }
                }

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    private Fragment getVisibleFragment() {
        FragmentManager fragmentManager = DashboardActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

}
