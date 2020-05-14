package com.shuqixiao.newsapp.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shuqixiao.newsapp.Fragment.FragmentBookmarks;
import com.shuqixiao.newsapp.Fragment.FragmentTrending;
import com.shuqixiao.newsapp.Fragment.Headlines.FragmentHeadlines;
import com.shuqixiao.newsapp.Fragment.Home.FragmentHome;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"Home", "", "",""};

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new FragmentHeadlines();
        } else if (position == 2) {
            return new FragmentTrending();
        }else if (position==3){
            return new FragmentBookmarks();
        }
        return new FragmentHome();
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
