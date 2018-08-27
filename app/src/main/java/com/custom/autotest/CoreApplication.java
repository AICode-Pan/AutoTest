package com.custom.autotest;

import android.app.Application;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

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
        Thread.setDefaultUncaughtExceptionHandler(new SimpleUncaughtExceptionHandler());
    }

    public class SimpleUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            //读取stacktrace信息
            Writer result = new StringWriter();
            PrintWriter printWriter = new PrintWriter(result);
            ex.printStackTrace(printWriter);
            printWriter.close();
            String errorReport = result.toString();
            Log.i(TAG, "uncaughtException errorReport=" + errorReport);
        }
    }
}
