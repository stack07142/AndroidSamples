package io.github.stack07142.colorbutton;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final VectorMasterView circleBtn = (VectorMasterView) findViewById(R.id.btn01);

        // << circle >>
        // Find the correct path using name
        final PathModel circle = circleBtn.getPathModelByName("circle");
        // set the fill color
        circle.setFillColor(Color.parseColor("#AAFF33"));

        // << check >>
        // Find the correct path using name
        final PathModel check = circleBtn.getPathModelByName("check");
        // set the stroke color
        check.setStrokeColor(Color.parseColor("#ED4337"));

        // simple animation
        check.setTrimPathEnd(0.0f);

        circleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("MainActivity", "circleBtn - onClickListener");

                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                valueAnimator.setDuration(1000);

                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {

                        // set trim end value and update view
                        check.setTrimPathEnd((Float) valueAnimator.getAnimatedValue());
                        circleBtn.update();
                    }
                });

                valueAnimator.start();
            }
        });

        Button clearButton = (Button) findViewById(R.id.btn_clear);

        clearButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Log.d("MainActivity", "clearBtn - onClickListener");

                check.setTrimPathEnd(0.0f);

                circleBtn.update();
            }
        });
    }
}
