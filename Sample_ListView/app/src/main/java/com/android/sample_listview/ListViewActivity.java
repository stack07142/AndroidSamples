package com.android.sample_listview;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ListViewActivity extends BaseActivity {

    private static final String TAG = ListViewActivity.class.getSimpleName();

    ListView listView1;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        listView1 = (ListView) findViewById(R.id.listview01);

        adapter = new ListViewAdapter(this);

        Resources res = getResources();

        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "추억의 테트리스", "30,000 다운로드", "900원"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "고스톱", "1,500 다운로드", "1,000원"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));
        adapter.addItem(new Item(res.getDrawable(R.drawable.ic_launcher), "리그오브레전드", "50,000 다운로드", "무료"));

        listView1.setAdapter(adapter);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Item curItem = (Item) adapter.getItem(position);

                String[] curData = curItem.getData();

                Toast.makeText(getApplicationContext(), "selected : " + curData[0], Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume()");

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    Thread.sleep(5000);

                    hideProgressDialog();
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }

            }
        });

        thread.start();
        showProgressDialog();
    }
}
