package com.example.admin.customtablayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CustomTabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initViewPager();

    }

    private void initView() {
        mTabLayout = findViewById(R.id.ctl_tab);
        mViewPager = findViewById(R.id.vp_main);

        Button textColBtn = findViewById(R.id.btn_text_col);
        Button lineColBtn = findViewById(R.id.btn_line_col);

        textColBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mTabLayout.setCheckedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                mTabLayout.setCheckedTextColor(getResources().getColor(R.color.colorAccent));
            }
        });

        lineColBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mTabLayout.setUnderlineColor(ContextCompat.getColor(getApplicationContext(), R.color.color_green_light_alpha_10));
//                mTabLayout.setUnderlineColor(getResources().getColor(R.color.color_green_light_alpha_10));
                PrintUtil.log("currentPosition", mTabLayout.getUnderlinePosition());
            }
        });


    }

    private void initData() {
        //设置tab标签
        mTabLayout.setTitleArr(new String[]{"title0", "title1", "title2", "title3",
                "title4", "title5", "title6", "title7", "title8", "title9"});

        //如果初始下划线position不为0，则调该方法调整初始position
        mTabLayout.initUnderlinePosition(3);

//        mTabLayout.setUnderlineColor(getResources().getColor(R.color.color_red_light));
//        mTabLayout.setCheckedTextColor(getResources().getColor(R.color.color_green_light));

        //tab点击事件
        mTabLayout.setOnTabClickListener(new CustomTabLayout.OnTabClickListener() {
            @Override
            public void tabClick(int position, String str) {
                //与ViewPager的联动
                mViewPager.setCurrentItem(position);
                /*if (position == 0){
                    mTabLayout.setUnderlineColor(getResources().getColor(R.color.color_blue_light));
                }else if (position == 1){
                    mTabLayout.setUnderlineColor(getResources().getColor(R.color.color_red_light));
                }*/
            }
        });


    }

    private void initViewPager() {
        FragmentManager manager = getSupportFragmentManager();
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            fragmentList.add(new TestFragment());
        }
        mViewPager.setAdapter(new MainVpAdapter(manager, fragmentList));
        mViewPager.setOffscreenPageLimit(9);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
