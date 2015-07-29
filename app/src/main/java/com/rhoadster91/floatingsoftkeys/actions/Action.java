package com.rhoadster91.floatingsoftkeys.actions;

import android.content.Context;

public abstract class Action {

    public interface OnActionPerformedListener {

        void onActionPerformed(Action thisAction);

    }

    OnActionPerformedListener onActionPerformedListener;

    public void performAction(Context context) {
        action(context);
        if(onActionPerformedListener!=null) {
            onActionPerformedListener.onActionPerformed(this);
        }
    }

    protected abstract void action(Context context);

}
