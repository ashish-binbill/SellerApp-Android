package com.binbill.seller.Dashboard;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.binbill.seller.AppSession;
import com.binbill.seller.AssistedService.AddAssistedServiceActivity_;
import com.binbill.seller.AssistedService.AdditionalServiceDialogFragment;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.YesNoDialogFragment;
import com.binbill.seller.Customer.AddCustomerActivity_;
import com.binbill.seller.Order.ActiveOrderFragment;
import com.binbill.seller.R;
import com.binbill.seller.Registration.RegistrationResolver;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.SharedPref;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

@EActivity(R.layout.activity_dashboard)
public class DashboardActivity extends BaseActivity implements YesNoDialogFragment.YesNoClickInterface,
        AdditionalServiceDialogFragment.AdditionalServiceClickInterface, NavigationView.OnNavigationItemSelectedListener {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    ImageView iv_notification, iv_search, iv_socket;

    @ViewById
    FrameLayout container;

    @ViewById
    DrawerLayout drawer_layout;

    @ViewById
    NavigationView nav_view;

    @ViewById
    BottomNavigationView bottom_navigation;
    private AssistedServiceFragment assistedServiceFragment;
    private MyCustomerFragment myCustomerFragment;

    @AfterViews
    public void setUpView() {
        connectSocket();
        setUpNavigationView();
        setUpListener();
        makeSellerProfileApiCall();
    }

    private void makeSellerProfileApiCall() {
        new RetrofitHelper(this).getSellerDetails(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onErrorResponse() {

            }
        });
    }

    io.socket.client.Socket mSocket;

    private void connectSocket() {
        IO.Options opts = new IO.Options();
        try {
            String authToken = SharedPref.getString(DashboardActivity.this, SharedPref.AUTH_TOKEN);
            opts.query = "token=" + authToken;
            mSocket = IO.socket(Constants.BASE_URL, opts);
            mSocket.connect();

            mSocket.on("order-placed", onServer);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Emitter.Listener onServer = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SHRUTI", "CONNECTED");
        }
    };

    private void setUpListener() {

        TextView manageCategory = nav_view.findViewById(R.id.tv_manage_category);
        manageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = RegistrationResolver.getNextIntent(DashboardActivity.this, 4);
                if (intent != null)
                    startActivity(intent);
            }
        });

        TextView profile = nav_view.findViewById(R.id.tv_shop_name);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity_.class));
            }
        });

        iv_socket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSocket.emit("init", AppSession.getInstance(DashboardActivity.this).getSellerId());
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
                                break;
                            case R.id.action_chat:
                                OrderFragment orderFragment = OrderFragment.newInstance();
                                replaceFragment(orderFragment, 2);
                                setUpToolbar(getString(R.string.my_order));

                                iv_notification.setVisibility(View.INVISIBLE);
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
    public void onAddService(String assistedServiceId, String linkId, String serviceTypeId, String price) {
        if (bottom_navigation.getSelectedItemId() == R.id.action_assisted) {
            assistedServiceFragment.onAddService(assistedServiceId, linkId, serviceTypeId, price);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
