package com.binbill.seller.Loyalty;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.CustomViews.AppButton;
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
    EditText et_points;

    @ViewById
    ProgressBar progress;

    @ViewById
    AppButton btn_confirm;

    @ViewById
    TextView tv_points, et_min_points;

    @AfterViews
    public void initiateViews() {
        setUpToolbar();
        setUpData();
        setUpListener();
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

                    new RetrofitHelper(LoyaltyRulesActivity.this).setLoyaltyPoints(ruleId, points, minPoints, new RetrofitHelper.RetrofitCallback() {
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
                                    setDataInUI(pointsPerItem, minPoints);

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
    }

    private boolean isValid() {

        String points = et_points.getText().toString();
        String minPoints = et_min_points.getText().toString();

        if (Utility.isEmpty(points))
            return false;

        if (Utility.isEmpty(minPoints))
            return false;

        return true;
    }

    private void setUpData() {

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
                        setDataInUI(pointsPerItem, minPoints);

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

    private void setDataInUI(String pointsPerItem, String minPoints) {

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

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        toolbarText.setText(getString(R.string.set_loyalty_rules));
    }
}
