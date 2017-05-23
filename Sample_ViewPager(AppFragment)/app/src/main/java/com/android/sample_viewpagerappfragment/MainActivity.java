package com.android.sample_viewpagerappfragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // view pager
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewpager);

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getFragmentManager());

        viewPager.setAdapter(myViewPagerAdapter);
    }
}
