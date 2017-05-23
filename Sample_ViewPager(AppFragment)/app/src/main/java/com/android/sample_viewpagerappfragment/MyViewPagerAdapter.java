package com.android.sample_viewpagerappfragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

    int[] images = {R.drawable.blueberries, R.drawable.buildings, R.drawable.bulb};

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return new ImageFragment().newInstance(images[position]);
    }

    @Override
    public int getCount() {

        return images.length;
    }
}
