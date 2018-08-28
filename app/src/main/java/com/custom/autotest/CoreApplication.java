package com.custom.autotest;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            Log.i(TAG, "UncaughtException ErrorReport=\n" + errorReport);
            writeFileData(errorReport);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    //向指定的文件中写入指定的数据
    public void writeFileData(String content) {
        FileOutputStream fos = null;
        try {
            /* 判断sd的外部设置状态是否可以读写 */
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Date currentTime = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmmss");
                String dateString = formatter.format(currentTime);
                String filePath = "/sdcard/logcat/";
                makeRootDirectory(filePath);
                String fileName = "logcat_" + dateString + ".txt";
                File file = new File(filePath, fileName);
                // 先清空内容再写入
                fos = new FileOutputStream(file);
                //将要写入的字符串转换为byte数组
                byte[] bytes = content.getBytes();
                fos.write(bytes);//将byte数组写入文件
                fos.close();//关闭文件输出流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }
}
