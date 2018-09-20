package com.binbill.seller.Login;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.binbill.seller.CustomViews.AppButton;
import com.binbill.seller.R;
import com.gohn.parallaxviewpager.ParallaxViewPager;
import com.rd.PageIndicatorView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_landing)
public class LandingActivity extends AppCompatActivity {

    @ViewById
    ParallaxViewPager vp_pager;
    private LandingPagerAdapter adapter;

    @ViewById
    AppButton btn_start;

    @ViewById
    PageIndicatorView piv_indicator;

    @AfterViews
    public void initialiseView() {
        adapter = new LandingPagerAdapter(getSupportFragmentManager());
        addFragments();
        vp_pager.setAdapter(adapter);
        piv_indicator.setViewPager(vp_pager);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buttonName = btn_start.getText().toString();
                if (buttonName.equalsIgnoreCase(getString(R.string.get_started))) {
                    startActivity(new Intent(LandingActivity.this, LoginActivity_.class));
                } else {
                    vp_pager.setCurrentItem(getItem(1));
                }
            }
        });

        vp_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3) {
                    btn_start.setText(getString(R.string.get_started));
                } else {
                    btn_start.setText(getString(R.string.next));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private int getItem(int i) {
        return vp_pager.getCurrentItem() + i;
    }

    private void addFragments() {
        adapter.addFragment(LandingFragment.newInstance(1));
        adapter.addFragment(LandingFragment.newInstance(2));
        adapter.addFragment(LandingFragment.newInstance(3));
        adapter.addFragment(LandingFragment.newInstance(4));
    }

    public class LandingPagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();

        public LandingPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        public void addFragment(Fragment fragment) {
            fragments.add(fragment);
        }
    }

}
