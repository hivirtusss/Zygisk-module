package com.floatingmenu;

import android.graphics.drawable.GradientDrawable;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/* loaded from: /workspace/module/classes.dex */
public abstract /* synthetic */ class a {
    public static /* bridge */ /* synthetic */ ViewPropertyAnimator a(ViewPropertyAnimator viewPropertyAnimator, Runnable runnable) {
        return viewPropertyAnimator.withEndAction(runnable);
    }

    public static /* bridge */ /* synthetic */ void c(Button button, GradientDrawable gradientDrawable) {
        button.setBackground(gradientDrawable);
    }

    public static /* bridge */ /* synthetic */ void d(EditText editText) {
        editText.setBackground(null);
    }

    public static /* bridge */ /* synthetic */ void g(LinearLayout linearLayout, GradientDrawable gradientDrawable) {
        linearLayout.setBackground(gradientDrawable);
    }
}
