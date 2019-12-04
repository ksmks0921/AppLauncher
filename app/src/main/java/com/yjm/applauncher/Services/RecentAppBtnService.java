package com.yjm.applauncher.Services;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class RecentAppBtnService extends AccessibilityService {



    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.getClassName() == null)
            return;
        Toast.makeText(this, " Connected! " , Toast.LENGTH_SHORT).show();
        String className = String.valueOf(event.getClassName());

        if (className.equals("com.android.internal.policy.impl.RecentApplicationsDialog")
                || className.equals("com.android.systemui.recent.RecentsActivity")
                || className.equals("com.android.systemui.recents.RecentsActivity")){



            //Recent button was pressed. Do something.
        }
    }

    @Override
    public void onInterrupt() {

    }





}
