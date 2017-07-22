package io.github.stack07142.sample_listpreference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button settingButton = (Button) findViewById(R.id.buttonset);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String storeColor = preferences.getString(getString(R.string.key_color), "#FFFF0059");

        ConstraintLayout layoutBackground = (ConstraintLayout) findViewById(R.id.changecolor);
        layoutBackground.setBackgroundColor(Color.parseColor(storeColor));

        settingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();

                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });


    }
}
