package com.rhoadster91.floatingsoftkeys.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class DaemonService extends Service {

    public static final String ACTION_PERFORM_TOUCH_EVENT = "com.rhoadster91.floatingsoftkeys.ACTION_PERFORM_TOUCH_EVENT";

    public static final String EXTRA_EVENT_ACTION = ":fsk:eventAction";

    public enum EventAction {
        BACK,
        HOME,
        MENU
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
                    break;

                case HOME:
                    break;

                case MENU:
                    break;

                default:
                    Log.i("FSK", "Unknown event action received");
                    break;
            }
        }
    }
}
