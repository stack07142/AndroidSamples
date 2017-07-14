package io.github.stack07142.sample_shapedrawable;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // LanguageColors
        final LanguageColors languageColors = new LanguageColors();

        // TextView
        final TextView language = (TextView) findViewById(R.id.language);

        // ImageView
        final ImageView languageImage = (ImageView) findViewById(R.id.language_image);

        // Spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Set Spinner Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.addAll("java", "objective-c", "swift", "groovy", "python", "ruby", "c");
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        // Spinner Listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            // 선택할때 뿐만 아니라 최초에도 실행됨
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedLanguage = (String) spinner.getItemAtPosition(position);
                // Change shape color dynamically
                GradientDrawable bgShape = (GradientDrawable) languageImage.getBackground();
                bgShape.setColor(languageColors.getColor(selectedLanguage));

                // Change Text
                language.setText(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
