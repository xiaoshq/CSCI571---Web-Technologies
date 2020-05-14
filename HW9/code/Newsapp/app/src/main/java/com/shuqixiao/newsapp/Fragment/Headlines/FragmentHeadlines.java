package com.shuqixiao.newsapp.Fragment.Headlines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.shuqixiao.newsapp.R;
import com.shuqixiao.newsapp.Adapter.MyHeadlinesPagerAdapter;

public class FragmentHeadlines extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyHeadlinesPagerAdapter viewPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_headlines, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.headlines_viewPager);
        viewPagerAdapter = new MyHeadlinesPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = (TabLayout) view.findViewById(R.id.headlines_tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
