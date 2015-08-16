package com.rhoadster91.floatingsoftkeys.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rhoadster91.floatingsoftkeys.actions.Action;

/**
 * Created by swechha on 16/8/15.
 */
public class FloatingButton extends Button {

    Action clickAction, longClickAction;

    public FloatingButton(Context context) {
        super(context);
    }

    public FloatingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FloatingButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("FSK", "Received");
        }
        return super.onTouchEvent(event);
    }

    public void bindAction(@Nullable Action clickAction, @Nullable Action longClickAction) {
        this.clickAction = clickAction;
        this.longClickAction = longClickAction;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FloatingButton.this.clickAction != null && getContext() != null) {
                    FloatingButton.this.clickAction.performAction(getContext());
                }
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (FloatingButton.this.longClickAction != null && getContext() != null) {
                    FloatingButton.this.longClickAction.performAction(getContext());
                }
                return false;
            }
        });
    }
}
