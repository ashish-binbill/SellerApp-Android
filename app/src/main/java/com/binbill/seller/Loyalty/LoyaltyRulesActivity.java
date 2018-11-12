package com.binbill.seller.Loyalty;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.Constants;
import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.CustomViews.PrefixEditText;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
import com.binbill.seller.Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

@EActivity(R.layout.activity_loyalty_rules)
public class LoyaltyRulesActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    RelativeLayout just_sec_layout;
    private String ruleId;

    @ViewById
    FrameLayout fl_button;

    @ViewById
    LinearLayout ll_auto_credit, points_layout_ac;

    @ViewById
    RelativeLayout rl_auto_credit_layout;

    @ViewById
    RelativeLayout rl_loyalty_layout;

    @ViewById
    EditText et_points;

    @ViewById
    PrefixEditText et_points_ac;

    @ViewById
    ProgressBar progress, progress_ac;

    @ViewById
    AppButton btn_confirm, btn_confirm_ac;

    @ViewById
    SwitchCompat sc_auto_credit;

    @ViewById
    TextView tv_points, et_min_points, tv_auto_credit_text, example_ac, tv_auto_credit;
    private String type = Constants.LOYALTY_POINTS;

    @AfterViews
    public void initiateViews() {

        if (getIntent() != null && getIntent().hasExtra(Constants.TYPE))
            type = getIntent().getStringExtra(Constants.TYPE);

        setUpToolbar();
        setUpData();
        setUpListener();

        makeApiCall();
    }

    private void makeApiCall() {
        just_sec_layout.setVisibility(View.VISIBLE);
        new RetrofitHelper(this).getLoyaltyPoints(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {

                        JSONObject ruleJson = jsonObject.getJSONObject("loyalty_rules");

                        ruleId = ruleJson.getString("id");
                        String pointsPerItem = ruleJson.getString("points_per_item");
                        String minPoints = ruleJson.getString("minimum_points");

                        String orderValue = ruleJson.getString("order_value");
                        boolean isAutoLoyaltySet = ruleJson.optBoolean("allow_auto_loyalty");

                        setDataInUI(pointsPerItem, minPoints, isAutoLoyaltySet, orderValue);

                        just_sec_layout.setVisibility(View.GONE);
                    } else {
                        showSnackBar(getString(R.string.something_went_wrong));
                        just_sec_layout.setVisibility(View.GONE);

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);
                    }
                } catch (
                        JSONException e)

                {
                    showSnackBar(getString(R.string.something_went_wrong));
                    just_sec_layout.setVisibility(View.GONE);

                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onErrorResponse() {
                showSnackBar(getString(R.string.something_went_wrong));
                just_sec_layout.setVisibility(View.GONE);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }
        });
    }

    private void setUpListener() {
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValid()) {
                    btn_confirm.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);

                    String points = et_points.getText().toString();
                    String minPoints = et_min_points.getText().toString();

                    new RetrofitHelper(LoyaltyRulesActivity.this).setLoyaltyPoints(ruleId, points, minPoints, false, null, new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {
                            btn_confirm.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.optBoolean("status")) {

                                    JSONObject ruleJson = jsonObject.getJSONObject("loyalty_rules");

                                    ruleId = ruleJson.getString("id");
                                    String pointsPerItem = ruleJson.getString("points_per_item");
                                    String minPoints = ruleJson.getString("minimum_points");
                                    String orderValue = ruleJson.getString("order_value");
                                    boolean isAutoLoyaltySet = ruleJson.optBoolean("allow_auto_loyalty");

                                    setDataInUI(pointsPerItem, minPoints, isAutoLoyaltySet, orderValue);

                                    showSnackBar(getString(R.string.details_saved_successfully));

                                    finish();

                                } else
                                    showSnackBar(getString(R.string.something_went_wrong));
                            } catch (JSONException e) {
                                showSnackBar(getString(R.string.something_went_wrong));
                            }
                        }

                        @Override
                        public void onErrorResponse() {
                            showSnackBar(getString(R.string.something_went_wrong));
                            btn_confirm.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });

        sc_auto_credit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                setSwitchState(checked, true);
            }
        });

        btn_confirm_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValid()) {
                    btn_confirm_ac.setVisibility(View.GONE);
                    progress_ac.setVisibility(View.VISIBLE);

                    String orderValue = "1";
                    if (sc_auto_credit.isChecked()) {
                        orderValue = et_points_ac.getText().toString();
                    }

                    new RetrofitHelper(LoyaltyRulesActivity.this).setLoyaltyPoints(ruleId, null, null, true, orderValue, new RetrofitHelper.RetrofitCallback() {
                        @Override
                        public void onResponse(String response) {
                            btn_confirm_ac.setVisibility(View.VISIBLE);
                            progress_ac.setVisibility(View.GONE);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.optBoolean("status")) {

                                    JSONObject ruleJson = jsonObject.getJSONObject("loyalty_rules");

                                    ruleId = ruleJson.getString("id");
                                    String pointsPerItem = ruleJson.getString("points_per_item");
                                    String minPoints = ruleJson.getString("minimum_points");
                                    String orderValue = ruleJson.getString("order_value");
                                    boolean isAutoLoyaltySet = ruleJson.optBoolean("allow_auto_loyalty");
                                    setDataInUI(pointsPerItem, minPoints, isAutoLoyaltySet, orderValue);


                                    finish();

                                } else
                                    showSnackBar(getString(R.string.something_went_wrong));
                            } catch (JSONException e) {
                                showSnackBar(getString(R.string.something_went_wrong));
                            }
                        }

                        @Override
                        public void onErrorResponse() {
                            showSnackBar(getString(R.string.something_went_wrong));
                            btn_confirm_ac.setVisibility(View.VISIBLE);
                            progress_ac.setVisibility(View.GONE);
                        }
                    });
                } else
                    showSnackBar(getString(R.string.incorrect_input));
            }
        });
    }

    private void setSwitchState(boolean checked, boolean makeApi) {
        if (checked) {
            points_layout_ac.setVisibility(View.VISIBLE);
            example_ac.setVisibility(View.VISIBLE);
            fl_button.setVisibility(View.VISIBLE);
            tv_auto_credit_text.setVisibility(View.GONE);
            tv_auto_credit.setText(getString(R.string.auto_credit_enabled));

            if (makeApi)
                setAutoCredit(true);

        } else {
            points_layout_ac.setVisibility(View.GONE);
            example_ac.setVisibility(View.GONE);
            fl_button.setVisibility(View.GONE);
            tv_auto_credit_text.setVisibility(View.VISIBLE);
            tv_auto_credit.setText(getString(R.string.enable_auto_credit_wo));

            if (makeApi)
                setAutoCredit(false);
        }
    }

    private void setAutoCredit(boolean isOn) {

        new RetrofitHelper(LoyaltyRulesActivity.this).setLoyaltyPoints(ruleId, null, null, isOn, null, new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {

                        JSONObject ruleJson = jsonObject.getJSONObject("loyalty_rules");

                        ruleId = ruleJson.getString("id");
                        String pointsPerItem = ruleJson.getString("points_per_item");
                        String minPoints = ruleJson.getString("minimum_points");
                        String orderValue = ruleJson.getString("order_value");
                        boolean isAutoLoyaltySet = ruleJson.optBoolean("allow_auto_loyalty");

                        setDataInUI(pointsPerItem, minPoints, isAutoLoyaltySet, orderValue);

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


    private boolean isValid() {

        if (type.equalsIgnoreCase(Constants.AUTO_CREDIT)) {
            String points = et_points_ac.getText().toString();

            if (Utility.isEmpty(points))
                return false;

            try {
                Double amount = Double.parseDouble(points);
                if (amount <= 0)
                    return false;
            } catch (Exception e) {
                return false;
            }

        } else {
            String points = et_points.getText().toString();
            String minPoints = et_min_points.getText().toString();

            if (Utility.isEmpty(points))
                return false;

            if (Utility.isEmpty(minPoints))
                return false;
        }

        Utility.hideKeyboard(this, et_min_points);
        return true;
    }

    private void setUpData() {
        if (type.equalsIgnoreCase(Constants.AUTO_CREDIT)) {
            rl_auto_credit_layout.setVisibility(View.VISIBLE);
            rl_loyalty_layout.setVisibility(View.GONE);

        } else {
            rl_auto_credit_layout.setVisibility(View.GONE);
            rl_loyalty_layout.setVisibility(View.VISIBLE);
        }
    }

    private void setDataInUI(String pointsPerItem, String minPoints, boolean isAutoLoyalty, String orderValue) {

        if (type.equalsIgnoreCase(Constants.AUTO_CREDIT)) {
            et_points_ac.setText(orderValue);
            if (isAutoLoyalty) {
                sc_auto_credit.setChecked(true);

                setSwitchState(true, false);
            } else {
                sc_auto_credit.setChecked(false);

                setSwitchState(false, false);
            }


        } else {
            if (!Utility.isEmpty(pointsPerItem)) {
                if (Integer.parseInt(pointsPerItem) > 0) {
                    et_points.setText(pointsPerItem);
                    tv_points.setVisibility(View.VISIBLE);
                }
            }

            if (!Utility.isEmpty(minPoints)) {
                if (Integer.parseInt(minPoints) > 0) {
                    et_min_points.setText(minPoints);
                }
            }
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_back));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.set_loyalty_rules));
    }
}
