package com.example.admin.customtablayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CustomTabLayout mTabLayout;
    private ViewPager mMainVp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
        initData();
        initViewPager();



    }

    private void initView(){
        mTabLayout = findViewById(R.id.ctl_tab);
        mMainVp = findViewById(R.id.vp_main);
    }

    private void initData(){
        mTabLayout.setTitleArr(new String[]{"title0", "title1", "title2", "title3", "title4", "title5", "title6",
                "title7", "title8", "title9"});

        mTabLayout.setOnTabClickListener(new CustomTabLayout.OnTabClickListener() {
            @Override
            public void tabClick(int position, String str) {
                PrintUtil.log("position", position);
                PrintUtil.log("str", str);
                mMainVp.setCurrentItem(position);
            }
        });

        mTabLayout.initUnderlinePosition(1);


    }

    private void initViewPager(){
        FragmentManager manager = getSupportFragmentManager();
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            fragmentList.add(new TestFragment());
        }
        mMainVp.setAdapter(new MainVpAdapter(manager, fragmentList));
        mMainVp.setOffscreenPageLimit(9);
        mMainVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout.moveToPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }



}
