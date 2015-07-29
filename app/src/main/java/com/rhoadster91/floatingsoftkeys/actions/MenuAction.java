package com.rhoadster91.floatingsoftkeys.actions;

import android.content.Context;
import android.content.Intent;

import com.rhoadster91.floatingsoftkeys.services.DaemonService;

public class MenuAction extends Action {
    @Override
    protected void action(Context context) {
        Intent actionIntent = new Intent(DaemonService.ACTION_PERFORM_TOUCH_EVENT);
        actionIntent.putExtra(DaemonService.EXTRA_EVENT_ACTION, DaemonService.EventAction.MENU);
        context.sendBroadcast(actionIntent);
    }
}
