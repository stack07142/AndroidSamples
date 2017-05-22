package com.android.sample_viewpager;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;


/**
 * 1. ViewPager 사용하여 이미지 갤러리 생성
 * 2. ActionBar 숨기기
 * 3. StatusBar 숨기기
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // actionbar 숨기기
        getSupportActionBar().hide();

        // status bar 숨기기(풀스크린)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // ViewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
    }
}
