package com.bolex.apptrack;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
                        View view = ViewHelp.creactMsgView(mActivity);
                        msgTextView = ViewHelp.getMsgTextView();
                        setMsgView(mActivity, view);
                        Intent intent = mActivity.getIntent();
                        ComponentName component = intent.getComponent();
                        String className = component.getClassName();
                        String packageName = component.getPackageName();
                        printActivityMsg(className, packageName);
                    }


                });
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


    private void setMsgView(Activity mActivity, View mView) {
        View decorView = mActivity.getWindow().getDecorView();
        if (decorView instanceof FrameLayout) {
            if (decorView.findViewWithTag(ViewHelp.getLogViewid()) == null) {
                ((FrameLayout) decorView).addView(mView);
            }
        }
    }


    private void printFragmentMsg(String fragmentName) {
        XposedBridge.log("TrackLog: 当前Fragmnet=[" + fragmentName + "]");
        setViewMsg(msg += "\n fra =" + fragmentName);
    }

    private void printActivityMsg(String className, String packageName) {
        XposedBridge.log("TrackLog: 源=[" + packageName + "]目标=[" + className + "]");
        setViewMsg(msg += "\n [" + packageName + "]=>[" + className + "]");
    }

    private void setViewMsg(String msg) {
        if (msgTextView != null) {
            msgTextView.setText(msg);
        }
    }

}
