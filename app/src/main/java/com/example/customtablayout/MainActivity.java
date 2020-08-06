package com.example.customtablayout;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CustomTabLayout mTabLayout;
    private ViewPager mViewPager;
    private String[] titleArr = null;

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
        Button thirdBtn = findViewById(R.id.btn_third);

    }

    private void initData() {
        titleArr = new String[]{"title0", "title111111111",
//                "title2", "title3",
//                "title5", "title6", "title7", "title8", "title9"
        };
        //设置tab标签
        mTabLayout.setTitleArr(titleArr);

        //tab点击事件
        mTabLayout.setOnTabClickListener(new CustomTabLayout.OnTabClickListener() {
            @Override
            public void tabClick(int position, String str) {
                //与ViewPager的联动
                mViewPager.setCurrentItem(position);

            }
        });

        mTabLayout.setOnTabScrollListener(new CustomTabLayout.OnTabScrollListener() {
            @Override
            public void scrollChange(int position, String text) {

            }
        });

    }

    private void initViewPager() {
        FragmentManager manager = getSupportFragmentManager();
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < titleArr.length; i++) {
            fragmentList.add(new TestFragment());
        }
        mViewPager.setAdapter(new MainVpAdapter(manager, fragmentList));
        mViewPager.setOffscreenPageLimit(fragmentList.size() - 1);
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
