package com.bolex.apptrack.hook;

import android.os.Build;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


/**
 * Created by liushenen on 2017/12/12.
 */

public class NetHook {
    public static final String TAG = "TrackLog-http:";

    public void hookNet(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        communicationHook(loadPackageParam);
    }

    private void communicationHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        final Class<?> http = XposedHelpers.findClass(getHttpUrlConnection(), loadPackageParam.classLoader);
        if (http == null) {
            XposedBridge.log(TAG+"Cannot find HttpURLConnection  class");
            return;
        }

        try {
            hookHttpUrlConnection(loadPackageParam);
        } catch (Exception e) {
            XposedBridge.log(e.toString());
            e.printStackTrace();
        }
    }

    private void hookHttpUrlConnection(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        XposedHelpers.findAndHookMethod(URL.class, "openConnection", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                URL url = (URL) param.thisObject;
                XposedBridge.log(TAG+"Request with HttpURLConnection: " + url.toString());
            }
        });
        XposedHelpers.findAndHookMethod(getHttpUrlConnection(), lpparam.classLoader, "setRequestMethod", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log(TAG+"URL: " + ((HttpURLConnection) param.thisObject).getURL().toString() + "\r\n Method: " + param.args[0]);
            }
        });
        XposedHelpers.findAndHookMethod(getHttpUrlConnection(), lpparam.classLoader, "setRequestProperty", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log(TAG+"Header: " + param.args[0] + "=" + param.args[1]);
            }
        });
        XposedHelpers.findAndHookMethod(getHttpUrlConnection(), null, "getOutputStream", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(new LoggedOutStream((OutputStream) param.getResult()));
            }
        });
        XposedHelpers.findAndHookMethod(getHttpUrlConnection(), null, "getInputStream", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(new LoggedInStream((InputStream) param.getResult()));
            }
        });
    }


    private String getHttpUrlConnection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return "com.android.okhttp.internal.huc.HttpURLConnectionImpl";
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return "com.android.okhttp.internal.http.HttpURLConnectionImpl";
        } else {
            return "libcore.net.http.HttpURLConnectionImpl";
        }
    }

    static class LoggedInStream extends FilterInputStream {
        public LoggedInStream(InputStream origin) throws IOException {
            super(null);
            byte[] bytes = read(origin);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            super.in = in;
            XposedBridge.log(TAG+"received:" + new String(bytes, "UTF-8"));
        }

        private byte[] read(InputStream in) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[128];
            int read = 0;
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
            in.close();
            return out.toByteArray();
        }
    }

    static class LoggedOutStream extends FilterOutputStream {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        public LoggedOutStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(byte[] buffer) throws IOException {
            super.write(buffer);
            bytes.write(buffer);
            XposedBridge.log(TAG+"out: " + new String(buffer,"UTF-8"));
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            super.write(buffer, offset, count);
            bytes.write(buffer, offset, count);
            XposedBridge.log(TAG+"out: " + new String(buffer, offset, count));
        }

        @Override
        public void write(int i) throws IOException {
            super.write(i);
            bytes.write(i);
            XposedBridge.log(String.valueOf((char) i));
        }

        @Override
        public void close() throws IOException {
            super.close();
            XposedBridge.log(TAG+"sent" + bytes.toString("UTF-8"));
        }

        @Override
        public void flush() throws IOException {
            super.flush();
            XposedBridge.log(TAG+"sent" + bytes.toString("UTF-8"));
        }
    }


}
