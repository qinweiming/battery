package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import layout.fragment_deal;
import layout.fragment_main;
import layout.fragment_search;
import layout.fragment_user;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"主页", "交易","搜索", "我"};

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new fragment_deal();
        } else if (position == 2) {
            return new fragment_search();
        }else if (position == 3) {
            return new fragment_user();
        }
        return new fragment_main();
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
