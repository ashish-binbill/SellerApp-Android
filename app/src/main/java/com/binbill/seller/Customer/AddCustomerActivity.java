package com.binbill.seller.Customer;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.Model.UserModel;
import com.binbill.seller.Offers.UserAdapter;
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

@EActivity(R.layout.activity_add_customer)
public class AddCustomerActivity extends BaseActivity implements UserAdapter.CardInteractionListener {
    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    ImageView iv_notification;

    @ViewById
    RecyclerView rv_user_list;

    @ViewById
    EditText search_view;

    @ViewById
    LinearLayout shimmer_view_container;

    @ViewById
    TextView tv_no_data;

    @ViewById
    AppButton btn_invite;

    @AfterViews
    public void setUpView() {
        setUpToolbar();
        setUpListener();
    }

    private void setUpListener() {
        search_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!Utility.isEmpty(editable.toString())) {
                    String text = editable.toString().trim();
                    int length = text.length();

                    if (length >= 5) {
                        makeFetchUserCallToLink(text);
                    }
                }
            }
        });

        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = search_view.getText().toString();
                if (!Utility.isEmpty(mobile) && isValidMobileNumber(mobile)) {
                    new RetrofitHelper(AddCustomerActivity.this).inviteUser(mobile, new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.optBoolean("status")) {
                                    invokeSuccessDialog();
                                } else
                                    showSnackBar(getString(R.string.something_went_wrong));

                            } catch (JSONException e) {
                                showSnackBar(getString(R.string.something_went_wrong));
                            }
                        }

                        @Override
                        public void onErrorResponse() {
                            showSnackBar(getString(R.string.something_went_wrong));
                        }
                    });
                }

            }
        });
    }

    public void invokeSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_assisted_service, null);

        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialogView.setMinimumWidth((int) (displayRectangle.width() * 0.9f));

        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView title = (TextView) dialogView.findViewById(R.id.title);
        title.setText(getString(R.string.invitation_sent));
        AppButton yesButton = (AppButton) dialogView.findViewById(R.id.btn_yes);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onBackPressed();
            }
        });

        dialog.show();
    }

    public boolean isValidMobileNumber(String mobileStr) {
        Long mobileNumber = Utility.isValidMobileNumber(mobileStr);
        return mobileNumber.compareTo(-1L) != 0;
    }

    private void makeFetchUserCallToLink(String number) {

        rv_user_list.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.VISIBLE);
        tv_no_data.setVisibility(View.GONE);
        btn_invite.setVisibility(View.GONE);

        new RetrofitHelper(this).fetchUsersToInvite(number, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                handleResponse(response);
            }

            @Override
            public void onErrorResponse() {
                tv_no_data.setVisibility(View.VISIBLE);
                shimmer_view_container.setVisibility(View.GONE);
                rv_user_list.setVisibility(View.GONE);
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
                showNoUserLayout();
            }
        } catch (JSONException e) {
            showNoUserLayout();
        }
    }

    private void showNoUserLayout() {

        String mobile = search_view.getText().toString();
        if (!Utility.isEmpty(mobile) && isValidMobileNumber(mobile)) {
            btn_invite.setVisibility(View.VISIBLE);
        } else
            btn_invite.setVisibility(View.GONE);

        tv_no_data.setVisibility(View.VISIBLE);
        shimmer_view_container.setVisibility(View.GONE);
        rv_user_list.setVisibility(View.GONE);
    }

    private void setUpUserInList(JSONArray userArray) {
        Type classType = new TypeToken<ArrayList<UserModel>>() {
        }.getType();

        ArrayList<UserModel> mUserList = new Gson().fromJson(userArray.toString(), classType);
        rv_user_list.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_user_list.setLayoutManager(llm);
        UserAdapter mAdapter = new UserAdapter(Constants.ADD_CUSTOMER, mUserList, this, false);
        rv_user_list.setAdapter(mAdapter);

        rv_user_list.setVisibility(View.VISIBLE);
        shimmer_view_container.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
        btn_invite.setVisibility(View.GONE);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.add_new_customer));

        iv_notification.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCardInteraction() {
        /**
         * Do nothing
         */
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
}
