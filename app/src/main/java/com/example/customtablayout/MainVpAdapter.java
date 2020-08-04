package com.example.customtablayout;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by darren on 2018/8/25.
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
