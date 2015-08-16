package com.rhoadster91.floatingsoftkeys.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.rhoadster91.floatingsoftkeys.R;
import com.rhoadster91.floatingsoftkeys.actions.BackAction;
import com.rhoadster91.floatingsoftkeys.views.FloatingButton;

public class WindowService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        FloatingButton button = (FloatingButton) inflater.inflate(R.layout.button, null, false);

        button.bindAction(new BackAction(), null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER;
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        manager.addView(button, params);
    }
}
