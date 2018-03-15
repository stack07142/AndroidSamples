package io.github.stack07142.proximitysensorsample;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setProximitySensor();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mSensor != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    private void setProximitySensor() {

        if (mSensorManager == null) {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // event.values[0] : Proximity sensor distance measured in centimeters
        float distance = event.values[0];

        Log.d("ProximitySensor","distance = " + distance);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
