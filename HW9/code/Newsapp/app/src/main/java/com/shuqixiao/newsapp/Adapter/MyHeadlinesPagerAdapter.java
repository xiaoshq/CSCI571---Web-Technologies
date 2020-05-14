package com.shuqixiao.newsapp.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.shuqixiao.newsapp.Fragment.Headlines.FragmentBusiness;
import com.shuqixiao.newsapp.Fragment.Headlines.FragmentPolitics;
import com.shuqixiao.newsapp.Fragment.Headlines.FragmentScience;
import com.shuqixiao.newsapp.Fragment.Headlines.FragmentSports;
import com.shuqixiao.newsapp.Fragment.Headlines.FragmentTechnology;
import com.shuqixiao.newsapp.Fragment.Headlines.FragmentWorld;

public class MyHeadlinesPagerAdapter extends FragmentPagerAdapter {

    private String title[] = {"WORLD", "BUSINESS", "POLITICS", "SPORTS", "TECHNOLOGY", "SCIENCE"};

    public MyHeadlinesPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new FragmentBusiness();
        } else if (position == 2) {
            return new FragmentPolitics();
        } else if (position==3){
            return new FragmentSports();
        } else if (position==4){
            return new FragmentTechnology();
        } else if (position==5){
            return new FragmentScience();
        }
        return new FragmentWorld();
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}