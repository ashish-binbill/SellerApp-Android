package com.binbill.seller.Registration;

import android.icu.util.ULocale;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.binbill.seller.Adapter.CategoryAdapter;
import com.binbill.seller.AppSession;
import com.binbill.seller.BaseActivity;
import com.binbill.seller.Model.CategoryModel;
import com.binbill.seller.Model.FMCGHeaderModel;
import com.binbill.seller.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@EActivity(R.layout.activity_category)
public class MainCategoryActivity extends BaseActivity {

    @ViewById
    ImageButton close_btn;

    @ViewById
    RecyclerView recycler_view;

    @ViewById
    LinearLayout submit_choices;

    CategoryAdapter mAdapter;

    public static List<CategoryModel> itemList = new ArrayList<>();

    public static boolean isLaunchedFirstTime = false;


    @AfterViews
    public void initiateViews() {

        recycler_view.setHasFixedSize(true);

        // use a linear layout manager
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<FMCGHeaderModel> categories = AppSession.getInstance(this).getCategories();

        for (FMCGHeaderModel model : categories) {
            String name = model.getName();
            String id = model.getId();
            String imageUrl = model.getImageUrl();
            CategoryModel st = new CategoryModel(name, id, imageUrl, false);
            if(!isLaunchedFirstTime){
                itemList.add(st);
            }

        }

        // create an Object for Adapter
        mAdapter = new CategoryAdapter(itemList, MainCategoryActivity.this);
        // set the adapter object to the Recyclerview
        recycler_view.setAdapter(mAdapter);
        isLaunchedFirstTime = true;
    }

    @Click(R.id.close_btn)
    public void closeActivity(View view){
        finish();
    }

    @Click(R.id.submit_choices)
    public void choicesSubmit(View view){
        String data = "";
        String str = "";
        List<CategoryModel> stList = ((CategoryAdapter) mAdapter)
                .getItemList();

        List<String> id_arr = new ArrayList<String>();
        List<String> name_arr = new ArrayList<String>();

        for (int i = 0; i < stList.size(); i++) {
            CategoryModel singleStudent = stList.get(i);
            if (singleStudent.isSelected() == true) {
                id_arr.add(singleStudent.getId());
                name_arr.add(singleStudent.getName());
               /* data = data + "/n" + singleStudent.getId().toString();
                str = str + "/n"+ singleStudent.getName().toString();*/
            }
        }
        String IdStr = android.text.TextUtils.join(",", id_arr);
        String NameStr = android.text.TextUtils.join(",", name_arr);
        int[] idInt = parseLineToIntArray(IdStr);
        Log.v("TAGGINT", ""+idInt);
        Log.v("TAGSTR", str);

        if(idInt.length==0){
            Toast.makeText(this, "Please select at-least one category", Toast.LENGTH_SHORT).show();
        }else{
            RegisterActivity.idSize = idInt.length;
            RegisterActivity.mainCategoryIds = new int[idInt.length];
            RegisterActivity.mainCategoryIds = Arrays.copyOf(idInt,idInt.length);

            RegisterActivity.mainCategoryDetails =  NameStr;
            finish();
        }
    }
    static int[] toIntArray(String[] arr) {
        int[] ints = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            ints[i] = Integer.parseInt(arr[i]);
        }
        return ints;
    }

    static int[] parseLineToIntArray(String line) {
        return toIntArray(line.split(","));
    }



}
