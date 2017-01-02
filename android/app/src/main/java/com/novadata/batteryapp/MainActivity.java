package com.novadata.batteryapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import adapter.MyFragmentPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    private TabLayout.Tab main_tab;
    private TabLayout.Tab deal_tab;
    private TabLayout.Tab search_tab;
    private TabLayout.Tab user_tab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();//隐藏掉整个ActionBar
        setContentView(R.layout.activity_main);

        //初始化视图
        initViews();

    }

    private void initViews() {

        //使用适配器将ViewPager与Fragment绑定在一起
        mViewPager= (ViewPager) findViewById(R.id.viewPager);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myFragmentPagerAdapter);

        //将TabLayout与ViewPager绑定在一起
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        assert mTabLayout != null;
        mTabLayout.setupWithViewPager(mViewPager);

        //指定Tab的位置
        main_tab = mTabLayout.getTabAt(0);
        deal_tab = mTabLayout.getTabAt(1);
        search_tab = mTabLayout.getTabAt(2);
        user_tab = mTabLayout.getTabAt(3);

        //设置Tab的图标
        main_tab.setIcon(R.drawable.ic_home_selector);
        deal_tab.setIcon(R.drawable.ic_deal_selector);
        search_tab.setIcon(R.drawable.ic_search_selector);
        user_tab.setIcon(R.drawable.ic_user_selector);

    }
}
