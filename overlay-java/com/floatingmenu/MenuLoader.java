package com.floatingmenu;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: /workspace/module/classes.dex */
public class MenuLoader {
    private static final String TAG = "ZygiskMenu @Hivirtus";
    private static boolean sActivityHooksApplied = false;
    private static Application sApplication = null;
    private static WeakReference sCurrentActivity = null;
    private static String sCurrentPackage = null;
    private static String sForegroundApp = "none";
    private static IBinder sIActivityManagerBinderProxy = null;
    private static IBinder sIPhoneSubInfoBinderProxy = null;
    private static IBinder sISmsBinderProxy = null;
    private static IBinder sISubBinderProxy = null;
    private static IBinder sITelephonyBinderProxy = null;
    public static boolean sIamNoRoot = false;
    public static boolean sIamNotDeveloper = false;
    public static boolean sIsTargetPackage = false;
    private static String sLastSentAddress = null;
    private static String sLastSentBody = null;
    private static long sLastSentTime = 0;
    private static View sLockOverlay = null;
    private static boolean sPackageManagerHooksApplied = false;
    private static boolean sPollerStarted = false;
    public static boolean sSim1Enabled = false;
    public static String sSim1Number = "";
    public static String sSim1Provider = "jio";
    public static boolean sSim2Enabled = false;
    public static String sSim2Number = "";
    public static String sSim2Provider = "jio";
    public static String sSimCountry = "in";
    public static boolean sMockDataSubscription = false;
    private static View sSystemUiCard;
    private static TextView sSystemUiErrorText;
    private static EditText sSystemUiInput;
    private static View sSystemUiOverlay;

    /* renamed from: com.floatingmenu.MenuLoader$6, reason: invalid class name */
    /* loaded from: /workspace/module/classes.dex */
    class AnonymousClass6 implements Runnable {
        final /* synthetic */ Activity val$activity;

        public AnonymousClass6(Activity activity) {
            this.val$activity = activity;
        }

        @Override // java.lang.Runnable
        public void run() {
            View findViewWithTag;
            try {
                ViewGroup viewGroup = (ViewGroup) this.val$activity.getWindow().getDecorView();
                if (viewGroup == null || (findViewWithTag = viewGroup.findViewWithTag("zygisk_floating_menu")) == null) {
                    return;
                }
                viewGroup.removeView(findViewWithTag);
                Log.d(MenuLoader.TAG, "Floating menu removed from activity decor view.");
            } catch (Exception e) {
                Log.e(MenuLoader.TAG, "Error removing floating menu: ", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: /workspace/module/classes.dex */
    public class FakeServiceCache extends HashMap {
        private FakeServiceCache() {
        }

        @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
        public boolean isEmpty() {
            try {
                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                    if (stackTraceElement.getMethodName().contains("initServiceCache")) {
                        return true;
                    }
                }
            } catch (Throwable unused) {
            }
            return super.isEmpty();
        }

        @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
        public int size() {
            try {
                for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                    if (stackTraceElement.getMethodName().contains("initServiceCache")) {
                        return 0;
                    }
                }
            } catch (Throwable unused) {
            }
            return super.size();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void applyActivityHooks(Class cls, Object obj) {
        try {
            if (!sActivityHooksApplied) {
                Method declaredMethod = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
                declaredMethod.setAccessible(true);
                final IBinder iBinder = (IBinder) declaredMethod.invoke(null, "activity");
                if (iBinder == null) {
                    Log.w(TAG, "activity binder service not found in ServiceManager.");
                    return;
                }
                if (Proxy.isProxyClass(iBinder.getClass())) {
                    Log.i(TAG, "Activity binder is already proxied.");
                    sIActivityManagerBinderProxy = iBinder;
                } else {
                    IBinder iBinder2 = (IBinder) Proxy.newProxyInstance(IBinder.class.getClassLoader(), new Class[]{IBinder.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.4
                        @Override // java.lang.reflect.InvocationHandler
                        public Object invoke(Object obj2, Method method, Object[] objArr) {
                            if ("queryLocalInterface".equals(method.getName()) && "android.app.IActivityManager".equals((String) objArr[0])) {
                                try {
                                    return MenuLoader.createIActivityManagerProxy(iBinder);
                                } catch (Throwable th) {
                                    Log.e(MenuLoader.TAG, "Failed to create IActivityManager proxy, falling back", th);
                                }
                            }
                            try {
                                return method.invoke(iBinder, objArr);
                            } catch (InvocationTargetException e) {
                                throw e.getTargetException();
                            }
                        }
                    });
                    sIActivityManagerBinderProxy = iBinder2;
                    Map orCreateServiceCache = getOrCreateServiceCache();
                    if (orCreateServiceCache != null) {
                        orCreateServiceCache.put("activity", iBinder2);
                        Log.d(TAG, "Successfully injected custom activity binder proxy into ServiceManager.sCache.");
                    }
                    clearActivityManagerCache();
                }
                sActivityHooksApplied = true;
            }
            if (obj != null) {
                Field declaredField = cls.getDeclaredField("mProviderMap");
                declaredField.setAccessible(true);
                Map map = (Map) declaredField.get(obj);
                if (map != null) {
                    synchronized (map) {
                        map.clear();
                    }
                    Log.d(TAG, "Successfully cleared mProviderMap cached providers.");
                }
            }
        } catch (Throwable th) {
            Log.e(TAG, "Failed to apply ActivityManager content provider hooks: ", th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void applyPackageManagerHooks() {
        if (sPackageManagerHooksApplied) {
            return;
        }
        try {
            Method declaredMethod = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            declaredMethod.setAccessible(true);
            final IBinder iBinder = (IBinder) declaredMethod.invoke(null, "package");
            if (iBinder == null) {
                Log.w(TAG, "package binder service not found in ServiceManager.");
                return;
            }
            Class<?> cls = Class.forName("android.content.pm.IPackageManager");
            Method declaredMethod2 = Class.forName("android.content.pm.IPackageManager$Stub").getDeclaredMethod("asInterface", IBinder.class);
            declaredMethod2.setAccessible(true);
            final Object invoke = declaredMethod2.invoke(null, iBinder);
            final Object newProxyInstance = Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.20
                private final List blockList = Arrays.asList("com.topjohnwu.magisk", "me.weishu.kernelsu", "io.github.a15f.ksu", "com.chelpus.lackypatcher", "com.noshufou.android.su", "com.thirdparty.superuser", "eu.chainfire.supersu", "com.koushikdutta.superuser", "org.meowcat.edxposed.manager", "org.lsposed.manager", "com.saurik.substrate", "com.xposed.manager");

                private boolean shouldBlock(String str) {
                    if (str == null) {
                        return false;
                    }
                    String lowerCase = str.toLowerCase();
                    Iterator it = this.blockList.iterator();
                    while (it.hasNext()) {
                        if (lowerCase.contains((String) it.next())) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override // java.lang.reflect.InvocationHandler
                public Object invoke(Object obj, Method method, Object[] objArr) {
                    Object invoke2;
                    String name = method.getName();
                    if ("getPackageInfo".equals(name) || "getApplicationInfo".equals(name)) {
                        if (objArr != null && objArr.length > 0) {
                            Object obj2 = objArr[0];
                            if (obj2 instanceof String) {
                                String str = (String) obj2;
                                if (shouldBlock(str)) {
                                    Log.d(MenuLoader.TAG, "IPackageManager: Blocking query for package: " + str);
                                    throw new PackageManager.NameNotFoundException(str);
                                }
                            }
                        }
                    } else if (!"getPackageInfoAsUser".equals(name) && !"getApplicationInfoAsUser".equals(name)) {
                        try {
                            if ("getInstalledPackages".equals(name)) {
                                Object invoke3 = method.invoke(invoke, objArr);
                                if (invoke3 != null) {
                                    if (invoke3.getClass().getName().contains("ParceledListSlice")) {
                                        Class<?> cls2 = invoke3.getClass();
                                        List list = (List) cls2.getMethod("getList", new Class[0]).invoke(invoke3, new Object[0]);
                                        ArrayList arrayList = new ArrayList();
                                        for (Object obj3 : list) {
                                            if (!(obj3 instanceof PackageInfo) || !shouldBlock(((PackageInfo) obj3).packageName)) {
                                                arrayList.add(obj3);
                                            }
                                        }
                                        Constructor<?> declaredConstructor = cls2.getDeclaredConstructor(List.class);
                                        declaredConstructor.setAccessible(true);
                                        return declaredConstructor.newInstance(arrayList);
                                    }
                                    if (invoke3 instanceof List) {
                                        ArrayList arrayList2 = new ArrayList();
                                        for (Object obj4 : (List) invoke3) {
                                            if (!(obj4 instanceof PackageInfo) || !shouldBlock(((PackageInfo) obj4).packageName)) {
                                                arrayList2.add(obj4);
                                            }
                                        }
                                        return arrayList2;
                                    }
                                }
                            } else if ("getInstalledApplications".equals(name) && (invoke2 = method.invoke(invoke, objArr)) != null) {
                                if (invoke2.getClass().getName().contains("ParceledListSlice")) {
                                    Class<?> cls3 = invoke2.getClass();
                                    List list2 = (List) cls3.getMethod("getList", new Class[0]).invoke(invoke2, new Object[0]);
                                    ArrayList arrayList3 = new ArrayList();
                                    for (Object obj5 : list2) {
                                        if (!(obj5 instanceof ApplicationInfo) || !shouldBlock(((ApplicationInfo) obj5).packageName)) {
                                            arrayList3.add(obj5);
                                        }
                                    }
                                    Constructor<?> declaredConstructor2 = cls3.getDeclaredConstructor(List.class);
                                    declaredConstructor2.setAccessible(true);
                                    return declaredConstructor2.newInstance(arrayList3);
                                }
                                if (invoke2 instanceof List) {
                                    ArrayList arrayList4 = new ArrayList();
                                    for (Object obj6 : (List) invoke2) {
                                        if (!(obj6 instanceof ApplicationInfo) || !shouldBlock(((ApplicationInfo) obj6).packageName)) {
                                            arrayList4.add(obj6);
                                        }
                                    }
                                    return arrayList4;
                                }
                            }
                        } catch (Throwable unused) {
                        }
                    } else if (objArr != null && objArr.length > 0) {
                        Object obj7 = objArr[0];
                        if (obj7 instanceof String) {
                            String str2 = (String) obj7;
                            if (shouldBlock(str2)) {
                                Log.d(MenuLoader.TAG, "IPackageManager: Blocking query for package: " + str2);
                                throw new PackageManager.NameNotFoundException(str2);
                            }
                        }
                    }
                    try {
                        return method.invoke(invoke, objArr);
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
            });
            Map orCreateServiceCache = getOrCreateServiceCache();
            if (orCreateServiceCache != null) {
                orCreateServiceCache.put("package", (IBinder) Proxy.newProxyInstance(IBinder.class.getClassLoader(), new Class[]{IBinder.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.21
                    @Override // java.lang.reflect.InvocationHandler
                    public Object invoke(Object obj, Method method, Object[] objArr) {
                        if ("queryLocalInterface".equals(method.getName()) && "android.content.pm.IPackageManager".equals((String) objArr[0])) {
                            return newProxyInstance;
                        }
                        try {
                            return method.invoke(iBinder, objArr);
                        } catch (InvocationTargetException e) {
                            throw e.getTargetException();
                        }
                    }
                }));
                Log.d(TAG, "Successfully injected custom IPackageManager proxy into ServiceManager sCache.");
            }
            try {
                Field declaredField = Class.forName("android.app.ActivityThread").getDeclaredField("sPackageManager");
                declaredField.setAccessible(true);
                declaredField.set(null, newProxyInstance);
                Log.d(TAG, "Successfully replaced ActivityThread.sPackageManager with proxy.");
            } catch (Throwable th) {
                Log.e(TAG, "Failed to replace ActivityThread.sPackageManager: ", th);
            }
            sPackageManagerHooksApplied = true;
        } catch (Throwable th2) {
            Log.e(TAG, "Failed to apply IPackageManager hooks: ", th2);
        }
    }

    private static void applySmsHooks() {
        try {
            Method declaredMethod = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            declaredMethod.setAccessible(true);
            final IBinder iBinder = (IBinder) declaredMethod.invoke(null, "isms");
            if (iBinder == null) {
                Log.w(TAG, "isms binder service not found in ServiceManager.");
                return;
            }
            IBinder iBinder2 = (IBinder) Proxy.newProxyInstance(IBinder.class.getClassLoader(), new Class[]{IBinder.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.2
                @Override // java.lang.reflect.InvocationHandler
                public Object invoke(Object obj, Method method, Object[] objArr) {
                    if ("queryLocalInterface".equals(method.getName())) {
                        Log.d(MenuLoader.TAG, "isms queryLocalInterface descriptor: " + ((String) objArr[0]));
                        try {
                            return MenuLoader.createISmsProxy(iBinder, Class.forName("com.android.internal.telephony.ISms"));
                        } catch (Throwable th) {
                            Log.e(MenuLoader.TAG, "Failed to create ISms proxy, falling back", th);
                        }
                    }
                    try {
                        return method.invoke(iBinder, objArr);
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
            });
            sISmsBinderProxy = iBinder2;
            Map orCreateServiceCache = getOrCreateServiceCache();
            if (orCreateServiceCache != null) {
                orCreateServiceCache.put("isms", iBinder2);
                Log.d(TAG, "Successfully injected custom isms binder proxy into ServiceManager.sCache.");
            }
            clearSmsManagerCache();
        } catch (Throwable th) {
            Log.e(TAG, "Failed to apply SMS hooks reflectively: ", th);
        }
    }

    private static void applyTelephonyHooks() {
        try {
            Method declaredMethod = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            declaredMethod.setAccessible(true);
            Map orCreateServiceCache = getOrCreateServiceCache();
            final IBinder iBinder = (IBinder) declaredMethod.invoke(null, "iphonesubinfo");
            if (iBinder != null) {
                IBinder iBinder2 = (IBinder) Proxy.newProxyInstance(IBinder.class.getClassLoader(), new Class[]{IBinder.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.13
                    @Override // java.lang.reflect.InvocationHandler
                    public Object invoke(Object obj, Method method, Object[] objArr) {
                        if ("queryLocalInterface".equals(method.getName())) {
                            try {
                                return MenuLoader.createIPhoneSubInfoProxy(iBinder, Class.forName("com.android.internal.telephony.IPhoneSubInfo"));
                            } catch (Throwable th) {
                                Log.e(MenuLoader.TAG, "Failed to create IPhoneSubInfo proxy", th);
                            }
                        }
                        try {
                            return method.invoke(iBinder, objArr);
                        } catch (InvocationTargetException e) {
                            throw e.getTargetException();
                        }
                    }
                });
                sIPhoneSubInfoBinderProxy = iBinder2;
                if (orCreateServiceCache != null) {
                    orCreateServiceCache.put("iphonesubinfo", iBinder2);
                    Log.d(TAG, "Successfully injected custom iphonesubinfo binder proxy.");
                }
            }
            final IBinder iBinder3 = (IBinder) declaredMethod.invoke(null, "phone");
            if (iBinder3 != null) {
                IBinder iBinder4 = (IBinder) Proxy.newProxyInstance(IBinder.class.getClassLoader(), new Class[]{IBinder.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.14
                    @Override // java.lang.reflect.InvocationHandler
                    public Object invoke(Object obj, Method method, Object[] objArr) {
                        if ("queryLocalInterface".equals(method.getName())) {
                            try {
                                return MenuLoader.createITelephonyProxy(iBinder3, Class.forName("com.android.internal.telephony.ITelephony"));
                            } catch (Throwable th) {
                                Log.e(MenuLoader.TAG, "Failed to create ITelephony proxy", th);
                            }
                        }
                        try {
                            return method.invoke(iBinder3, objArr);
                        } catch (InvocationTargetException e) {
                            throw e.getTargetException();
                        }
                    }
                });
                sITelephonyBinderProxy = iBinder4;
                if (orCreateServiceCache != null) {
                    orCreateServiceCache.put("phone", iBinder4);
                    Log.d(TAG, "Successfully injected custom phone binder proxy.");
                }
            }
            final IBinder iBinder5 = (IBinder) declaredMethod.invoke(null, "isub");
            if (iBinder5 != null) {
                IBinder iBinder6 = (IBinder) Proxy.newProxyInstance(IBinder.class.getClassLoader(), new Class[]{IBinder.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.15
                    @Override // java.lang.reflect.InvocationHandler
                    public Object invoke(Object obj, Method method, Object[] objArr) {
                        if ("queryLocalInterface".equals(method.getName())) {
                            try {
                                return MenuLoader.createISubProxy(iBinder5, Class.forName("com.android.internal.telephony.ISub"));
                            } catch (Throwable th) {
                                Log.e(MenuLoader.TAG, "Failed to create ISub proxy", th);
                            }
                        }
                        try {
                            return method.invoke(iBinder5, objArr);
                        } catch (InvocationTargetException e) {
                            throw e.getTargetException();
                        }
                    }
                });
                sISubBinderProxy = iBinder6;
                if (orCreateServiceCache != null) {
                    orCreateServiceCache.put("isub", iBinder6);
                    Log.d(TAG, "Successfully injected custom isub binder proxy.");
                }
            }
        } catch (Throwable th) {
            Log.e(TAG, "Failed to apply Telephony hooks: ", th);
        }
    }

    private static void clearActivityManagerCache() {
        Field field;
        try {
            Class<?> cls = Class.forName("android.app.ActivityManager");
            try {
                try {
                    field = cls.getDeclaredField("IActivityManagerSingleton");
                } catch (NoSuchFieldException unused) {
                    field = null;
                }
            } catch (NoSuchFieldException unused2) {
                field = cls.getDeclaredField("gDefault");
            }
            if (field != null) {
                field.setAccessible(true);
                Object obj = field.get(null);
                if (obj != null) {
                    Field declaredField = Class.forName("android.util.Singleton").getDeclaredField("mInstance");
                    declaredField.setAccessible(true);
                    declaredField.set(obj, null);
                    Log.d(TAG, "Cleared ActivityManager singleton cache.");
                }
            }
        } catch (Throwable th) {
            Log.e(TAG, "Failed to clear ActivityManager cache: ", th);
        }
    }

    private static void clearSmsManagerCache() {
        try {
            Class<?> cls = Class.forName("android.telephony.SmsManager");
            try {
                Field declaredField = cls.getDeclaredField("sSubIdToInstanceMap");
                declaredField.setAccessible(true);
                Map map = (Map) declaredField.get(null);
                if (map != null) {
                    map.clear();
                    Log.d(TAG, "Cleared SmsManager.sSubIdToInstanceMap cache.");
                }
            } catch (NoSuchFieldException unused) {
            }
            try {
                Field declaredField2 = cls.getDeclaredField("sInstance");
                declaredField2.setAccessible(true);
                declaredField2.set(null, null);
                Log.d(TAG, "Cleared SmsManager.sInstance cache.");
            } catch (NoSuchFieldException unused2) {
            }
            try {
                Field declaredField3 = cls.getDeclaredField("mDefaultSmsManager");
                declaredField3.setAccessible(true);
                declaredField3.set(null, null);
                Log.d(TAG, "Cleared SmsManager.mDefaultSmsManager cache.");
            } catch (NoSuchFieldException unused3) {
            }
        } catch (Throwable th) {
            Log.e(TAG, "Failed to clear static SmsManager caches: ", th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static Object createIActivityManagerProxy(IBinder iBinder) {
        RuntimeException runtimeException;
        final Object invoke;
        Class<?> cls = Class.forName("android.app.IActivityManager");
        try {
            Method declaredMethod = Class.forName("android.app.IActivityManager$Stub").getDeclaredMethod("asInterface", IBinder.class);
            declaredMethod.setAccessible(true);
            invoke = declaredMethod.invoke(null, iBinder);
        } finally {
            try {
                return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls, IInterface.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.5
                    @Override // java.lang.reflect.InvocationHandler
                    public Object invoke(Object obj, Method method, Object[] objArr) {
                        String name = method.getName();
                        if ("asBinder".equals(name)) {
                            Log.d(MenuLoader.TAG, "Intercepted IActivityManager asBinder(), returning sIActivityManagerBinderProxy");
                            return MenuLoader.sIActivityManagerBinderProxy;
                        }
                        if (!"getContentProvider".equals(name)) {
                            try {
                                return method.invoke(invoke, objArr);
                            } catch (InvocationTargetException e) {
                                throw e.getTargetException();
                            }
                        }
                        Object invoke2 = method.invoke(invoke, objArr);
                        if (invoke2 != null && (FloatingMenu.sHookOutgoing || MenuLoader.sIamNotDeveloper)) {
                            try {
                                Field declaredField = Class.forName("android.app.ContentProviderHolder").getDeclaredField("provider");
                                declaredField.setAccessible(true);
                                final Object obj2 = declaredField.get(invoke2);
                                if (obj2 != null) {
                                    Class<?> cls2 = Class.forName("android.content.IContentProvider");
                                    declaredField.set(invoke2, Proxy.newProxyInstance(cls2.getClassLoader(), new Class[]{cls2, IInterface.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.5.1
                                        /* JADX WARN: Code restructure failed: missing block: B:103:0x0123, code lost:
                                        
                                            if (r14.contains("oem_unlock_disallowed") != false) goto L91;
                                         */
                                        /* JADX WARN: Removed duplicated region for block: B:94:0x012c  */
                                        @Override // java.lang.reflect.InvocationHandler
                                        /*
                                            Code decompiled incorrectly, please refer to instructions dump.
                                        */
                                        public Object invoke(Object obj3, Method method2, Object[] objArr2) {
                                            Uri uri;
                                            String[] strArr;
                                            String[] strArr2;
                                            String name2 = method2.getName();
                                            String str = "oem_unlock_disallowed";
                                            if (MenuLoader.sIamNotDeveloper && name2 != null && name2.startsWith("call") && objArr2 != null) {
                                                for (Object obj4 : objArr2) {
                                                    if (obj4 instanceof String) {
                                                        String str2 = (String) obj4;
                                                        if ("development_settings_enabled".equals(str2) || "adb_enabled".equals(str2) || "oem_unlock_supported".equals(str2) || "oem_unlock_disallowed".equals(str2)) {
                                                            Log.d(MenuLoader.TAG, "IContentProvider: Intercepted Settings call for key: " + str2 + ", returning 0");
                                                            Bundle bundle = new Bundle();
                                                            bundle.putString("value", "0");
                                                            return bundle;
                                                        }
                                                    }
                                                }
                                            }
                                            if ("query".equals(name2)) {
                                                if (objArr2 != null) {
                                                    for (Object obj5 : objArr2) {
                                                        if (obj5 instanceof Uri) {
                                                            uri = (Uri) obj5;
                                                            break;
                                                        }
                                                    }
                                                }
                                                uri = null;
                                                if (MenuLoader.sIamNotDeveloper && uri != null && uri.toString().contains("settings")) {
                                                    String lastPathSegment = uri.getLastPathSegment();
                                                    if (!"development_settings_enabled".equals(lastPathSegment) && !"adb_enabled".equals(lastPathSegment) && !"oem_unlock_supported".equals(lastPathSegment) && !"oem_unlock_disallowed".equals(lastPathSegment)) {
                                                        lastPathSegment = null;
                                                    }
                                                    if (lastPathSegment == null && objArr2 != null) {
                                                        for (Object obj6 : objArr2) {
                                                            if (obj6 instanceof String[]) {
                                                                for (String str3 : (String[]) obj6) {
                                                                    if ("development_settings_enabled".equals(str3) || "adb_enabled".equals(str3) || "oem_unlock_supported".equals(str3) || "oem_unlock_disallowed".equals(str3)) {
                                                                        lastPathSegment = str3;
                                                                        break;
                                                                    }
                                                                }
                                                            } else if (obj6 instanceof String) {
                                                                String str4 = (String) obj6;
                                                                if (str4.contains("development_settings_enabled") || str4.contains("adb_enabled") || str4.contains("oem_unlock_supported") || str4.contains("oem_unlock_disallowed")) {
                                                                    if (str4.contains("development_settings_enabled")) {
                                                                        str = "development_settings_enabled";
                                                                    } else if (str4.contains("adb_enabled")) {
                                                                        str = "adb_enabled";
                                                                    } else if (str4.contains("oem_unlock_supported")) {
                                                                        str = "oem_unlock_supported";
                                                                    }
                                                                    if (str != null) {
                                                                        Log.d(MenuLoader.TAG, "IContentProvider: Intercepted Settings query for key: " + str + ", returning MatrixCursor with 0");
                                                                        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"_id", "name", "value"});
                                                                        matrixCursor.addRow(new Object[]{1, str, "0"});
                                                                        return matrixCursor;
                                                                    }
                                                                }
                                                            } else {
                                                                continue;
                                                            }
                                                        }
                                                    }
                                                    str = lastPathSegment;
                                                    if (str != null) {
                                                    }
                                                }
                                                if (FloatingMenu.sHookOutgoing && uri != null && uri.toString().contains("sms")) {
                                                    Log.d(MenuLoader.TAG, "IContentProvider: Intercepted sent SMS database query: " + uri);
                                                    if (objArr2 != null) {
                                                        for (Object obj7 : objArr2) {
                                                            if (obj7 instanceof String[]) {
                                                                strArr2 = (String[]) obj7;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    strArr2 = null;
                                                    return MenuLoader.createMockSmsCursor(strArr2);
                                                }
                                                if (uri != null && uri.toString().contains("telephony/siminfo")) {
                                                    Log.d(MenuLoader.TAG, "IContentProvider: Intercepted telephony/siminfo query: " + uri);
                                                    if (objArr2 != null) {
                                                        for (Object obj8 : objArr2) {
                                                            if (obj8 instanceof String[]) {
                                                                strArr = (String[]) obj8;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    strArr = null;
                                                    Cursor createMockSimCursor = MenuLoader.createMockSimCursor(strArr);
                                                    if (createMockSimCursor != null) {
                                                        return createMockSimCursor;
                                                    }
                                                }
                                            }
                                            try {
                                                return method2.invoke(obj2, objArr2);
                                            } catch (InvocationTargetException e2) {
                                                throw e2.getTargetException();
                                            }
                                        }
                                    }));
                                    Log.d(MenuLoader.TAG, "Successfully wrapped IContentProvider inside ContentProviderHolder.");
                                }
                            } catch (Throwable th) {
                                Log.e(MenuLoader.TAG, "Error proxying IContentProvider client: ", th);
                            }
                        }
                        return invoke2;
                    }
                });
            } catch (Throwable th) {
            }
        }
        return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls, IInterface.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.5
            @Override // java.lang.reflect.InvocationHandler
            public Object invoke(Object obj, Method method, Object[] objArr) {
                String name = method.getName();
                if ("asBinder".equals(name)) {
                    Log.d(MenuLoader.TAG, "Intercepted IActivityManager asBinder(), returning sIActivityManagerBinderProxy");
                    return MenuLoader.sIActivityManagerBinderProxy;
                }
                if (!"getContentProvider".equals(name)) {
                    try {
                        return method.invoke(invoke, objArr);
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
                Object invoke2 = method.invoke(invoke, objArr);
                if (invoke2 != null && (FloatingMenu.sHookOutgoing || MenuLoader.sIamNotDeveloper)) {
                    try {
                        Field declaredField = Class.forName("android.app.ContentProviderHolder").getDeclaredField("provider");
                        declaredField.setAccessible(true);
                        final Object obj2 = declaredField.get(invoke2);
                        if (obj2 != null) {
                            Class<?> cls2 = Class.forName("android.content.IContentProvider");
                            declaredField.set(invoke2, Proxy.newProxyInstance(cls2.getClassLoader(), new Class[]{cls2, IInterface.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.5.1
                                /* JADX WARN: Code restructure failed: missing block: B:103:0x0123, code lost:
                                
                                    if (r14.contains("oem_unlock_disallowed") != false) goto L91;
                                 */
                                /* JADX WARN: Removed duplicated region for block: B:94:0x012c  */
                                @Override // java.lang.reflect.InvocationHandler
                                /*
                                    Code decompiled incorrectly, please refer to instructions dump.
                                */
                                public Object invoke(Object obj3, Method method2, Object[] objArr2) {
                                    Uri uri;
                                    String[] strArr;
                                    String[] strArr2;
                                    String name2 = method2.getName();
                                    String str = "oem_unlock_disallowed";
                                    if (MenuLoader.sIamNotDeveloper && name2 != null && name2.startsWith("call") && objArr2 != null) {
                                        for (Object obj4 : objArr2) {
                                            if (obj4 instanceof String) {
                                                String str2 = (String) obj4;
                                                if ("development_settings_enabled".equals(str2) || "adb_enabled".equals(str2) || "oem_unlock_supported".equals(str2) || "oem_unlock_disallowed".equals(str2)) {
                                                    Log.d(MenuLoader.TAG, "IContentProvider: Intercepted Settings call for key: " + str2 + ", returning 0");
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("value", "0");
                                                    return bundle;
                                                }
                                            }
                                        }
                                    }
                                    if ("query".equals(name2)) {
                                        if (objArr2 != null) {
                                            for (Object obj5 : objArr2) {
                                                if (obj5 instanceof Uri) {
                                                    uri = (Uri) obj5;
                                                    break;
                                                }
                                            }
                                        }
                                        uri = null;
                                        if (MenuLoader.sIamNotDeveloper && uri != null && uri.toString().contains("settings")) {
                                            String lastPathSegment = uri.getLastPathSegment();
                                            if (!"development_settings_enabled".equals(lastPathSegment) && !"adb_enabled".equals(lastPathSegment) && !"oem_unlock_supported".equals(lastPathSegment) && !"oem_unlock_disallowed".equals(lastPathSegment)) {
                                                lastPathSegment = null;
                                            }
                                            if (lastPathSegment == null && objArr2 != null) {
                                                for (Object obj6 : objArr2) {
                                                    if (obj6 instanceof String[]) {
                                                        for (String str3 : (String[]) obj6) {
                                                            if ("development_settings_enabled".equals(str3) || "adb_enabled".equals(str3) || "oem_unlock_supported".equals(str3) || "oem_unlock_disallowed".equals(str3)) {
                                                                lastPathSegment = str3;
                                                                break;
                                                            }
                                                        }
                                                    } else if (obj6 instanceof String) {
                                                        String str4 = (String) obj6;
                                                        if (str4.contains("development_settings_enabled") || str4.contains("adb_enabled") || str4.contains("oem_unlock_supported") || str4.contains("oem_unlock_disallowed")) {
                                                            if (str4.contains("development_settings_enabled")) {
                                                                str = "development_settings_enabled";
                                                            } else if (str4.contains("adb_enabled")) {
                                                                str = "adb_enabled";
                                                            } else if (str4.contains("oem_unlock_supported")) {
                                                                str = "oem_unlock_supported";
                                                            }
                                                            if (str != null) {
                                                                Log.d(MenuLoader.TAG, "IContentProvider: Intercepted Settings query for key: " + str + ", returning MatrixCursor with 0");
                                                                MatrixCursor matrixCursor = new MatrixCursor(new String[]{"_id", "name", "value"});
                                                                matrixCursor.addRow(new Object[]{1, str, "0"});
                                                                return matrixCursor;
                                                            }
                                                        }
                                                    } else {
                                                        continue;
                                                    }
                                                }
                                            }
                                            str = lastPathSegment;
                                            if (str != null) {
                                            }
                                        }
                                        if (FloatingMenu.sHookOutgoing && uri != null && uri.toString().contains("sms")) {
                                            Log.d(MenuLoader.TAG, "IContentProvider: Intercepted sent SMS database query: " + uri);
                                            if (objArr2 != null) {
                                                for (Object obj7 : objArr2) {
                                                    if (obj7 instanceof String[]) {
                                                        strArr2 = (String[]) obj7;
                                                        break;
                                                    }
                                                }
                                            }
                                            strArr2 = null;
                                            return MenuLoader.createMockSmsCursor(strArr2);
                                        }
                                        if (uri != null && uri.toString().contains("telephony/siminfo")) {
                                            Log.d(MenuLoader.TAG, "IContentProvider: Intercepted telephony/siminfo query: " + uri);
                                            if (objArr2 != null) {
                                                for (Object obj8 : objArr2) {
                                                    if (obj8 instanceof String[]) {
                                                        strArr = (String[]) obj8;
                                                        break;
                                                    }
                                                }
                                            }
                                            strArr = null;
                                            Cursor createMockSimCursor = MenuLoader.createMockSimCursor(strArr);
                                            if (createMockSimCursor != null) {
                                                return createMockSimCursor;
                                            }
                                        }
                                    }
                                    try {
                                        return method2.invoke(obj2, objArr2);
                                    } catch (InvocationTargetException e2) {
                                        throw e2.getTargetException();
                                    }
                                }
                            }));
                            Log.d(MenuLoader.TAG, "Successfully wrapped IContentProvider inside ContentProviderHolder.");
                        }
                    } catch (Throwable th2) {
                        Log.e(MenuLoader.TAG, "Error proxying IContentProvider client: ", th2);
                    }
                }
                return invoke2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object createIPhoneSubInfoProxy(IBinder iBinder, Class cls) {
        try {
            Method declaredMethod = Class.forName("com.android.internal.telephony.IPhoneSubInfo$Stub").getDeclaredMethod("asInterface", IBinder.class);
            declaredMethod.setAccessible(true);
            final Object invoke = declaredMethod.invoke(null, iBinder);
            return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls, IInterface.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.16
                @Override // java.lang.reflect.InvocationHandler
                public Object invoke(Object obj, Method method, Object[] objArr) {
                    String mockSimIccid;
                    StringBuilder sb;
                    String str;
                    String name = method.getName();
                    if ("asBinder".equals(name)) {
                        Log.d(MenuLoader.TAG, "Intercepted IPhoneSubInfo asBinder(), returning sIPhoneSubInfoBinderProxy");
                        return MenuLoader.sIPhoneSubInfoBinderProxy;
                    }
                    if (name.startsWith("getLine1Number")) {
                        int slotIndexFromSubId = MenuLoader.getSlotIndexFromSubId(MenuLoader.getSubIdFromArgs(objArr));
                        mockSimIccid = MenuLoader.getMockSimNumber(slotIndexFromSubId);
                        if (mockSimIccid != null && !mockSimIccid.isEmpty()) {
                            sb = new StringBuilder("Intercepted ");
                            sb.append(name);
                            sb.append(" for slot ");
                            sb.append(slotIndexFromSubId);
                            str = ", returning mock number: ";
                            sb.append(str);
                            sb.append(mockSimIccid);
                            Log.d(MenuLoader.TAG, sb.toString());
                            return mockSimIccid;
                        }
                        try {
                            return method.invoke(invoke, objArr);
                        } catch (InvocationTargetException e) {
                            throw e.getTargetException();
                        }
                    }
                    if (name.startsWith("getSubscriberId")) {
                        int slotIndexFromSubId2 = MenuLoader.getSlotIndexFromSubId(MenuLoader.getSubIdFromArgs(objArr));
                        mockSimIccid = MenuLoader.getMockSimImsi(slotIndexFromSubId2);
                        if (mockSimIccid != null && !mockSimIccid.isEmpty()) {
                            sb = new StringBuilder("Intercepted ");
                            sb.append(name);
                            sb.append(" for slot ");
                            sb.append(slotIndexFromSubId2);
                            str = ", returning mock IMSI: ";
                            sb.append(str);
                            sb.append(mockSimIccid);
                            Log.d(MenuLoader.TAG, sb.toString());
                            return mockSimIccid;
                        }
                        return method.invoke(invoke, objArr);
                    }
                    if (name.startsWith("getSimSerialNumber")) {
                        int slotIndexFromSubId3 = MenuLoader.getSlotIndexFromSubId(MenuLoader.getSubIdFromArgs(objArr));
                        mockSimIccid = MenuLoader.getMockSimIccid(slotIndexFromSubId3);
                        if (mockSimIccid != null && !mockSimIccid.isEmpty()) {
                            sb = new StringBuilder("Intercepted ");
                            sb.append(name);
                            sb.append(" for slot ");
                            sb.append(slotIndexFromSubId3);
                            str = ", returning mock ICCID: ";
                            sb.append(str);
                            sb.append(mockSimIccid);
                            Log.d(MenuLoader.TAG, sb.toString());
                            return mockSimIccid;
                        }
                    }
                    return method.invoke(invoke, objArr);
                }
            });
        } catch (Throwable th) {
            Log.e(TAG, "Failed to build IPhoneSubInfo dynamic proxy", th);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object createISmsProxy(IBinder iBinder, Class cls) {
        try {
            Method declaredMethod = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            declaredMethod.setAccessible(true);
            final Object invoke = declaredMethod.invoke(null, iBinder);
            return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls, IInterface.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.3
                @Override // java.lang.reflect.InvocationHandler
                public Object invoke(Object obj, Method method, Object[] objArr) {
                    final List list;
                    final PendingIntent pendingIntent;
                    String name = method.getName();
                    Log.d(MenuLoader.TAG, "ISms proxy method called: " + name);
                    if ("asBinder".equals(name)) {
                        Log.d(MenuLoader.TAG, "Intercepted asBinder(), returning sISmsBinderProxy");
                        return MenuLoader.sISmsBinderProxy;
                    }
                    if ("isSmsSimPickActivityNeeded".equals(name)) {
                        Log.d(MenuLoader.TAG, "Intercepted isSmsSimPickActivityNeeded(), returning false");
                        return Boolean.FALSE;
                    }
                    if ("getPreferredSmsSubscription".equals(name)) {
                        int i = MenuLoader.sMockDataSubscription ? MenuLoader.resolveMockDataSubId() : 1;
                        if (!MenuLoader.sMockDataSubscription && !MenuLoader.sSim1Enabled && MenuLoader.sSim2Enabled) {
                            i = 2;
                        }
                        Log.d(MenuLoader.TAG, "Intercepted getPreferredSmsSubscription(), returning " + i);
                        return Integer.valueOf(i);
                    }
                    if (name.startsWith("sendText") || name.startsWith("sendMultipartText")) {
                        Log.d(MenuLoader.TAG, "Intercepted SMS Method call: ".concat(name));
                        String str = null;
                        String str2 = null;
                        if (objArr != null) {
                            for (int i2 = 0; i2 < objArr.length; i2++) {
                                Object obj2 = objArr[i2];
                                StringBuilder sb = new StringBuilder("  Arg[");
                                sb.append(i2);
                                sb.append("]: ");
                                sb.append(obj2 != null ? obj2.toString() : "null");
                                Log.d(MenuLoader.TAG, sb.toString());
                                if (obj2 instanceof String) {
                                    String str3 = (String) obj2;
                                    if (str3.length() < 20 && !str3.contains(" ")) {
                                        str = str3;
                                    } else if (str3.length() > 5) {
                                        str2 = str3;
                                    }
                                }
                            }
                        }
                        if (FloatingMenu.sHookOutgoing) {
                            Log.d(MenuLoader.TAG, "sHookOutgoing is enabled! Mocking SMS success...");
                            if (str != null) {
                                String unused = MenuLoader.sLastSentAddress = str;
                            }
                            if (str2 != null) {
                                String unused2 = MenuLoader.sLastSentBody = str2;
                            }
                            long unused3 = MenuLoader.sLastSentTime = System.currentTimeMillis();
                            if (str != null && str2 != null) {
                                FloatingMenu.forwardSmsToTelegram(str, str2);
                            }
                            if (objArr != null) {
                                for (Object obj3 : objArr) {
                                    if (obj3 instanceof PendingIntent) {
                                        pendingIntent = (PendingIntent) obj3;
                                        list = null;
                                        break;
                                    }
                                    if (obj3 instanceof List) {
                                        List list2 = (List) obj3;
                                        if (!list2.isEmpty() && (list2.get(0) instanceof PendingIntent)) {
                                            list = list2;
                                            pendingIntent = null;
                                            break;
                                        }
                                    }
                                }
                            }
                            list = null;
                            pendingIntent = null;
                            if (pendingIntent == null && list == null) {
                                Log.w(MenuLoader.TAG, "No PendingIntent callback found in method arguments.");
                            } else {
                                new Thread(new Runnable() { // from class: com.floatingmenu.MenuLoader.3.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        try {
                                            Thread.sleep(300L);
                                            if (pendingIntent != null) {
                                                Log.d(MenuLoader.TAG, "Triggering PendingIntent asynchronously with Activity.RESULT_OK");
                                                pendingIntent.send(-1);
                                            } else if (list != null) {
                                                Log.d(MenuLoader.TAG, "Triggering multi-part PendingIntents asynchronously with Activity.RESULT_OK");
                                                for (PendingIntent pendingIntent2 : list) {
                                                    if (pendingIntent2 != null) {
                                                        pendingIntent2.send(-1);
                                                    }
                                                }
                                            }
                                        } catch (Throwable th) {
                                            Log.e(MenuLoader.TAG, "Error triggering Mock PendingIntent callback: ", th);
                                        }
                                    }
                                }).start();
                            }
                            return null;
                        }
                    }
                    return method.invoke(invoke, objArr);
                }
            });
        } catch (Throwable th) {
            Log.e(TAG, "Failed to build ISms dynamic proxy: ", th);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object createISubProxy(IBinder iBinder, Class cls) {
        try {
            Method declaredMethod = Class.forName("com.android.internal.telephony.ISub$Stub").getDeclaredMethod("asInterface", IBinder.class);
            declaredMethod.setAccessible(true);
            final Object invoke = declaredMethod.invoke(null, iBinder);
            return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls, IInterface.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.18
                /* JADX WARN: Removed duplicated region for block: B:65:0x010b  */
                /* JADX WARN: Removed duplicated region for block: B:68:0x0112  */
                /* JADX WARN: Type inference failed for: r0v5, types: [int, boolean] */
                @Override // java.lang.reflect.InvocationHandler
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                */
                public Object invoke(Object obj, Method method, Object[] objArr) {
                    int i;
                    String name = method.getName();
                    if ("asBinder".equals(name)) {
                        Log.d(MenuLoader.TAG, "Intercepted ISub asBinder(), returning sISubBinderProxy");
                        return MenuLoader.sISubBinderProxy;
                    }
                    if (MenuLoader.sMockDataSubscription && (MenuLoader.sSim1Enabled || MenuLoader.sSim2Enabled)) {
                        if ("getDefaultDataSubId".equals(name) || "getDefaultDataSubscriptionId".equals(name) || "getActiveDataSubscriptionId".equals(name) || "getDefaultDataSubIdAsUser".equals(name)) {
                            int dataSubId = MenuLoader.resolveMockDataSubId();
                            Log.d(MenuLoader.TAG, "Intercepted " + name + ", returning mock data subId " + dataSubId);
                            return Integer.valueOf(dataSubId);
                        }
                    }
                    if (MenuLoader.sSim1Enabled || MenuLoader.sSim2Enabled) {
                        if ("getActiveSubscriptionInfoList".equals(name) || "getAllSubscriptionInfoList".equals(name)) {
                            ArrayList arrayList = new ArrayList();
                            if (MenuLoader.sSim1Enabled) {
                                String str = MenuLoader.sSim1Number;
                                String mockSimOperator = MenuLoader.getMockSimOperator(0);
                                Object createMockSubscriptionInfo = MenuLoader.createMockSubscriptionInfo(1, 0, str, MenuLoader.getMockSimOperatorName(0), mockSimOperator != null ? mockSimOperator.substring(0, 3) : "405", mockSimOperator != null ? mockSimOperator.substring(3) : "840");
                                if (createMockSubscriptionInfo != null) {
                                    arrayList.add(createMockSubscriptionInfo);
                                }
                            }
                            if (MenuLoader.sSim2Enabled) {
                                String str2 = MenuLoader.sSim2Number;
                                String mockSimOperator2 = MenuLoader.getMockSimOperator(1);
                                Object createMockSubscriptionInfo2 = MenuLoader.createMockSubscriptionInfo(2, 1, str2, MenuLoader.getMockSimOperatorName(1), mockSimOperator2 != null ? mockSimOperator2.substring(0, 3) : "405", mockSimOperator2 != null ? mockSimOperator2.substring(3) : "840");
                                if (createMockSubscriptionInfo2 != null) {
                                    arrayList.add(createMockSubscriptionInfo2);
                                }
                            }
                            Log.d(MenuLoader.TAG, "Intercepted " + name + ", returning mock list with " + arrayList.size() + " items");
                            return arrayList;
                        }
                        if ("getActiveSubscriptionInfo".equals(name)) {
                            int subIdFromArgs = MenuLoader.getSubIdFromArgs(objArr);
                            int slotIndexFromSubId = MenuLoader.getSlotIndexFromSubId(subIdFromArgs);
                            if (slotIndexFromSubId == 1 ? MenuLoader.sSim2Enabled : MenuLoader.sSim1Enabled) {
                                String str3 = slotIndexFromSubId == 1 ? MenuLoader.sSim2Number : MenuLoader.sSim1Number;
                                String mockSimOperator3 = MenuLoader.getMockSimOperator(slotIndexFromSubId);
                                Object createMockSubscriptionInfo3 = MenuLoader.createMockSubscriptionInfo(subIdFromArgs, slotIndexFromSubId, str3, MenuLoader.getMockSimOperatorName(slotIndexFromSubId), mockSimOperator3 != null ? mockSimOperator3.substring(0, 3) : "405", mockSimOperator3 != null ? mockSimOperator3.substring(3) : "840");
                                Log.d(MenuLoader.TAG, "Intercepted " + name + " for subId " + subIdFromArgs + ", slot " + slotIndexFromSubId + ", returning mock info");
                                return createMockSubscriptionInfo3;
                            }
                        } else if ("getActiveSubscriptionInfoForSimSlotIndex".equals(name)) {
                            if (objArr != null && objArr.length > 0) {
                                Object obj2 = objArr[0];
                                if (obj2 instanceof Integer) {
                                    i = ((Integer) obj2).intValue();
                                    if (i != 0 && MenuLoader.sSim1Enabled) {
                                        String str4 = MenuLoader.sSim1Number;
                                        String mockSimOperator4 = MenuLoader.getMockSimOperator(0);
                                        Object createMockSubscriptionInfo4 = MenuLoader.createMockSubscriptionInfo(1, 0, str4, MenuLoader.getMockSimOperatorName(0), mockSimOperator4 != null ? mockSimOperator4.substring(0, 3) : "405", mockSimOperator4 != null ? mockSimOperator4.substring(3) : "840");
                                        Log.d(MenuLoader.TAG, "Intercepted " + name + " for slot 0, returning mock info");
                                        return createMockSubscriptionInfo4;
                                    }
                                    if (i == 1 && MenuLoader.sSim2Enabled) {
                                        String str5 = MenuLoader.sSim2Number;
                                        String mockSimOperator5 = MenuLoader.getMockSimOperator(1);
                                        Object createMockSubscriptionInfo5 = MenuLoader.createMockSubscriptionInfo(2, 1, str5, MenuLoader.getMockSimOperatorName(1), mockSimOperator5 != null ? mockSimOperator5.substring(0, 3) : "405", mockSimOperator5 != null ? mockSimOperator5.substring(3) : "840");
                                        Log.d(MenuLoader.TAG, "Intercepted " + name + " for slot 1, returning mock info");
                                        return createMockSubscriptionInfo5;
                                    }
                                }
                            }
                            i = -1;
                            if (i != 0) {
                            }
                            if (i == 1) {
                                String str52 = MenuLoader.sSim2Number;
                                String mockSimOperator52 = MenuLoader.getMockSimOperator(1);
                                Object createMockSubscriptionInfo52 = MenuLoader.createMockSubscriptionInfo(2, 1, str52, MenuLoader.getMockSimOperatorName(1), mockSimOperator52 != null ? mockSimOperator52.substring(0, 3) : "405", mockSimOperator52 != null ? mockSimOperator52.substring(3) : "840");
                                Log.d(MenuLoader.TAG, "Intercepted " + name + " for slot 1, returning mock info");
                                return createMockSubscriptionInfo52;
                            }
                        } else {
                            if ("getActiveSubscriptionInfoCount".equals(name)) {
                                int i2 = MenuLoader.sSim1Enabled ? 1 : 0;
                                if (MenuLoader.sSim2Enabled) {
                                    i2 = r0 + 1;
                                }
                                Log.d(MenuLoader.TAG, "Intercepted " + name + ", returning mock count: " + i2);
                                return Integer.valueOf(i2);
                            }
                            if ("getActiveSubIdList".equals(name)) {
                                ArrayList arrayList2 = new ArrayList();
                                if (MenuLoader.sSim1Enabled) {
                                    arrayList2.add(1);
                                }
                                if (MenuLoader.sSim2Enabled) {
                                    arrayList2.add(2);
                                }
                                int[] iArr = new int[arrayList2.size()];
                                for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                                    iArr[i3] = ((Integer) arrayList2.get(i3)).intValue();
                                }
                                Log.d(MenuLoader.TAG, "Intercepted " + name + ", returning mock array");
                                return iArr;
                            }
                        }
                    }
                    try {
                        return method.invoke(invoke, objArr);
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
            });
        } catch (Throwable th) {
            Log.e(TAG, "Failed to build ISub dynamic proxy", th);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object createITelephonyProxy(IBinder iBinder, Class cls) {
        try {
            Method declaredMethod = Class.forName("com.android.internal.telephony.ITelephony$Stub").getDeclaredMethod("asInterface", IBinder.class);
            declaredMethod.setAccessible(true);
            final Object invoke = declaredMethod.invoke(null, iBinder);
            return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls, IInterface.class}, new InvocationHandler() { // from class: com.floatingmenu.MenuLoader.17
                /* JADX WARN: Type inference failed for: r9v2, types: [int, boolean] */
                @Override // java.lang.reflect.InvocationHandler
                public Object invoke(Object obj, Method method, Object[] objArr) {
                    int slotIndexFromSubId;
                    String mockSimOperator;
                    StringBuilder sb;
                    String name = method.getName();
                    if ("asBinder".equals(name)) {
                        Log.d(MenuLoader.TAG, "Intercepted ITelephony asBinder(), returning sITelephonyBinderProxy");
                        return MenuLoader.sITelephonyBinderProxy;
                    }
                    if (MenuLoader.sSim1Enabled || MenuLoader.sSim2Enabled) {
                        if (name.startsWith("hasIccCard")) {
                            Log.d(MenuLoader.TAG, "Intercepted " + name + ", returning true");
                            return Boolean.TRUE;
                        }
                        if ("getSimState".equals(name) || "getSimStateForSlotIndex".equals(name)) {
                            Log.d(MenuLoader.TAG, "Intercepted " + name + ", returning SIM_STATE_READY (5)");
                            return 5;
                        }
                        if ("getPhoneCount".equals(name) || "getActiveModemCount".equals(name) || "getSimCount".equals(name)) {
                            int r9 = MenuLoader.sSim1Enabled ? 1 : 0;
                            int i = r9;
                            if (MenuLoader.sSim2Enabled) {
                                i = r9 + 1;
                            }
                            Log.d(MenuLoader.TAG, "Intercepted " + name + ", returning mock count: " + i);
                            return Integer.valueOf(i);
                        }
                        if (name.startsWith("getSimCountryIso") || name.startsWith("getNetworkCountryIso")) {
                            int slotIndexFromSubId2 = MenuLoader.getSlotIndexFromSubId(MenuLoader.getSubIdFromArgs(objArr));
                            if (slotIndexFromSubId2 == 1 ? MenuLoader.sSim2Enabled : MenuLoader.sSim1Enabled) {
                                Log.d(MenuLoader.TAG, "Intercepted " + name + " for slot " + slotIndexFromSubId2 + ", returning " + MenuLoader.sSimCountry);
                                return MenuLoader.sSimCountry;
                            }
                        } else if (name.startsWith("getCardStateForSlotIndex")) {
                            int i2 = 0;
                            if (objArr != null && objArr.length > 0) {
                                Object obj2 = objArr[0];
                                if (obj2 instanceof Integer) {
                                    i2 = ((Integer) obj2).intValue();
                                }
                            }
                            if (i2 == 1 ? MenuLoader.sSim2Enabled : MenuLoader.sSim1Enabled) {
                                Log.d(MenuLoader.TAG, "Intercepted " + name + " for slot " + i2 + ", returning CARD_STATE_PRESENT (2)");
                                return 2;
                            }
                        }
                    }
                    if (name.startsWith("getSimOperatorName")) {
                        slotIndexFromSubId = MenuLoader.getSlotIndexFromSubId(MenuLoader.getSubIdFromArgs(objArr));
                        mockSimOperator = MenuLoader.getMockSimOperatorName(slotIndexFromSubId);
                        if (mockSimOperator != null && !mockSimOperator.isEmpty()) {
                            sb = new StringBuilder("Intercepted ");
                            sb.append(name);
                            sb.append(" for slot ");
                            sb.append(slotIndexFromSubId);
                            sb.append(", returning: ");
                            sb.append(mockSimOperator);
                            Log.d(MenuLoader.TAG, sb.toString());
                            return mockSimOperator;
                        }
                        try {
                            return method.invoke(invoke, objArr);
                        } catch (InvocationTargetException e) {
                            throw e.getTargetException();
                        }
                    }
                    if (name.startsWith("getSimOperator")) {
                        slotIndexFromSubId = MenuLoader.getSlotIndexFromSubId(MenuLoader.getSubIdFromArgs(objArr));
                        mockSimOperator = MenuLoader.getMockSimOperator(slotIndexFromSubId);
                        if (mockSimOperator != null && !mockSimOperator.isEmpty()) {
                            sb = new StringBuilder("Intercepted ");
                            sb.append(name);
                            sb.append(" for slot ");
                            sb.append(slotIndexFromSubId);
                            sb.append(", returning: ");
                            sb.append(mockSimOperator);
                            Log.d(MenuLoader.TAG, sb.toString());
                            return mockSimOperator;
                        }
                        return method.invoke(invoke, objArr);
                    }
                    if (name.startsWith("getNetworkOperatorName")) {
                        slotIndexFromSubId = MenuLoader.getSlotIndexFromSubId(MenuLoader.getSubIdFromArgs(objArr));
                        mockSimOperator = MenuLoader.getMockSimOperatorName(slotIndexFromSubId);
                        if (mockSimOperator != null && !mockSimOperator.isEmpty()) {
                            sb = new StringBuilder("Intercepted ");
                            sb.append(name);
                            sb.append(" for slot ");
                            sb.append(slotIndexFromSubId);
                            sb.append(", returning: ");
                            sb.append(mockSimOperator);
                            Log.d(MenuLoader.TAG, sb.toString());
                            return mockSimOperator;
                        }
                        return method.invoke(invoke, objArr);
                    }
                    if (name.startsWith("getNetworkOperator")) {
                        slotIndexFromSubId = MenuLoader.getSlotIndexFromSubId(MenuLoader.getSubIdFromArgs(objArr));
                        mockSimOperator = MenuLoader.getMockSimOperator(slotIndexFromSubId);
                        if (mockSimOperator != null && !mockSimOperator.isEmpty()) {
                            sb = new StringBuilder("Intercepted ");
                            sb.append(name);
                            sb.append(" for slot ");
                            sb.append(slotIndexFromSubId);
                            sb.append(", returning: ");
                            sb.append(mockSimOperator);
                            Log.d(MenuLoader.TAG, sb.toString());
                            return mockSimOperator;
                        }
                    }
                    return method.invoke(invoke, objArr);
                }
            });
        } catch (Throwable th) {
            Log.e(TAG, "Failed to build ITelephony dynamic proxy", th);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Cursor createMockSimCursor(String[] strArr) {
        MatrixCursor matrixCursor;
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8;
        if (!sSim1Enabled && !sSim2Enabled) {
            return null;
        }
        String str9 = "mnc_string";
        String str10 = "mcc_string";
        int i = 0;
        String[] strArr2 = strArr == null ? new String[]{"_id", "subscription_id", "sim_id", "sim_slot_index", "number", "display_name", "carrier_name", "mcc", "mnc", "mcc_string", "mnc_string", "icc_id", "card_id"} : strArr;
        MatrixCursor matrixCursor2 = new MatrixCursor(strArr2);
        if (sSim1Enabled) {
            Object[] objArr = new Object[strArr2.length];
            String str11 = "card_id";
            String mockSimOperator = getMockSimOperator(0);
            String str12 = "icc_id";
            String substring = mockSimOperator != null ? mockSimOperator.substring(0, 3) : "405";
            String substring2 = mockSimOperator != null ? mockSimOperator.substring(3) : "840";
            String mockSimOperatorName = getMockSimOperatorName(0);
            while (i < strArr2.length) {
                String str13 = strArr2[i];
                if ("_id".equals(str13) || "subscription_id".equals(str13)) {
                    str6 = str9;
                    str7 = str11;
                    str8 = str12;
                    objArr[i] = 1;
                } else if ("sim_id".equals(str13) || "sim_slot_index".equals(str13)) {
                    str6 = str9;
                    str7 = str11;
                    str8 = str12;
                    objArr[i] = 0;
                } else if ("number".equals(str13)) {
                    objArr[i] = sSim1Number;
                    str6 = str9;
                    str7 = str11;
                    str8 = str12;
                } else if ("display_name".equals(str13) || "carrier_name".equals(str13)) {
                    str6 = str9;
                    str7 = str11;
                    str8 = str12;
                    objArr[i] = mockSimOperatorName;
                } else if ("mcc".equals(str13) || "mcc_string".equals(str13)) {
                    str6 = str9;
                    str7 = str11;
                    str8 = str12;
                    objArr[i] = substring;
                } else if ("mnc".equals(str13) || str9.equals(str13)) {
                    str6 = str9;
                    str7 = str11;
                    str8 = str12;
                    objArr[i] = substring2;
                } else {
                    str6 = str9;
                    String str14 = str12;
                    if (str14.equals(str13)) {
                        objArr[i] = getMockSimIccid(0);
                        str8 = str14;
                        str7 = str11;
                    } else {
                        str8 = str14;
                        str7 = str11;
                        if (str7.equals(str13)) {
                            objArr[i] = 0;
                        } else {
                            objArr[i] = null;
                        }
                    }
                }
                i++;
                str11 = str7;
                str9 = str6;
                str12 = str8;
            }
            matrixCursor = matrixCursor2;
            str2 = str9;
            str3 = str11;
            str = str12;
            matrixCursor.addRow(objArr);
        } else {
            matrixCursor = matrixCursor2;
            str = "icc_id";
            str2 = "mnc_string";
            str3 = "card_id";
        }
        if (sSim2Enabled) {
            Object[] objArr2 = new Object[strArr2.length];
            String mockSimOperator2 = getMockSimOperator(1);
            MatrixCursor matrixCursor3 = matrixCursor;
            int i2 = 0;
            String substring3 = mockSimOperator2 != null ? mockSimOperator2.substring(0, 3) : "405";
            String substring4 = mockSimOperator2 != null ? mockSimOperator2.substring(3) : "840";
            String mockSimOperatorName2 = getMockSimOperatorName(1);
            while (i2 < strArr2.length) {
                String str15 = strArr2[i2];
                if ("_id".equals(str15) || "subscription_id".equals(str15)) {
                    str4 = str10;
                    str5 = str;
                    objArr2[i2] = 2;
                } else if ("sim_id".equals(str15) || "sim_slot_index".equals(str15)) {
                    str4 = str10;
                    str5 = str;
                    objArr2[i2] = 1;
                } else if ("number".equals(str15)) {
                    objArr2[i2] = sSim2Number;
                    str4 = str10;
                    str5 = str;
                } else if ("display_name".equals(str15) || "carrier_name".equals(str15)) {
                    str4 = str10;
                    str5 = str;
                    objArr2[i2] = mockSimOperatorName2;
                } else if ("mcc".equals(str15) || str10.equals(str15)) {
                    str4 = str10;
                    str5 = str;
                    objArr2[i2] = substring3;
                } else {
                    if ("mnc".equals(str15)) {
                        str4 = str10;
                        str5 = str;
                    } else {
                        str4 = str10;
                        str5 = str;
                        if (!str2.equals(str15)) {
                            if (str5.equals(str15)) {
                                objArr2[i2] = getMockSimIccid(1);
                            } else if (str3.equals(str15)) {
                                objArr2[i2] = 1;
                            } else {
                                objArr2[i2] = null;
                            }
                        }
                    }
                    objArr2[i2] = substring4;
                }
                i2++;
                str = str5;
                str10 = str4;
            }
            matrixCursor = matrixCursor3;
            matrixCursor.addRow(objArr2);
        }
        Log.d(TAG, "Served mock MatrixCursor for SIM info queries.");
        return matrixCursor;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Cursor createMockSmsCursor(String[] strArr) {
        String str;
        String str2;
        String str3 = "creator";
        String str4 = "_id";
        String str5 = "error_code";
        String[] strArr2 = strArr == null ? new String[]{"_id", "thread_id", "address", "person", "date", "date_sent", "protocol", "read", "status", "type", "reply_path_present", "subject", "body", "service_center", "locked", str5, str3} : strArr;
        String str6 = "locked";
        MatrixCursor matrixCursor = new MatrixCursor(strArr2);
        Object[] objArr = new Object[strArr2.length];
        String str7 = "service_center";
        String str8 = "body";
        int i = 0;
        while (i < strArr2.length) {
            String str9 = strArr2[i];
            if (str4.equals(str9)) {
                objArr[i] = 999999;
            } else if ("thread_id".equals(str9)) {
                objArr[i] = 1;
            } else if ("address".equals(str9)) {
                String str10 = sLastSentAddress;
                if (str10 == null) {
                    str10 = "+917319106888";
                }
                objArr[i] = str10;
            } else if ("person".equals(str9)) {
                objArr[i] = 0;
            } else if ("date".equals(str9)) {
                long j = sLastSentTime;
                if (j <= 0) {
                    j = System.currentTimeMillis();
                }
                objArr[i] = Long.valueOf(j);
            } else if ("date_sent".equals(str9)) {
                long j2 = sLastSentTime;
                if (j2 <= 0) {
                    j2 = System.currentTimeMillis();
                }
                objArr[i] = Long.valueOf(j2);
            } else if ("protocol".equals(str9)) {
                objArr[i] = 0;
            } else if ("read".equals(str9)) {
                objArr[i] = 1;
            } else if ("status".equals(str9)) {
                objArr[i] = 0;
            } else {
                if ("type".equals(str9)) {
                    objArr[i] = 2;
                } else if ("reply_path_present".equals(str9)) {
                    objArr[i] = 0;
                } else if ("subject".equals(str9)) {
                    objArr[i] = null;
                } else {
                    str = str4;
                    String str11 = str8;
                    if (str11.equals(str9)) {
                        String str12 = sLastSentBody;
                        if (str12 == null) {
                            str12 = "NPCI:UPI:DEVICE:BIND:SECURETOKEN:MOCK";
                        }
                        objArr[i] = str12;
                        str8 = str11;
                    } else {
                        str8 = str11;
                        String str13 = str7;
                        if (str13.equals(str9)) {
                            objArr[i] = null;
                            str7 = str13;
                        } else {
                            str7 = str13;
                            String str14 = str6;
                            if (str14.equals(str9)) {
                                objArr[i] = 0;
                                str6 = str14;
                            } else {
                                str6 = str14;
                                String str15 = str5;
                                if (str15.equals(str9)) {
                                    objArr[i] = 0;
                                    str5 = str15;
                                } else {
                                    str5 = str15;
                                    str2 = str3;
                                    if (str2.equals(str9)) {
                                        String str16 = sCurrentPackage;
                                        if (str16 == null) {
                                            str16 = "com.npcivirtual.testapp";
                                        }
                                        objArr[i] = str16;
                                    } else {
                                        objArr[i] = null;
                                    }
                                    i++;
                                    str3 = str2;
                                    str4 = str;
                                }
                            }
                        }
                    }
                    str2 = str3;
                    i++;
                    str3 = str2;
                    str4 = str;
                }
                str = str4;
                str2 = str3;
                i++;
                str3 = str2;
                str4 = str;
            }
            str = str4;
            str2 = str3;
            i++;
            str3 = str2;
            str4 = str;
        }
        matrixCursor.addRow(objArr);
        Log.d(TAG, "Served mock MatrixCursor for sent SMS checking: address=" + sLastSentAddress + ", body=" + sLastSentBody);
        return matrixCursor;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x0115, code lost:
    
        r8[r9] = java.lang.Integer.valueOf(r20);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static Object createMockSubscriptionInfo(int i, int i2, String str, String str2, String str3, String str4) {
        try {
            Constructor<?>[] declaredConstructors = Class.forName("android.telephony.SubscriptionInfo").getDeclaredConstructors();
            StringBuilder sb = new StringBuilder("createMockSubscriptionInfo: Found ");
            sb.append(declaredConstructors != null ? declaredConstructors.length : 0);
            sb.append(" constructors in SubscriptionInfo");
            Log.d(TAG, sb.toString());
            Constructor<?> constructor = null;
            for (Constructor<?> constructor2 : declaredConstructors) {
                StringBuilder sb2 = new StringBuilder();
                for (Class<?> cls : constructor2.getParameterTypes()) {
                    sb2.append(cls.getName());
                    sb2.append(", ");
                }
                Log.d(TAG, "Constructor candidate: " + constructor2.getName() + " with params: [" + sb2.toString() + "]");
                if (constructor == null || constructor2.getParameterTypes().length > constructor.getParameterTypes().length) {
                    constructor = constructor2;
                }
            }
            if (constructor == null) {
                return null;
            }
            constructor.setAccessible(true);
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] objArr = new Object[parameterTypes.length];
            int i3 = 0;
            while (i3 < parameterTypes.length) {
                Class<?> cls2 = parameterTypes[i3];
                if (cls2 != Integer.TYPE && cls2 != Integer.class) {
                    if (cls2 == String.class) {
                        objArr[i3] = null;
                    } else if (cls2 == CharSequence.class) {
                        objArr[i3] = str2;
                    } else {
                        if (cls2 != Boolean.TYPE && cls2 != Boolean.class) {
                            if (cls2 == Bitmap.class) {
                                objArr[i3] = null;
                            } else {
                                if (cls2 != Double.TYPE && cls2 != Double.class) {
                                    if (cls2 != Float.TYPE && cls2 != Float.class) {
                                        if (cls2 != Long.TYPE && cls2 != Long.class) {
                                            objArr[i3] = null;
                                        }
                                        objArr[i3] = 0L;
                                    }
                                    objArr[i3] = Float.valueOf(0.0f);
                                }
                                objArr[i3] = Double.valueOf(0.0d);
                            }
                        }
                        objArr[i3] = Boolean.FALSE;
                    }
                    i3++;
                }
                if (i3 != 2 && (i3 != 1 || parameterTypes.length >= 15)) {
                    objArr[i3] = 0;
                    i3++;
                }
                objArr[i3] = Integer.valueOf(i2);
                i3++;
            }
            if (parameterTypes.length >= 5) {
                Class<?> cls3 = parameterTypes[0];
                Class<?> cls4 = Integer.TYPE;
                if (cls3 == cls4) {
                    objArr[0] = Integer.valueOf(i);
                }
                if (parameterTypes[1] == String.class) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("8991");
                    sb3.append(i2 == 1 ? "22" : "11");
                    sb3.append("0000000012345");
                    objArr[1] = sb3.toString();
                }
                if (parameterTypes[2] == cls4) {
                    objArr[2] = Integer.valueOf(i2);
                }
                if (parameterTypes[3] == CharSequence.class) {
                    objArr[3] = str2;
                }
                if (parameterTypes[4] == CharSequence.class) {
                    objArr[4] = str2;
                }
            }
            int i4 = 0;
            for (int i5 = 0; i5 < parameterTypes.length; i5++) {
                if (parameterTypes[i5] == String.class) {
                    int i6 = i4 + 1;
                    if (i6 == 2) {
                        objArr[i5] = str;
                    } else if (i6 == 3) {
                        objArr[i5] = str3;
                    } else if (i6 == 4) {
                        objArr[i5] = str4;
                    } else if (i6 == 5) {
                        objArr[i5] = sSimCountry;
                    }
                    i4 = i6;
                }
            }
            Object newInstance = constructor.newInstance(objArr);
            setFieldValue(newInstance, "mId", Integer.valueOf(i));
            setFieldValue(newInstance, "mSimSlotIndex", Integer.valueOf(i2));
            setFieldValue(newInstance, "mNumber", str);
            setFieldValue(newInstance, "mDisplayName", str2);
            setFieldValue(newInstance, "mCarrierName", str2);
            setFieldValue(newInstance, "mCountryIso", sSimCountry);
            setFieldValue(newInstance, "mMcc", str3);
            setFieldValue(newInstance, "mMnc", str4);
            StringBuilder sb4 = new StringBuilder();
            sb4.append("8991");
            sb4.append(i2 == 1 ? "22" : "11");
            sb4.append("0000000012345");
            setFieldValue(newInstance, "mIccId", sb4.toString());
            setFieldValue(newInstance, "mCardId", Integer.valueOf(i2));
            return newInstance;
        } catch (Throwable th) {
            Log.e(TAG, "Failed to create mock SubscriptionInfo using reflection constructor", th);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void dismissFloatingMenu(Activity activity) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void dismissSystemUiOverlay(final Application application) {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.floatingmenu.MenuLoader.12
            @Override // java.lang.Runnable
            public void run() {
                if (MenuLoader.sSystemUiOverlay != null) {
                    try {
                        WindowManager windowManager = (WindowManager) application.getSystemService("window");
                        if (windowManager != null) {
                            windowManager.removeView(MenuLoader.sSystemUiOverlay);
                            Log.d(MenuLoader.TAG, "SystemUI Overlay dismissed from WindowManager.");
                        }
                    } catch (Exception e) {
                        Log.e(MenuLoader.TAG, "Error dismissing SystemUI overlay: ", e);
                    }
                    View unused = MenuLoader.sSystemUiOverlay = null;
                    TextView unused2 = MenuLoader.sSystemUiErrorText = null;
                    EditText unused3 = MenuLoader.sSystemUiInput = null;
                    View unused4 = MenuLoader.sSystemUiCard = null;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int dpToPx(Context context, float f) {
        return (int) TypedValue.applyDimension(1, f, context.getResources().getDisplayMetrics());
    }

    private static Application getApplicationContext() {
        return sApplication;
    }

    private static boolean getJsonBoolean(String str, String str2) {
        if (str != null && !str.isEmpty()) {
            String str3 = "\"" + str2 + "\":";
            int indexOf = str.indexOf(str3);
            if (indexOf == -1) {
                return false;
            }
            int length = str3.length() + indexOf;
            while (length < str.length() && Character.isWhitespace(str.charAt(length))) {
                length++;
            }
            if (length < str.length()) {
                return str.startsWith("true", length);
            }
        }
        return false;
    }

    private static String getJsonString(String str, String str2) {
        int indexOf;
        char c;
        if (str == null || str.isEmpty()) {
            return "";
        }
        String str3 = "\"" + str2 + "\":";
        int indexOf2 = str.indexOf(str3);
        if (indexOf2 == -1 || (indexOf = str.indexOf("\"", str3.length() + indexOf2)) == -1) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean z = false;
        for (int i = indexOf + 1; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (z) {
                if (charAt == 'n') {
                    c = '\n';
                } else if (charAt == 'r') {
                    c = '\r';
                } else if (charAt == 't') {
                    c = '\t';
                } else {
                    sb.append(charAt);
                    z = false;
                }
                sb.append(c);
                z = false;
            } else if (charAt == '\\') {
                z = true;
            } else {
                if (charAt == '\"') {
                    return sb.toString();
                }
                sb.append(charAt);
            }
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getMockSimIccid(int i) {
        if (getMockSimNumber(i) == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder("8991");
        sb.append(i == 1 ? "22" : "11");
        sb.append("0000000012345");
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getMockSimImsi(int i) {
        String mockSimOperator = getMockSimOperator(i);
        if (mockSimOperator != null) {
            return mockSimOperator.concat("123456789");
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getMockSimNumber(int i) {
        if (i == 1 ? sSim2Enabled : sSim1Enabled) {
            return i == 1 ? sSim2Number : sSim1Number;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getMockSimOperator(int i) {
        if (!(i == 1 ? sSim2Enabled : sSim1Enabled)) {
            return null;
        }
        String str = i == 1 ? sSim2Provider : sSim1Provider;
        if (str == null) {
            str = "jio";
        }
        String lowerCase = str.toLowerCase();
        if ("jio".equals(lowerCase)) {
            return "405840";
        }
        if ("airtel".equals(lowerCase)) {
            return "40445";
        }
        if ("vi".equals(lowerCase)) {
            return "40420";
        }
        if ("bsnl".equals(lowerCase)) {
            return "40473";
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getMockSimOperatorName(int i) {
        if (!(i == 1 ? sSim2Enabled : sSim1Enabled)) {
            return null;
        }
        String str = i == 1 ? sSim2Provider : sSim1Provider;
        if (str == null) {
            str = "jio";
        }
        String lowerCase = str.toLowerCase();
        if ("jio".equals(lowerCase)) {
            return "Jio";
        }
        if ("airtel".equals(lowerCase)) {
            return "Airtel";
        }
        if ("vi".equals(lowerCase)) {
            return "Vi";
        }
        if ("bsnl".equals(lowerCase)) {
            return "BSNL";
        }
        return null;
    }

    private static Map getOrCreateServiceCache() {
        try {
            Field declaredField = Class.forName("android.os.ServiceManager").getDeclaredField("sCache");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(null);
            if (obj != null && (obj instanceof FakeServiceCache)) {
                return (Map) obj;
            }
            FakeServiceCache fakeServiceCache = new FakeServiceCache();
            if (obj instanceof Map) {
                fakeServiceCache.putAll((Map) obj);
            }
            declaredField.set(null, fakeServiceCache);
            return fakeServiceCache;
        } catch (Throwable th) {
            Log.e(TAG, "Failed to get or create ServiceCache", th);
            return null;
        }
    }

    private static String getProcessName() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/self/cmdline"));
            String readLine = bufferedReader.readLine();
            bufferedReader.close();
            if (readLine != null) {
                return readLine.trim();
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getSlotIndexFromSubId(int i) {
        if (i < 0) {
            return 0;
        }
        try {
            Method declaredMethod = Class.forName("android.telephony.SubscriptionManager").getDeclaredMethod("getSlotIndex", Integer.TYPE);
            declaredMethod.setAccessible(true);
            Integer num = (Integer) declaredMethod.invoke(null, Integer.valueOf(i));
            if (num != null && num.intValue() >= 0) {
                return num.intValue();
            }
        } catch (Throwable unused) {
            if (i != 1 && i == 2) {
                return 1;
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getSubIdFromArgs(Object[] objArr) {
        if (objArr == null) {
            return -1;
        }
        for (Object obj : objArr) {
            if (obj instanceof Integer) {
                return ((Integer) obj).intValue();
            }
        }
        return -1;
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0132  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void init(final String str, final String str2) {
        final String str3;
        final String str4;
        String str5;
        String jsonString;
        Log.i(TAG, "MenuLoader: init() triggered from JNI for process: " + str);
        try {
            Class<?> cls = Class.forName("dalvik.system.VMRuntime");
            Method declaredMethod = cls.getDeclaredMethod("getRuntime", new Class[0]);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(null, new Object[0]);
            Method declaredMethod2 = cls.getDeclaredMethod("setHiddenApiExemptions", String[].class);
            declaredMethod2.setAccessible(true);
            declaredMethod2.invoke(invoke, new String[]{"L"});
            Log.d(TAG, "Successfully exempted hidden APIs via VMRuntime");
        } catch (Throwable th) {
            Log.e(TAG, "Failed to exempt hidden APIs: ", th);
        }
        final boolean z = str != null && str.contains("systemui");
        String str6 = "";
        if (str2 == null || str2.isEmpty()) {
            str3 = "";
            str4 = str3;
        } else {
            try {
                sSim1Enabled = getJsonBoolean(str2, "sim1_enabled");
                sSim1Provider = getJsonString(str2, "sim1_provider");
                sSim1Number = getJsonString(str2, "sim1_number");
                sSim2Enabled = getJsonBoolean(str2, "sim2_enabled");
                sSim2Provider = getJsonString(str2, "sim2_provider");
                sSim2Number = getJsonString(str2, "sim2_number");
                sIsTargetPackage = getJsonBoolean(str2, "is_target_package");
                String jsonString2 = getJsonString(str2, "sim_country");
                sSimCountry = jsonString2;
                try {
                    if (jsonString2 != null) {
                        if (jsonString2.isEmpty()) {
                        }
                        sIamNotDeveloper = getJsonBoolean(str2, "iamnotdeveloper");
                        sIamNoRoot = getJsonBoolean(str2, "iamnoroot");
                        sMockDataSubscription = getJsonBoolean(str2, "mock_data_subscription");
                        jsonString = getJsonString(str2, "license_key");
                        str6 = getJsonString(str2, "license_status");
                        Log.d(TAG, "Synchronously initialized SIM settings: SIM1=" + sSim1Enabled + ", SIM2=" + sSim2Enabled + ", IsTarget = " + sIsTargetPackage + ", Country=" + sSimCountry + ", IAmNotDeveloper=" + sIamNotDeveloper + ", IAmNoRoot=" + sIamNoRoot + ", MockDataSub=" + sMockDataSubscription);
                        str3 = str6;
                        str4 = jsonString;
                    }
                    str6 = getJsonString(str2, "license_status");
                    Log.d(TAG, "Synchronously initialized SIM settings: SIM1=" + sSim1Enabled + ", SIM2=" + sSim2Enabled + ", IsTarget = " + sIsTargetPackage + ", Country=" + sSimCountry + ", IAmNotDeveloper=" + sIamNotDeveloper + ", IAmNoRoot=" + sIamNoRoot);
                    str3 = str6;
                    str4 = jsonString;
                } catch (Throwable th2) {
                    th = th2;
                    str5 = str6;
                    str6 = jsonString;
                    Log.e(TAG, "Failed to parse configJson synchronously: ", th);
                    str4 = str6;
                    str3 = str5;
                    if (!z) {
                    }
                    new Thread(new Runnable() { // from class: com.floatingmenu.MenuLoader.1
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                Log.i(MenuLoader.TAG, "Zygisk initialized in process: " + str);
                                Class<?> cls2 = Class.forName("android.app.ActivityThread");
                                Method declaredMethod3 = cls2.getDeclaredMethod("currentActivityThread", new Class[0]);
                                declaredMethod3.setAccessible(true);
                                Object obj = null;
                                for (int i = 0; i < 200 && (obj = declaredMethod3.invoke(null, new Object[0])) == null; i++) {
                                    Thread.sleep(50L);
                                }
                                if (obj == null) {
                                    Log.e(MenuLoader.TAG, "ActivityThread is null, aborting.");
                                    return;
                                }
                                Method declaredMethod4 = cls2.getDeclaredMethod("currentApplication", new Class[0]);
                                declaredMethod4.setAccessible(true);
                                Application application = null;
                                for (int i2 = 0; i2 < 200; i2++) {
                                    application = (Application) declaredMethod4.invoke(null, new Object[0]);
                                    if (application != null) {
                                        break;
                                    }
                                    Thread.sleep(50L);
                                }
                                if (application == null) {
                                    Log.e(MenuLoader.TAG, "Application is null for process " + str + ", aborting.");
                                    return;
                                }
                                String unused = MenuLoader.sCurrentPackage = application.getPackageName();
                                Application unused2 = MenuLoader.sApplication = application;
                                Log.i(MenuLoader.TAG, "Application context found for package: " + MenuLoader.sCurrentPackage + " (process: " + str + ")");
                                if (!z) {
                                    Log.i(MenuLoader.TAG, "Completing delayed Activity hooks (mProviderMap clearing)...");
                                    MenuLoader.applyActivityHooks(cls2, obj);
                                }
                                String str7 = str2;
                                if (str7 != null && !str7.isEmpty()) {
                                    SharedPreferences sharedPreferences = application.getSharedPreferences("zygisk_menu_prefs", 0);
                                    "active".equals(str3);
                                    sharedPreferences.edit().putString("sim1_enabled", String.valueOf(MenuLoader.sSim1Enabled)).putString("sim1_provider", MenuLoader.sSim1Provider).putString("sim1_number", MenuLoader.sSim1Number).putString("sim2_enabled", String.valueOf(MenuLoader.sSim2Enabled)).putString("sim2_provider", MenuLoader.sSim2Provider).putString("sim2_number", MenuLoader.sSim2Number).putString("sim_country", MenuLoader.sSimCountry).putString("iamnotdeveloper", String.valueOf(MenuLoader.sIamNotDeveloper)).putString("iamnoroot", String.valueOf(MenuLoader.sIamNoRoot)).putBoolean("is_licensed", true).putString("license_key", str4).putString("validation_status", str3).apply();
                                    Log.d(MenuLoader.TAG, "Wrote config.json values to SharedPreferences on startup");
                                }
                                b.a(application, new Application.ActivityLifecycleCallbacks() { // from class: com.floatingmenu.MenuLoader.1.1
                                    @Override // android.app.Application.ActivityLifecycleCallbacks
                                    public void onActivityCreated(Activity activity, Bundle bundle) {
                                    }

                                    @Override // android.app.Application.ActivityLifecycleCallbacks
                                    public void onActivityDestroyed(Activity activity) {
                                        if (MenuLoader.sCurrentActivity != null && MenuLoader.sCurrentActivity.get() == activity) {
                                            WeakReference unused3 = MenuLoader.sCurrentActivity = null;
                                        }
                                        if (MenuLoader.sLockOverlay == null || MenuLoader.sLockOverlay.getContext() != activity) {
                                            return;
                                        }
                                        View unused4 = MenuLoader.sLockOverlay = null;
                                    }

                                    @Override // android.app.Application.ActivityLifecycleCallbacks
                                    public void onActivityPaused(Activity activity) {
                                        try {
                                            Intent intent = new Intent("com.floatingmenu.ACTION_FOREGROUND_CHANGED");
                                            intent.putExtra("package_name", "none");
                                            intent.addFlags(16777216);
                                            activity.sendBroadcast(intent);
                                        } catch (Throwable th3) {
                                            Log.e(MenuLoader.TAG, "Failed to send foreground none broadcast: ", th3);
                                        }
                                    }

                                    @Override // android.app.Application.ActivityLifecycleCallbacks
                                    public void onActivityResumed(final Activity activity) {
                                        Log.d(MenuLoader.TAG, "Activity resumed: ".concat(activity.getClass().getName()));
                                        WeakReference unused3 = MenuLoader.sCurrentActivity = new WeakReference(activity);
                                        try {
                                            Intent intent = new Intent("com.floatingmenu.ACTION_FOREGROUND_CHANGED");
                                            intent.putExtra("package_name", activity.getPackageName());
                                            intent.addFlags(16777216);
                                            activity.sendBroadcast(intent);
                                        } catch (Throwable th3) {
                                            Log.e(MenuLoader.TAG, "Failed to send foreground broadcast: ", th3);
                                        }
                                        final SharedPreferences sharedPreferences2 = activity.getSharedPreferences("zygisk_menu_prefs", 0);
                                        if (MenuLoader.sIsTargetPackage) {
                                            FloatingMenu.show(activity);
                                        }
                                        new Thread(new Runnable() { // from class: com.floatingmenu.MenuLoader.1.1.1
                                            @Override // java.lang.Runnable
                                            public void run() {
                                                final String querySocket = MenuLoader.querySocket("GET_LICENSE");
                                                final String querySocket2 = MenuLoader.querySocket("GET_LICENSE_STATUS");
                                                final String querySocket3 = MenuLoader.querySocket("GET_SIM_SETTINGS");
                                                if (querySocket == null || querySocket2 == null || querySocket3 == null) {
                                                    Log.w(MenuLoader.TAG, "Socket query returned null. Retaining current license and SIM settings.");
                                                } else {
                                                    new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.floatingmenu.MenuLoader.1.1.1.1
                                                        @Override // java.lang.Runnable
                                                        public void run() {
                                                            SharedPreferences.Editor putString;
                                                            String str8;
                                                            if (querySocket3.contains("|")) {
                                                                String[] split = querySocket3.split("\\|", -1);
                                                                if (split.length >= 6) {
                                                                    MenuLoader.sSim1Enabled = "true".equalsIgnoreCase(split[0]);
                                                                    MenuLoader.sSim1Provider = split[1];
                                                                    MenuLoader.sSim1Number = split[2];
                                                                    MenuLoader.sSim2Enabled = "true".equalsIgnoreCase(split[3]);
                                                                    MenuLoader.sSim2Provider = split[4];
                                                                    MenuLoader.sSim2Number = split[5];
                                                                    if (split.length >= 7) {
                                                                        String str9 = split[6];
                                                                        MenuLoader.sSimCountry = str9;
                                                                        if (str9 == null || str9.isEmpty()) {
                                                                            MenuLoader.sSimCountry = "in";
                                                                        }
                                                                    }
                                                                    if (split.length >= 8) {
                                                                        MenuLoader.sIamNotDeveloper = "true".equalsIgnoreCase(split[7]);
                                                                    }
                                                                    if (split.length >= 9) {
                                                                        MenuLoader.sIamNoRoot = "true".equalsIgnoreCase(split[8]);
                                                                    }
                                                                    if (split.length >= 10) {
                                                                        MenuLoader.sMockDataSubscription = "true".equalsIgnoreCase(split[9]);
                                                                    }
                                                                    sharedPreferences2.edit().putString("sim1_enabled", split[0]).putString("sim1_provider", split[1]).putString("sim1_number", split[2]).putString("sim2_enabled", split[3]).putString("sim2_provider", split[4]).putString("sim2_number", split[5]).putString("sim_country", MenuLoader.sSimCountry).putString("iamnotdeveloper", String.valueOf(MenuLoader.sIamNotDeveloper)).putString("iamnoroot", String.valueOf(MenuLoader.sIamNoRoot)).putString("mock_data_subscription", String.valueOf(MenuLoader.sMockDataSubscription)).apply();
                                                                }
                                                            }
                                                            if (querySocket.isEmpty()) {
                                                                putString = sharedPreferences2.edit().putBoolean("is_licensed", false).putString("license_key", "");
                                                                str8 = "invalid";
                                                            } else {
                                                                if ("active".equals(querySocket2)) {
                                                                    if (!sharedPreferences2.getBoolean("is_licensed", false)) {
                                                                        sharedPreferences2.edit().putBoolean("is_licensed", true).putString("license_key", querySocket).putString("validation_status", "active").apply();
                                                                    }
                                                                    if (MenuLoader.sIsTargetPackage) {
                                                                        FloatingMenu.show(activity);
                                                                        return;
                                                                    }
                                                                    return;
                                                                }
                                                                putString = sharedPreferences2.edit().putBoolean("is_licensed", false).putString("license_key", querySocket);
                                                                str8 = querySocket2;
                                                            }
                                                            putString.putString("validation_status", str8).apply();
                                                            if (MenuLoader.sIsTargetPackage) {
                                                                return;
                                                            }
                                                            MenuLoader.dismissFloatingMenu(activity);
                                                        }
                                                    });
                                                }
                                            }
                                        }).start();
                                    }

                                    @Override // android.app.Application.ActivityLifecycleCallbacks
                                    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                                    }

                                    @Override // android.app.Application.ActivityLifecycleCallbacks
                                    public void onActivityStarted(Activity activity) {
                                    }

                                    @Override // android.app.Application.ActivityLifecycleCallbacks
                                    public void onActivityStopped(Activity activity) {
                                    }
                                });
                                Log.d(MenuLoader.TAG, "ActivityLifecycleCallbacks registered successfully.");
                            } catch (Throwable th3) {
                                Log.e(MenuLoader.TAG, "Error in MenuLoader thread: ", th3);
                            }
                        }
                    }).start();
                }
                sSimCountry = "in";
                sIamNotDeveloper = getJsonBoolean(str2, "iamnotdeveloper");
                sIamNoRoot = getJsonBoolean(str2, "iamnoroot");
                sMockDataSubscription = getJsonBoolean(str2, "mock_data_subscription");
                jsonString = getJsonString(str2, "license_key");
            } catch (Throwable th3) {
                th = th3;
                str5 = "";
            }
        }
        if (!z) {
            sCurrentPackage = str != null ? str.contains(":") ? str.split(":")[0] : str : null;
            Log.i(TAG, "Early applying SMS, Telephony, and Activity hooks for: " + sCurrentPackage);
            try {
                applySmsHooks();
                applyTelephonyHooks();
                applyActivityHooks(Class.forName("android.app.ActivityThread"), null);
                if (sIamNoRoot) {
                    Log.i(TAG, "Root spoofing enabled early for target: " + sCurrentPackage);
                    applyPackageManagerHooks();
                    spoofBuildFields();
                }
            } catch (Throwable th4) {
                Log.e(TAG, "Failed to apply early hooks", th4);
            }
        }
        new Thread(new Runnable() { // from class: com.floatingmenu.MenuLoader.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Log.i(MenuLoader.TAG, "Zygisk initialized in process: " + str);
                    Class<?> cls2 = Class.forName("android.app.ActivityThread");
                    Method declaredMethod3 = cls2.getDeclaredMethod("currentActivityThread", new Class[0]);
                    declaredMethod3.setAccessible(true);
                    Object obj = null;
                    for (int i = 0; i < 200 && (obj = declaredMethod3.invoke(null, new Object[0])) == null; i++) {
                        Thread.sleep(50L);
                    }
                    if (obj == null) {
                        Log.e(MenuLoader.TAG, "ActivityThread is null, aborting.");
                        return;
                    }
                    Method declaredMethod4 = cls2.getDeclaredMethod("currentApplication", new Class[0]);
                    declaredMethod4.setAccessible(true);
                    Application application = null;
                    for (int i2 = 0; i2 < 200; i2++) {
                        application = (Application) declaredMethod4.invoke(null, new Object[0]);
                        if (application != null) {
                            break;
                        }
                        Thread.sleep(50L);
                    }
                    if (application == null) {
                        Log.e(MenuLoader.TAG, "Application is null for process " + str + ", aborting.");
                        return;
                    }
                    String unused = MenuLoader.sCurrentPackage = application.getPackageName();
                    Application unused2 = MenuLoader.sApplication = application;
                    Log.i(MenuLoader.TAG, "Application context found for package: " + MenuLoader.sCurrentPackage + " (process: " + str + ")");
                    if (!z) {
                        Log.i(MenuLoader.TAG, "Completing delayed Activity hooks (mProviderMap clearing)...");
                        MenuLoader.applyActivityHooks(cls2, obj);
                    }
                    String str7 = str2;
                    if (str7 != null && !str7.isEmpty()) {
                        SharedPreferences sharedPreferences = application.getSharedPreferences("zygisk_menu_prefs", 0);
                        "active".equals(str3);
                        sharedPreferences.edit().putString("sim1_enabled", String.valueOf(MenuLoader.sSim1Enabled)).putString("sim1_provider", MenuLoader.sSim1Provider).putString("sim1_number", MenuLoader.sSim1Number).putString("sim2_enabled", String.valueOf(MenuLoader.sSim2Enabled)).putString("sim2_provider", MenuLoader.sSim2Provider).putString("sim2_number", MenuLoader.sSim2Number).putString("sim_country", MenuLoader.sSimCountry).putString("iamnotdeveloper", String.valueOf(MenuLoader.sIamNotDeveloper)).putString("iamnoroot", String.valueOf(MenuLoader.sIamNoRoot)).putBoolean("is_licensed", true).putString("license_key", str4).putString("validation_status", str3).apply();
                        Log.d(MenuLoader.TAG, "Wrote config.json values to SharedPreferences on startup");
                    }
                    b.a(application, new Application.ActivityLifecycleCallbacks() { // from class: com.floatingmenu.MenuLoader.1.1
                        @Override // android.app.Application.ActivityLifecycleCallbacks
                        public void onActivityCreated(Activity activity, Bundle bundle) {
                        }

                        @Override // android.app.Application.ActivityLifecycleCallbacks
                        public void onActivityDestroyed(Activity activity) {
                            if (MenuLoader.sCurrentActivity != null && MenuLoader.sCurrentActivity.get() == activity) {
                                WeakReference unused3 = MenuLoader.sCurrentActivity = null;
                            }
                            if (MenuLoader.sLockOverlay == null || MenuLoader.sLockOverlay.getContext() != activity) {
                                return;
                            }
                            View unused4 = MenuLoader.sLockOverlay = null;
                        }

                        @Override // android.app.Application.ActivityLifecycleCallbacks
                        public void onActivityPaused(Activity activity) {
                            try {
                                Intent intent = new Intent("com.floatingmenu.ACTION_FOREGROUND_CHANGED");
                                intent.putExtra("package_name", "none");
                                intent.addFlags(16777216);
                                activity.sendBroadcast(intent);
                            } catch (Throwable th32) {
                                Log.e(MenuLoader.TAG, "Failed to send foreground none broadcast: ", th32);
                            }
                        }

                        @Override // android.app.Application.ActivityLifecycleCallbacks
                        public void onActivityResumed(final Activity activity) {
                            Log.d(MenuLoader.TAG, "Activity resumed: ".concat(activity.getClass().getName()));
                            WeakReference unused3 = MenuLoader.sCurrentActivity = new WeakReference(activity);
                            try {
                                Intent intent = new Intent("com.floatingmenu.ACTION_FOREGROUND_CHANGED");
                                intent.putExtra("package_name", activity.getPackageName());
                                intent.addFlags(16777216);
                                activity.sendBroadcast(intent);
                            } catch (Throwable th32) {
                                Log.e(MenuLoader.TAG, "Failed to send foreground broadcast: ", th32);
                            }
                            final SharedPreferences sharedPreferences2 = activity.getSharedPreferences("zygisk_menu_prefs", 0);
                            if (MenuLoader.sIsTargetPackage) {
                                FloatingMenu.show(activity);
                            }
                            new Thread(new Runnable() { // from class: com.floatingmenu.MenuLoader.1.1.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    final String querySocket = MenuLoader.querySocket("GET_LICENSE");
                                    final String querySocket2 = MenuLoader.querySocket("GET_LICENSE_STATUS");
                                    final String querySocket3 = MenuLoader.querySocket("GET_SIM_SETTINGS");
                                    if (querySocket == null || querySocket2 == null || querySocket3 == null) {
                                        Log.w(MenuLoader.TAG, "Socket query returned null. Retaining current license and SIM settings.");
                                    } else {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.floatingmenu.MenuLoader.1.1.1.1
                                            @Override // java.lang.Runnable
                                            public void run() {
                                                SharedPreferences.Editor putString;
                                                String str8;
                                                if (querySocket3.contains("|")) {
                                                    String[] split = querySocket3.split("\\|", -1);
                                                    if (split.length >= 6) {
                                                        MenuLoader.sSim1Enabled = "true".equalsIgnoreCase(split[0]);
                                                        MenuLoader.sSim1Provider = split[1];
                                                        MenuLoader.sSim1Number = split[2];
                                                        MenuLoader.sSim2Enabled = "true".equalsIgnoreCase(split[3]);
                                                        MenuLoader.sSim2Provider = split[4];
                                                        MenuLoader.sSim2Number = split[5];
                                                        if (split.length >= 7) {
                                                            String str9 = split[6];
                                                            MenuLoader.sSimCountry = str9;
                                                            if (str9 == null || str9.isEmpty()) {
                                                                MenuLoader.sSimCountry = "in";
                                                            }
                                                        }
                                                        if (split.length >= 8) {
                                                            MenuLoader.sIamNotDeveloper = "true".equalsIgnoreCase(split[7]);
                                                        }
                                                        if (split.length >= 9) {
                                                            MenuLoader.sIamNoRoot = "true".equalsIgnoreCase(split[8]);
                                                        }
                                                        if (split.length >= 10) {
                                                            MenuLoader.sMockDataSubscription = "true".equalsIgnoreCase(split[9]);
                                                        }
                                                        sharedPreferences2.edit().putString("sim1_enabled", split[0]).putString("sim1_provider", split[1]).putString("sim1_number", split[2]).putString("sim2_enabled", split[3]).putString("sim2_provider", split[4]).putString("sim2_number", split[5]).putString("sim_country", MenuLoader.sSimCountry).putString("iamnotdeveloper", String.valueOf(MenuLoader.sIamNotDeveloper)).putString("iamnoroot", String.valueOf(MenuLoader.sIamNoRoot)).putString("mock_data_subscription", String.valueOf(MenuLoader.sMockDataSubscription)).apply();
                                                    }
                                                }
                                                if (querySocket.isEmpty()) {
                                                    putString = sharedPreferences2.edit().putBoolean("is_licensed", false).putString("license_key", "");
                                                    str8 = "invalid";
                                                } else {
                                                    if ("active".equals(querySocket2)) {
                                                        if (!sharedPreferences2.getBoolean("is_licensed", false)) {
                                                            sharedPreferences2.edit().putBoolean("is_licensed", true).putString("license_key", querySocket).putString("validation_status", "active").apply();
                                                        }
                                                        if (MenuLoader.sIsTargetPackage) {
                                                            FloatingMenu.show(activity);
                                                            return;
                                                        }
                                                        return;
                                                    }
                                                    putString = sharedPreferences2.edit().putBoolean("is_licensed", false).putString("license_key", querySocket);
                                                    str8 = querySocket2;
                                                }
                                                putString.putString("validation_status", str8).apply();
                                                if (MenuLoader.sIsTargetPackage) {
                                                    return;
                                                }
                                                MenuLoader.dismissFloatingMenu(activity);
                                            }
                                        });
                                    }
                                }
                            }).start();
                        }

                        @Override // android.app.Application.ActivityLifecycleCallbacks
                        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                        }

                        @Override // android.app.Application.ActivityLifecycleCallbacks
                        public void onActivityStarted(Activity activity) {
                        }

                        @Override // android.app.Application.ActivityLifecycleCallbacks
                        public void onActivityStopped(Activity activity) {
                        }
                    });
                    Log.d(MenuLoader.TAG, "ActivityLifecycleCallbacks registered successfully.");
                } catch (Throwable th32) {
                    Log.e(MenuLoader.TAG, "Error in MenuLoader thread: ", th32);
                }
            }
        }).start();
    }

    public static boolean isSensitiveApp(String str) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isTargetApplication(String str) {
        if (str != null && !str.isEmpty() && !"none".equals(str)) {
            String lowerCase = str.toLowerCase();
            if (!lowerCase.contains("launcher") && !lowerCase.contains("settings") && !lowerCase.contains("systemui") && !lowerCase.contains("inputmethod") && !lowerCase.contains("keyboard") && !lowerCase.contains("vending") && !lowerCase.contains("gms") && !lowerCase.contains("gsf") && !lowerCase.contains("webview") && !"android".equals(lowerCase)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String querySocket(String str) {
        LocalSocket localSocket;
        try {
            localSocket = new LocalSocket();
            try {
                localSocket.connect(new LocalSocketAddress("zygisk_menu_socket"));
                localSocket.setSoTimeout(3000);
                OutputStream outputStream = localSocket.getOutputStream();
                outputStream.write((str + "\n").getBytes("UTF-8"));
                outputStream.flush();
                String readLine = new BufferedReader(new InputStreamReader(localSocket.getInputStream(), "UTF-8")).readLine();
                if (readLine != null) {
                    String trim = readLine.trim();
                    try {
                        localSocket.close();
                    } catch (Exception unused) {
                    }
                    return trim;
                }
                try {
                    localSocket.close();
                    return "";
                } catch (Exception unused2) {
                    return "";
                }
            } catch (Throwable unused3) {
                if (localSocket != null) {
                    try {
                        localSocket.close();
                    } catch (Exception unused4) {
                    }
                }
                return null;
            }
        } catch (Throwable unused5) {
            localSocket = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void registerSystemUiReceiver(final Application application) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.floatingmenu.ACTION_FOREGROUND_CHANGED");
        intentFilter.addAction("com.floatingmenu.ACTION_LICENSE_STATE_CHANGED");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.floatingmenu.MenuLoader.7
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String str;
                String action = intent.getAction();
                if ("com.floatingmenu.ACTION_FOREGROUND_CHANGED".equals(action)) {
                    String unused = MenuLoader.sForegroundApp = intent.getStringExtra("package_name");
                    if (MenuLoader.sForegroundApp == null) {
                        String unused2 = MenuLoader.sForegroundApp = "none";
                    }
                    str = "SystemUI received foreground app change: " + MenuLoader.sForegroundApp;
                } else if (!"com.floatingmenu.ACTION_LICENSE_STATE_CHANGED".equals(action)) {
                    return;
                } else {
                    str = "SystemUI received license state change broadcast";
                }
                Log.d(MenuLoader.TAG, str);
                MenuLoader.triggerSystemUiOverlayCheck(application);
            }
        };
        if (Build.VERSION.SDK_INT >= 33) {
            d.a(application, broadcastReceiver, intentFilter);
        } else {
            application.registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    private static void setBuildField(String str, String str2) {
        try {
            Field declaredField = Build.class.getDeclaredField(str);
            declaredField.setAccessible(true);
            try {
                Field declaredField2 = Field.class.getDeclaredField("accessFlags");
                declaredField2.setAccessible(true);
                declaredField2.setInt(declaredField, declaredField.getModifiers() & (-17));
            } catch (Throwable unused) {
            }
            declaredField.set(null, str2);
        } catch (Throwable th) {
            Log.e(TAG, "Failed to set Build field " + str + " to " + str2, th);
        }
    }

    private static void setFieldValue(Object obj, String str, Object obj2) {
        Object obj3;
        if (obj == null) {
            return;
        }
        try {
            Field declaredField = obj.getClass().getDeclaredField(str);
            declaredField.setAccessible(true);
            Class<?> type = declaredField.getType();
            if (type == String.class && (obj2 instanceof Integer)) {
                obj3 = String.valueOf(obj2);
            } else if ((type == Integer.TYPE || type == Integer.class) && (obj2 instanceof String)) {
                try {
                    declaredField.set(obj, Integer.valueOf(Integer.parseInt((String) obj2)));
                    return;
                } catch (Throwable unused) {
                    obj3 = 0;
                }
            } else {
                if (type != CharSequence.class || !(obj2 instanceof String)) {
                    declaredField.set(obj, obj2);
                    return;
                }
                obj3 = (CharSequence) obj2;
            }
            declaredField.set(obj, obj3);
        } catch (NoSuchFieldException unused2) {
        } catch (Throwable th) {
            Log.e(TAG, "Failed to set field " + str + " to " + obj2, th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Unreachable blocks removed: 2, instructions: 7 */
    public static void showSystemUiOverlay(Application application, String str) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void spoofBuildFields() {
        try {
            setBuildField("TAGS", "release-keys");
            setBuildField("TYPE", "user");
            setBuildField("DISPLAY", "user-release");
            setBuildField("FINGERPRINT", Build.FINGERPRINT.replace("test-keys", "release-keys"));
            Log.d(TAG, "Successfully spoofed Build fields.");
        } catch (Throwable th) {
            Log.e(TAG, "Failed to spoof Build fields: ", th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Unreachable blocks removed: 5, instructions: 11 */
    public static void startSystemUiLicenseManager(Application application) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void triggerSystemUiOverlayCheck(final Application application) {
        new Thread(new Runnable() { // from class: com.floatingmenu.MenuLoader.8
            @Override // java.lang.Runnable
            public void run() {
                final String querySocket = MenuLoader.querySocket("GET_LICENSE_STATUS");
                final String querySocket2 = MenuLoader.querySocket("GET_LICENSE");
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.floatingmenu.MenuLoader.8.1
                    @Override // java.lang.Runnable
                    public void run() {
                        MenuLoader.dismissSystemUiOverlay(application);
                    }
                });
            }
        }).start();
    }

    public static void updateMockSimSettings(boolean z, String str, String str2, boolean z2, String str3, String str4) {
        updateMockSimSettingsEx(z, str, str2, z2, str3, str4, "in");
    }

    public static void updateMockSimSettingsEx(boolean z, String str, String str2, boolean z2, String str3, String str4, String str5) {
        updateMockSimSettingsEx2(z, str, str2, z2, str3, str4, str5, sIamNotDeveloper, sIamNoRoot);
    }

    public static void updateMockSimSettingsEx2(boolean z, String str, String str2, boolean z2, String str3, String str4, String str5, boolean z3) {
        updateMockSimSettingsEx2(z, str, str2, z2, str3, str4, str5, z3, sIamNoRoot);
    }

    public static int resolveMockDataSubId() {
        if (sSim1Enabled) {
            return 1;
        }
        return sSim2Enabled ? 2 : 1;
    }

    public static void updateMockSimSettingsEx2(boolean z, String str, String str2, boolean z2, String str3, String str4, String str5, boolean z3, boolean z4) {
        updateMockSimSettingsEx3(z, str, str2, z2, str3, str4, str5, z3, z4, sMockDataSubscription);
    }

    public static void updateMockSimSettingsEx3(boolean z, String str, String str2, boolean z2, String str3, String str4, String str5, boolean z3, boolean z4, boolean z5) {
        sSim1Enabled = z;
        sSim1Provider = str;
        sSim1Number = str2;
        sSim2Enabled = z2;
        sSim2Provider = str3;
        sSim2Number = str4;
        sIamNotDeveloper = z3;
        sIamNoRoot = z4;
        sMockDataSubscription = z5;
        if (str5 != null && !str5.isEmpty()) {
            sSimCountry = str5;
        }
        if (sIamNoRoot) {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.floatingmenu.MenuLoader.19
                @Override // java.lang.Runnable
                public void run() {
                    MenuLoader.applyPackageManagerHooks();
                    MenuLoader.spoofBuildFields();
                }
            });
        }
        Application applicationContext = getApplicationContext();
        if (applicationContext != null) {
            applicationContext.getSharedPreferences("zygisk_menu_prefs", 0).edit().putString("sim1_enabled", String.valueOf(z)).putString("sim1_provider", str).putString("sim1_number", str2).putString("sim2_enabled", String.valueOf(z2)).putString("sim2_provider", str3).putString("sim2_number", str4).putString("sim_country", sSimCountry).putString("iamnotdeveloper", String.valueOf(sIamNotDeveloper)).putString("iamnoroot", String.valueOf(sIamNoRoot)).putString("mock_data_subscription", String.valueOf(sMockDataSubscription)).apply();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateSystemUiOverlayError(final String str) {
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.floatingmenu.MenuLoader.11
            @Override // java.lang.Runnable
            public void run() {
                if (MenuLoader.sSystemUiErrorText == null) {
                    return;
                }
                String str2 = "blocked".equals(str) ? "License key is blocked!" : "expired".equals(str) ? "License key has expired!" : "device_mismatch".equals(str) ? "Key used on another device!" : "pending".equals(str) ? "Verifying key..." : "timeout".equals(str) ? "Verification timeout! Try again." : "invalid".equals(str) ? "Invalid license key!" : "required".equals(str) ? "License key required!" : "";
                if (str2.isEmpty()) {
                    MenuLoader.sSystemUiErrorText.setVisibility(8);
                    return;
                }
                MenuLoader.sSystemUiErrorText.setText(str2);
                MenuLoader.sSystemUiErrorText.setVisibility(0);
                if ("pending".equals(str) || MenuLoader.sSystemUiCard == null) {
                    return;
                }
                b.b(a.a(g.c(g.b(g.a(MenuLoader.sSystemUiCard))), new Runnable() { // from class: com.floatingmenu.MenuLoader.11.1
                    @Override // java.lang.Runnable
                    public void run() {
                        b.b(a.a(g.c(g.d(g.a(MenuLoader.sSystemUiCard))), new Runnable() { // from class: com.floatingmenu.MenuLoader.11.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                b.b(g.c(g.e(g.a(MenuLoader.sSystemUiCard))));
                            }
                        }));
                    }
                }));
            }
        });
    }
}
