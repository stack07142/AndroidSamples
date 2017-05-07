package com.android.sample_listview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * ListView 사용하기
 *
 * 1. 아이템을 위한 XML 레이아웃 정의하기 (list_item.xml)
 * 2. 아이템을 위한 뷰 정의하기
 * 3. 어댑터 정의하기
 * 4. 리스트뷰 다루기
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_move_listview).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ListViewActivity.class);
                startActivity(intent);
                // showProgressDialog();
            }
        });
    }
}
