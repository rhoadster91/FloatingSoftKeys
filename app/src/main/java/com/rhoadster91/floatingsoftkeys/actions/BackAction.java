package com.rhoadster91.floatingsoftkeys.actions;

import android.support.annotation.NonNull;

import com.rhoadster91.floatingsoftkeys.services.DaemonService;

public class BackAction extends StandardAction {

    @NonNull
    @Override
    protected DaemonService.EventAction getEventAction() {
        return DaemonService.EventAction.BACK;
    }
}
