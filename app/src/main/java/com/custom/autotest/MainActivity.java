package com.custom.autotest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/8/27
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class MainActivity extends Activity implements View.OnClickListener {
    private String TAG = "MainActivity";
    private Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5, 5, 5, 5);
        for (int i = 0 ; i < 4 ; i++) {
            Button button = new Button(MainActivity.this);
            button.setId(i);
            button.setText("button" + i);
            button.setLayoutParams(lp);
            button.setOnClickListener(this);
            linearLayout.addView(button);
        }

        setContentView(linearLayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 0:
                Toast.makeText(MainActivity.this, "Hello Word!", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                AutoTest.getInstance().performKeyEvent();
                break;
            case 2:
                intent.setAction("");
                break;
            case 3:
                AutoTest.getInstance().performBroadcast();
                break;
        }
    }
}
