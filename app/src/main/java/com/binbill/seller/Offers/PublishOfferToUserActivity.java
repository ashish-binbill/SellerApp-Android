package com.binbill.seller.Offers;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.AppButtonGreyed;
import com.binbill.seller.Model.UserModel;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

@EActivity(R.layout.activity_publish_offer_to_user)
public class PublishOfferToUserActivity extends BaseActivity implements UserAdapter.CardInteractionListener {


    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    RecyclerView rv_user_list;

    @ViewById
    LinearLayout shimmer_view_container;

    @ViewById
    LinearLayout no_data_layout, btn_publish_progress;

    @ViewById
    TextView tv_no_data;

    @ViewById
    CheckBox cb_select_all;

    @ViewById
    AppButton btn_no_data;
    private ArrayList<UserModel> mUserList;

    @ViewById
    AppButton btn_publish;

    @ViewById
    ImageView iv_notification;

    @ViewById
    SwipeRefreshLayout sl_pull_to_refresh;

    private String mOfferId;
    private String mFlowType;
    private boolean isCreatingOffer;
    private UserAdapter mAdapter;

    @AfterViews
    public void setUpView() {

        if (getIntent() != null && getIntent().hasExtra(Constants.OFFER_ITEM)) {
            OfferItem offerItem = (OfferItem) getIntent().getSerializableExtra(Constants.OFFER_ITEM);
            mOfferId = offerItem.getOfferId();
            isCreatingOffer = getIntent().getBooleanExtra(Constants.CREATE_OFFER, true);
        }

        mFlowType = getIntent().getStringExtra(Constants.FLOW_TYPE);

        setUpToolbar();
        sl_pull_to_refresh.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (mFlowType) {
            case Constants.SHOW_LINKED_USERS:
                setUpToolbar();
                makeUserFetchApiCallForLinkedUsers();
                break;
            case Constants.ADD_USER_FOR_OFFER:
                setUpToolbar();
                makeUserFetchApiCallToAddUsers();
                break;

        }
    }

    private void setUpListeners() {
        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> selectedUserId = getSelectedUserId();

                if (selectedUserId != null && selectedUserId.size() > 0
                        && mOfferId != null && !Utility.isEmpty(mOfferId))
                    makePublishApiCallToServer(selectedUserId);
            }
        });

        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFlowType = Constants.ADD_USER_FOR_OFFER;
                setUpToolbar();
                makeUserFetchApiCallToAddUsers();

                sl_pull_to_refresh.setEnabled(false);
            }
        });

        sl_pull_to_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (mFlowType) {
                    case Constants.SHOW_LINKED_USERS:
                        makeUserFetchApiCallForLinkedUsers();
                        break;
                    case Constants.ADD_USER_FOR_OFFER:
                        makeUserFetchApiCallToAddUsers();
                        break;
                }

                sl_pull_to_refresh.setRefreshing(false);
            }
        });

        cb_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {

                    for (UserModel user : mUserList) {
                        user.setSelected(true);
                    }

                    mAdapter.notifyDataSetChanged();

                } else {

                    boolean areAllUserUnselect = true;
                    for (UserModel user : mUserList) {

                        if (!user.isSelected()) {
                            areAllUserUnselect = false;
                            break;
                        }
                    }

                    if (areAllUserUnselect) {
                        for (UserModel user : mUserList)
                            user.setSelected(false);
                    }

                    mAdapter.notifyDataSetChanged();
                }

                enableDisableVerifyButton();
            }
        });

    }

    private void makePublishApiCallToServer(ArrayList<String> userId) {

        btn_publish.setVisibility(View.GONE);
        btn_publish_progress.setVisibility(View.VISIBLE);
        new RetrofitHelper(this).publishOfferToUsers(userId, mOfferId, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        invokeSuccessDialog();
                    } else {
                        showSnackBar(getString(R.string.something_went_wrong));
                    }
                } catch (JSONException e) {
                    showSnackBar(getString(R.string.something_went_wrong));
                }

                btn_publish.setVisibility(View.VISIBLE);
                btn_publish_progress.setVisibility(View.GONE);

            }

            @Override
            public void onErrorResponse() {
                showSnackBar(getString(R.string.something_went_wrong));

                btn_publish.setVisibility(View.VISIBLE);
                btn_publish_progress.setVisibility(View.GONE);
            }
        });
    }

    private ArrayList<String> getSelectedUserId() {

        ArrayList<String> idList = new ArrayList<>();

        if (mUserList != null && mUserList.size() > 0) {
            for (UserModel user : mUserList) {
                if (user.isSelected())
                    idList.add(user.getUserId());
            }
        }

        return idList;
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        switch (mFlowType) {
            case Constants.SHOW_LINKED_USERS:
                toolbarText.setText(getString(R.string.linked_user));
                iv_notification.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_offer));
                iv_notification.setVisibility(View.VISIBLE);
                btn_publish.setVisibility(View.GONE);

                cb_select_all.setVisibility(View.GONE);
                break;
            case Constants.ADD_USER_FOR_OFFER:
                toolbarText.setText(getString(R.string.select_user));
                iv_notification.setVisibility(View.INVISIBLE);
                btn_publish.setVisibility(View.VISIBLE);

                cb_select_all.setVisibility(View.VISIBLE);
                break;
        }

        setUpListeners();
        enableDisableVerifyButton();
    }

    @Override
    public void onBackPressed() {

        if (isCreatingOffer) {
            super.onBackPressed();
        } else {
            switch (mFlowType) {
                case Constants.SHOW_LINKED_USERS:
                    super.onBackPressed();
                    break;
                case Constants.ADD_USER_FOR_OFFER:
                    mFlowType = Constants.SHOW_LINKED_USERS;
                    setUpToolbar();
                    makeUserFetchApiCallForLinkedUsers();
                    break;

            }
        }
    }

    private void makeUserFetchApiCallToAddUsers() {

        shimmer_view_container.setVisibility(View.VISIBLE);
        rv_user_list.setVisibility(View.GONE);
        no_data_layout.setVisibility(View.GONE);


        new RetrofitHelper(this).fetchOfferUsersForSeller(mOfferId, false, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                handleResponse(response);
            }

            @Override
            public void onErrorResponse() {
                showSnackBar(getString(R.string.something_went_wrong));
                showNoUserLayout();
            }
        });
    }

    private void makeUserFetchApiCallForLinkedUsers() {

        shimmer_view_container.setVisibility(View.VISIBLE);
        rv_user_list.setVisibility(View.GONE);
        no_data_layout.setVisibility(View.GONE);


        new RetrofitHelper(this).fetchOfferUsersForSeller(mOfferId, true, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                handleResponse(response);
            }

            @Override
            public void onErrorResponse() {
                showSnackBar(getString(R.string.something_went_wrong));
                showNoUserLayout();
            }
        });
    }

    private void handleResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("status")) {

                if (jsonObject.optJSONArray("result") != null) {
                    JSONArray userArray = jsonObject.getJSONArray("result");
                    if (userArray.length() > 0) {
                        setUpUserInList(userArray);
                    } else
                        showNoUserLayout();
                }
            } else {
                showSnackBar(getString(R.string.something_went_wrong));
                showNoUserLayout();
            }
        } catch (JSONException e) {
            showSnackBar(getString(R.string.something_went_wrong));
            showNoUserLayout();
        }
    }

    private void setUpUserInList(JSONArray userArray) {
        /**
         * setup recycler view
         */
        boolean linkUser = false;
        switch (mFlowType) {
            case Constants.SHOW_LINKED_USERS:
                linkUser = false;
                break;
            case Constants.ADD_USER_FOR_OFFER:
                linkUser = true;
        }

        Type classType = new TypeToken<ArrayList<UserModel>>() {
        }.getType();

        mUserList = new Gson().fromJson(userArray.toString(), classType);
        rv_user_list.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_user_list.setLayoutManager(llm);
        mAdapter = new UserAdapter(Constants.OFFER, mUserList, this, linkUser);
        rv_user_list.setAdapter(mAdapter);

        rv_user_list.setVisibility(View.VISIBLE);
        shimmer_view_container.setVisibility(View.GONE);
        no_data_layout.setVisibility(View.GONE);
    }

    private void showNoUserLayout() {

        switch (mFlowType) {
            case Constants.SHOW_LINKED_USERS:
                tv_no_data.setText(getString(R.string.no_users_linked));
                break;
            case Constants.ADD_USER_FOR_OFFER:
                tv_no_data.setText(getString(R.string.no_users_added));
                break;
        }

        cb_select_all.setVisibility(View.GONE);
        btn_publish.setVisibility(View.GONE);

        btn_no_data.setVisibility(View.GONE);
        no_data_layout.setVisibility(View.VISIBLE);
        rv_user_list.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.GONE);
    }

    private void enableDisableVerifyButton() {
        boolean enable = false;
        if (mUserList != null && mUserList.size() > 0) {
            for (UserModel user : mUserList) {
                if (user.isSelected()) {
                    enable = true;
                    break;
                }
            }
        }

        if (enable)
            Utility.enableButton(this, btn_publish, true);
        else
            Utility.enableButton(this, btn_publish, false);
    }

    @Override
    public void onCardInteraction() {

        boolean allUserChecked = true;
        for(UserModel user: mUserList){
            if(!user.isSelected()){
                allUserChecked = false;
            }
        }

        cb_select_all.setChecked(allUserChecked);

        enableDisableVerifyButton();
    }

    @Override
    public void onCardClicked(int position) {
        /**
         * Do nothing
         */
    }

    @Override
    public void onAddPoints(int position) {
        /**
         * Do nothing
         */
    }

    @Override
    public void onAddCredits(int position) {

    }

    @Override
    public void onCustomerAdded(int position) {

    }

    public void invokeSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_yes_no, null);

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
        TextView titleText = (TextView) dialogView.findViewById(R.id.title);
        AppButton yesButton = (AppButton) dialogView.findViewById(R.id.btn_yes);
        AppButtonGreyed noButton = (AppButtonGreyed) dialogView.findViewById(R.id.btn_no);
        noButton.setVisibility(View.GONE);

        titleText.setText(getString(R.string.offer_published_success));
        headerTitle.setText(getString(R.string.offer_published));
        yesButton.setText(getString(R.string.ok));

        headerTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onBackPressed();
            }
        });

        dialog.show();
    }
}
