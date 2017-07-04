package io.github.stack07142.sample_customview2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MyCustomView indicator = (MyCustomView) findViewById(R.id.indicator);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int selected = indicator.getSelected();

                selected++;
                selected %= 3;

                indicator.setSelected(selected);
            }
        });
    }
}
