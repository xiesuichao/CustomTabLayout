package com.example.admin.customtablayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by xiesuichao on 2018/8/25.
 */

public class MainVpAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;

    public MainVpAdapter(FragmentManager manager, List<Fragment> fragmentList){
        super(manager);
        this.mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
