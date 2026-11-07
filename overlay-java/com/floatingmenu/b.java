package com.floatingmenu;

import android.app.Application;
import android.view.ViewPropertyAnimator;
import android.widget.Button;

/* loaded from: /workspace/module/classes.dex */
public abstract /* synthetic */ class b {
    public static /* bridge */ /* synthetic */ void a(Application application, Application.ActivityLifecycleCallbacks activityLifecycleCallbacks) {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    public static /* bridge */ /* synthetic */ void b(ViewPropertyAnimator viewPropertyAnimator) {
        viewPropertyAnimator.start();
    }

    public static /* bridge */ /* synthetic */ void i(Button button) {
        button.setAllCaps(true);
    }
}
