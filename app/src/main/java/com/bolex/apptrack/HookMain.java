package com.bolex.apptrack;


import com.bolex.apptrack.hook.LifeHook;
import com.bolex.apptrack.hook.NetHook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Bolex on 2017/12/7.
 */

public class HookMain implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log("TrackLog: Package=[" + loadPackageParam.packageName + "]");
        LifeHook lifeHook = new LifeHook();
        NetHook netHook = new NetHook();
        lifeHook.hookFragment(loadPackageParam);
        lifeHook.hookV4Fragment(loadPackageParam);
        lifeHook.hookActivity(loadPackageParam);
        netHook.hookNet(loadPackageParam);
    }


}
