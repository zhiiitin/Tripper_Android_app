package com.example.tripper_android_app.explore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tripper_android_app.group.Group1Fragment;
import com.example.tripper_android_app.group.Group2Fragment;

import java.util.ArrayList;
import java.util.List;

public class ExplorePagerAdapter extends FragmentStateAdapter {
    List<Fragment> fragmentList = new ArrayList<>();

    public ExplorePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        Fragment ExploreFragment1 =  new ExploreFragment1();
        fragmentList.add(ExploreFragment1);
        Fragment ExploreFragment2 =  new ExploreFragment2();
        fragmentList.add(ExploreFragment2);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = fragmentList.get(position);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
