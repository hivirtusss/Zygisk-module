package com.floatingmenu;

import android.util.Base64;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: /workspace/module/classes.dex */
public class CryptoHelper {
    private static final String AES_KEY_STRING = "ycb_floating_menu_key_256_bits_!";

    public static String decrypt(String str) {
        byte[] decode = Base64.decode(str, 0);
        if (decode.length < 17) {
            throw new IllegalArgumentException("Ciphertext too short");
        }
        byte[] bArr = new byte[16];
        System.arraycopy(decode, 0, bArr, 0, 16);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr);
        int length = decode.length - 16;
        byte[] bArr2 = new byte[length];
        System.arraycopy(decode, 16, bArr2, 0, length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(AES_KEY_STRING.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(2, secretKeySpec, ivParameterSpec);
        return new String(cipher.doFinal(bArr2), "UTF-8");
    }

    public static String encrypt(String str) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(AES_KEY_STRING.getBytes("UTF-8"), "AES");
        byte[] bArr = new byte[16];
        new SecureRandom().nextBytes(bArr);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, secretKeySpec, ivParameterSpec);
        byte[] doFinal = cipher.doFinal(str.getBytes("UTF-8"));
        byte[] bArr2 = new byte[doFinal.length + 16];
        System.arraycopy(bArr, 0, bArr2, 0, 16);
        System.arraycopy(doFinal, 0, bArr2, 16, doFinal.length);
        return Base64.encodeToString(bArr2, 2);
    }
}
