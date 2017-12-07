package com.bolex.apptrack;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Bolex on 2017/12/7.
 */

public class HookMain implements IXposedHookLoadPackage {

    private TextView msgTextView;
    static String msg = "";
    private ScrollView msgScrollView;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log("TrackLog: Package=[" + loadPackageParam.packageName + "]");
        hookFragment(loadPackageParam);
        hookV4Fragment(loadPackageParam);
        hookActivity(loadPackageParam);
    }

    private void hookActivity(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers.findAndHookMethod("android.app.Activity", loadPackageParam.classLoader, "onResume",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Activity mActivity = (Activity) param.thisObject;
                        addMsg(mActivity);

                        Intent intent = mActivity.getIntent();
                        ComponentName component = intent.getComponent();
                        String className = component.getClassName();
                        String packageName = component.getPackageName();
                        printActivityMsg(className, packageName);
                    }


                });


    }

    private void addMsg(Activity mActivity) {
        creactMsgView(mActivity);
        View decorView = mActivity.getWindow().getDecorView();
        if (decorView instanceof FrameLayout) {
            ((FrameLayout) decorView).addView(msgScrollView);
        }
    }

    private void creactMsgView(Activity mActivity) {
        msgScrollView = new ScrollView(mActivity);
        msgScrollView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                150));
        msgTextView = new TextView(mActivity);
        msgTextView.setTextColor(Color.WHITE);
        msgTextView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        msgTextView.setBackgroundColor(Color.parseColor("#cc888888"));
        msgTextView.setTextSize(10f);
        msgScrollView.addView(msgTextView);
    }


    private void hookFragment(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers.findAndHookMethod("android.app.Fragment", loadPackageParam.classLoader, "onResume",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        String fragmentName = param.thisObject.getClass().getName();
                        printFragmentMsg(fragmentName);
                    }
                });
    }

    private void hookV4Fragment(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers.findAndHookMethod("android.support.v4.app.Fragment", loadPackageParam.classLoader, "onResume",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        String fragmentName = param.thisObject.getClass().getName();
                        printFragmentMsg(fragmentName);
                    }
                });
    }


    private void printFragmentMsg(String fragmentName) {
        XposedBridge.log("TrackLog: 当前Fragmnet=[" + fragmentName + "]");
        setViewMsg(msg += "\n fra =" + fragmentName);
    }

    private void printActivityMsg(String className, String packageName) {
        XposedBridge.log("TrackLog: 源=[" + packageName + "]目标=[" + className + "]");
        setViewMsg(msg += "\n 源=[" + packageName + "]目标=[" + className + "]");
    }

    private void setViewMsg(String msg) {
        if (msgTextView != null) {
            msgTextView.setText(msg);
        }
    }

}
