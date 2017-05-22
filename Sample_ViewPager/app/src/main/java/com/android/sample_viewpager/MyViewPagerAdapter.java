package com.android.sample_viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyViewPagerAdapter extends FragmentPagerAdapter {

    int images[] = {R.drawable.blueberries, R.drawable.buildings, R.drawable.bulb};

    public MyViewPagerAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return ItemFragment.newInstance(images[position]);
    }

    @Override
    public int getCount() {

        return images.length;
    }
}
