package com.binbill.seller.Dashboard;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.APIHelper.ApiHelper;
import com.binbill.seller.AppSession;
import com.binbill.seller.AssistedService.AddAssistedServiceActivity_;
import com.binbill.seller.AssistedService.AdditionalServiceDialogFragment;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.YesNoDialogFragment;
import com.binbill.seller.Customer.AddCustomerActivity_;
import com.binbill.seller.DeliveryAgent.DeliveryAgentActivity_;
import com.binbill.seller.Login.LoginActivity_;
import com.binbill.seller.Model.DashboardModel;
import com.binbill.seller.Model.MainCategory;
import com.binbill.seller.R;
import com.binbill.seller.Registration.RegistrationResolver;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
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

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

@EActivity(R.layout.activity_dashboard)
public class DashboardActivity extends BaseActivity implements YesNoDialogFragment.YesNoClickInterface,
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

    @ViewById
    ImageView iv_notification, iv_search;

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
    private MyCustomerFragment myCustomerFragment;
    public int sellerType;

    @AfterViews
    public void setUpView() {
        setUpNavigationView();
        setUpListener();
        makeSellerProfileApiCall();
        ApiHelper.getUserSelectedCategories(this);
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

                        JSONArray paymentModesArray = jsonObject.getJSONArray("payment_modes");
                        classType = new TypeToken<ArrayList<MainCategory>>() {
                        }.getType();

                        ArrayList<MainCategory> paymentModes = new Gson().fromJson(paymentModesArray.toString(), classType);
                        AppSession.getInstance(DashboardActivity.this).setPaymentModes(paymentModes);

                        setUpHamburger();
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onErrorResponse() {

            }
        });
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
        params.setMargins(0, 0, 0, 0);
        iv_user_image.setLayoutParams(params);

        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
        picasso.load(Constants.BASE_URL + "customer/" + model.getId() + "/images")
                .config(Bitmap.Config.RGB_565)
                .into(iv_user_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) iv_user_image.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                        iv_user_image.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        tv_shop_name.setText(model.getName());
        tv_shop_number.setText(model.getContactNo());

        TextView wallet = nav_view.findViewById(R.id.wallet_amount);
        wallet.setText(getString(R.string.wallet_points, model.getCashBack()));
    }

    private void setUpListener() {

        TextView manageDelivery = nav_view.findViewById(R.id.tv_manage_delivery_boy);
        manageDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(DashboardActivity.this, DeliveryAgentActivity_.class));
            }
        });

        TextView manageCategory = nav_view.findViewById(R.id.tv_manage_category);
        manageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);
                Intent intent = RegistrationResolver.getNextIntent(DashboardActivity.this, 4);
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
                Intent intent = RegistrationResolver.getNextIntent(DashboardActivity.this, 5);
                if (intent != null)
                    startActivity(intent);
            }
        });

        TextView logout = nav_view.findViewById(R.id.tv_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer_layout.isDrawerOpen(GravityCompat.START))
                    drawer_layout.closeDrawer(GravityCompat.START);
                just_sec_layout.setVisibility(View.VISIBLE);
                SharedPref.clearSharedPreferences(DashboardActivity.this);
                AppSession.setInstanceToNull();

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

        TextView wallet = nav_view.findViewById(R.id.wallet_amount);
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        iv_notification.setOnClickListener(new View.OnClickListener() {
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
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
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
                        myCustomerFragment.showSearchView();
                        break;
                    case R.id.action_assisted:
                        assistedServiceFragment.showSearchView();
                        break;
                }
            }
        });
    }

    private void setUpNavigationView() {

        HomeFragment homeFragment = HomeFragment.newInstance("", "");
        replaceFragment(homeFragment, 0);
        setUpToolbar(getString(R.string.dashboard));
        iv_notification.setImageDrawable(ContextCompat.getDrawable(DashboardActivity.this, R.drawable.ic_notify_bell));
        iv_notification.setVisibility(View.VISIBLE);

        bottom_navigation.setSelectedItemId(R.id.action_home);
        /**
         * To disable the default shifting of bottom navigation menu items
         */
//        Utility.disableShiftMode(bottom_navigation);

        DashboardModel dashboardModel = AppSession.getInstance(this).getDashboardData();

        sellerType = FMCG_ASSISTED_USER_POS;

        if (dashboardModel.isAssisted()) {
            if (dashboardModel.isFmcg()) {
                if (dashboardModel.isHasPos())
                    sellerType = FMCG_ASSISTED_USER_POS;
                else
                    sellerType = FMCG_ASSISTED_USER_NO_POS;
            } else
                sellerType = ASSISTED_ONLY_USER;
        } else {
            if (dashboardModel.isFmcg()) {
                if (dashboardModel.isHasPos())
                    sellerType = FMCG_ONLY_USER_HAS_POS;
                else
                    sellerType = FMCG_ONLY_USER_NO_POS;
            }
        }

        dashboardModel.setSellerType(sellerType);
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


        bottom_navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                HomeFragment homeFragment = HomeFragment.newInstance("", "");
                                replaceFragment(homeFragment, 0);
                                setUpToolbar(getString(R.string.dashboard));

                                iv_notification.setImageDrawable(ContextCompat.getDrawable(DashboardActivity.this, R.drawable.ic_notify_bell));
                                iv_notification.setVisibility(View.VISIBLE);
                                iv_search.setVisibility(View.GONE);
                                break;
                            case R.id.action_verification:
                                VerificationFragment verificationFragment = VerificationFragment.newInstance();
                                replaceFragment(verificationFragment, 1);
                                setUpToolbar(getString(R.string.verification));

                                iv_notification.setVisibility(View.INVISIBLE);
                                iv_search.setVisibility(View.GONE);
                                break;
                            case R.id.action_chat:
                                OrderFragment orderFragment = OrderFragment.newInstance();
                                replaceFragment(orderFragment, 2);
                                setUpToolbar(getString(R.string.my_order));

                                iv_notification.setVisibility(View.INVISIBLE);
                                iv_search.setVisibility(View.GONE);
                                break;
                            case R.id.action_customers:
                                myCustomerFragment = MyCustomerFragment.newInstance();
                                replaceFragment(myCustomerFragment, 3);
                                setUpToolbar(getString(R.string.my_customers));

                                iv_notification.setVisibility(View.VISIBLE);
                                iv_notification.setImageDrawable(ContextCompat.getDrawable(DashboardActivity.this, R.drawable.ic_add_offer));
                                iv_search.setVisibility(View.VISIBLE);
                                break;
                            case R.id.action_assisted:
                                assistedServiceFragment = AssistedServiceFragment.newInstance("", "");
                                replaceFragment(assistedServiceFragment, 4);
                                setUpToolbar(getString(R.string.assisted_services));

                                iv_notification.setVisibility(View.VISIBLE);
                                iv_notification.setImageDrawable(ContextCompat.getDrawable(DashboardActivity.this, R.drawable.ic_add_offer));
                                iv_search.setVisibility(View.VISIBLE);
                                break;

                        }
                        return true;
                    }
                });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer_layout.openDrawer(GravityCompat.START);
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
    public void onAddService(String assistedServiceId, String linkId, String serviceTypeId, String price, String overTimePrice) {
        if (bottom_navigation.getSelectedItemId() == R.id.action_assisted) {
            assistedServiceFragment.onAddService(assistedServiceId, linkId, serviceTypeId, price, overTimePrice);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
