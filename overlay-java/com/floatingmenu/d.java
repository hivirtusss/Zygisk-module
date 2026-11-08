package com.floatingmenu;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

/* loaded from: /workspace/module/classes.dex */
public abstract /* synthetic */ class d {
    public static /* bridge */ /* synthetic */ void a(Application application, BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        application.registerReceiver(broadcastReceiver, intentFilter, 2);
    }
}
