package com.example.tripper_android_app.group;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStateAdapter {

    List<Fragment> fragmentList = new ArrayList<>();

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        Fragment group1Fragment =  new Group1Fragment();
        fragmentList.add(group1Fragment);
        Fragment group2Fragment =  new Group2Fragment();
        fragmentList.add(group2Fragment);
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
