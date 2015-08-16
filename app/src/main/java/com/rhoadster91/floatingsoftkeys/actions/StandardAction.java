package com.rhoadster91.floatingsoftkeys.actions;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.rhoadster91.floatingsoftkeys.services.DaemonService;

public abstract class StandardAction extends Action {

    protected abstract @NonNull DaemonService.EventAction getEventAction();

    @Override
    protected final void action(Context context) {
        Intent actionIntent = new Intent(DaemonService.ACTION_PERFORM_TOUCH_EVENT);
        actionIntent.putExtra(DaemonService.EXTRA_EVENT_ACTION, getEventAction());
        context.sendBroadcast(actionIntent);
    }
}
