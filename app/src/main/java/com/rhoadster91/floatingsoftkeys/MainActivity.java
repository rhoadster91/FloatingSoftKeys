package com.rhoadster91.floatingsoftkeys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rhoadster91.floatingsoftkeys.services.DaemonService;
import com.rhoadster91.floatingsoftkeys.services.WindowService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, DaemonService.class));
        startService(new Intent(this, WindowService.class));
    }
}
