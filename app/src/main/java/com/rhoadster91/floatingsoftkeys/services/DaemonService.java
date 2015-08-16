package com.rhoadster91.floatingsoftkeys.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;

import com.rhoadster91.floatingsoftkeys.utils.EventHandler;

public class DaemonService extends Service {

    public static final String ACTION_PERFORM_TOUCH_EVENT = "com.rhoadster91.floatingsoftkeys.ACTION_PERFORM_TOUCH_EVENT";

    public static final String EXTRA_EVENT_ACTION = ":fsk:eventAction";
    private static final int LONG_PRESS_DURATION = 1500;

    private EventHandler mEventHandler = new EventHandler(this);

    public enum EventAction {
        BACK,
        HOME,
        MENU,
        VOLUME_DOWN,
        VOLUME_UP,
        POWER,
        LONG_POWER
    }

    private BroadcastReceiver mReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mReceiver==null) {
            mReceiver = new EventBroadcastReceiver();
            registerReceiver(mReceiver, new IntentFilter(ACTION_PERFORM_TOUCH_EVENT));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException iae) {
            // receiver already unregistered
        }
    }

    private class EventBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            EventAction action = (EventAction) intent.getSerializableExtra(EXTRA_EVENT_ACTION);
            switch (action) {
                case BACK:
                    mEventHandler.sendKeys(KeyEvent.KEYCODE_BACK);
                    break;

                case HOME:
                    mEventHandler.sendKeys(KeyEvent.KEYCODE_HOME);
                    break;

                case MENU:
                    mEventHandler.sendKeys(KeyEvent.KEYCODE_MENU);
                    break;

                case VOLUME_DOWN:
                    mEventHandler.sendKeys(KeyEvent.KEYCODE_VOLUME_DOWN);
                    break;

                case VOLUME_UP:
                    mEventHandler.sendKeys(KeyEvent.KEYCODE_VOLUME_UP);
                    break;

                case POWER:
                    mEventHandler.sendKeys(KeyEvent.KEYCODE_POWER);
                    break;

                case LONG_POWER:
                    mEventHandler.sendDownTouchKeys(KeyEvent.KEYCODE_POWER);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEventHandler.sendUpTouchKeys(KeyEvent.KEYCODE_POWER);
                        }
                    }, LONG_PRESS_DURATION);
                    break;

                default:
                    Log.i("FSK", "Unknown event action received");
                    break;
            }
        }
    }
}
