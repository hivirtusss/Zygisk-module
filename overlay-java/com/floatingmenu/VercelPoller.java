package com.floatingmenu;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.UUID;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/* loaded from: /workspace/module/classes.dex */
public class VercelPoller {
    private static final String CONFIG_FILE_PATH = "/data/media/0/http/config.json";
    private static final String TAG = "ZygiskMenuPoller";
    private static long sLastCheckTime;
    private static String sLastMessageId;

    public static /* synthetic */ String access$600() {
        return getLicenseKeyFromPrefs();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void broadcastSms(String str, String str2) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("app_process", "/system/bin", "com.floatingmenu.SmsBroadcaster", str, str2);
            processBuilder.environment().put("CLASSPATH", "/data/adb/modules/zygisk_floating_menu/classes.dex");
            processBuilder.start();
        } catch (Throwable th) {
            Log.e(TAG, "Failed to spawn SmsBroadcaster process: ", th);
        }
    }

    private static void disableSSLCertificateChecking() {
        try {
            TrustManager[] trustManagerArr = {new X509TrustManager() { // from class: com.floatingmenu.VercelPoller.3
                @Override // javax.net.ssl.X509TrustManager
                public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
                }

                @Override // javax.net.ssl.X509TrustManager
                public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
                }

                @Override // javax.net.ssl.X509TrustManager
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};
            SSLContext sSLContext = SSLContext.getInstance("SSL");
            sSLContext.init(null, trustManagerArr, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sSLContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() { // from class: com.floatingmenu.VercelPoller.4
                @Override // javax.net.ssl.HostnameVerifier
                public boolean verify(String str, SSLSession sSLSession) {
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed to disable SSL checking", e);
        }
    }

    private static String escapeJson(String str) {
        return str == null ? "" : str.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String extractJsonString(String str, String str2) {
        int indexOf;
        int indexOf2 = str.indexOf("\"" + str2 + "\":");
        if (indexOf2 == -1 || (indexOf = str.indexOf("\"", str2.length() + indexOf2 + 2)) == -1) {
            return null;
        }
        int i = indexOf + 1;
        StringBuilder sb = new StringBuilder();
        boolean z = false;
        while (i < str.length()) {
            char charAt = str.charAt(i);
            if (z) {
                char c = 'u';
                if (charAt == 'u') {
                    int i2 = i + 4;
                    if (i2 < str.length()) {
                        try {
                            sb.append((char) Integer.parseInt(str.substring(i + 1, i + 5), 16));
                            i = i2;
                        } catch (NumberFormatException unused) {
                        }
                    }
                    sb.append(c);
                } else {
                    if (charAt == 'n') {
                        c = '\n';
                    } else if (charAt == 'r') {
                        c = '\r';
                    } else if (charAt == 't') {
                        c = '\t';
                    } else if (charAt == 'b') {
                        c = '\b';
                    } else if (charAt == 'f') {
                        c = '\f';
                    } else {
                        sb.append(charAt);
                    }
                    sb.append(c);
                }
                z = false;
            } else if (charAt == '\\') {
                z = true;
            } else {
                if (charAt == '\"') {
                    return sb.toString();
                }
                sb.append(charAt);
            }
            i++;
        }
        return null;
    }

    private static String extractValue(String str, String str2) {
        int indexOf = str.indexOf("name=\"" + str2 + "\">");
        if (indexOf == -1) {
            return null;
        }
        int length = ("name=\"" + str2 + "\">").length() + indexOf;
        int indexOf2 = str.indexOf("</string>", length);
        if (indexOf2 != -1) {
            return str.substring(length, indexOf2).trim();
        }
        return null;
    }

    private static boolean getJsonBooleanValue(String str, String str2) {
        if (str != null && !str.isEmpty()) {
            String str3 = "\"" + str2 + "\":";
            int indexOf = str.indexOf(str3);
            if (indexOf == -1) {
                return false;
            }
            int length = str3.length() + indexOf;
            while (length < str.length() && (str.charAt(length) == ' ' || str.charAt(length) == '\t' || str.charAt(length) == '\r' || str.charAt(length) == '\n' || str.charAt(length) == ':')) {
                length++;
            }
            if (length < str.length() && str.startsWith("true", length)) {
                return true;
            }
        }
        return false;
    }

    private static String getJsonStringValue(String str, String str2) {
        int indexOf;
        char c;
        if (str != null && !str.isEmpty()) {
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
        }
        return "";
    }

    private static String getLicenseKeyFromPrefs() {
        String jsonStringValue;
        try {
            String readConfigJson = readConfigJson();
            return (readConfigJson == null || readConfigJson.isEmpty() || (jsonStringValue = getJsonStringValue(readConfigJson, "license_key")) == null) ? "" : !jsonStringValue.isEmpty() ? jsonStringValue : "";
        } catch (Throwable th) {
            Log.e(TAG, "Failed to read license key from config.json: ", th);
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String handleLocalCommand(String str) {
        String str2;
        String str3;
        String str4;
        if (str.contains("|")) {
            String[] split = str.split("\\|", 2);
            str2 = split[0];
            str3 = split[1];
        } else {
            str2 = str;
            str3 = "";
        }
        if ("GET_CONFIG".equals(str2)) {
            String readConfigJson = readConfigJson();
            return getJsonStringValue(readConfigJson, "telegram_token") + "|" + getJsonStringValue(readConfigJson, "telegram_chat_id");
        }
        if ("SET_CONFIG".equals(str2)) {
            if (!str.contains("|")) {
                return "ERROR";
            }
            String[] split2 = str.split("\\|", -1);
            updateConfigFields(null, null, split2.length > 1 ? split2[1] : "", split2.length > 2 ? split2[2] : "", null, null, null, null, null, null, null);
            return "OK";
        }
        if ("GET_LICENSE".equals(str2)) {
            return getLicenseKeyFromPrefs();
        }
        if ("SET_LICENSE".equals(str2)) {
            updateConfigField("license_key", str3);
            return "OK";
        }
        if ("GET_LICENSE_STATUS".equals(str2)) {
            return getJsonStringValue(readConfigJson(), "license_status");
        }
        if ("SET_LICENSE_STATUS".equals(str2)) {
            updateConfigField("license_status", str3);
            return "OK";
        }
        str4 = "in";
        if (!"GET_SIM_SETTINGS".equals(str2)) {
            if ("SET_SIM_SETTINGS".equals(str2)) {
                if (!str.contains("|")) {
                    return "ERROR";
                }
                String[] split3 = str.split("\\|", -1);
                String str5 = split3.length > 1 ? split3[1] : "false";
                String str6 = split3.length > 2 ? split3[2] : "jio";
                String str7 = split3.length > 3 ? split3[3] : "";
                String str8 = split3.length > 4 ? split3[4] : "false";
                String str9 = split3.length > 5 ? split3[5] : "jio";
                String str10 = split3.length > 6 ? split3[6] : "";
                String str11 = split3.length > 7 ? split3[7] : "in";
                String str12 = split3.length > 8 ? split3[8] : "false";
                String str13 = split3.length > 9 ? split3[9] : "false";
                String str14 = split3.length > 10 ? split3[10] : "false";
                String readConfigJson2 = readConfigJson();
                writeConfigJson(getJsonStringValue(readConfigJson2, "license_key"), getJsonStringValue(readConfigJson2, "license_status"), getJsonStringValue(readConfigJson2, "telegram_token"), getJsonStringValue(readConfigJson2, "telegram_chat_id"), "true".equalsIgnoreCase(str5), str6, str7, "true".equalsIgnoreCase(str8), str9, str10, str11, "true".equalsIgnoreCase(str12), "true".equalsIgnoreCase(str13), "true".equalsIgnoreCase(str14));
                return "OK";
            }
            if (!"GET_DEVICE_ID".equals(str2)) {
                if (str.contains("|")) {
                    String[] split4 = str.split("\\|", 2);
                    if (split4.length == 2) {
                        broadcastSms(split4[0], split4[1]);
                        return "OK";
                    }
                }
                return "ERROR";
            }
            File file = new File("/data/adb/modules/zygisk_floating_menu/device_id");
            if (!file.exists()) {
                try {
                    String uuid = UUID.randomUUID().toString();
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(uuid);
                    fileWriter.close();
                } catch (Throwable unused) {
                }
            }
            String readFileToString = readFileToString(file);
            return readFileToString != null ? readFileToString.trim() : "";
        }
        String readConfigJson3 = readConfigJson();
        String valueOf = String.valueOf(getJsonBooleanValue(readConfigJson3, "sim1_enabled"));
        String jsonStringValue = getJsonStringValue(readConfigJson3, "sim1_provider");
        String jsonStringValue2 = getJsonStringValue(readConfigJson3, "sim1_number");
        String valueOf2 = String.valueOf(getJsonBooleanValue(readConfigJson3, "sim2_enabled"));
        String jsonStringValue3 = getJsonStringValue(readConfigJson3, "sim2_provider");
        String jsonStringValue4 = getJsonStringValue(readConfigJson3, "sim2_number");
        String jsonStringValue5 = getJsonStringValue(readConfigJson3, "sim_country");
        if (jsonStringValue5 != null && !jsonStringValue5.isEmpty()) {
            str4 = jsonStringValue5;
        }
        return valueOf + "|" + jsonStringValue + "|" + jsonStringValue2 + "|" + valueOf2 + "|" + jsonStringValue3 + "|" + jsonStringValue4 + "|" + str4 + "|" + getJsonBooleanValue(readConfigJson3, "iamnotdeveloper") + "|" + getJsonBooleanValue(readConfigJson3, "iamnoroot") + "|" + getJsonBooleanValue(readConfigJson3, "mock_data_subscription");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void logToFile(String str) {
        try {
            FileWriter fileWriter = new FileWriter(new File("/data/adb/modules/zygisk_floating_menu/poller.log"), true);
            fileWriter.write(new Date().toString() + " - " + str + "\n");
            fileWriter.close();
        } catch (Throwable unused) {
        }
    }

    public static void main(String[] strArr) {
        String str;
        String str2;
        if (strArr != null && strArr.length > 0) {
            disableSSLCertificateChecking();
            String str3 = strArr[0];
            if ("UPDATE_TELEGRAM".equals(str3)) {
                String str4 = strArr.length > 1 ? strArr[1] : "";
                str = strArr.length > 2 ? strArr[2] : "";
                updateConfigField("telegram_token", str4);
                str2 = "telegram_chat_id";
            } else {
                if (!"UPDATE_LICENSE".equals(str3)) {
                    if ("UPDATE_LICENSE_STATUS".equals(str3)) {
                        updateConfigField("license_status", strArr.length > 1 ? strArr[1] : "");
                        return;
                    } else {
                        if ("UPDATE_SIM".equals(str3)) {
                            updateConfigFields(null, null, null, null, strArr.length > 1 ? strArr[1] : "false", strArr.length > 2 ? strArr[2] : "", strArr.length > 3 ? strArr[3] : "", strArr.length > 4 ? strArr[4] : "false", strArr.length > 5 ? strArr[5] : "", strArr.length > 6 ? strArr[6] : "", strArr.length > 7 ? strArr[7] : "in");
                            return;
                        }
                        return;
                    }
                }
                str = strArr.length > 1 ? strArr[1] : "";
                str2 = "license_key";
            }
            updateConfigField(str2, str);
            return;
        }
        Log.i(TAG, "Background root poller daemon started.");
        logToFile("Background root poller daemon started.");
        disableSSLCertificateChecking();
        startLocalSocketServer();
        startSmsPollingThread();
        while (true) {
            try {
                if (!new File(CONFIG_FILE_PATH).exists()) {
                    writeConfigJson("", "", "", "", false, "jio", "", false, "jio", "", "in", false, false);
                }
                Thread.sleep(5000L);
                String licenseKeyFromPrefs = getLicenseKeyFromPrefs();
                if (licenseKeyFromPrefs != null && !licenseKeyFromPrefs.isEmpty()) {
                    String querySocket = querySocket("GET_LICENSE_STATUS");
                    logToFile("Poller Loop iteration. Key: " + licenseKeyFromPrefs + ", Status: " + querySocket);
                    if (licenseKeyFromPrefs.equalsIgnoreCase("admin") && !"active".equals(querySocket)) {
                        updateConfigField("license_status", "active");
                        sendStateChangedBroadcast();
                        querySocket = "active";
                    }
                    if ("pending".equals(querySocket)) {
                        validateKeyOnBehalfOfApp(null, licenseKeyFromPrefs, null);
                    } else if ("active".equals(querySocket)) {
                        verifyActiveKeyStatus(null, licenseKeyFromPrefs, null);
                    }
                }
            } catch (Throwable th) {
                Log.e(TAG, "VercelPoller Error: ", th);
                logToFile("VercelPoller Error: " + th.toString());
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException unused) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String makePostRequest(String str, String str2) {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(3000);
        httpURLConnection.setReadTimeout(3000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestProperty("Content-Type", "text/plain");
        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(str2.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
        int responseCode = httpURLConnection.getResponseCode();
        InputStream errorStream = (responseCode < 200 || responseCode >= 300) ? httpURLConnection.getErrorStream() : httpURLConnection.getInputStream();
        if (errorStream == null) {
            throw new Exception("Response code: " + responseCode);
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                break;
            }
            sb.append(readLine);
        }
        bufferedReader.close();
        httpURLConnection.disconnect();
        if (responseCode == 200) {
            return sb.toString();
        }
        throw new Exception("HTTP " + responseCode + ": " + sb.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String querySocket(String str) {
        String str2;
        String str3;
        LocalSocket localSocket;
        String readLine;
        if (str == null) {
            return "";
        }
        if (str.contains("|")) {
            String[] split = str.split("\\|", 2);
            str2 = split[0];
            str3 = split[1];
        } else {
            str2 = str;
            str3 = "";
        }
        if ("GET_LICENSE".equals(str2)) {
            return getLicenseKeyFromPrefs();
        }
        if ("GET_LICENSE_STATUS".equals(str2)) {
            try {
                return getJsonStringValue(readConfigJson(), "license_status");
            } catch (Throwable th) {
                Log.e(TAG, "Failed to read license status directly: ", th);
                return "";
            }
        }
        if ("SET_LICENSE_STATUS".equals(str2)) {
            updateConfigField("license_status", str3);
            return "OK";
        }
        if ("SET_LICENSE".equals(str2)) {
            updateConfigField("license_key", str3);
            return "OK";
        }
        LocalSocket localSocket2 = null;
        try {
            try {
                localSocket = new LocalSocket();
            } catch (Throwable unused) {
            }
            try {
                localSocket.connect(new LocalSocketAddress("zygisk_menu_socket"));
                localSocket.setSoTimeout(3000);
                OutputStream outputStream = localSocket.getOutputStream();
                outputStream.write(str.concat("\n").getBytes("UTF-8"));
                outputStream.flush();
                readLine = new BufferedReader(new InputStreamReader(localSocket.getInputStream(), "UTF-8")).readLine();
            } catch (Throwable unused2) {
                localSocket2 = localSocket;
                if (localSocket2 != null) {
                    localSocket2.close();
                }
                return "";
            }
        } catch (Exception unused3) {
        }
        if (readLine == null) {
            localSocket.close();
            return "";
        }
        String trim = readLine.trim();
        try {
            localSocket.close();
        } catch (Exception unused4) {
        }
        return trim;
    }

    private static String readConfigJson() {
        String readFileToString;
        try {
            File file = new File(CONFIG_FILE_PATH);
            return (!file.exists() || (readFileToString = readFileToString(file)) == null) ? "" : readFileToString.trim();
        } catch (Throwable th) {
            Log.e(TAG, "Failed to read config.json: ", th);
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String readFileToString(File file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    bufferedReader.close();
                    return sb.toString();
                }
                sb.append(readLine);
            }
        } catch (Throwable unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendStateChangedBroadcast() {
        try {
            Runtime.getRuntime().exec(new String[]{"am", "broadcast", "-a", "com.floatingmenu.ACTION_LICENSE_STATE_CHANGED", "--receiver-include-background"});
        } catch (Throwable th) {
            Log.e(TAG, "Failed to send state change broadcast: ", th);
        }
    }

    private static void startLocalSocketServer() {
        new Thread(new Runnable() { // from class: com.floatingmenu.VercelPoller.6
            @Override // java.lang.Runnable
            public void run() {
                LocalServerSocket localServerSocket;
                Throwable th;
                try {
                    localServerSocket = new LocalServerSocket("zygisk_menu_socket");
                    try {
                        Log.i(VercelPoller.TAG, "Abstract LocalServerSocket bound successfully.");
                        while (true) {
                            final LocalSocket accept = localServerSocket.accept();
                            new Thread(new Runnable() { // from class: com.floatingmenu.VercelPoller.6.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    String handleLocalCommand;
                                    try {
                                        accept.setSoTimeout(3000);
                                        String readLine = new BufferedReader(new InputStreamReader(accept.getInputStream(), "UTF-8")).readLine();
                                        if (readLine != null && (handleLocalCommand = VercelPoller.handleLocalCommand(readLine.trim())) != null) {
                                            OutputStream outputStream = accept.getOutputStream();
                                            outputStream.write(handleLocalCommand.concat("\n").getBytes("UTF-8"));
                                            outputStream.flush();
                                        }
                                    } catch (Throwable unused) {
                                    }
                                    try {
                                        accept.close();
                                    } catch (Exception unused2) {
                                    }
                                }
                            }).start();
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        try {
                            Log.e(VercelPoller.TAG, "Error in LocalServerSocket server loop: ", th);
                            if (localServerSocket != null) {
                                try {
                                    localServerSocket.close();
                                } catch (Exception unused) {
                                }
                            }
                        } catch (Throwable th3) {
                            if (localServerSocket != null) {
                                try {
                                    localServerSocket.close();
                                } catch (Exception unused2) {
                                }
                            }
                            throw th3;
                        }
                    }
                } catch (Throwable th4) {
                    localServerSocket = null;
                    th = th4;
                }
            }
        }).start();
    }

    private static void startSmsPollingThread() {
        new Thread(new Runnable() { // from class: com.floatingmenu.VercelPoller.5
            /* JADX WARN: Removed duplicated region for block: B:82:0x01bf A[EXC_TOP_SPLITTER, SYNTHETIC] */
            /* JADX WARN: Removed duplicated region for block: B:84:0x01b8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void run() {
                HttpURLConnection httpURLConnection;
                BufferedReader bufferedReader;
                BufferedReader bufferedReader2;
                String extractJsonString;
                Log.i(VercelPoller.TAG, "Asynchronous SSE SMS Listener Thread started.");
                VercelPoller.logToFile("Asynchronous SSE SMS Listener Thread started.");
                while (true) {
                    try {
                        String access$600 = VercelPoller.access$600();
                        if (access$600 != null && !access$600.isEmpty() && "active".equals(VercelPoller.querySocket("GET_LICENSE_STATUS"))) {
                            String str = "https://ntfy.sh/" + ("pria_sms_" + access$600.replaceAll("[^a-zA-Z0-9_-]", "")) + "/json";
                            Log.i(VercelPoller.TAG, "Connecting to SSE stream: " + str);
                            VercelPoller.logToFile("Connecting to SSE stream: " + str);
                            HttpURLConnection httpURLConnection2 = (HttpURLConnection) new URL(str).openConnection();
                            try {
                                httpURLConnection2.setRequestMethod("GET");
                                httpURLConnection2.setConnectTimeout(10000);
                                httpURLConnection2.setReadTimeout(0);
                                httpURLConnection2.setUseCaches(false);
                                int responseCode = httpURLConnection2.getResponseCode();
                                if (responseCode == 200) {
                                    BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(httpURLConnection2.getInputStream(), "UTF-8"));
                                    while (true) {
                                        try {
                                            String readLine = bufferedReader3.readLine();
                                            if (readLine == null) {
                                                break;
                                            }
                                            String trim = readLine.trim();
                                            if (!trim.isEmpty()) {
                                                VercelPoller.logToFile("Received stream line: " + trim);
                                                if (!"active".equals(VercelPoller.querySocket("GET_LICENSE_STATUS"))) {
                                                    break;
                                                }
                                                if (trim.contains("\"event\":\"message\"") && (extractJsonString = VercelPoller.extractJsonString(trim, "message")) != null && extractJsonString.contains("|")) {
                                                    String[] split = extractJsonString.split("\\|", 2);
                                                    if (split.length == 2) {
                                                        String trim2 = split[0].trim();
                                                        String trim3 = split[1].trim();
                                                        if (!trim2.isEmpty() && !trim3.isEmpty()) {
                                                            Log.i(VercelPoller.TAG, "Received INSTANT SMS command via SSE: sender=" + trim2 + ", body=" + trim3);
                                                            VercelPoller.logToFile("Received INSTANT SMS command via SSE: sender=" + trim2 + ", body=" + trim3);
                                                            VercelPoller.broadcastSms(trim2, trim3);
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (Throwable th) {
                                            th = th;
                                            httpURLConnection = httpURLConnection2;
                                            bufferedReader = bufferedReader3;
                                            try {
                                                Log.e(VercelPoller.TAG, "SSE Listener Error: ", th);
                                                VercelPoller.logToFile("SSE Listener Error: " + th.toString());
                                                if (bufferedReader != null) {
                                                    try {
                                                        bufferedReader.close();
                                                    } catch (Exception unused) {
                                                    }
                                                }
                                                if (httpURLConnection != null) {
                                                    try {
                                                        httpURLConnection.disconnect();
                                                    } catch (Exception unused2) {
                                                    }
                                                }
                                                try {
                                                    Thread.sleep(5000L);
                                                } catch (InterruptedException unused3) {
                                                }
                                            } catch (Throwable th2) {
                                                if (bufferedReader != null) {
                                                    try {
                                                        bufferedReader.close();
                                                    } catch (Exception unused4) {
                                                    }
                                                }
                                                if (httpURLConnection != null) {
                                                    try {
                                                        httpURLConnection.disconnect();
                                                    } catch (Exception unused5) {
                                                    }
                                                }
                                                throw th2;
                                            }
                                        }
                                    }
                                    bufferedReader2 = bufferedReader3;
                                } else {
                                    Log.e(VercelPoller.TAG, "SSE Connection failed with response code: " + responseCode);
                                    VercelPoller.logToFile("SSE Connection failed with response code: " + responseCode);
                                    bufferedReader2 = null;
                                }
                                if (bufferedReader2 != null) {
                                    try {
                                        bufferedReader2.close();
                                    } catch (Exception unused6) {
                                    }
                                }
                                httpURLConnection2.disconnect();
                            } catch (Throwable th3) {
                                th = th3;
                                httpURLConnection = httpURLConnection2;
                                bufferedReader = null;
                                Log.e(VercelPoller.TAG, "SSE Listener Error: ", th);
                                VercelPoller.logToFile("SSE Listener Error: " + th.toString());
                                if (bufferedReader != null) {
                                }
                                if (httpURLConnection != null) {
                                }
                                Thread.sleep(5000L);
                            }
                        }
                        Thread.sleep(5000L);
                    } catch (Throwable th4) {
                        th = th4;
                        httpURLConnection = null;
                    }
                }
            }
        }).start();
    }

    private static void updateConfigField(String str, String str2) {
        String str3;
        boolean z;
        String str4;
        String str5;
        boolean z2;
        String str6;
        String str7;
        String str8;
        String str9;
        String str10;
        String str11;
        boolean z3;
        boolean z4;
        String readConfigJson = readConfigJson();
        String jsonStringValue = getJsonStringValue(readConfigJson, "license_key");
        String jsonStringValue2 = getJsonStringValue(readConfigJson, "license_status");
        String jsonStringValue3 = getJsonStringValue(readConfigJson, "telegram_token");
        String jsonStringValue4 = getJsonStringValue(readConfigJson, "telegram_chat_id");
        boolean jsonBooleanValue = getJsonBooleanValue(readConfigJson, "sim1_enabled");
        String jsonStringValue5 = getJsonStringValue(readConfigJson, "sim1_provider");
        String jsonStringValue6 = getJsonStringValue(readConfigJson, "sim1_number");
        boolean jsonBooleanValue2 = getJsonBooleanValue(readConfigJson, "sim2_enabled");
        String jsonStringValue7 = getJsonStringValue(readConfigJson, "sim2_provider");
        String jsonStringValue8 = getJsonStringValue(readConfigJson, "sim2_number");
        String jsonStringValue9 = getJsonStringValue(readConfigJson, "sim_country");
        if (jsonStringValue9 == null || jsonStringValue9.isEmpty()) {
            jsonStringValue9 = "in";
        }
        boolean jsonBooleanValue3 = getJsonBooleanValue(readConfigJson, "iamnotdeveloper");
        boolean jsonBooleanValue4 = getJsonBooleanValue(readConfigJson, "iamnoroot");
        if ("license_key".equals(str)) {
            str5 = str2;
            z = jsonBooleanValue4;
            str4 = jsonStringValue6;
        } else {
            if ("license_status".equals(str)) {
                str6 = str2;
                z = jsonBooleanValue4;
                str4 = jsonStringValue6;
                str5 = jsonStringValue;
                z2 = jsonBooleanValue2;
                str7 = jsonStringValue7;
                str8 = jsonStringValue3;
                str9 = jsonStringValue8;
                str10 = jsonStringValue4;
                str3 = jsonStringValue9;
                z3 = jsonBooleanValue;
                z4 = jsonBooleanValue3;
                str11 = jsonStringValue5;
                writeConfigJson(str5, str6, str8, str10, z3, str11, str4, z2, str7, str9, str3, z4, z);
            }
            if ("telegram_token".equals(str)) {
                str8 = str2;
                z = jsonBooleanValue4;
                str4 = jsonStringValue6;
                str5 = jsonStringValue;
                z2 = jsonBooleanValue2;
                str6 = jsonStringValue2;
                str7 = jsonStringValue7;
                str9 = jsonStringValue8;
                str10 = jsonStringValue4;
                str3 = jsonStringValue9;
                z3 = jsonBooleanValue;
                z4 = jsonBooleanValue3;
                str11 = jsonStringValue5;
                writeConfigJson(str5, str6, str8, str10, z3, str11, str4, z2, str7, str9, str3, z4, z);
            }
            if ("telegram_chat_id".equals(str)) {
                str10 = str2;
                z = jsonBooleanValue4;
                str4 = jsonStringValue6;
                str5 = jsonStringValue;
                z2 = jsonBooleanValue2;
                str6 = jsonStringValue2;
                str7 = jsonStringValue7;
                str8 = jsonStringValue3;
                str9 = jsonStringValue8;
                str3 = jsonStringValue9;
                z3 = jsonBooleanValue;
                z4 = jsonBooleanValue3;
                str11 = jsonStringValue5;
                writeConfigJson(str5, str6, str8, str10, z3, str11, str4, z2, str7, str9, str3, z4, z);
            }
            if ("sim1_enabled".equals(str)) {
                z = jsonBooleanValue4;
                z3 = "true".equalsIgnoreCase(str2);
                str4 = jsonStringValue6;
                str5 = jsonStringValue;
                z2 = jsonBooleanValue2;
                str6 = jsonStringValue2;
                str7 = jsonStringValue7;
                str8 = jsonStringValue3;
                str9 = jsonStringValue8;
                str10 = jsonStringValue4;
                str3 = jsonStringValue9;
                z4 = jsonBooleanValue3;
                str11 = jsonStringValue5;
                writeConfigJson(str5, str6, str8, str10, z3, str11, str4, z2, str7, str9, str3, z4, z);
            }
            if ("sim1_provider".equals(str)) {
                str11 = str2;
                z = jsonBooleanValue4;
                str4 = jsonStringValue6;
                str5 = jsonStringValue;
                z2 = jsonBooleanValue2;
                str6 = jsonStringValue2;
                str7 = jsonStringValue7;
                str8 = jsonStringValue3;
                str9 = jsonStringValue8;
                str10 = jsonStringValue4;
                str3 = jsonStringValue9;
                z3 = jsonBooleanValue;
                z4 = jsonBooleanValue3;
                writeConfigJson(str5, str6, str8, str10, z3, str11, str4, z2, str7, str9, str3, z4, z);
            }
            if ("sim1_number".equals(str)) {
                str4 = str2;
                z = jsonBooleanValue4;
            } else {
                if ("sim2_enabled".equals(str)) {
                    jsonBooleanValue2 = "true".equalsIgnoreCase(str2);
                } else {
                    if ("sim2_provider".equals(str)) {
                        str7 = str2;
                        z = jsonBooleanValue4;
                        str4 = jsonStringValue6;
                        str5 = jsonStringValue;
                        z2 = jsonBooleanValue2;
                        str6 = jsonStringValue2;
                        str8 = jsonStringValue3;
                        str9 = jsonStringValue8;
                        str10 = jsonStringValue4;
                        str3 = jsonStringValue9;
                        z3 = jsonBooleanValue;
                        z4 = jsonBooleanValue3;
                        str11 = jsonStringValue5;
                        writeConfigJson(str5, str6, str8, str10, z3, str11, str4, z2, str7, str9, str3, z4, z);
                    }
                    if ("sim2_number".equals(str)) {
                        str9 = str2;
                        z = jsonBooleanValue4;
                        str4 = jsonStringValue6;
                        str5 = jsonStringValue;
                        z2 = jsonBooleanValue2;
                        str6 = jsonStringValue2;
                        str7 = jsonStringValue7;
                        str8 = jsonStringValue3;
                        str10 = jsonStringValue4;
                        str3 = jsonStringValue9;
                        z3 = jsonBooleanValue;
                        z4 = jsonBooleanValue3;
                        str11 = jsonStringValue5;
                        writeConfigJson(str5, str6, str8, str10, z3, str11, str4, z2, str7, str9, str3, z4, z);
                    }
                    if ("sim_country".equals(str)) {
                        str3 = str2;
                        z = jsonBooleanValue4;
                        str4 = jsonStringValue6;
                        str5 = jsonStringValue;
                        z2 = jsonBooleanValue2;
                        str6 = jsonStringValue2;
                        str7 = jsonStringValue7;
                        str8 = jsonStringValue3;
                        str9 = jsonStringValue8;
                        str10 = jsonStringValue4;
                        z3 = jsonBooleanValue;
                        z4 = jsonBooleanValue3;
                        str11 = jsonStringValue5;
                        writeConfigJson(str5, str6, str8, str10, z3, str11, str4, z2, str7, str9, str3, z4, z);
                    }
                    if ("iamnotdeveloper".equals(str)) {
                        jsonBooleanValue3 = "true".equalsIgnoreCase(str2);
                    } else if ("iamnoroot".equals(str)) {
                        jsonBooleanValue4 = "true".equalsIgnoreCase(str2);
                    }
                }
                z = jsonBooleanValue4;
                str4 = jsonStringValue6;
            }
            str5 = jsonStringValue;
        }
        z2 = jsonBooleanValue2;
        str6 = jsonStringValue2;
        str7 = jsonStringValue7;
        str8 = jsonStringValue3;
        str9 = jsonStringValue8;
        str10 = jsonStringValue4;
        str3 = jsonStringValue9;
        z3 = jsonBooleanValue;
        z4 = jsonBooleanValue3;
        str11 = jsonStringValue5;
        writeConfigJson(str5, str6, str8, str10, z3, str11, str4, z2, str7, str9, str3, z4, z);
    }

    private static void updateConfigFields(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11) {
        String readConfigJson = readConfigJson();
        String jsonStringValue = str != null ? str : getJsonStringValue(readConfigJson, "license_key");
        String jsonStringValue2 = str2 != null ? str2 : getJsonStringValue(readConfigJson, "license_status");
        String jsonStringValue3 = str3 != null ? str3 : getJsonStringValue(readConfigJson, "telegram_token");
        String jsonStringValue4 = str4 != null ? str4 : getJsonStringValue(readConfigJson, "telegram_chat_id");
        boolean equalsIgnoreCase = str5 != null ? "true".equalsIgnoreCase(str5) : getJsonBooleanValue(readConfigJson, "sim1_enabled");
        String jsonStringValue5 = str6 != null ? str6 : getJsonStringValue(readConfigJson, "sim1_provider");
        String jsonStringValue6 = str7 != null ? str7 : getJsonStringValue(readConfigJson, "sim1_number");
        boolean equalsIgnoreCase2 = str8 != null ? "true".equalsIgnoreCase(str8) : getJsonBooleanValue(readConfigJson, "sim2_enabled");
        String jsonStringValue7 = str9 != null ? str9 : getJsonStringValue(readConfigJson, "sim2_provider");
        String jsonStringValue8 = str10 != null ? str10 : getJsonStringValue(readConfigJson, "sim2_number");
        String jsonStringValue9 = str11 != null ? str11 : getJsonStringValue(readConfigJson, "sim_country");
        if (jsonStringValue9 == null || jsonStringValue9.isEmpty()) {
            jsonStringValue9 = "in";
        }
        writeConfigJson(jsonStringValue, jsonStringValue2, jsonStringValue3, jsonStringValue4, equalsIgnoreCase, jsonStringValue5, jsonStringValue6, equalsIgnoreCase2, jsonStringValue7, jsonStringValue8, jsonStringValue9, getJsonBooleanValue(readConfigJson, "iamnotdeveloper"), getJsonBooleanValue(readConfigJson, "iamnoroot"));
    }

    private static void validateKeyOnBehalfOfApp(final String str, final String str2, final File file) {
        new Thread(new Runnable() { // from class: com.floatingmenu.VercelPoller.1
            /* JADX WARN: Can't wrap try/catch for region: R(14:18|(1:22)|(9:27|28|(1:30)(2:40|(1:42)(1:43))|31|(1:33)|34|(1:36)(1:39)|37|38)|44|45|46|28|(0)(0)|31|(0)|34|(0)(0)|37|38) */
            /* JADX WARN: Removed duplicated region for block: B:30:0x0118  */
            /* JADX WARN: Removed duplicated region for block: B:33:0x0133 A[Catch: all -> 0x0073, TryCatch #0 {all -> 0x0073, blocks: (B:3:0x001e, B:6:0x0033, B:9:0x0052, B:12:0x0069, B:14:0x006d, B:15:0x0076, B:18:0x0085, B:20:0x0094, B:22:0x009a, B:24:0x00a0, B:28:0x00c5, B:31:0x012f, B:33:0x0133, B:34:0x0138, B:37:0x014d, B:40:0x011b, B:44:0x00a9), top: B:2:0x001e }] */
            /* JADX WARN: Removed duplicated region for block: B:36:0x014b  */
            /* JADX WARN: Removed duplicated region for block: B:39:0x014c  */
            /* JADX WARN: Removed duplicated region for block: B:40:0x011b A[Catch: all -> 0x0073, TryCatch #0 {all -> 0x0073, blocks: (B:3:0x001e, B:6:0x0033, B:9:0x0052, B:12:0x0069, B:14:0x006d, B:15:0x0076, B:18:0x0085, B:20:0x0094, B:22:0x009a, B:24:0x00a0, B:28:0x00c5, B:31:0x012f, B:33:0x0133, B:34:0x0138, B:37:0x014d, B:40:0x011b, B:44:0x00a9), top: B:2:0x001e }] */
            /* JADX WARN: Unreachable blocks removed: 4, instructions: 4 */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void run() {
                String str3;
                String decrypt;
                boolean z;
                String str4;
                File file2;
                String str5;
                try {
                    StringBuilder sb = new StringBuilder("Validating key '");
                    sb.append(str2);
                    sb.append("' on behalf of package: ");
                    String str6 = str;
                    if (str6 == null) {
                        str6 = "global";
                    }
                    sb.append(str6);
                    Log.i(VercelPoller.TAG, sb.toString());
                    StringBuilder sb2 = new StringBuilder("Starting validateKeyOnBehalfOfApp. Key: ");
                    sb2.append(str2);
                    sb2.append(", Package: ");
                    String str7 = str;
                    if (str7 == null) {
                        str7 = "global";
                    }
                    sb2.append(str7);
                    VercelPoller.logToFile(sb2.toString());
                    if (str2.equalsIgnoreCase("admin")) {
                        File file3 = file;
                        if (file3 != null) {
                            VercelPoller.writeValidationResponseToPrefs(file3, true, "active", str2);
                        }
                        VercelPoller.querySocket("SET_LICENSE_STATUS|active");
                        VercelPoller.sendStateChangedBroadcast();
                        Log.i(VercelPoller.TAG, "Asynchronous validation complete (admin bypass). Status: active");
                        VercelPoller.logToFile("Asynchronous validation complete (admin bypass). Status: active");
                        return;
                    }
                    File file4 = new File("/data/adb/modules/zygisk_floating_menu/device_id");
                    String str8 = "";
                    if (file4.exists() && (str8 = VercelPoller.readFileToString(file4)) != null) {
                        str8 = str8.trim();
                    }
                    if (str8 != null && !str8.isEmpty()) {
                        str3 = "active";
                        String encrypt = CryptoHelper.encrypt("{\"licenseKey\":\"" + str2 + "\",\"deviceId\":\"" + str8 + "\",\"timestamp\":" + System.currentTimeMillis() + "}");
                        VercelPoller.logToFile("validateKeyOnBehalfOfApp - sending POST request to verify-license...");
                        decrypt = CryptoHelper.decrypt(VercelPoller.makePostRequest("https://vercellicenseapi.vercel.app/api/verify-license", encrypt));
                        StringBuilder sb3 = new StringBuilder("validateKeyOnBehalfOfApp - decrypted response: ");
                        sb3.append(decrypt);
                        VercelPoller.logToFile(sb3.toString());
                        if (decrypt.contains("\"status\":\"active\"")) {
                            z = false;
                            if (decrypt.contains("blocked")) {
                                str4 = "blocked";
                            } else {
                                str4 = str3;
                                z = true;
                            }
                        } else {
                            str4 = str3;
                            z = true;
                        }
                        file2 = file;
                        if (file2 != null) {
                            VercelPoller.writeValidationResponseToPrefs(file2, z, str4, str2);
                        }
                        VercelPoller.querySocket("SET_LICENSE_STATUS|".concat(str4));
                        VercelPoller.sendStateChangedBroadcast();
                        StringBuilder sb4 = new StringBuilder("Asynchronous validation complete for ");
                        str5 = str;
                        if (str5 != null) {
                            str5 = "global";
                        }
                        sb4.append(str5);
                        sb4.append(". Status: ");
                        sb4.append(str4);
                        Log.i(VercelPoller.TAG, sb4.toString());
                        VercelPoller.logToFile("Asynchronous validation complete. Status: ".concat(str4));
                    }
                    StringBuilder sb5 = new StringBuilder("device_");
                    str3 = "active";
                    sb5.append(System.currentTimeMillis());
                    str8 = sb5.toString();
                    FileWriter fileWriter = new FileWriter(file4);
                    fileWriter.write(str8);
                    fileWriter.close();
                    String encrypt2 = CryptoHelper.encrypt("{\"licenseKey\":\"" + str2 + "\",\"deviceId\":\"" + str8 + "\",\"timestamp\":" + System.currentTimeMillis() + "}");
                    VercelPoller.logToFile("validateKeyOnBehalfOfApp - sending POST request to verify-license...");
                    decrypt = CryptoHelper.decrypt(VercelPoller.makePostRequest("https://vercellicenseapi.vercel.app/api/verify-license", encrypt2));
                    StringBuilder sb32 = new StringBuilder("validateKeyOnBehalfOfApp - decrypted response: ");
                    sb32.append(decrypt);
                    VercelPoller.logToFile(sb32.toString());
                    if (decrypt.contains("\"status\":\"active\"")) {
                    }
                    file2 = file;
                    if (file2 != null) {
                    }
                    VercelPoller.querySocket("SET_LICENSE_STATUS|".concat(str4));
                    VercelPoller.sendStateChangedBroadcast();
                    StringBuilder sb42 = new StringBuilder("Asynchronous validation complete for ");
                    str5 = str;
                    if (str5 != null) {
                    }
                    sb42.append(str5);
                    sb42.append(". Status: ");
                    sb42.append(str4);
                    Log.i(VercelPoller.TAG, sb42.toString());
                    VercelPoller.logToFile("Asynchronous validation complete. Status: ".concat(str4));
                } catch (Throwable th) {
                    StringBuilder sb6 = new StringBuilder("Failed to validate key on behalf of ");
                    String str9 = str;
                    sb6.append(str9 != null ? str9 : "global");
                    sb6.append(": ");
                    Log.e(VercelPoller.TAG, sb6.toString(), th);
                    VercelPoller.logToFile("Failed to validate key: " + th.toString());
                }
            }
        }).start();
    }

    private static void verifyActiveKeyStatus(final String str, final String str2, final File file) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - sLastCheckTime < 600000) {
            return;
        }
        sLastCheckTime = currentTimeMillis;
        new Thread(new Runnable() { // from class: com.floatingmenu.VercelPoller.2
            /* JADX WARN: Can't wrap try/catch for region: R(11:9|(1:13)|(3:18|19|(2:21|22)(2:23|(7:25|26|(1:28)|29|(1:31)(1:34)|32|33)(2:35|36)))|37|38|39|40|41|42|19|(0)(0)) */
            /* JADX WARN: Removed duplicated region for block: B:21:0x00ee A[Catch: all -> 0x00fa, TryCatch #1 {all -> 0x00fa, blocks: (B:19:0x009c, B:21:0x00ee, B:23:0x00fc, B:26:0x011a, B:28:0x011e, B:29:0x0124, B:32:0x0139, B:35:0x0106, B:40:0x0086), top: B:39:0x0086 }] */
            /* JADX WARN: Removed duplicated region for block: B:23:0x00fc A[Catch: all -> 0x00fa, TryCatch #1 {all -> 0x00fa, blocks: (B:19:0x009c, B:21:0x00ee, B:23:0x00fc, B:26:0x011a, B:28:0x011e, B:29:0x0124, B:32:0x0139, B:35:0x0106, B:40:0x0086), top: B:39:0x0086 }] */
            /* JADX WARN: Unreachable blocks removed: 4, instructions: 4 */
            @Override // java.lang.Runnable
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void run() {
                String str3;
                StringBuilder sb;
                String str4;
                String decrypt;
                try {
                    Log.i(VercelPoller.TAG, "Silent check running for key: " + str2);
                    VercelPoller.logToFile("Starting verifyActiveKeyStatus check. Key: " + str2);
                    if (str2.equalsIgnoreCase("admin")) {
                        VercelPoller.querySocket("SET_LICENSE_STATUS|active");
                        VercelPoller.sendStateChangedBroadcast();
                        VercelPoller.logToFile("verifyActiveKeyStatus - developer key admin bypass.");
                        return;
                    }
                    File file2 = new File("/data/adb/modules/zygisk_floating_menu/device_id");
                    String str5 = "";
                    if (file2.exists() && (str5 = VercelPoller.readFileToString(file2)) != null) {
                        str5 = str5.trim();
                    }
                    try {
                        if (str5 != null && !str5.isEmpty()) {
                            str3 = VercelPoller.TAG;
                            str4 = "verifyActiveKeyStatus - key is no longer active! Status updated to: ";
                            StringBuilder sb2 = new StringBuilder("{\"licenseKey\":\"");
                            sb2.append(str2);
                            sb2.append("\",\"deviceId\":\"");
                            sb2.append(str5);
                            sb2.append("\",\"timestamp\":");
                            String str6 = str4;
                            sb2.append(System.currentTimeMillis());
                            sb2.append("}");
                            String encrypt = CryptoHelper.encrypt(sb2.toString());
                            VercelPoller.logToFile("verifyActiveKeyStatus - sending POST request to verify-license...");
                            decrypt = CryptoHelper.decrypt(VercelPoller.makePostRequest("https://vercellicenseapi.vercel.app/api/verify-license", encrypt));
                            VercelPoller.logToFile("verifyActiveKeyStatus - decrypted response: " + decrypt);
                            if (!decrypt.contains("\"status\":\"active\"")) {
                                VercelPoller.querySocket("SET_LICENSE_STATUS|active");
                                VercelPoller.sendStateChangedBroadcast();
                                VercelPoller.logToFile("verifyActiveKeyStatus - key remains active.");
                                return;
                            }
                            if (!decrypt.contains("blocked")) {
                                VercelPoller.querySocket("SET_LICENSE_STATUS|active");
                                VercelPoller.sendStateChangedBroadcast();
                                VercelPoller.logToFile("verifyActiveKeyStatus - key remains active.");
                                return;
                            }
                            File file3 = file;
                            if (file3 != null) {
                                VercelPoller.writeValidationResponseToPrefs(file3, false, "blocked", str2);
                            }
                            VercelPoller.querySocket("SET_LICENSE_STATUS|".concat("blocked"));
                            VercelPoller.sendStateChangedBroadcast();
                            StringBuilder sb3 = new StringBuilder("Key for ");
                            String str7 = str;
                            if (str7 == null) {
                                str7 = "socket";
                            }
                            sb3.append(str7);
                            sb3.append(" is no longer active. Status updated to: ");
                            sb3.append("blocked");
                            Log.w(str3, sb3.toString());
                            VercelPoller.logToFile(str6.concat("blocked"));
                            return;
                        }
                        sb.append(System.currentTimeMillis());
                        str5 = sb.toString();
                        FileWriter fileWriter = new FileWriter(file2);
                        fileWriter.write(str5);
                        fileWriter.close();
                        StringBuilder sb22 = new StringBuilder("{\"licenseKey\":\"");
                        sb22.append(str2);
                        sb22.append("\",\"deviceId\":\"");
                        sb22.append(str5);
                        sb22.append("\",\"timestamp\":");
                        String str62 = str4;
                        sb22.append(System.currentTimeMillis());
                        sb22.append("}");
                        String encrypt2 = CryptoHelper.encrypt(sb22.toString());
                        VercelPoller.logToFile("verifyActiveKeyStatus - sending POST request to verify-license...");
                        decrypt = CryptoHelper.decrypt(VercelPoller.makePostRequest("https://vercellicenseapi.vercel.app/api/verify-license", encrypt2));
                        VercelPoller.logToFile("verifyActiveKeyStatus - decrypted response: " + decrypt);
                        if (!decrypt.contains("\"status\":\"active\"")) {
                        }
                    } catch (Throwable th) {
                        th = th;
                        StringBuilder sb4 = new StringBuilder("Failed to verify active key status for ");
                        String str8 = str;
                        sb4.append(str8 != null ? str8 : "socket");
                        Log.e(str3, sb4.toString(), th);
                        VercelPoller.logToFile("Failed to verify active key status: " + th.toString());
                        return;
                    }
                    sb = new StringBuilder("device_");
                    str3 = VercelPoller.TAG;
                    str4 = "verifyActiveKeyStatus - key is no longer active! Status updated to: ";
                } catch (Throwable th2) {
                    th = th2;
                    str3 = VercelPoller.TAG;
                }
            }
        }).start();
    }

    private static void writeConfigJson(String str, String str2, String str3, String str4, boolean z, String str5, String str6, boolean z2, String str7, String str8, String str9, boolean z3, boolean z4) {
        writeConfigJson(str, str2, str3, str4, z, str5, str6, z2, str7, str8, str9, z3, z4, getJsonBooleanValue(readConfigJson(), "mock_data_subscription"));
    }

    private static void writeConfigJson(String str, String str2, String str3, String str4, boolean z, String str5, String str6, boolean z2, String str7, String str8, String str9, boolean z3, boolean z4, boolean z5) {
        try {
            File file = new File(CONFIG_FILE_PATH);
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }
            String str10 = "{\n  \"license_key\": \"" + escapeJson(str) + "\",\n  \"license_status\": \"" + escapeJson(str2) + "\",\n  \"telegram_token\": \"" + escapeJson(str3) + "\",\n  \"telegram_chat_id\": \"" + escapeJson(str4) + "\",\n  \"sim1_enabled\": " + z + ",\n  \"sim1_provider\": \"" + escapeJson(str5) + "\",\n  \"sim1_number\": \"" + escapeJson(str6) + "\",\n  \"sim2_enabled\": " + z2 + ",\n  \"sim2_provider\": \"" + escapeJson(str7) + "\",\n  \"sim2_number\": \"" + escapeJson(str8) + "\",\n  \"sim_country\": \"" + escapeJson(str9) + "\",\n  \"iamnotdeveloper\": " + z3 + ",\n  \"iamnoroot\": " + z4 + ",\n  \"mock_data_subscription\": " + z5 + "\n}";
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(str10);
            fileWriter.close();
            Runtime.getRuntime().exec(new String[]{"chmod", "666", file.getAbsolutePath()});
            if (parentFile != null) {
                Runtime.getRuntime().exec(new String[]{"chmod", "777", parentFile.getAbsolutePath()});
            }
        } catch (Throwable th) {
            Log.e(TAG, "Failed to write config.json: ", th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void writeValidationResponseToPrefs(File file, boolean z, String str, String str2) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\n<map>\n    <boolean name=\"is_licensed\" value=\"" + z + "\" />\n    <string name=\"validation_status\">" + str + "</string>\n    <string name=\"license_key\">" + str2 + "</string>\n</map>\n");
            fileWriter.close();
            Runtime.getRuntime().exec(new String[]{"chmod", "660", file.getAbsolutePath()});
        } catch (Throwable th) {
            Log.e(TAG, "Failed to write validation response to prefs file " + file.getAbsolutePath(), th);
        }
    }
}
