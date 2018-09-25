package com.binbill.seller.FAQ;

import android.support.v7.widget.Toolbar;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.binbill.seller.BaseActivity;
import com.binbill.seller.R;
import com.binbill.seller.Retrofit.RetrofitHelper;
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
import java.util.HashMap;

@EActivity(R.layout.activity_faq)
public class FaqActivity extends BaseActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById(R.id.toolbar_text)
    TextView toolbarText;

    @ViewById
    ExpandableListView elv_faq;

    @AfterViews
    public void initiateViews() {

        setUpToolbar();
        setUpListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAllFaqs();
    }

    private void fetchAllFaqs() {
        new RetrofitHelper(this).fetchFaq(new RetrofitHelper.RetrofitCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optBoolean("status")) {
                        JSONArray faqArray = jsonObject.getJSONArray("faq");
                        Type classType = new TypeToken<ArrayList<FaqModel.FaqListItem>>() {
                        }.getType();

                        ArrayList<FaqModel.FaqListItem> faqList = new Gson().fromJson(faqArray.toString(), classType);
                        populateData(faqList);
                    } else
                        handleError();

                } catch (JSONException e) {
                    handleError();
                }
            }

            @Override
            public void onErrorResponse() {
                handleError();
            }
        });
    }

    private void populateData(ArrayList<FaqModel.FaqListItem> faqList) {

        if (faqList != null && faqList.size() > 0) {

            ArrayList<String> headerList = new ArrayList<>();
            HashMap<String, String> valueList = new HashMap<>();

            for (FaqModel.FaqListItem item : faqList) {
                headerList.add(item.getQuestion());
                valueList.put(item.getQuestion(), item.getAnswer());
            }

            FaqExpandableAdapter adapter = new FaqExpandableAdapter(this, headerList, valueList);
            elv_faq.setAdapter(adapter);
            elv_faq.expandGroup(0);
        }
    }

    private void handleError() {
        showSnackBar(getString(R.string.something_went_wrong));
    }

    private void setUpListeners() {

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        toolbarText.setText(getString(R.string.faqs));
    }

}
