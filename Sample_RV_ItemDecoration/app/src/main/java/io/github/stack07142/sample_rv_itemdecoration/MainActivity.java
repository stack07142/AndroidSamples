package io.github.stack07142.sample_rv_itemdecoration;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setAdapter(new MyRVAdapter());
        recyclerView.addItemDecoration(
                new SettingDividerItemDecoration.Builder(this, SettingDividerItemDecoration.VERTICAL_ALL)
                        .setOutboundTopLeftMargin(100)
                        .setOutboundTopRightMargin(50)
                        .setOutboundBottomLeftMargin(50)
                        .setOutboundBottomRightMargin(100)
                        .setInnerLeftMargin(40)
                        .setInnerRightMargin(40)
                        .build());
    }
}
