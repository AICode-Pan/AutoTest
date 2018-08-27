package com.custom.autotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/8/27
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class MainActivity extends Activity {
    private String TAG = "MainActivity";
    private Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");


        intent.setAction("");
    }
}
