# CustomTabLayout
自定义TabLayout

      <declare-styleable name="CustomTabLayout">
        <attr name="ctlTextSize" format="integer"/>
        <attr name="ctlTextColor" format="color"/>
        <attr name="ctlTvBackground" format="color"/>
        <attr name="ctlTextStyle" format="enum">
            <enum name="normal" value="0"/>
            <enum name="bold" value="1"/>
        </attr>
        <attr name="ctlUnderlineWidth" format="integer"/>
        <attr name="ctlUnderlineHeight" format="integer"/>
        <attr name="ctlUnderlineColor" format="color"/>
        <attr name="ctlUnderlineDuration" format="integer"/>
        <attr name="ctlHorizontalSpace" format="integer"/>
    </declare-styleable>
    
支持设置：   
字体大小，字体颜色，TextView背景色，是否粗体    
下划线宽度，下划线高度，下划线颜色（文字选中色与下划线一致）,下划线移动时间，tab之间的水平宽度


![image](https://github.com/xiesuichao/CustomTabLayout/raw/master/image/a2.png)


     private void initData() {
        //设置tab标签
        mTabLayout.setTitleArr(new String[]{"title0", "title1", "title2", "title3",
                "title4", "title5", "title6", "title7", "title8", "title9"});

        //tab点击事件
        mTabLayout.setOnTabClickListener(new CustomTabLayout.OnTabClickListener() {
            @Override
            public void tabClick(int position, String str) {
                //与ViewPager的联动
                mViewPager.setCurrentItem(position);
            }
        });

        //如果初始下划线position不为0，则调该方法调整初始position
        mTabLayout.initUnderlinePosition(1);
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
                //与viewpager联动
                mTabLayout.moveToPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

