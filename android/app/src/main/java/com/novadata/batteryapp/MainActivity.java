package com.novadata.batteryapp;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

import adapter.MyFragmentPagerAdapter;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.AfterPermissionGranted;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    public static MainActivity mainActivity;

    private TabLayout.Tab main_tab;
    private TabLayout.Tab deal_tab;
    private TabLayout.Tab search_tab;
    private TabLayout.Tab user_tab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;

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

    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();
        Toast.makeText(this, "交易功能需要在 “我” 标签页登录后使用",Toast.LENGTH_SHORT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }
}
