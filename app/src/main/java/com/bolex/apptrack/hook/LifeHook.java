package com.bolex.apptrack.hook;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bolex.apptrack.ui.ViewHelp;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by liushenen on 2017/12/12.
 */

public class LifeHook {


    private TextView msgTextView;
    private static String msg = "";
    private String TAG = "TrackLog-life:";

    public  void hookActivity(XC_LoadPackage.LoadPackageParam loadPackageParam) {
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

    public void hookFragment(XC_LoadPackage.LoadPackageParam loadPackageParam) {
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

    public void hookV4Fragment(XC_LoadPackage.LoadPackageParam loadPackageParam) {
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


    public void setMsgView(Activity mActivity, final View mView) {
        View decorView = mActivity.getWindow().getDecorView();
        if (decorView instanceof FrameLayout) {
            FrameLayout decorViewParent = (FrameLayout) decorView;
            if (decorView.findViewWithTag(ViewHelp.getLogViewid()) == null) {
                decorViewParent.addView(mView);

            }
            // 修复微信重新加载层级问题
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mView.bringToFront();
                }
            }, 3000);

        }

    }


    private void printFragmentMsg(String fragmentName) {
        XposedBridge.log(TAG + " 当前Fragmnet=[" + fragmentName + "]");
        setViewMsg(msg += "\n fra =" + fragmentName);
    }

    private void printActivityMsg(String className, String packageName) {


        XposedBridge.log(TAG + " 源=[" + packageName + "]目标=[" + className + "]");
        setViewMsg(msg += "\n [" + packageName + "]=>[" + className + "]");
    }

    private void setViewMsg(String msg) {
        if (msgTextView != null) {
            msgTextView.setText(msg);
        }
    }
}
