package com.nilhcem.androidthings.driver.blinkt.sample;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.nilhcem.androidthings.driver.blinkt.Blinkt;

import java.io.IOException;

public class SampleActivity extends Activity {

    private static final String TAG = SampleActivity.class.getSimpleName();

    private Blinkt blinkt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            blinkt = new Blinkt();
            blinkt.setBrightness(1);

            int[] rainbow = new int[Blinkt.LEDSTRIP_LENGTH];
            for (int i = 0; i < rainbow.length; i++) {
                rainbow[i] = Color.HSVToColor(255, new float[]{i * 360.f / rainbow.length, 1.0f, 1.0f});
            }

            blinkt.write(rainbow);
        } catch (IOException e) {
            Log.e(TAG, "Error initializing Blinkt!", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (blinkt != null) {
            try {
                blinkt.close();
            } catch (Exception e) {
                Log.e(TAG, "Error closing", e);
            } finally {
                blinkt = null;
            }
        }
    }
}
