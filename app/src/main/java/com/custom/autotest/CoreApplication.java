package com.custom.autotest;

import android.app.Application;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/8/27
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class CoreApplication extends Application {
    private final String TAG = "CoreApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        AutoTest.getInstance().init(getApplicationContext());
    }
}
