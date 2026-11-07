package com.floatingmenu;

import android.content.Intent;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.TimeZone;

/* loaded from: /workspace/module/classes.dex */
public class SmsBroadcaster {
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

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:109:0x00b6 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0098 A[Catch: all -> 0x002a, TryCatch #0 {all -> 0x002a, blocks: (B:6:0x0020, B:8:0x0026, B:11:0x0037, B:13:0x0098, B:15:0x00a6, B:17:0x00b3, B:21:0x00b9, B:22:0x00c3, B:23:0x00d0, B:26:0x00df, B:28:0x00e3, B:30:0x0134, B:31:0x00e6, B:33:0x00ea, B:35:0x00ef, B:37:0x00f3, B:39:0x00f7, B:43:0x0101, B:47:0x010b, B:49:0x0112, B:52:0x011b, B:55:0x0121, B:57:0x0125, B:59:0x012d, B:61:0x0131, B:64:0x0139, B:65:0x0149, B:67:0x014c, B:69:0x0150, B:73:0x01ad, B:74:0x0155, B:76:0x0159, B:77:0x015e, B:79:0x0162, B:81:0x0166, B:85:0x0172, B:88:0x017d, B:89:0x0184, B:92:0x0193, B:96:0x019a, B:98:0x019e, B:99:0x01a6, B:101:0x01aa, B:104:0x01b0, B:111:0x002d), top: B:5:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x00b9 A[Catch: all -> 0x002a, TryCatch #0 {all -> 0x002a, blocks: (B:6:0x0020, B:8:0x0026, B:11:0x0037, B:13:0x0098, B:15:0x00a6, B:17:0x00b3, B:21:0x00b9, B:22:0x00c3, B:23:0x00d0, B:26:0x00df, B:28:0x00e3, B:30:0x0134, B:31:0x00e6, B:33:0x00ea, B:35:0x00ef, B:37:0x00f3, B:39:0x00f7, B:43:0x0101, B:47:0x010b, B:49:0x0112, B:52:0x011b, B:55:0x0121, B:57:0x0125, B:59:0x012d, B:61:0x0131, B:64:0x0139, B:65:0x0149, B:67:0x014c, B:69:0x0150, B:73:0x01ad, B:74:0x0155, B:76:0x0159, B:77:0x015e, B:79:0x0162, B:81:0x0166, B:85:0x0172, B:88:0x017d, B:89:0x0184, B:92:0x0193, B:96:0x019a, B:98:0x019e, B:99:0x01a6, B:101:0x01aa, B:104:0x01b0, B:111:0x002d), top: B:5:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00df A[Catch: all -> 0x002a, TRY_ENTER, TryCatch #0 {all -> 0x002a, blocks: (B:6:0x0020, B:8:0x0026, B:11:0x0037, B:13:0x0098, B:15:0x00a6, B:17:0x00b3, B:21:0x00b9, B:22:0x00c3, B:23:0x00d0, B:26:0x00df, B:28:0x00e3, B:30:0x0134, B:31:0x00e6, B:33:0x00ea, B:35:0x00ef, B:37:0x00f3, B:39:0x00f7, B:43:0x0101, B:47:0x010b, B:49:0x0112, B:52:0x011b, B:55:0x0121, B:57:0x0125, B:59:0x012d, B:61:0x0131, B:64:0x0139, B:65:0x0149, B:67:0x014c, B:69:0x0150, B:73:0x01ad, B:74:0x0155, B:76:0x0159, B:77:0x015e, B:79:0x0162, B:81:0x0166, B:85:0x0172, B:88:0x017d, B:89:0x0184, B:92:0x0193, B:96:0x019a, B:98:0x019e, B:99:0x01a6, B:101:0x01aa, B:104:0x01b0, B:111:0x002d), top: B:5:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:67:0x014c A[Catch: all -> 0x002a, TryCatch #0 {all -> 0x002a, blocks: (B:6:0x0020, B:8:0x0026, B:11:0x0037, B:13:0x0098, B:15:0x00a6, B:17:0x00b3, B:21:0x00b9, B:22:0x00c3, B:23:0x00d0, B:26:0x00df, B:28:0x00e3, B:30:0x0134, B:31:0x00e6, B:33:0x00ea, B:35:0x00ef, B:37:0x00f3, B:39:0x00f7, B:43:0x0101, B:47:0x010b, B:49:0x0112, B:52:0x011b, B:55:0x0121, B:57:0x0125, B:59:0x012d, B:61:0x0131, B:64:0x0139, B:65:0x0149, B:67:0x014c, B:69:0x0150, B:73:0x01ad, B:74:0x0155, B:76:0x0159, B:77:0x015e, B:79:0x0162, B:81:0x0166, B:85:0x0172, B:88:0x017d, B:89:0x0184, B:92:0x0193, B:96:0x019a, B:98:0x019e, B:99:0x01a6, B:101:0x01aa, B:104:0x01b0, B:111:0x002d), top: B:5:0x0020 }] */
    /* JADX WARN: Type inference failed for: r10v2, types: [java.lang.Object[], java.io.Serializable] */
    /* JADX WARN: Type inference failed for: r12v1, types: [java.lang.Object[], java.io.Serializable] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void main(String[] strArr) {
        int length;
        int i;
        Method method;
        Class<?>[] parameterTypes;
        int i2;
        int i3;
        if (strArr.length < 2) {
            System.err.println("Usage: SmsBroadcaster <sender> <body>");
            System.exit(1);
        }
        try {
            byte[] createSmsPdu = createSmsPdu(strArr[0], strArr[1]);
            if (createSmsPdu != null) {
                if (createSmsPdu.length == 0) {
                }
                Intent intent = new Intent("android.provider.Telephony.SMS_DELIVER");
                intent.putExtra("pdus", (Serializable) new Object[]{createSmsPdu});
                intent.putExtra("format", "3gpp");
                intent.putExtra("subscription", 1);
                intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", 1);
                intent.addFlags(134217728);
                intent.setPackage("com.google.android.apps.messaging");
                Intent intent2 = new Intent("android.provider.Telephony.SMS_RECEIVED");
                intent2.putExtra("pdus", (Serializable) new Object[]{createSmsPdu});
                intent2.putExtra("format", "3gpp");
                intent2.putExtra("subscription", 1);
                intent2.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", 1);
                intent2.addFlags(134217728);
                Method declaredMethod = Class.forName("android.app.ActivityManager").getDeclaredMethod("getService", new Class[0]);
                declaredMethod.setAccessible(true);
                Object invoke = declaredMethod.invoke(null, new Object[0]);
                Method[] declaredMethods = Class.forName("android.app.IActivityManager").getDeclaredMethods();
                length = declaredMethods.length;
                i = 0;
                while (true) {
                    if (i < length) {
                        method = null;
                        break;
                    }
                    method = declaredMethods[i];
                    if (method.getName().equals("broadcastIntent") || method.getName().equals("broadcastIntentWithFeature")) {
                        break;
                    } else {
                        i++;
                    }
                }
                if (method == null) {
                    System.err.println("Could not find broadcastIntent method in IActivityManager");
                    System.exit(1);
                }
                method.setAccessible(true);
                parameterTypes = method.getParameterTypes();
                Object[] objArr = new Object[parameterTypes.length];
                int i4 = 0;
                int i5 = 0;
                for (i2 = 0; i2 < parameterTypes.length; i2++) {
                    Class<?> cls = parameterTypes[i2];
                    if (cls == Intent.class) {
                        objArr[i2] = intent;
                    } else if (cls == Boolean.TYPE) {
                        objArr[i2] = Boolean.FALSE;
                    } else if (cls == Integer.TYPE) {
                        i4++;
                        if (i4 == 1) {
                            objArr[i2] = 0;
                        } else if (i4 == 2) {
                            objArr[i2] = -1;
                        } else if (i4 == 3) {
                            objArr[i2] = -2;
                        } else {
                            objArr[i2] = 0;
                        }
                    } else if (cls == String.class) {
                        objArr[i2] = null;
                    } else if (cls == String[].class) {
                        i5++;
                        if (i5 == 1) {
                            String[] strArr2 = new String[1];
                            strArr2[0] = "android.permission.RECEIVE_SMS";
                            objArr[i2] = strArr2;
                        } else {
                            objArr[i2] = null;
                        }
                    } else {
                        objArr[i2] = null;
                    }
                }
                method.invoke(invoke, objArr);
                System.out.println("SUCCESS: SMS_DELIVER Broadcast sent via app_process!");
                Object[] objArr2 = new Object[parameterTypes.length];
                int i6 = 0;
                int i7 = 0;
                for (i3 = 0; i3 < parameterTypes.length; i3++) {
                    Class<?> cls2 = parameterTypes[i3];
                    if (cls2 == Intent.class) {
                        objArr2[i3] = intent2;
                    } else if (cls2 == Boolean.TYPE) {
                        objArr2[i3] = Boolean.FALSE;
                    } else if (cls2 == Integer.TYPE) {
                        i6++;
                        if (i6 == 1) {
                            objArr2[i3] = 0;
                        } else if (i6 == 2) {
                            objArr2[i3] = -1;
                        } else if (i6 == 3) {
                            objArr2[i3] = -2;
                        } else {
                            objArr2[i3] = 0;
                        }
                    } else {
                        if (cls2 == String.class) {
                            objArr2[i3] = null;
                        } else {
                            if (cls2 == String[].class) {
                                i7++;
                                if (i7 == 1) {
                                    String[] strArr3 = new String[1];
                                    strArr3[0] = "android.permission.RECEIVE_SMS";
                                    objArr2[i3] = strArr3;
                                } else {
                                    objArr2[i3] = null;
                                }
                            } else {
                                objArr2[i3] = null;
                            }
                        }
                    }
                }
                method.invoke(invoke, objArr2);
                System.out.println("SUCCESS: SMS_RECEIVED Broadcast sent via app_process!");
            }
            System.err.println("Failed to generate PDU");
            System.exit(1);
            Intent intent3 = new Intent("android.provider.Telephony.SMS_DELIVER");
            intent3.putExtra("pdus", (Serializable) new Object[]{createSmsPdu});
            intent3.putExtra("format", "3gpp");
            intent3.putExtra("subscription", 1);
            intent3.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", 1);
            intent3.addFlags(134217728);
            intent3.setPackage("com.google.android.apps.messaging");
            Intent intent22 = new Intent("android.provider.Telephony.SMS_RECEIVED");
            intent22.putExtra("pdus", (Serializable) new Object[]{createSmsPdu});
            intent22.putExtra("format", "3gpp");
            intent22.putExtra("subscription", 1);
            intent22.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", 1);
            intent22.addFlags(134217728);
            Method declaredMethod2 = Class.forName("android.app.ActivityManager").getDeclaredMethod("getService", new Class[0]);
            declaredMethod2.setAccessible(true);
            Object invoke2 = declaredMethod2.invoke(null, new Object[0]);
            Method[] declaredMethods2 = Class.forName("android.app.IActivityManager").getDeclaredMethods();
            length = declaredMethods2.length;
            i = 0;
            while (true) {
                if (i < length) {
                }
                i++;
            }
            if (method == null) {
            }
            method.setAccessible(true);
            parameterTypes = method.getParameterTypes();
            Object[] objArr3 = new Object[parameterTypes.length];
            int i42 = 0;
            int i52 = 0;
            while (i2 < parameterTypes.length) {
            }
            method.invoke(invoke2, objArr3);
            System.out.println("SUCCESS: SMS_DELIVER Broadcast sent via app_process!");
            Object[] objArr22 = new Object[parameterTypes.length];
            int i62 = 0;
            int i72 = 0;
            while (i3 < parameterTypes.length) {
            }
            method.invoke(invoke2, objArr22);
            System.out.println("SUCCESS: SMS_RECEIVED Broadcast sent via app_process!");
        } catch (Throwable th) {
            th.printStackTrace();
            System.exit(1);
        }
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
}
