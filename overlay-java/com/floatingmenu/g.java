package com.floatingmenu;

import android.view.View;
import android.view.ViewPropertyAnimator;

/* loaded from: /workspace/module/classes.dex */
public abstract /* synthetic */ class g {
    public static /* bridge */ /* synthetic */ ViewPropertyAnimator a(View view) {
        return view.animate();
    }

    public static /* bridge */ /* synthetic */ ViewPropertyAnimator b(ViewPropertyAnimator viewPropertyAnimator) {
        return viewPropertyAnimator.translationX(20.0f);
    }

    public static /* bridge */ /* synthetic */ ViewPropertyAnimator c(ViewPropertyAnimator viewPropertyAnimator) {
        return viewPropertyAnimator.setDuration(50L);
    }

    public static /* bridge */ /* synthetic */ ViewPropertyAnimator d(ViewPropertyAnimator viewPropertyAnimator) {
        return viewPropertyAnimator.translationX(-20.0f);
    }

    public static /* bridge */ /* synthetic */ ViewPropertyAnimator e(ViewPropertyAnimator viewPropertyAnimator) {
        return viewPropertyAnimator.translationX(0.0f);
    }
}
